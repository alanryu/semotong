import json
import time
import traceback
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
import re

TAG = '[EGMobile]'

class EGMobileListAction:
    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result = []

        # ✅ 두 개의 키값을 리스트로 저장하고, 해당 키값에 따른 hidden_yn 값 설정
        acckeys = [
            ("2E1C40F809994CF71DBF1775B5B982A8299FC157", 1),  # hidden_yn = 1
            ("779CED96038F883A9BA78E6961A66A1D05AC8833", 0)   # hidden_yn = 0
        ]

        for acckey, hidden_value in acckeys:
            try:
                url = "https://api.egmobile.co.kr/plan/list.php"
                headers = {"Content-Type": "application/json; charset=UTF-8"}
                data = {"acckey": acckey}

                # ✅ API 호출
                print(f"{TAG} HTTP POST 요청 시작 (hidden_yn={hidden_value}): {url}")
                req_start = time.time()
                response = requests.post(url, headers=headers, json=data)
                req_elapsed = time.time() - req_start
                print(f"{TAG} HTTP 응답 완료 ({req_elapsed:.1f}초), 상태코드: {response.status_code}")

                # ✅ 응답 상태 확인
                if response.status_code != 200:
                    print(f"{TAG} [ERROR] API 요청 실패: {response.status_code}")
                    error_count += 1
                    continue  # 다음 키값으로 진행

                json_data = response.json()

                # ✅ JSON 데이터 저장
                save_path = f"./response_files/eg_mobile_data_{hidden_value}.json"
                with open(save_path, "w", encoding="utf-8") as json_file:
                    json.dump(json_data, json_file, indent=4, ensure_ascii=False)

                # print(f"JSON 데이터 저장 완료: {save_path}")

                # ✅ JSON 데이터에서 "result" 키를 통해 데이터 추출
                items = json_data.get("result", [])
                print(f"{TAG} (hidden_yn={hidden_value}) 요금제 {len(items)}건 발견")
                for idx, item in enumerate(items):
                    try:
                        if hidden_value == 0:  # hidden_yn = 0 인 경우
                            skip_count += 1
                            continue  # 다음 요금제로 넘어감

                        # ✅ uuid 생성 (planId 앞에 "EGMOBILE_" 추가)
                        uuid = f"EGMOBILE_{item.get('planId', '')}"

                        # ✅ 요금제 기본 정보
                        plan_type = item.get("network", "")  # LTE, 5G
                        mno = item.get("mno", "")  # SKT, KT, LGU
                        plan_name = item.get("planName", "")

                        # ✅ 데이터 정보
                        data = f"{item.get('basicData', '')}{item.get('basicDataUnit', '')}"
                        daily_data = f"{item.get('dailyData', '')}{item.get('dailyDataUnit', '')}"
                        qos = f"{item.get('qos', '')}{item.get('qosUnit', '')}"

                        # ✅ 통화/문자 정보
                        voice_call = "무제한" if str(item.get("voice", "")) == "9999" else f"{item.get('voice', '')}분"
                        message = "무제한" if str(item.get("message", "")) == "9999" else f"{item.get('message', '')}건"
                        buga_call = "무제한" if str(item.get("additionalCall", "")) == "9999" else f"{item.get('additionalCall', '')}분"

                        # ✅ 요금 정보
                        sale_price = int(item.get("discountFee", 0))  # 할인 가격
                        normal_price = int(item.get("originalFee", 0))  # 기본 가격
                        after_price = normal_price  # 기본 가격을 이후 가격으로 설정
                        discount_period = int(item.get("discountPeriod", 0))

                        # ✅ sale_price 및 after_price 변환
                        sale_price = int(sale_price) if isinstance(sale_price, str) and sale_price.isdigit() else sale_price
                        after_price = int(after_price) if isinstance(after_price, str) and after_price.isdigit() else after_price

                        


                        # ✅ 요금제 링크 (PC 우선, 없으면 모바일)
                        plan_code = item.get("planId", "")
                        url = item.get("pcPlanUrl", "") or item.get("mobilePlanUrl", "")

                        # ✅ 추가 정보
                        benefit = item.get("benefit", "")
                        freebies = item.get("freebie", "")
                        promotion_period = f"{discount_period}개월" if discount_period > 0 else ""

                        # ✅ 숫자 값 변환
                        contract_option = int(item.get("contractOption", 0) or 0)
                        special_category = int(item.get("specialCategory", 0)) if str(item.get("specialCategory", "0")).isdigit() else 0
                        datasharing_yn = int(item.get("dataSharingSupported", 0) or 0)
                        micropayment_yn = int(item.get("microPaymentSupported", 0) or 0)
                        agreement_period = int(item.get("agreementPeriod", 0) or 0)

                        # ✅ 테더링 데이터 변환 (GB → MB 변환)
                        tethering_data = item.get("tetheringData", "")
                        tethering_unit = item.get("tetheringDataUnit", "")
                        if tethering_unit == "GB":
                            data_tethering = int(float(tethering_data) * 1024) if tethering_data else 0
                        elif tethering_unit == "MB":
                            data_tethering = int(float(tethering_data)) if tethering_data else 0
                        else:
                            data_tethering = 0

                        # ✅ 일일 테더링 데이터 변환 (GB → MB 변환)
                        tethering_daily_data = item.get("tetheringDailyData", "")
                        tethering_daily_unit = item.get("tetheringDailyDataUnit", "")
                        if tethering_daily_unit == "GB":
                            data_daily_tethering = int(float(tethering_daily_data) * 1024) if tethering_daily_data else 0
                        elif tethering_daily_unit == "MB":
                            data_daily_tethering = int(float(tethering_daily_data)) if tethering_daily_data else 0
                        else:
                            data_daily_tethering = 0
                       
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

                        # ✅ DTO 생성 및 리스트 추가
                        dto = PlanData(
                            uuid=uuid,
                            mno=mno,
                            telecom=SiteTargetListType.EGMOBILE_LIST.value,
                            company_id=SiteTargetListIDType.EGMOBILE_LIST.value,
                            url=url,
                            plan_type=plan_type,
                            plan_name=plan_name,
                            data=data,
                            voice_call=voice_call,
                            message=message,
                            normal_price=normal_price,
                            sale_price=sale_price,
                            benefit=benefit,
                            qos=qos,
                            business_name="코드모바일",
                            after_price=after_price,
                            combination=False,
                            freebies=freebies,
                            etc='',
                            promotion_period=promotion_period,
                            buga_call=buga_call,
                            plan_code=plan_code,
                            daily_data=daily_data,
                            m12_price=m12_price,
                            m24_price=m24_price,
                            contract_option=contract_option,
                            hidden_yn=hidden_value,  # ✅ acckey에 따라 hidden_yn 값 변경
                            special_category=special_category,
                            datasharing_yn=datasharing_yn,
                            micropayment_yn=micropayment_yn,
                            data_tethering=data_tethering,
                            agreement_period=agreement_period,
                            data_daily_tethering=data_daily_tethering
                        )
                        result.append(dto)
                        success_count += 1
                        print(f"{TAG} [{idx+1}/{len(items)}] 파싱 완료: {mno} | {plan_name}")

                    except Exception as e:
                        error_count += 1
                        print(f"{TAG} [{idx+1}/{len(items)}] [ERROR] 요금제 파싱 실패: {e}")
                        traceback.print_exc()

            except Exception as e:
                error_count += 1
                print(f"{TAG} [ERROR] API 호출 중 예외 발생: {e}")
                traceback.print_exc()

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        is_end = True
        return result, is_end
