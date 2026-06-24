import json
import re
import traceback
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
from utils.site.url_maker import make_url

class HelloMobileListAction():

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result = []
        file_counter = 0  # 파일 번호를 위한 카운터 초기화

        # 리스트 URL
        url_list = [
            'https://direct.lghellovision.net/fund/ajaxRateList.do?pgNum=0301&tabLink=Y&rateGubun=U&telecomGubun=LGU&menuGubun=5G',
        ]

        for base_url in url_list:
            url = make_url(base_url=base_url)
            cookies = requests.get(url).cookies
            # 리스트 URL 응답
            response = requests.get(url, 
                                    headers={
                "Host": "direct.lghellovision.net",
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
                }, cookies=cookies
            )        
            data = response.json()['list']  

            # JSON 파일 저장
            filename = f"./response_files/list_HelloMobile_{file_counter}.json"
            with open(filename, "w", encoding="utf-8") as file:
                json.dump(data, file, ensure_ascii=False, indent=4)
            file_counter += 1  # 파일 번호 증가
                
            for item in data:
                try:
                    uuid = "HELLOMOBILE_" + item['PAYMENTCODE']  # HELLOMOBILE_ 접두어 추가
                    # plan_type이 None이거나 예상치 못한 값일 경우 기본값을 'LTE'로 설정
                    plan_type = item.get('USIM_TYPE')
                    if plan_type not in ['LTE', '5G']:
                        plan_type = 'LTE'  # 기본값 설정

                    # URL 생성
                    dto_url = f'https://direct.lghellovision.net/rate/rateView.do?pgNum=0301&rateGubun=U&telecomGubun=LGU&menuGubun={plan_type}'

                    # 가격 정보 처리 (None 방지)
                    normal_price = int(item['DEFAULT_PRICE']) if item['DEFAULT_PRICE'] is not None else 0
                    sale_price = int(item['DEFAULT_PRICE']) - int(item['DIRECT_PROMOTION_PRICE']) if item['DIRECT_PROMOTION_PRICE'] is not None else normal_price

                    # QoS 및 daily_data 처리 (BASIC_DATA_TEXT 활용)
                    # BASIC_DATA_TEXT에서 qos 및 daily_data 값 추출
                    basic_data_text = (item.get('BASIC_DATA_TEXT') or "").strip()  # None 방지
                    qos = ""
                    daily_data = "0GB"  # 기본값 0GB

                    # "소진 후 X Kbps/Mbps 속도로 데이터 무제한"
                    if match := re.search(r"소진 후\s*(\d+\.?\d*)\s*(Kbps|Mbps)", basic_data_text):
                        qos = f"{match.group(1)}{match.group(2)}"
                        daily_data = "0GB"  # QoS가 존재하면 기본적으로 0GB

                    # "소진 후 일 YGB＋X Mbps 속도로 데이터 무제한"
                    if match := re.search(r"소진 후\s*일\s*(\d+)\s*GB\s*＋\s*(\d+\.?\d*)\s*Mbps", basic_data_text):
                        daily_data = f"{match.group(1)}GB"
                        qos = f"{match.group(2)}Mbps"

                    # "일일 데이터 소진시 최대 X Mbps 속도로 무제한"
                    if match := re.search(r"일일 데이터 소진시 최대\s*(\d+\.?\d*)\s*Mbps", basic_data_text):
                        qos = f"{match.group(1)}Mbps"
                        daily_data = "0GB"

                    # "최대 X Kbps/Mbps 속도로 무제한"
                    if match := re.search(r"최대\s*(\d+\.?\d*)\s*(Kbps|Mbps)\s*속도로\s*무제한", basic_data_text):
                        qos = f"{match.group(1)}{match.group(2)}"
                        daily_data = "0GB"

                    # "소진시 최대 X Mbps 속도로 속도제어"
                    if match := re.search(r"소진시\s*최대\s*(\d+\.?\d*)\s*Mbps\s*속도로\s*속도제어", basic_data_text):
                        qos = f"{match.group(1)}Mbps"
                        daily_data = "0GB"


                    # 이후 가격 처리 (문자열 → 정수 변환)
                    after_price = sale_price
                    event_comment = item["EVENT_COMMENT"] if item["EVENT_COMMENT"] else ''
                    if "이후" in event_comment:
                        after_price_str = event_comment.split('이후')[1].split('원')[0].strip().replace(',', '')
                        after_price = int(after_price_str) if after_price_str.isdigit() else sale_price

                    # 정규식으로 "N개월간" 패턴 추출
                    match = re.search(r"(\d+)개월간", event_comment)
                    promotion_period = match.group(1) + "개월" if match else ''


                    # 부가통화 텍스트 처리
                    buga_call = item['BASIC_VOICE_TEXT'] if item['BASIC_VOICE_TEXT'] else ''
                    buga_call = buga_call.replace("부가통화", "").strip()

                    # 음성통화 및 문자 제공량 처리 (None 방지 + 단위 추가)
                    voice_call = item['BASIC_VOICE'] if item['BASIC_VOICE'] is not None else (
                        f"{item['BASIC_VOICE_NEW']}분" if item['BASIC_VOICE_NEW'] is not None else '기본제공'
                    )
                    message = item['BASIC_SMS'] if item['BASIC_SMS'] is not None else (
                    "기본제공" if item['BASIC_SMS_NEW'] == "-1" else (
                        f"{item['BASIC_SMS_NEW']}건" if item['BASIC_SMS_NEW'] is not None else "0건"
                    )
                )

                    # 12개월 & 24개월 총 요금(m12_price, m24_price) 초기화
                    m12_price = 0
                    m24_price = 0

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
                        
                    dto = PlanData(
                        uuid=uuid,
                        mno=item['Telecom'],
                        telecom=SiteTargetListType.HELLO_MOBILE_LIST.value,
                        company_id=SiteTargetListIDType.HELLO_MOBILE_LIST.value,  # company_id 추가
                        url=dto_url,
                        plan_type=plan_type,
                        plan_name=item['SALES_NAME'],
                        data=item['BASIC_DATA'] if item['BASIC_DATA'] else '',
                        voice_call=voice_call,
                        message=message,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        after_price=after_price,
                        benefit='',
                        qos=qos,
                        business_name='주식회사 엘지헬로비전',   
                        combination=False,
                        freebies='',
                        etc='',
                        promotion_period=promotion_period,
                        buga_call=buga_call,
                        plan_code='',
                        daily_data=daily_data,
                        m12_price=int(m12_price),
                        m24_price=int(m24_price),
                    )
                    result.append(dto)

                except Exception as e:
                    traceback.print_exc()
            
        is_end = True 
        
        return result, is_end
