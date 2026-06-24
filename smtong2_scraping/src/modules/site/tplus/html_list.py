# src/modules/site/tplus/html_list.py

import ssl
import traceback
import warnings
import urllib3
import re
import time
import requests
from bs4 import BeautifulSoup
from requests.adapters import HTTPAdapter
from urllib3.poolmanager import PoolManager

from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import AllData, DetailInfo, PlanData, Telecom
from utils.etc.functions import filter_detail

warnings.simplefilter('ignore', urllib3.exceptions.InsecureRequestWarning)

TAG = '[TPlus]'

# /uc 판매몰 엔드포인트
BASE = 'https://www.tplusmobile.com'
UC_LIST_PAGE = f'{BASE}/uc'                                  # mallcd 추출용
UC_RATE_API = f'{BASE}/BackBone/rate/sub_rate_list'          # 요금제 목록 페이징 API
UC_DETAIL_URL = f'{BASE}/uc/rate/plan_details?seq='          # 요금제 상세

# 응답 구분자 (TRUE ㅹㆄ HTML ㅹㆄ 전체수 ㅹㆄ 로드수 ㅹㆄ tp ㅹㆄ 다음key)
SEP = 'ㅹㆄ'

USER_AGENT = ('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 '
              '(KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36')


class TLSAdapter(HTTPAdapter):
    def init_poolmanager(self, connections, maxsize, block=False, **kwargs):
        ctx = ssl.create_default_context()
        ctx.set_ciphers('DEFAULT@SECLEVEL=1')
        ctx.check_hostname = False
        ctx.verify_mode = ssl.CERT_NONE
        kwargs['ssl_context'] = ctx
        self.poolmanager = PoolManager(
            num_pools=connections,
            maxsize=maxsize,
            block=block,
            **kwargs
        )


class TPlusListAction:
    def root(self, page: int = 1, *args, **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result = []
        detail_result = []
        start_time = time.time()

        print(f"{TAG} 스크래핑 시작")

        try:
            # requests 세션 생성 및 TLSAdapter 장착
            session = requests.Session()
            session.mount('https://', TLSAdapter())
            session.headers.update({'user-agent': USER_AGENT})

            # === mallcd 추출 (/uc 페이지 인라인 스크립트에 박혀있음) ===
            print(f"{TAG} mallcd 추출 중...")
            uc_html = session.get(UC_LIST_PAGE, verify=False, timeout=30).text
            mallcd_match = re.search(r'mallcd["\'\s:]*([A-F0-9]{32})', uc_html)
            if not mallcd_match:
                print(f"{TAG} [CRITICAL] mallcd를 찾지 못해 스크래핑을 중단합니다.")
                return AllData(planData=result, detailInfo=detail_result), True
            mallcd = mallcd_match.group(1)
            print(f"{TAG} mallcd 추출 완료: {mallcd}")

            # === 요금제 목록 페이징 수집 (key 기반, 중복/빈응답 시 종료) ===
            print(f"{TAG} 요금제 목록 페이징 시작...")
            html_parts = []
            seen_seq = set()
            key = ''
            req_page = 0
            while True:
                req_page += 1
                body = (
                    f'tp=F&key={key}&keyword=&seloptiontp=&selcompanytp='
                    f'&filter_data_s=0&filter_data_e=30001'
                    f'&filter_voice_s=0&filter_voice_e=501'
                    f'&filter_price_s=0&filter_price_e=50001'
                    f'&mallcd={mallcd}&selsorttp='
                    f'&sel_companies=&sel_sms=&sel_network=&sel_qos=&sel_term=&sel_benefit_seqs='
                )
                try:
                    resp = session.post(
                        UC_RATE_API,
                        headers={
                            'content-type': 'application/x-www-form-urlencoded; charset=UTF-8',
                            'referer': UC_LIST_PAGE,
                        },
                        data=body,
                        verify=False,
                        timeout=30,
                    ).text
                except Exception as e:
                    print(f"{TAG} [ERROR] 목록 페이징 요청 실패 (page {req_page}): {e}")
                    break

                parts = resp.split(SEP)
                if not parts[0].startswith('TRUE'):
                    print(f"{TAG} 목록 응답 정상종료/실패 신호 (page {req_page}, flag={parts[0][:20]!r})")
                    break

                page_html = parts[1]
                next_key = parts[-1]

                page_seqs = re.findall(r'seq=([A-Za-z0-9]+)', page_html)
                new_seqs = [s for s in page_seqs if s not in seen_seq]
                if not new_seqs:
                    print(f"{TAG} 새 항목 없음 → 페이징 종료 (page {req_page}, 누적 {len(seen_seq)}개)")
                    break

                for s in new_seqs:
                    seen_seq.add(s)
                html_parts.append(page_html)

                if req_page == 1:
                    print(f"{TAG} 전체 {parts[-4]}건 (페이지당 {parts[-3]}건)")
                if req_page % 5 == 0:
                    print(f"{TAG} 페이징 {req_page}회 - 누적 {len(seen_seq)}개")

                if not next_key or next_key == key:
                    print(f"{TAG} key 종료 신호 → 페이징 종료 (page {req_page}, 누적 {len(seen_seq)}개)")
                    break
                key = next_key

                if req_page > 100:
                    print(f"{TAG} [WARNING] 페이징 안전장치 도달 → 중단")
                    break

            # BeautifulSoup로 파싱
            soup = BeautifulSoup(''.join(html_parts), 'html.parser')
            card_list = soup.select('a[href*="plan_details?seq="]')
            total_count = len(card_list)
            print(f"{TAG} HTML 파싱 완료 - 총 {total_count}개 요금제 발견")

            success_count = 0
            skip_count = 0
            error_count = 0
            detail_success_count = 0
            detail_error_count = 0

            for idx, item in enumerate(card_list):
                try:
                    # === UUID 추출 ===
                    href = item.get('href', '')
                    uuid_match = re.search(r'seq=([A-Za-z0-9]+)', href)
                    if not uuid_match:
                        skip_count += 1
                        continue
                    uuid = uuid_match.group(1)

                    # === 통신사 (MNO) ===
                    badge = item.select_one('.badge.kt, .badge.skt, .badge.lgu')
                    if not badge:
                        skip_count += 1
                        print(f"{TAG} [{idx+1}/{total_count}] uuid={uuid} 통신사 배지 없음 → skip")
                        continue
                    telecom_txt = badge.get_text(strip=True)
                    telecom = {
                        'LGU+': Telecom.LGU.name,
                        'KT': Telecom.KT.name,
                        'SKT': Telecom.SKT.name
                    }.get(telecom_txt, '')

                    # === 요금제 타입 (5G / LTE) ===
                    badges = item.select('.badgeArea .badge')
                    plan_type = 'LTE'
                    for b in badges:
                        if '5G' in b.get_text():
                            plan_type = '5G'
                            break

                    # === 요금제명 ===
                    title_el = item.select_one('h4.title')
                    if not title_el:
                        skip_count += 1
                        print(f"{TAG} [{idx+1}/{total_count}] uuid={uuid} 요금제명 없음 → skip")
                        continue
                    plan_name = title_el.get_text(strip=True)

                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {telecom_txt} | {plan_name}")

                    # === 데이터 / 일일데이터 / QoS ===
                    data_el = item.select_one('.desc.desc20px700')
                    data_text = data_el.get_text(' ', strip=True) if data_el else ''

                    data_str = '0GB'
                    daily_data = ''
                    qos = ''

                    # QoS 추출
                    qos_match = re.search(r'(\d+(?:\.\d+)?)\s*(Mbps|Kbps)', data_text, re.IGNORECASE)
                    if qos_match:
                        qos = f"{qos_match.group(1)}{qos_match.group(2)}"

                    # 일일 데이터 추출
                    daily_match = re.search(r'일\s*(\d+(?:\.\d+)?)\s*(GB|MB)', data_text)
                    if daily_match:
                        daily_data = f"{daily_match.group(1)}{daily_match.group(2)}"

                    # 기본 데이터 추출
                    data_matches = re.findall(r'(\d+(?:\.\d+)?)\s*(GB|MB)', data_text)
                    if data_matches:
                        main_data = data_matches[0]
                        data_str = f"{main_data[0]}{main_data[1]}"

                    # === 통화 ===
                    call_el = item.select_one('.ico.call')
                    voice_call = call_el.get_text(strip=True) if call_el else '0분'

                    # === 문자 ===
                    msg_el = item.select_one('.ico.message')
                    message = msg_el.get_text(strip=True) if msg_el else '0건'

                    # === 부가통화 (영상/부가통화) ===
                    buga_call = ''
                    video_el = item.select_one('.ico.video')
                    if video_el:
                        video_text = video_el.get_text(strip=True)
                        buga_match = re.search(r'(\d+분)', video_text)
                        if buga_match:
                            buga_call = buga_match.group(1)

                    # === 할인가 (sale_price) ===
                    amount_area = item.select_one('.amountArea')
                    sale_price_el = amount_area.select_one('.textPoint') if amount_area else None
                    sale_price = int(sale_price_el.get_text(strip=True).replace(',', '')) if sale_price_el else 0

                    # === 정상가 (normal_price) ===
                    throth_el = item.select_one('.throthDesc')
                    if throth_el:
                        np_match = re.search(r'([\d,]+)원', throth_el.get_text(strip=True))
                        normal_price = int(np_match.group(1).replace(',', '')) if np_match else sale_price
                    else:
                        normal_price = sale_price

                    # === 프로모션 기간 & 할인 후 가격 ===
                    promo_period = '평생'
                    after_price = sale_price

                    # amountArea의 할인 기간 텍스트에서 추출
                    if amount_area:
                        price_desc = amount_area.select_one('.desc16px700')
                        if price_desc:
                            price_text = price_desc.get_text(' ', strip=True)
                            period_match = re.search(r'(\d+개월간|평생)', price_text)
                            if period_match:
                                period_txt = period_match.group(1)
                                if '평생' in period_txt:
                                    promo_period = '평생'
                                    after_price = sale_price
                                else:
                                    promo_period = period_txt.replace('간', '')

                    # rateDetail에서 할인 후 가격 추출
                    rate_details = item.select('.rateDetail .text')
                    if rate_details:
                        first_detail = rate_details[0].get_text(strip=True)
                        # "7개월간 월 23,000원, 7개월 후 월 44,000원" 패턴
                        after_match = re.search(r'후\s*월\s*([\d,]+)\s*원', first_detail)
                        if after_match:
                            after_price = int(after_match.group(1).replace(',', ''))
                        elif '평생' in first_detail:
                            promo_period = '평생'
                            ap_match = re.search(r'([\d,]+)\s*원', first_detail)
                            if ap_match:
                                after_price = int(ap_match.group(1).replace(',', ''))

                    # === 12개월 / 24개월 총 요금 계산 ===
                    if promo_period == '평생':
                        m12_price = sale_price * 12
                        m24_price = sale_price * 24
                    elif '개월' in promo_period:
                        match = re.search(r'(\d+)', promo_period)
                        months = int(match.group(1)) if match else 0
                        if months >= 24:
                            m12_price = sale_price * 12
                            m24_price = sale_price * 24
                        elif months >= 12:
                            m12_price = sale_price * 12
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                        else:
                            m12_price = (sale_price * months) + (after_price * (12 - months))
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                    else:
                        m12_price = normal_price * 12
                        m24_price = normal_price * 24

                    # === 혜택 (benefits) ===
                    benefits = []
                    for rd in rate_details:
                        txt = rd.get_text(strip=True)
                        if txt:
                            benefits.append(txt)
                    benefit = '|'.join(benefits) if benefits else ''

                    # === 결합 가능 여부 ===
                    combination = '결합가능' in item.get_text()

                    # === 상세 페이지 파싱 ===
                    detail_url = f'{UC_DETAIL_URL}{uuid}'
                    detail_start = time.time()
                    try:
                        detail_response = session.get(detail_url, verify=False, timeout=30).text
                        detail_elapsed = time.time() - detail_start
                        print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 응답 완료 ({detail_elapsed:.1f}초)")
                    except requests.exceptions.Timeout:
                        detail_elapsed = time.time() - detail_start
                        print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 상세 페이지 TIMEOUT ({detail_elapsed:.1f}초) uuid={uuid}")
                        detail_error_count += 1
                        continue
                    except Exception as e:
                        detail_elapsed = time.time() - detail_start
                        print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 상세 페이지 요청 실패 ({detail_elapsed:.1f}초) uuid={uuid}: {e}")
                        detail_error_count += 1
                        continue

                    detail_soup = BeautifulSoup(detail_response, 'html.parser')

                    data_detail = ''
                    voice_call_detail = ''
                    message_detail = ''
                    over_fee_detail = ''

                    # accoBox 기반 상세 정보 추출
                    acco_boxes = detail_soup.select('.accoBox')
                    for box in acco_boxes:
                        title_el = box.select_one('.title18px700')
                        if not title_el:
                            continue
                        title_text = title_el.get_text(strip=True)
                        content_el = box.select_one('.accoContent')
                        if not content_el:
                            continue

                        if title_text == '통화':
                            voice_call_detail = content_el.get_text(strip=True)
                        elif title_text == '데이터':
                            data_detail = content_el.get_text(strip=True)
                        elif title_text == '문자':
                            message_detail = content_el.get_text(strip=True)
                        elif title_text == '요금제 초과율':
                            over_fee_detail = str(content_el)

                    filter_result = filter_detail(detail_response)
                    detail_success_count += 1

                    detail_result.append(DetailInfo(
                        uuid='TPLUS_' + uuid,
                        data=data_detail,
                        voice_call=voice_call_detail,
                        message=message_detail,
                        precautions='',
                        over_fee=over_fee_detail,
                        event='',
                        micropayment=filter_result[0],
                        overseas_roaming=filter_result[1],
                        mobile_hotspot=filter_result[2],
                        data_sharing=filter_result[3]
                    ))

                    result.append(PlanData(
                        uuid='TPLUS_' + uuid,
                        mno=telecom,
                        telecom=SiteTargetListType.TPLUS_LIST.value,
                        company_id=SiteTargetListIDType.TPLUS_LIST.value,
                        url=f'{UC_DETAIL_URL}{uuid}',
                        plan_type=plan_type,
                        plan_name=plan_name,
                        data=data_str,
                        voice_call=voice_call,
                        message=message,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit=benefit,
                        qos=qos,
                        business_name='㈜한국케이블텔레콤',
                        after_price=after_price,
                        combination=combination,
                        freebies='',
                        etc='',
                        promotion_period=promo_period,
                        buga_call=buga_call,
                        plan_code='',
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price
                    ))
                    success_count += 1
                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

        except Exception as e:
            print(f"{TAG} [CRITICAL] 스크래핑 중 치명적 오류 발생: {e}")
            traceback.print_exc()

        elapsed_total = time.time() - start_time
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 리스트 파싱: 성공 {success_count}건, skip {skip_count}건, 오류 {error_count}건")
        print(f"{TAG} 상세 페이지: 성공 {detail_success_count}건, 오류 {detail_error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건, DetailInfo {len(detail_result)}건")

        is_end = True
        return AllData(planData=result, detailInfo=detail_result), is_end
