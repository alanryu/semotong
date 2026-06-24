# import json
# import re
# import traceback
# import requests
# from modules.site.target import SiteTargetListType, SiteTargetListIDType
# from modules.dto.input_queue_dto import PlanData
# from utils.site.url_maker import make_url
# import  os

# class HelloMobileListAction():
    
#     def fetch_plan_data(self, payload: dict) -> dict:
#         """ 개별 API 요청을 처리하는 함수 """
#         try:
#             response = requests.post(
#                 "https://direct.lghellovision.net/fund/ajaxRateList.do",
#                 headers={
#                     "Host": "direct.lghellovision.net",
#                     "User-Agent": "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36",
#                     "Accept": "application/json, text/javascript, */*; q=0.01",
#                     "Accept-Encoding": "gzip, deflate, br, zstd",
#                     "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
#                     "Connection": "keep-alive",
#                     "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
#                     "Origin": "https://direct.lghellovision.net",
#                     "Referer": "https://direct.lghellovision.net/rate/rateView.do?pgNum=0301&tabLink=Y&rateGubun=U&telecomGubun=LGU&menuGubun=5G",
#                     "X-Requested-With": "XMLHttpRequest"
#                 },
#                 data=payload
#             )
#             response.raise_for_status()
#             return response.json()
#         except Exception as e:
#             traceback.print_exc()
#             return {}

#     def fetch_all_plan_data(self) -> dict:
#         """ 모든 API 요청을 실행하고 데이터를 통합하는 함수 """
#         combined_data = {"list": []}
#         api_requests = [
#             {"rateType": "U", "telecom": "LGU", "usimType": "5G", "pIdx": 200},
#             {"rateType": "U", "telecom": "LGU", "usimType": "5G", "pIdx": 244},
#             {"rateType": "U", "telecom": "LGU", "usimType": "5G", "pIdx": 245},
#             {"rateType": "U", "telecom": "LGU", "usimType": "LTE", "pIdx": 139},
#             {"rateType": "U", "telecom": "LGU", "usimType": "LTE", "pIdx": 143},
#             {"rateType": "U", "telecom": "LGU", "usimType": "LTE", "pIdx": 140},
#             {"rateType": "U", "telecom": "LGU", "usimType": "LTE", "pIdx": 150},
#             {"rateType": "U", "telecom": "LGU", "usimType": "LTE", "pIdx": 193},
#             {"rateType": "U", "telecom": "LGU", "usimType": "LTE", "pIdx": 141},
#             {"rateType": "U", "telecom": "LGU", "usimType": "LTE", "pIdx": 148},
#             {"rateType": "U", "telecom": "LGU", "usimType": "LTE", "pIdx": 222},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 66},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 156},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 62},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 202},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 93},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 51},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 85},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 58},
#             {"rateType": "U", "telecom": "KT", "usimType": "LTE", "pIdx": 81},
#             {"rateType": "U", "telecom": "SKT", "usimType": "LTE", "pIdx": 75},
#         ]

#         for payload in api_requests:
#             data = self.fetch_plan_data(payload)
#             if "list" in data:
#                 combined_data["list"].extend(data["list"])

#         return combined_data

#     def root(self, page: int, *args, **kwargs) -> tuple[list, bool]:
#         result = []
#         is_end = False

#         # 모든 API 요청 실행 및 통합
#         combined_data = self.fetch_all_plan_data()

#         # JSON 파일 저장
#         os.makedirs("./response_files", exist_ok=True)
#         with open("./response_files/hello_mobile_combined.json", "w", encoding="utf-8") as file:
#             json.dump(combined_data, file, ensure_ascii=False, indent=4)

#         # combined_data에서 데이터 추출 및 가공
#         for item in combined_data.get("list", []):
#             try:
#                 uuid = "HELLOMOBILE_" + item.get('PAYMENTCODE', '')  # HELLOMOBILE_ 접두어 추가
                
#                 mno=item.get('Telecom', '')
                
#                 plan_type = item.get('USIM_TYPE', 'LTE')  # 기본값 LTE
#                 if plan_type not in ['LTE', '5G']:
#                     plan_type = 'LTE'

#                 plan_name=item.get('SALES_NAME', '')

#                 data=item.get('BASIC_DATA', '')

#                 url = f"https://direct.lghellovision.net/rate/rateView.do?pgNum=0301&rateGubun=U&telecomGubun={mno}&menuGubun={plan_type}"

#                 if item.get('BASIC_VOICE') is not None:
#                     voice_call = item['BASIC_VOICE']
#                 else:
#                     voice_new = item.get('BASIC_VOICE_NEW')
#                     if voice_new == "-1":
#                         voice_call = "기본제공"
#                     elif voice_new is not None:
#                         voice_call = f"{voice_new}분"
#                     else:
#                         voice_yn = item.get('BASIC_VOICE_YN')
#                         voice_call = "0분" if voice_yn == "N" else "기본제공"

                
#                 if item.get('BASIC_SMS') is not None:
#                     message = item['BASIC_SMS']
#                 else:
#                     sms_new = item.get('BASIC_SMS_NEW')
#                     if sms_new == "-1":
#                         message = "기본제공"
#                     elif sms_new is not None:
#                         message = f"{sms_new}건"
#                     else:
#                         message = "0건"

#                 normal_price = int(item.get('DEFAULT_PRICE', "0").replace(",", ""))  # 문자열을 정수로 변환, 쉼표 제거

#                 direct_promotion = item.get('DIRECT_PROMOTION_PRICE', "0")  # None 방지
#                 direct_promotion = direct_promotion.replace(",", "") if isinstance(direct_promotion, str) else "0"  # 쉼표 제거

#                 # direct_promotion이 숫자인지 체크 후 변환, 숫자가 아니면 normal_price 유지
#                 sale_price = normal_price - int(direct_promotion) if direct_promotion.isdigit() else normal_price

#                 after_price = sale_price

#                 # QoS 및 Daily Data 정규식 패턴 적용
#                 basic_data_text = (item.get('BASIC_DATA_TEXT') or "").strip()  # None 방지
#                 qos = ""
#                 daily_data = "0GB"  # 기본값 0GB

#                 # QoS (속도 제한) 추출
#                 qos_pattern = re.compile(r"(?:소진 후|최대|일일 데이터 소진시 최대|소진시 최대|＋)\s*(\d+\.?\d*)\s*(Kbps|Mbps)")
#                 qos_matches = qos_pattern.findall(basic_data_text)
#                 if qos_matches:
#                     qos = " / ".join([f"{match[0]}{match[1]}" for match in qos_matches])

#                 # Daily Data (일일 데이터 제공량) 추출
#                 daily_data_pattern = re.compile(
#                     r"(?:소진 후\s*일\s*(\d+)\s*GB|일\s*(\d+)\s*GB|매일\s*(\d+)\s*GB|1일\s*(\d+)\s*GB|"
#                     r"소진 후\s*(\d+)\s*GB|기본제공\s*(\d+)\s*GB|월\s*(\d+)\s*GB|"
#                     r"일\s*(\d+)\s*GB\s*＋\s*최대\s*\d+\.?\d*\s*(Kbps|Mbps))"
#                 )
#                 daily_data_match = daily_data_pattern.findall(basic_data_text)
#                 if daily_data_match:
#                     daily_data = " / ".join([f"{match[i]}GB" for match in daily_data_match for i in range(len(match) - 1) if match[i]])



#                 # 이후 가격 처리 (문자열 → 정수 변환)
#                 after_price = sale_price
#                 event_comment = item["EVENT_COMMENT"] if item["EVENT_COMMENT"] else ''
#                 if "이후" in event_comment:
#                     after_price_str = event_comment.split('이후')[1].split('원')[0].strip().replace(',', '')
#                     after_price = int(after_price_str) if after_price_str.isdigit() else sale_price

#                 # 정규식으로 "N개월간" 패턴 추출
#                 match = re.search(r"(\d+)개월간", event_comment)
#                 promotion_period = match.group(1) + "개월" if match else ''


#                 # 부가통화 텍스트 처리
#                 buga_call = item['BASIC_VOICE_TEXT'] if item['BASIC_VOICE_TEXT'] else ''
#                 buga_call = buga_call.replace("부가통화", "").strip()


#                 # 12개월 & 24개월 총 요금(m12_price, m24_price) 초기화
#                 m12_price = 0
#                 m24_price = 0

#                 # 12개월 & 24개월 요금 계산
#                 if not promotion_period or promotion_period == "평생":  # 빈 값, None, "평생" 동일 처리
#                     m12_price = sale_price * 12
#                     m24_price = sale_price * 24  # 평생일 때도 24개월 가격 계산
#                 elif "개월" in promotion_period:
#                     months = int(re.search(r"(\d+)", promotion_period).group(1))  # 개월 수 추출
#                     if months >= 24:  # 24개월 이상일 경우 24개월 할인 적용
#                         m12_price = sale_price * 12
#                         m24_price = sale_price * 24
#                     elif months >= 12:  # 12개월 초과 24개월 이하일 경우
#                         m12_price = sale_price * 12
#                         m24_price = (sale_price * months) + (after_price * (24 - months))
#                     else:  # 12개월 이하일 경우
#                         m12_price = (sale_price * months) + (after_price * (12 - months))
#                         m24_price = (sale_price * months) + (after_price * (24 - months))
#                 else:
#                     m12_price = normal_price * 12  # 기본값
#                     m24_price = normal_price * 24  # 기본값

#                 dto = PlanData(
#                     uuid=uuid,
#                     mno=mno,
#                     telecom=SiteTargetListType.HELLO_MOBILE_LIST.value,
#                     company_id=SiteTargetListIDType.HELLO_MOBILE_LIST.value,
#                     url=url,
#                     plan_type=plan_type,
#                     plan_name=plan_name,
#                     data=data,
#                     voice_call=voice_call,
#                     message=message,
#                     normal_price=normal_price,
#                     sale_price=sale_price,
#                     after_price=after_price,
#                     benefit='',
#                     qos=qos,
#                     business_name='주식회사 엘지헬로비전',
#                     combination=False,
#                     freebies='',
#                     etc='',
#                     promotion_period=promotion_period,
#                     buga_call=buga_call,
#                     plan_code='',
#                     daily_data=daily_data,
#                     m12_price=int(m12_price),
#                     m24_price=int(m24_price),
#                 )
#                 result.append(dto)

#             except Exception as e:
#                 traceback.print_exc()
#             is_end = True
#         return result, is_end




import json
import re
import time
import traceback
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
from utils.site.url_maker import make_url
import  os

TAG = '[HelloMobile]'

class HelloMobileListAction():
    
    def fetch_plan_data(self, payload: dict) -> dict:
        """ 개별 API 요청을 처리하는 함수 """
        try:
            telecom = payload.get("telecom", "")
            print(f"{TAG} API 요청 시작: telecom={telecom}")
            t0 = time.time()

            # 기존 payload를 실제 요청 형식으로 변환
            form_data = {
                "reqRateType": payload.get("rateType", "U"),
                "reqTelecom": payload.get("telecom", "LGU"),
                "reqOrder": "S1",            
                "reqUsimType": payload.get("usimType", ""),  
                "reqDataSum": "",
                "reqDepleRateList": "",
                "reqCalls": "",
                "reqPriceGubunList": "",
                "reqMin": "0",
                "reqMax": "70000",
                "reqText": "",
                "pIdx": str(payload.get("pIdx", "")),
            }
            
            response = requests.post(
                "https://direct.lghellovision.net/fund/ajaxRateList.do",
                headers={
                    "Host": "direct.lghellovision.net",
                    "User-Agent": "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36",
                    "Accept": "application/json, text/javascript, */*; q=0.01",
                    "Accept-Encoding": "gzip, deflate, br, zstd",
                    "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
                    "Connection": "keep-alive",
                    "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                    "Origin": "https://direct.lghellovision.net",
                    "Referer": "https://direct.lghellovision.net/rate/rateViewUsim.do",
                    "X-Requested-With": "XMLHttpRequest"
                },
                data=form_data
            )
            response.raise_for_status()
            elapsed = time.time() - t0
            print(f"{TAG} API 응답 완료: telecom={telecom} ({elapsed:.1f}초)")

            return response.json()
        except Exception as e:
            print(f"{TAG} [ERROR] API 요청 실패: telecom={telecom}: {e}")
            traceback.print_exc()
            return {}

    def fetch_all_plan_data(self) -> dict:
        """ 모든 API 요청을 실행하고 데이터를 통합하는 함수 """
        combined_data = {"list": []}
        api_requests = [
            {"rateType": "U", "telecom": "LGU"},
            {"rateType": "U", "telecom": "KT"},
        ]

        for payload in api_requests:
            data = self.fetch_plan_data(payload)
            if "list" in data:
                combined_data["list"].extend(data["list"])

        return combined_data

    def root(self, page: int, *args, **kwargs) -> tuple[list, bool]:
        print(f"{TAG} 스크래핑 시작")
        total_start = time.time()
        result = []
        is_end = False
        success_count = 0
        skip_count = 0
        error_count = 0

        # 모든 API 요청 실행 및 통합
        combined_data = self.fetch_all_plan_data()

        # JSON 파일 저장
        os.makedirs("./response_files", exist_ok=True)
        with open("./response_files/hello_mobile_combined.json", "w", encoding="utf-8") as file:
            json.dump(combined_data, file, ensure_ascii=False, indent=4)

        # combined_data에서 데이터 추출 및 가공
        items = combined_data.get("list", [])
        total_count = len(items)
        print(f"{TAG} 파싱 대상 요금제 수: {total_count}건")

        for idx, item in enumerate(items):
            try:
                uuid = "HELLOMOBILE_" + item.get('paymentcode', '')  # HELLOMOBILE_ 접두어 추가
                
                mno=item.get('telecom', '')
                
                plan_type = item.get('usimType', 'LTE')  # 기본값 LTE
                if plan_type not in ['LTE', '5G']:
                    plan_type = 'LTE'

                plan_name=item.get('salesName', '')
                print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {mno} | {plan_name}")

                url = f"https://direct.lghellovision.net/rate/rateViewUsim.do?pgNum=0301&rateGubun=U&telecomGubun={mno}&menuGubun={plan_type}"

                # 통화 
                dedicated_calls_gubun = item.get('dedicatedCallsGubun')
                dedicated_calls_value = item.get('dedicatedCallsValue')

                if dedicated_calls_gubun == "L":
                    voice_call = "기본제공"
                elif dedicated_calls_gubun == "B":
                    # 값이 존재하고 비어 있지 않으면 해당 값 + "분"
                    if dedicated_calls_value:
                        voice_call = f"{dedicated_calls_value}분"
                    else:
                        voice_call = "0분"
                else:
                    voice_call = "0분"

                # 문자
                dedicated_sms_gubun = item.get('dedicatedSmsGubun')
                dedicated_sms_value = item.get('dedicatedSmsValue')
                
                if dedicated_sms_gubun == "L":
                    message = "기본제공"
                elif dedicated_sms_gubun == "B":
                    # 값이 존재하고 비어 있지 않으면 해당 값 + "건"
                    if dedicated_sms_value:
                        message = f"{dedicated_sms_value}건"
                    else:
                        message = "0건"
                else:
                    message = "0건"


                # 가격 정책
                # 값 가져오기 (문자열 쉼표 제거 후 정수 변환)
                def parse_price(value):
                    if value is None or value == "":
                        return 0
                    return int(str(value).replace(",", ""))

                direct_price = parse_price(item.get("directPromotionDirectmallPrice"))
                after_price_val = parse_price(item.get("directPromotionAfterPrice"))
                month_chk = item.get("directPromotionAfterMonthChk")

                # 로직 적용
                if not month_chk:  # "" 또는 None → 프로모션 없음
                    normal_price = direct_price
                    sale_price = direct_price
                    after_price = direct_price
                else:  # 값이 있음 → 프로모션 있음
                    normal_price = after_price_val
                    after_price = after_price_val
                    sale_price = direct_price  # 7개월간 할인 적용
                
                
                # data & qos
                # 단위 변환 함수
                def convert_unit(gubun):
                    if gubun == "G":
                        return "GB"
                    elif gubun == "M":
                        return "MB"
                    else:
                        return ""

                # 데이터 값 조합 함수
                def make_data(value, gubun):
                    if not value:  # None 또는 빈문자열
                        value = "0"
                    unit = convert_unit(gubun)
                    return f"{value}{unit if unit else ''}"

                # QoS 변환 테이블
                qos_map = {
                    "1": "400Kbps",
                    "2": "1Mbps",
                    "3": "3Mbps",
                    "4": "5Mbps"
                }

                # 데이터 조합
                data = make_data(item.get("dedicatedMonthlyOfferValue"), item.get("dedicatedMonthlyOfferGubun"))
                daily_data = make_data(item.get("dedicatedDailyOfferValue"), item.get("dedicatedDailyOfferGubun"))

                # QoS 매핑
                rate = item.get("dedicatedDataDepletionRate")
                qos = qos_map.get(rate) if rate and rate != "0" else None


                # 부가콜
                # 부가통화(buga_call) 계산
                vcall_value = item.get("dedicatedViedocallsValue")
                buga_call = f"{vcall_value}분" if vcall_value and vcall_value.strip() != "" else None
                
                
                # 12개월 & 24개월 총 요금(m12_price, m24_price) 초기화
                m12_price = 0
                m24_price = 0
                promotion_period_raw = item.get("directPromotionAfterMonthChk")
                
                # promotion_period 가공: 저장용 문자열로 변환
                if not promotion_period_raw or promotion_period_raw == "평생":
                    promotion_period = ""
                else:
                    # 숫자 문자열인 경우 → "7개월" 형태로 변환
                    promotion_period = f"{promotion_period_raw}개월"

                # 실제 계산용 개월 수 추출
                months = 0
                if promotion_period_raw and promotion_period_raw.isdigit():
                    months = int(promotion_period_raw)

                # 12개월 & 24개월 요금 계산
                if not promotion_period_raw or promotion_period_raw == "평생":
                    m12_price = sale_price * 12
                    m24_price = sale_price * 24
                else:
                    if months >= 24:
                        m12_price = sale_price * 12
                        m24_price = sale_price * 24
                    elif months >= 12:
                        m12_price = sale_price * 12
                        m24_price = (sale_price * months) + (after_price * (24 - months))
                    else:
                        m12_price = (sale_price * months) + (after_price * (12 - months))
                        m24_price = (sale_price * months) + (after_price * (24 - months))

                dto = PlanData(
                    uuid=uuid,
                    mno=mno,
                    telecom=SiteTargetListType.HELLO_MOBILE_LIST.value,
                    company_id=SiteTargetListIDType.HELLO_MOBILE_LIST.value,
                    url=url,
                    plan_type=plan_type,
                    plan_name=plan_name,
                    data=data,
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
                success_count += 1

            except Exception as e:
                error_count += 1
                print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                traceback.print_exc()
            is_end = True

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
  