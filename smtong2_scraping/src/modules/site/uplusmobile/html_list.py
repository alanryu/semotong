import json
import traceback
import time
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType
from modules.dto.input_queue_dto import PlanData
import re
from utils.site.url_maker import UrlParm, make_url
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[UplusMobile]'

class UplusMobileListAction():

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result = []

        # 리스트 URL
        url_list = [
            'https://www.uplusumobile.com/product/pric/usim/pricList?fltrTypeCtgr=LTE',
            'https://www.uplusumobile.com/product/pric/usim/pricList?fltrTypeCtgr=5G',
        ]
        for base_url in url_list:
            url = make_url(base_url=base_url)

            # 리스트 URL 응답
            plan_type_label = base_url.split('fltrTypeCtgr=')[1]
            print(f"{TAG} 목록 페이지 요청: {plan_type_label} - {url}")
            req_start = time.time()
            response = requests.get(url,
                                    headers={
                "Host": "www.uplusumobile.com",
                "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
                }
            )
            req_elapsed = time.time() - req_start
            print(f"{TAG} 목록 페이지 응답 완료 ({req_elapsed:.1f}초)")
            html = response.text
            bs = BeautifulSoup(html, 'html.parser')
            ul_list = bs.find_all('ul', 'fee-list')
            li_list = [li for ul in ul_list for li in ul.find_all('li')]
            total_count = len(li_list)
            print(f"{TAG} {plan_type_label} 파싱할 항목 수: {total_count}건")

            for idx, item in enumerate(li_list):
                try:
                    if item.find('a') is None:
                        continue
                    seq = item.find('a').attrs['seq']
                    upPpnCd = item.find('a').attrs['upppncd']
                    devKdCd = item.find('a').attrs['ctgrid']

                    uuid = "UPLUS_" + seq + upPpnCd + devKdCd
                    plan_type = base_url.split('fltrTypeCtgr=')[1]
                    feature = item.find('div', 'feature')

                    detail_url = make_url(
                        base_url='https://www.uplusumobile.com/product/pric/pricDetail',
                        url_params=[
                            UrlParm(key='seq', value=seq),
                            UrlParm(key='upPpnCd', value=upPpnCd),
                            UrlParm(key='devKdCd', value=devKdCd),
                        ]
                    )

                    # 디테일 페이지
                    plan_name_log = item.find('a').find('strong').get_text(strip=True) if item.find('a') and item.find('a').find('strong') else uuid
                    print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 요청: {plan_name_log}")
                    detail_start = time.time()
                    detail_page = requests.get(detail_url,
                                               headers={
                        "Host": "www.uplusumobile.com",
                        "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36",
                    }).text
                    detail_elapsed = time.time() - detail_start
                    print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 응답 완료 ({detail_elapsed:.1f}초)")

                    bs = BeautifulSoup(detail_page, 'html.parser')
                    benefit_html = bs.find('section', 'basic')
                    headers = benefit_html.find_all('h3')
                    benefit_list = []
                    for header in headers:
                        header_text = header.text
                        if len(header.find_next_sibling().find_all('li')) > 0:
                            contents = [content_html.text for content_html in header.find_next_sibling().find_all('li')]
                            string_contents = '|'.join(contents)
                            benefit_list.append(f"{header_text}:{string_contents}")

                    qos = ''
                    qos_text = item.find('li', 'tooltip-wrap').text if item.find('li', 'tooltip-wrap') else ''
                    if qos_text:
                        if 'Kbps' in qos_text:
                            qos = qos_text.split('Kbps')[0].split(' ')[-1] + 'Kbps'
                        elif 'Mbps' in qos_text:
                            qos = qos_text.split('Mbps')[0].split(' ')[-1] + 'Mbps'

                    # 가격 정보 추출 (쉼표 및 '원' 제거 후 정수 변환)
                    normal_price = item.find('span', 'origin-pay').text if item.find('span', 'origin-pay') else item.find('span', 'discount-pay').text
                    sale_price = item.find('strong', 'discount-pay').text if item.find('strong', 'discount-pay') else item.find('span', 'discount-pay').text

                    # 값이 None이거나 빈 값이면 기본값 "0"을 설정
                    normal_price = normal_price.replace('원', '').replace(',', '').strip() if normal_price else "0"
                    sale_price = sale_price.replace('원', '').replace(',', '').strip() if sale_price else "0"

                    # int 변환 (숫자가 아니면 기본값 0 적용)
                    normal_price = int(normal_price) if normal_price.isdigit() else 0
                    sale_price = int(sale_price) if sale_price.isdigit() else 0
                    after_price = sale_price




                    detail_voice_call = feature.find('span', 'limit').text

                    # 부가 통화 정보 추출
                    buga_call = ''
                    buga_call_items = bs.find_all('li', class_='tooltip-wrap')

                    for li in buga_call_items:
                        if '부가 통화' in li.text:
                            buga_call_text = li.get_text(strip=True)  # 텍스트 가져오기 (공백 제거)
                            match = re.search(r'(\d+)분', buga_call_text)  # "숫자+분" 패턴 찾기
                            if match:
                                buga_call = match.group(1) + '분'  # 첫 번째 일치하는 숫자만 저장
                            break  # 첫 번째 부가 통화 정보만 저장하고 종료
                    
                    promotion_period = ''

                    # 데이터에서 데일리 데이터 분리
                    data = feature.find('span', 'vol').text if feature.find('span', 'vol') else ""
                    
                    daily_data = ""
                    daily_match = re.search(r'일\s*(\d+\.?\d*)GB', data)
                    if daily_match:
                        daily_data = f"{daily_match.group(1)}GB"
                        data = re.sub(r'\+?\s*일\s*\d+\.?\d*GB', '', data).strip()
                    
                    if not data or data == "+":
                        data = "0GB"


                    # 12개월 & 24개월 요금 계산
                    if not promotion_period or promotion_period == "평생":  # 빈 값, None, "평생" 동일 처리
                        m12_price = sale_price * 12
                        m24_price = sale_price * 24  # 평생일 때도 24개월 가격 계산
                    elif "개월" in promotion_period:
                        months = int(re.search(r"(\d+)", promotion_period).group(1))  # 개월 수 추출
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
                        

                    benefit=""

                    # PlanData 객체 생성
                    dto = PlanData(
                        uuid=uuid,
                        mno='LGU',
                        telecom=SiteTargetListType.UPLUS_MOBILE_LIST.value,
                        url=detail_url,
                        plan_type=plan_type,
                        plan_name=item.find('a').find('strong').get_text(strip=True),
                        data=data,
                        voice_call=detail_voice_call,
                        message=feature.find('span', 'supply').text,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit=benefit,
                        qos=qos,
                        business_name='(주)미디어로그',
                        after_price=after_price,
                        combination='',
                        freebies='',
                        etc='',
                        promotion_period='',
                        plan_code='',
                        company_id=SiteTargetListIDType.UPLUS_MOBILE_LIST.value,
                        buga_call=buga_call,
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price,
                    )
                    result.append(dto)
                    success_count += 1

                except Exception as e:
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()
                    error_count += 1

        is_end = True

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")

        return result, is_end
