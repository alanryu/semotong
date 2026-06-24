import json
import time
import requests
import os
import re
from modules.dto.input_queue_dto import AllData, PlanData
from utils.site.url_maker import make_url
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[KgMobile]'


class KgMobileListAction():
    def root(self, page: int, *args, **kwargs) -> tuple[AllData, bool]:
        print(f"{TAG} 스크래핑 시작")
        total_start = time.time()
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0

        url = make_url(f'https://www.kgmobile.co.kr/api/product/plan?block=5&isAdult=&isUser=true&limit=-1&network=&orderType=RECOMMEND&page={page}&searchAmount=&searchData=&useFlag=Y')
        t0 = time.time()
        print(f"{TAG} API 요청 시작")
        response = requests.get(url)
        response_json = response.json()
        print(f"{TAG} API 응답 완료 ({time.time() - t0:.1f}초)")

        # JSON 데이터를 response_files 폴더에 저장
        os.makedirs("response_files", exist_ok=True)
        json_filename = f"response_files/list_kgmobile_page_{page}.json"
        with open(json_filename, "w", encoding="utf-8") as file:
            json.dump(response_json, file, ensure_ascii=False, indent=4)

        if "entity" in response_json and "list" in response_json["entity"]:
            li_list = response_json["entity"]["list"]
            total_count = len(li_list)
            print(f"{TAG} 파싱 대상 요금제 수: {total_count}건")

            for idx, item in enumerate(li_list):
                try:
                    plan_code = str(item.get("planCode", ""))  # ✅ 정수 값을 문자열로 변환
                    uuid = "KG_" + plan_code
                    plan_name_log = item.get("planName", "")
                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {item.get('telco', '')} | {plan_name_log}")
                    
                    # telco 값을 기반으로 mno 변환
                    telco_to_mno = {
                        "LGT": "LGU",
                        "SKT": "SKT",
                        "SK": "SKT",
                        "KT": "KT"
                    }
                    mno = telco_to_mno.get(item.get("telco", ""), "")
                    #url = "https://www.kgmobile.co.kr/plan/" + str(item.get("planNo", ""))
                    plan_no = item.get("planNo", "")
                    url = f"https://www.kgmobile.co.kr/plan/{plan_no}?alliance=ALAC1021"
                    network = item.get("network", "")
                    plan_type = "LTE" if network == "4G" else network  # ✅ 4G → LTE 변경

                    # data 값을 basicData와 basicDataUnit에서 가져와 변환
                    data=item.get("basicMonthData", "") + item.get("basicMonthDataUnit", "")
                    # -1GB이면 0GB로 변환 (매일 5GB 제공 등의 경우)
                    if data == "-1GB":
                        data = "0GB"

                    # voice_call 값을 basicVoice에서 가져와 변환
                    basic_voice = str(item.get("basicVoice", "")).strip()
                    voice_call = "무제한" if basic_voice in ["-", "-1"] else basic_voice + "분" if basic_voice.isdigit() else basic_voice

                    # message 값을 basicSms에서 가져와 변환
                    basic_sms = str(item.get("basicSms", "")).strip()
                    message = "무제한" if basic_sms in ["무제한", "-1"] else basic_sms + "건" if basic_sms.isdigit() else basic_sms

                    # 기본 요금 설정
                    basic_amount = int(item.get("basicAmount", 0))
                    sale_price = basic_amount  # 기본값 설정

                    # sale_price 계산 (saleList가 비어있지 않을 때만)
                    sale_list = item.get("saleList", [])

                    if sale_list:
                        # ltSaleAmount 값을 가져옴
                        lt_sale_amount = int(sale_list[0].get("ltSaleAmount", 0))

                        # ltSaleAmount가 0이면 prSaleAmount를 사용
                        if lt_sale_amount == 0:
                            pr_sale_amount = int(sale_list[0].get("prSaleAmount", 0))
                            sale_price = basic_amount - pr_sale_amount
                        else:
                            sale_price = basic_amount - lt_sale_amount


                    # 플랜 이름에서 "평생" 포함 여부 확인
                    plan_name = item.get("planName", "")

                    # 기본적으로 promotion_period를 빈 문자열로 초기화
                    promotion_period = ""

                    if "평생" in plan_name:
                        promotion_period = "평생"
                    else:
                        # promotion_period 값을 contents에서 추출 (1개월 이상 모든 개월 수 및 "평생" 포함)
                        contents_text = item.get("contents", "")

                        # "평생"이 있는지 먼저 확인하고, 있으면 "평생" 처리
                        if "할인기간 제한이 없는 평생 할인 요금제입니다." in contents_text:
                            promotion_period = "평생"
                        else:
                            # "가입월 포함 *개월간 적용" 형태로 개월 수 추출
                            match = re.search(r"가입월 포함\s*(\d+)개월간 적용됩니다", contents_text)
                            if match:
                                promotion_period = f"{match.group(1)}개월"


                    
                    # after_price 값을 promotion_period 기준으로 설정
                    after_price = basic_amount if promotion_period and promotion_period != "평생" else sale_price

                    # combination 값을 contents에서 찾기 (해당 문구 포함 여부에 따라 True 또는 False 설정)
                    combination = "인터넷 결합 안내" in contents_text or "인터넷결합안내" in contents_text

                    # notice 필드에서 "*분" 패턴 검색
                    notice_text = item.get("notice", "")
                    match = re.search(r"(\d+)\s?분", notice_text)

                    # 부가통화 값을 basicAddVoice에서 가져오기
                    basic_add_voice = item.get("basicAddVoice", "")

                    # 값이 None인 경우 빈 문자열로 변환
                    if basic_add_voice is None:
                        basic_add_voice = ""

                    # 문자열 타입으로 변환 후 앞뒤 공백 제거
                    basic_add_voice = str(basic_add_voice).strip()

                    # 값이 "-" 또는 빈 문자열이면 "0분"으로 설정
                    if basic_add_voice == "-" or basic_add_voice == "":
                        buga_call = "0분"

                    # 숫자일 경우 "300분" 형태로 변환
                    elif basic_add_voice.isdigit():
                        buga_call = f"{basic_add_voice}분"

                    # 예외적인 값이 들어오면 기본값 "0분" 처리
                    else:
                        buga_call = "0분"


                    normal_price = basic_amount

                    # 12개월 & 24개월 총 요금(m12_price, m24_price) 계산
                    if not promotion_period or promotion_period == "평생":  # 빈 값, None, "평생" 동일 처리
                        m12_price = int(sale_price * 12)
                        m24_price = int(sale_price * 24)  # 평생일 때도 24개월 가격 계산
                    elif "개월" in promotion_period:
                        months = int(re.search(r"(\d+)", promotion_period).group(1))  # 개월 수 추출
                        
                        if months >= 24:  # 24개월 이상일 경우 24개월 할인 적용
                            m12_price = int(sale_price * 12)
                            m24_price = int(sale_price * 24)
                        elif months >= 12:  # 12개월 초과 24개월 이하일 경우
                            m12_price = int(sale_price * 12)
                            m24_price = int((sale_price * months) + (after_price * (24 - months)))
                        else:  # 12개월 이하일 경우
                            m12_price = int((sale_price * months) + (after_price * (12 - months)))
                            m24_price = int((sale_price * months) + (after_price * (24 - months)))
                    else:
                        m12_price = int(normal_price * 12)  # 기본값
                        m24_price = int(normal_price * 24)  # 기본값

                    # daily_data 값 설정 (basicDayData와 basicDayDataUnit 결합)
                    basic_day_data = item.get("basicDayData", "")
                    basic_day_data_unit = item.get("basicDayDataUnit", "")

                    # daily_data 값 설정 (basicDayData와 basicDayDataUnit 결합)
                    basic_day_data = item.get("basicDayData", "")
                    basic_day_data_unit = item.get("basicDayDataUnit", "")

                    # basicDayData가 -1이면 "0GB", 그렇지 않으면 "{값}{단위}" 형식으로 저장
                    if basic_day_data == -1:
                        daily_data = "0GB"
                    else:
                        daily_data = f"{basic_day_data}{basic_day_data_unit}".strip() if basic_day_data and basic_day_data_unit else "0GB"




                    # DTO 생성
                    dto = PlanData(
                        uuid=uuid,
                        mno=mno,
                        telecom=SiteTargetListType.KG_MOBILE_LIST.value,
                        company_id=SiteTargetListIDType.KG_MOBILE_LIST.value,
                        url=url,
                        plan_type=plan_type,
                        plan_name=item.get("planName", ""),
                        data=data,
                        voice_call=voice_call,
                        message=message,
                        normal_price=basic_amount,
                        sale_price=sale_price,
                        after_price=after_price,
                        benefit='',
                        qos=item.get("basicQos", ""),
                        business_name='주식회사 케이지모빌리언스',
                        combination=combination,
                        freebies='',
                        etc='',
                        promotion_period=promotion_period,
                        buga_call=buga_call,
                        plan_code='',
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price,
                    )

                    # ✅ DTO 추가 전에 planCode 로그 출력
                  #  print(f"Processing DTO: {uuid}")

                    result.append(dto)
                    success_count += 1

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: planCode={item.get('planCode', '')}, planName={item.get('planName', '')}: {e}")
                    continue

        else:
            print(f"{TAG} JSON 데이터가 비어 있음!")

        # ✅ DTO 개수 검증 로그 추가
        # print(f"✅ 최종 추가된 DTO 개수: {len(result)} (예상: {len(li_list)})")

        is_end = True
        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return AllData(planData=result, detailInfo=[]), is_end
