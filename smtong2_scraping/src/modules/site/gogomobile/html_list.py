import json
import os
import time
import requests
import traceback
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData, Telecom

TAG = '[GogoMobile]'

class GogoMobileListAction():
    def __init__(self):
        self.api_url = "https://api.gogofactory.co.kr/info/semo/online_plan.php"
        self.headers = {
            "Authorization": "057485accc44c076e27b7fa2328232e4"
        }
        self.output_dir = "./response_files"
        os.makedirs(self.output_dir, exist_ok=True)  # 폴더가 없으면 생성

    def save_json_data(self, data_list, file_name="gogomobile_data.json"):
        """API 응답 데이터를 JSON 파일로 저장"""
        file_path = os.path.join(self.output_dir, file_name)
        with open(file_path, "w", encoding="utf-8") as json_file:
            json.dump(data_list, json_file, ensure_ascii=False, indent=4)
        # print(f"✅ JSON 데이터가 {file_path} 파일로 저장되었습니다.")

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        """API를 호출하여 요금제 데이터를 가져옴"""
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        result = []
        is_end = False

        try:
            print(f"{TAG} HTTP GET 요청 시작: {self.api_url}")
            req_start = time.time()
            response = requests.get(self.api_url, headers=self.headers)
            req_elapsed = time.time() - req_start
            print(f"{TAG} HTTP 응답 완료 ({req_elapsed:.1f}초), 상태코드: {response.status_code}")
            if response.status_code != 200:
                print(f"{TAG} [ERROR] API 요청 실패: {response.status_code}")
                return [], True

            # API 응답 확인 및 JSON 변환
            try:
                data = response.json()
                data_list = data.get("result", [])  # 'result' 키의 데이터를 가져옴
                if not isinstance(data_list, list):
                    print(f"{TAG} [ERROR] API 응답이 예상과 다릅니다: {data}")
                    return [], True
            except json.JSONDecodeError:
                print(f"{TAG} [ERROR] API 응답을 JSON으로 변환할 수 없습니다")
                return [], True

            self.save_json_data(data_list)  # JSON 데이터 저장
            total_count = len(data_list)
            print(f"{TAG} 요금제 {total_count}건 발견")

            for idx, item in enumerate(data_list):
                try:
                    if not isinstance(item, dict):  # item이 dict인지 확인
                        print(f"{TAG} [{idx+1}/{total_count}] [WARNING] 잘못된 데이터 형식, 스킵")
                        skip_count += 1
                        continue

                    hidden_yn = 1 if item.get("hiddenYn", "").lower() == "normal" else 0
                    if hidden_yn == 0:
                        skip_count += 1
                        continue  # hidden_yn이 0인 경우 다음 요금제로 넘어감


                    uuid = f"GOGO_{item.get('planId', '')}"
                    mno = item.get("mno", "")
                    plan_name = item.get("planName", "")
                    network = item.get("network", "")

                    # 데이터 처리
                    basic_data = f"{item.get('basicData', '0')}{item.get('basicDataUnit', 'GB')}"
                    daily_data = f"{item.get('dailyData', '0')}{item.get('dailyDataUnit', 'GB')}" if item.get('dailyData', '0') != '0' else ""

                    # QoS 속도 처리
                    qos = f"{item.get('qos', '0')}{item.get('qosUnit', '')}" if item.get('qos', '0') != '0' else ""

                   # 할인 및 기본 요금 처리
                    discount_fee = int(item.get("discountFee", 0))
                    original_fee = int(item.get("originalFee", 0))
                    discount_period = item.get("discountPeriod", "0")
                    sale_price = discount_fee if discount_fee else original_fee

                    # 할인 기간이 있을 경우 after_price 설정
                    after_price = original_fee if original_fee else sale_price
                    normal_price = original_fee if original_fee else sale_price
                    
                    # 부가통화 및 문자 처리
                    voice_call = "무제한" if item.get("voice", "0") == "9999" else f"{item.get('voice', '0')}분"
                    buga_call = f"{item.get('additionalCall', '0')}분" if item.get("additionalCall", "0") != "0" else ""
                    message = "무제한" if item.get("message", "0") == "9999" else f"{item.get('message', '0')}건"

                    # 12개월 & 24개월 총 요금 계산
                    if discount_period == "0":
                        m12_price = sale_price * 12
                        m24_price = sale_price * 24
                    else:
                        discount_period = int(discount_period)
                        if discount_period >= 24:
                            m12_price = sale_price * 12
                            m24_price = sale_price * 24
                        elif discount_period >= 12:
                            m12_price = sale_price * 12
                            m24_price = (sale_price * discount_period) + (after_price * (24 - discount_period))
                        else:
                            m12_price = (sale_price * discount_period) + (after_price * (12 - discount_period))
                            m24_price = (sale_price * discount_period) + (after_price * (24 - discount_period))

                    # 요금제 링크 (PC URL이 우선, 없으면 모바일 URL 사용)
                    url = item.get("pcPlanUrl", item.get("mobilePlanUrl", ""))



                    # 혜택 정보
                    benefits = item.get("benefit", [])
                    benefit = "|".join(benefits) if benefits else ""

                   


                    # PlanData 추가
                    dto = PlanData(
                        uuid=uuid,
                        mno=mno,
                        telecom=SiteTargetListType.GOGOMOBILE_LIST.value,
                        company_id=SiteTargetListIDType.GOGOMOBILE_LIST.value,
                        url=url,
                        plan_type=network,
                        plan_name=plan_name,
                        data=basic_data,
                        voice_call=voice_call,
                        message=message,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit=benefit,
                        qos=qos,
                        business_name='고고모바일',
                        after_price=after_price,
                        combination="",
                        freebies="",
                        etc="",
                        promotion_period=f"{discount_period}개월" if discount_period != "0" else "",
                        buga_call=buga_call,
                        plan_code=item.get("planId", ""),
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price,
                        hidden_yn=hidden_yn,
                    )
                    result.append(dto)
                    success_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 완료: {mno} | {plan_name}")

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

        except Exception as e:
            error_count += 1
            print(f"{TAG} [ERROR] API 요청 중 오류 발생: {e}")
            traceback.print_exc()

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        is_end = True
        return result, is_end
