import json
import re
import time
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import AllData, DetailInfo, PlanData, Telecom
from utils.etc.functions import filter_detail

TAG = '[InsMobile]'

class InsMobileListAction():
    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        print(f"{TAG} 스크래핑 시작")
        total_start = time.time()
        is_end = False
        result = []
        detail_result = []
        success_count = 0
        skip_count = 0
        error_count = 0
        item_idx = 0

        t0 = time.time()
        print(f"{TAG} 메인 페이지 요청 시작")
        response = requests.get('https://www.insmobile.co.kr/rate_plan.do').text
        print(f"{TAG} 메인 페이지 응답 완료 ({time.time() - t0:.1f}초)")
        bs = BeautifulSoup(response, 'html.parser')

        # 리스트 URL
        url_list = []

        url_li_list = bs.find('ul', 'tab_box_list_t3').find_all('li')
        for li in url_li_list:
            url = 'https://www.insmobile.co.kr/' + li.find('a')['href']
            url_list.append(url)

        print(f"{TAG} 카테고리 URL 수: {len(url_list)}개")

        file_counter = 1
        for base_url in url_list:
            t0 = time.time()
            response = requests.get(base_url)
            print(f"{TAG} 카테고리 페이지 응답 완료 ({time.time() - t0:.1f}초): {base_url}")
            html = response.text
            bs = BeautifulSoup(html, 'html.parser')
            li_list = bs.find_all('li', 'card_list_item')
            print(f"{TAG} 카테고리 내 요금제 수: {len(li_list)}건")

            for item in li_list:
                item_idx += 1
                try:
                    if not item.find('a'):
                        skip_count += 1
                        continue

                    href = item.find('a')['href']
                    detail_url = "https://www.insmobile.co.kr/" + href
                    t0 = time.time()
                    detail_response = requests.get(detail_url).text
                    elapsed_detail = time.time() - t0

                    # 상세 데이터 저장
                    
                    with open(f'./response_files/detail_InsMobile{file_counter}.html', 'w', encoding='utf-8') as file:
                        file.write(detail_response)
                    file_counter += 1
                    
                    detail_bs = BeautifulSoup(detail_response, 'html.parser')
                                      
                    # price_div 대신 table 태그로 변경
                    price_table = detail_bs.find('table', class_='__se_tbl')

                    normal_price = "0"
                    sale_price = "0"

                    if price_table:
                        # 각 행 데이터를 가져옵니다.
                        rows = price_table.find_all('tr')
                        if len(rows) > 1:  # 첫 번째 행은 헤더, 두 번째 행은 데이터
                            # 두 번째 행의 모든 열(td) 가져오기
                            columns = rows[1].find_all('td')
                            if len(columns) >= 4:
                                # 기본료 (8,800)
                                normal_price = columns[0].text.strip().replace(',', '')
                                # 실 청구금액 (1,870)
                                # sale_price = columns[3].text.strip().replace(',', '')
                                # after_price = normal_price
                    
                    
                    # sale_price 추출 로직 수정
                    # 기본 데이터 추출
                    price_div = detail_bs.find('div', class_='price')
                    after_price = normal_price  # 기본값은 normal_price로 설정
                    if price_div:
                        # "월 1,770원" 추출
                        price_paragraph = price_div.find('p')
                        if price_paragraph:
                            price_text = price_paragraph.text.strip()
                            # "월 1,770원"에서 숫자만 추출
                            sale_price_match = re.search(r'월\s*([\d,]+)원', price_text)
                            if sale_price_match:
                                sale_price = sale_price_match.group(1).replace(',', '')  # 쉼표 제거
                    
                    
                    


                    if price_div:
                        ref_paragraph = price_div.find('p', class_='ref')
                        if ref_paragraph:
                            # *12개월 이후 28,500원에서 '원' 앞의 숫자 추출
                            after_price_match = re.search(r'(\d{1,3}(,\d{3})*)원', ref_paragraph.text)
                            if after_price_match:
                                after_price = after_price_match.group(1).replace(',', '')  # 쉼표 제거
                    


                    title = item.find('p', 'title')
                    plan_name = title.text.strip() if title else 'Unknown'

                    telecom_text = item.find('span', 'me-2').text if item.find('span', 'me-2') else ''
                    telecom = {'LGU+': Telecom.LGU.name, 'SKT': Telecom.SKT.name, 'KT': Telecom.KT.name}.get(telecom_text, '')

                    print(f"{TAG} [{item_idx}] 파싱 중: {telecom_text} | {plan_name}")
                    print(f"{TAG} [{item_idx}] 상세 페이지 응답 완료 ({elapsed_detail:.1f}초)")


                    promotion_period = ''
                    badge_wrap = item.find('p', class_='badge_wrap')
                    if badge_wrap:
                        badges = badge_wrap.find_all('span', class_='badge')
                        for badge in badges:
                            if '개월 할인' in badge.text:
                                promotion_period = badge.text.split('개월 할인')[0].strip() + '개월'
                                break
                            
                    detail2_url = detail_url.split(";jsessionid=")[0] + "?" + detail_url.split("?")[1]
                    
                    # 데이터, 음성, 메시지 추출
                    desc = detail_bs.find('ul', class_='desc')
                    li_list2 = desc.find_all('li') if desc else []

                    data, qos, voice_call, message = '', '', '', ''

                    if li_list2:
                        for li in li_list2:
                            # alt 속성을 사용하여 항목 구분
                            icon = li.find('img', class_='icon')
                            text = li.find('span').text.strip() if li.find('span') else ''

                            if icon and 'data' in icon.get('alt', ''):
                                data_text = text.split('+')  # '+' 기준으로 데이터 구분
                                data = ''
                                daily_data = ''
                                qos = ''

                                for part in data_text:
                                    part = part.strip()

                                    # ✅ "일 XGB" 패턴 감지 (예: "일 5GB")
                                    match = re.search(r'일\s*(\d+(?:\.\d+)?)\s*(GB|MB)', part, re.IGNORECASE)
                                    if match:
                                        value, unit = match.groups()
                                        daily_data = f"{value}{unit}"  # ✅ "일 5GB" → daily_data = "5GB"
                                    elif "Mbps" in part:
                                        qos = part  # ✅ QoS 값 설정
                                    else:
                                        data = part  # ✅ 기존 기본 데이터 저장

                                # ✅ `data`가 비어 있고, `daily_data`가 존재하면 `data = 0GB`
                                if not data and daily_data:
                                    data = "0GB"

                                # ✅ `daily_data`가 비어 있으면 기본값 "0GB"로 설정
                                if not daily_data:
                                    daily_data = "0GB"



                                # print(f"Extracted Data: {data}, Daily Data: {daily_data}, QoS: {qos}")  # 최종 값 확인


                            elif icon and 'telephone' in icon.get('alt', ''):
                                # 음성 통화 처리
                                voice_call = text.split(' ')[0].strip()
                                # 숫자인 경우 '분' 추가
                                if re.match(r'^\d+$', voice_call):
                                    voice_call = f"{voice_call}분".replace(' ', '')
                            elif icon and 'message' in icon.get('alt', ''):
                                # 메시지 처리
                                message = text.split(' ')[0].strip()
                                # 숫자인 경우 '건' 추가
                                if re.match(r'^\d+$', message):
                                    message = f"{message}건".replace(' ', '')
                    
                    # 부가통화 값 추출
                    buga_call = ''
                    rate_caption = detail_bs.find('div', class_='rate_caption')
                    if rate_caption:
                        rate_caption_list = rate_caption.find('ul', class_='rate_caption_list')
                        if rate_caption_list:
                            li_items = rate_caption_list.find_all('li')
                            for li in li_items:
                                if '부가통화' in li.text:
                                    # "부가통화 300분"에서 숫자와 "분" 추출
                                    match = re.search(r'부가통화\s*(\d+\s*분)', li.text)
                                    if match:
                                        buga_call = match.group(1).strip()  # 예: "300분"
                                    break
                    
                    if '평생' in plan_name:
                        promotion_period = '평생'
                        after_price = sale_price

                    
                    # sale_price 및 after_price 변환
                    sale_price = int(sale_price) if sale_price.isdigit() else 0
                    after_price = int(after_price) if after_price.isdigit() else 0

                    # 12개월 & 24개월 총 요금(m12_price, m24_price) 계산
                    if not promotion_period or promotion_period == "평생":  # 빈 값, None, "평생" 동일 처리
                        m12_price = sale_price * 12
                        m24_price = sale_price * 24  # 평생일 때도 24개월 가격 계산
                    elif "개월" in promotion_period:
                        match = re.search(r"(\d+)", promotion_period)
                        if match:
                            months = int(match.group(1))  # 개월 수 추출
                        else:
                            months = 0  # 기본값 설정

                        if months >= 24:  # 24개월 이상일 경우 24개월 할인 적용
                            m12_price = sale_price * 12
                            m24_price = sale_price * 24
                        elif months >= 12:  # 12개월 초과 24개월 이하일 경우
                            m12_price = sale_price * 12
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                        else:  # 12개월 이하일 경우
                            m12_price = (sale_price * months) + (after_price * (12 - months))
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                    else:
                        m12_price = normal_price * 12  # 기본값
                        m24_price = normal_price * 24  # 기본값

                    # ✅ "결합 가능" 및 "인터넷 결합" 여부 확인
                    combination = False  # 기본값은 False로 설정

                    badge_red_list = item.find_all('span', class_='badge badge_red')  # ✅ 모든 해당 요소 가져오기
                    for badge in badge_red_list:
                        if "결합 가능" in badge.text.strip() or "인터넷 결합" in badge.text.strip():  # ✅ "인터넷 결합"도 포함하여 검사
                            combination = True
                            break  # ✅ 하나라도 해당되면 True로 설정하고 종료

                        
                    dto = PlanData(
                        uuid='INS_' + href.split('no=')[-1],
                        mno=telecom,
                        telecom=SiteTargetListType.INS_MOBILE_LIST.value,
                        company_id=SiteTargetListIDType.INS_MOBILE_LIST.value,
                        url=detail2_url,
                        plan_type='5G' if '5G' in plan_name else 'LTE',
                        plan_name=plan_name,
                        data=data,
                        voice_call= voice_call,
                        message= message,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit='',
                        qos=qos,
                        business_name='(주)인스코리아',
                        after_price=after_price,
                        combination=combination,
                        freebies='',
                        etc='',
                        promotion_period=promotion_period,
                        buga_call= buga_call,
                        plan_code = '',
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price,
                    )
                    result.append(dto)
                    success_count += 1

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{item_idx}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

        is_end = True
        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return AllData(planData=result, detailInfo=detail_result), is_end
