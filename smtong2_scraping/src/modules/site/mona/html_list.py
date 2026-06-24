import json
import re
import time
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
from utils.site.url_maker import UrlParm, make_url
import json

TAG = '[Mona]'

class MonaListAction():

    def root(self,
        page: int,
        *args,
        **kwargs) -> tuple[list[PlanData], bool]:
        start_time = time.time()
        print(f"{TAG} 스크래핑 시작")
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0
        file_counter = 0  # 파일 번호를 위한 카운터 초기화

        # 리스트 URL
        url_list = [
            'https://mobilemona.co.kr/common/component/plan/AjaxRate_plan.aspx',
        ]
        for base_url in url_list:

            url = make_url(base_url=base_url)
            # 리스트 URL 응답
            t0 = time.time()
            print(f"{TAG} 목록 API 요청: {base_url}")
            response = requests.post(url,
                                    headers={
                "Host": "mobilemona.co.kr",
                "content-length": "65"
                },
                json={
                    "header": [
                        {
                        "type": "01"
                        }
                    ],
                    "body": [
                        {
                        "e_agcd": "",
                        "keywordSeq": ""
                        }
                    ]
                }
            )
            
            elapsed = time.time() - t0
            print(f"{TAG} 목록 API 응답 완료 ({elapsed:.1f}초)")
            li_list = response.json()["DATA"]
            total_count = len(li_list)
            print(f"{TAG} 요금제 {total_count}건 발견")
            # JSON 응답을 파일로 저장
            file_path = f'./response_files/mona_response_{file_counter}.json'
            with open(file_path, 'w', encoding='utf-8') as f:
                json.dump(li_list, f, ensure_ascii=False, indent=4)

            for idx, item in enumerate(li_list):
                try:
                    # ✅ `SEEDATA`에서 데이터, 일일 데이터, QoS 값 추출
                    data_unit = item["SEEDATA"].replace("\n", "").strip()  # 개행 제거 및 공백 정리

                    data = "0GB"  # 기본 데이터
                    daily_data = ""  # 일일 데이터
                    qos = ""  # QoS 속도

                    # ✅ QoS 속도 추출 (예: "3Mbps", "1Mbps", "5Mbps")
                    qos_match = re.search(r"(\d+Mbps)", data_unit)
                    if qos_match:
                        qos = qos_match.group(1)
                        data_unit = data_unit.replace(qos, "").strip()  # QoS 제거

                    # ✅ 일일 데이터 추출 (예: "일2GB", "매일5GB")
                    daily_match = re.search(r"(일\s?\d+GB|매일\s?\d+GB)", data_unit)
                    if daily_match:
                        daily_data = re.search(r"(\d+GB)", daily_match.group(0)).group(1)  # "2GB" 부분만 추출
                        data_unit = data_unit.replace(daily_match.group(0), "").strip()  # 일일 데이터 제거

                    # ✅ 남은 값이 있으면 기본 데이터량으로 설정 (예: "11GB", "95GB")
                    if data_unit:
                        data = data_unit.strip()

                    # ✅ 데이터 값이 "+" 또는 빈 값이면 기본값 "0GB"로 설정
                    if data == "+" or data == "":
                        data = "0GB"

                    # ✅ 프로모션 기간 변환
                    promotion_period = item['EVENT_PERIOD']
                    if promotion_period == '':
                        pass
                    elif promotion_period == '120':
                        promotion_period = '평생'
                    else:
                        promotion_period += '개월'
                        
                    SEEVOICE = item['SEEVOICE']
                    
                    # ✅ 부가통화 값 추출
                    buga_call = ""
                    match = re.search(r"(부가음성|부가)\s*([0-9]+분)", SEEVOICE.replace('\n', ''))
                    if match:
                        buga_call = match.group(2)  # "XXX분" 값 추출
                    
                    normal_price = item["TT_AMT"].replace(',', '').replace('원', '')
                    discount_price = item["DISCOUNT"].replace(',', '').replace('원', '')
                    sale_price = discount_price if discount_price != '0' else normal_price
                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {item.get('GDNM', '')}")
                    url = f'https://mobilemona.co.kr/view/plan/rate_detail.aspx?gdcd={item["GDCD"]}'
                    t1 = time.time()
                    response = requests.get(url).text
                    elapsed_detail = time.time() - t1
                    print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 응답 완료 ({elapsed_detail:.1f}초)")
                    
                    file_counter += 1  # 파일 번호 증가
                    
                    bs = BeautifulSoup(response, 'html.parser')
                    after_price = bs.find("input", id="ContentPlaceHolder1_hidOrgCharge")['value'].replace(',', '')
                    after_price = after_price if after_price != '0' else normal_price
                    
                    uuid = item["GDCD"]

                    # ✅ 줄바꿈 제거 + 쉼표 제거
                    seevoice = item["SEEVOICE"].replace("\n", " ").replace(",", "")

                    # ✅ "기본제공"이면 그대로 저장
                    if "기본제공" in seevoice:
                        voice_call = "기본제공"
                    else:
                        # ✅ 숫자 + "분" 형태가 있으면 추출 (쉼표 제거 후 매칭)
                        match = re.search(r"(\d+)\s*분", seevoice)
                        voice_call = f"{match.group(1)}분" if match else "0분"  # 값이 없으면 "0분" 기본값
                    # ✅ combination 값 설정 (U+ 유선상품과 결합 가능 여부 확인)
                    combination = False
                    combination_element = bs.find("ul", id="ContentPlaceHolder1_content")
                    if combination_element:
                        if "U+ 유선상품과 결합이 가능한 요금제입니다." in combination_element.text:
                            combination = True

                    # ✅ sale_price 및 after_price 변환
                    sale_price = int(sale_price) if sale_price.isdigit() else 0
                    after_price = int(after_price) if after_price.isdigit() else 0

                    # ✅ 12개월 & 24개월 총 요금 계산 (m12_price, m24_price)
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
                        
                    dto = PlanData(
                        uuid='MONA_' + uuid,
                        telecom=SiteTargetListType.MONA_LIST.value,
                        company_id=SiteTargetListIDType.MONA_LIST.value,
                        mno='LGU',
                        url=url,
                        plan_type=item["NETDIV"],
                        plan_name=item["GDNM"],
                        data=data,
                        daily_data=daily_data,  # ✅ 추가
                        voice_call=voice_call,
                        message=item["SEELETTER"],
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit='',
                        qos=qos,
                        business_name='코나아이(주)',
                        after_price=after_price,
                        combination=combination,
                        freebies='',
                        etc='',
                        promotion_period=promotion_period,
                        buga_call=buga_call,
                        plan_code='',
                        m12_price=m12_price,
                        m24_price=m24_price,  
                    )
                    result.append(dto)
                    success_count += 1

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

        is_end = True
        elapsed_total = time.time() - start_time
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초) | 성공: {success_count}, 스킵: {skip_count}, 에러: {error_count}")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
