import os
import json
import requests
import traceback
import re
import time
from modules.dto.input_queue_dto import PlanData
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[SptizMobile]'

class SptizMobileListAction:
    API_URL = "https://www.sptizmobile.com/common/component/plan/AjaxPhone_plan.aspx"
    SAVE_DIR = "./response_files"

    def fetch_api_data(self, network_type: str) -> list:
        """API 요청을 보내고 응답에서 DATA 부분만 반환하는 함수"""
        headers = {
            "Accept": "application/json, text/javascript, */*; q=0.01",
            "Accept-Encoding": "gzip, deflate, br, zstd",
            "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
            "Connection": "keep-alive",
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "Origin": "https://www.sptizmobile.com",
            "Referer": "https://www.sptizmobile.com/view/plan/phone_plan.aspx",
            "Sec-Fetch-Dest": "empty",
            "Sec-Fetch-Mode": "cors",
            "Sec-Fetch-Site": "same-origin",
            "User-Agent": "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36",
            "X-Requested-With": "XMLHttpRequest",
        }

        payload = json.dumps({
            "header": [{"type": "07"}],
            "body": [{
                "order_type": "DISCOUNT",
                "order_align": "ASC",
                "lte5gValue": network_type,  # ✅ LTE 또는 5G를 동적으로 설정
                "dataValue": "0_100100",
                "callsValue": "0_310",
                "priceValue": "0_70000",
                "speedValue": "",
                "periodValue": ""
            }]
        })

        try:
            print(f"{TAG} API 요청 시작: {network_type}")
            req_start = time.time()
            response = requests.post(self.API_URL, headers=headers, data=payload)
            req_elapsed = time.time() - req_start
            print(f"{TAG} API 응답 완료: {network_type} (상태코드: {response.status_code}, {req_elapsed:.1f}초)")

            if response.status_code == 200:
                response_json = response.json()  # JSON 변환
                data_list = response_json.get("DATA", [])
                print(f"{TAG} {network_type} 데이터 수: {len(data_list)}건")
                return data_list

            else:
                print(f"{TAG} [ERROR] {network_type} API 요청 실패: {response.status_code}")
                return []

        except Exception as e:
            print(f"{TAG} [ERROR] API 요청 중 오류 발생 ({network_type}): {e}")
            traceback.print_exc()
            return []

    def save_response_to_file(self, data: list, filename: str):
        """DATA 부분을 파일로 저장하는 함수 (LTE/5G 구분)"""
        file_path = os.path.join(self.SAVE_DIR, filename)

        try:
            with open(file_path, "w", encoding="utf-8") as file:
                json.dump(data, file, ensure_ascii=False, indent=4)  # JSON 저장
        except Exception as e:
            print("파일 저장 중 오류 발생:", e)
            traceback.print_exc()

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        result = []
        is_end = False

        try:
            for network_type in ["LTE", "5G"]:  # ✅ LTE와 5G 각각 처리
                data = self.fetch_api_data(network_type)

                if not data:
                    print(f"{TAG} {network_type} 데이터 없음, 스킵")
                    continue

                filename = f"sptiz_{network_type.lower()}_data.json"  # ✅ LTE/5G 구분하여 저장
                self.save_response_to_file(data, filename)

                total_count = len(data)
                for idx, plan in enumerate(data):
                  try:
                    plan_name = plan.get("GDNM", "")  # 요금제 이름
                    uuid = plan.get("GDCD", "")  # 요금제 코드 (UUID)
                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {network_type} | {plan_name}")
                    mno = "KT"  # ✅ 모든 요금제의 통신사 값 고정
                    url = f"https://sptizmobile.com/view/plan/phone_plan.aspx?AGCD=a4e7f512a7830bf97bfd53d02b683e20&GDCD={uuid}"
                    plan_type = network_type  # ✅ 네트워크 유형(LTE 또는 5G)

                    # ✅ SEEDATA 분석 (데이터량, 일별 데이터, QoS 속도 제한)
                    seedata_original = plan.get("SEEDATA", "").strip().replace(" ", "")

                    # # ✅ "+ 기준으로 분할하여 개별 처리"
                    # converted_parts = []
                    # data_value, daily_data, qos = "", "", ""

                    # for part in seedata_original.split("+"):
                    #     part = part.strip()

                    #     # ✅ G → GB 변환 (ex: 7G → 7GB)
                    #     if re.match(r"^\d+G$", part):
                    #         part = part.replace("G", "GB")
                    #         data_value = part  # ✅ 기본 데이터값 저장

                    #     # ✅ M → Mbps 변환 (ex: 1M → 1Mbps)
                    #     elif re.match(r"^\d+M$", part):
                    #         part = part.replace("M", "Mbps")
                    #         qos = part  # ✅ QoS 속도 저장

                    #     # ✅ 일XG → 일XGB 변환 (ex: 일2G → 일2GB)
                    #     elif re.match(r"^일\d+G$", part):
                    #         part = part.replace("G", "GB")
                    #         daily_data = part.replace("일", "")  # ✅ 일별 데이터 저장

                    #     converted_parts.append(part)

                    # # ✅ 변환된 값 다시 합치기
                    # seedata_transformed = "+".join(converted_parts)

                    # # ✅ "0MB+2GB(결합시)" 같은 경우 처리 (데이터와 데일리 데이터 모두 0GB)
                    # if "결합시" in seedata_transformed:
                    #     data_value, daily_data, qos = "0GB", "0GB", ""

                    # # ✅ 기본 데이터가 없는 경우, "XGB"만 포함되면 자동으로 설정
                    # if not data_value:
                    #     data_match = re.search(r"(\d+GB)", seedata_transformed)
                    #     if data_match:
                    #         data_value = data_match.group(1)

                    # # ✅ "일XGB+XMbps" 같은 경우 (기본 데이터 없음)
                    # if re.match(r"^일\d+GB\+\d+Mbps$", seedata_transformed):
                    #     data_value = "0GB"
                    #     daily_data_match = re.match(r"일(\d+)GB\+(\d+)Mbps", seedata_transformed)
                    #     if daily_data_match:
                    #         daily_data = f"{daily_data_match.group(1)}GB"
                    #         qos = f"{daily_data_match.group(2)}Mbps"

                    # # ✅ "XGB+XMbps" 패턴 처리 (QoS 가 포함된 경우, Data 유지)
                    # if re.match(r"^\d+GB\+\d+Mbps$", seedata_transformed):
                    #     match = re.match(r"(\d+)GB\+(\d+)Mbps", seedata_transformed)
                    #     if match:
                    #         data_value = f"{match.group(1)}GB"
                    #         qos = f"{match.group(2)}Mbps"

                    # # ✅ "XGB+일XGB+XMbps" 같은 복합 패턴 처리
                    # complex_match = re.match(r"(\d+)GB\+일(\d+)GB\+(\d+)Mbps", seedata_transformed)
                    # if complex_match:
                    #     data_value = f"{complex_match.group(1)}GB"
                    #     daily_data = f"{complex_match.group(2)}GB"
                    #     qos = f"{complex_match.group(3)}Mbps"

                    # ✅ 디버깅 출력 추가 (변환 전/후 비교)
                    # print(f"🔍 SEEDATA 변환 디버깅 | 원본: {seedata_original} → 변환 후: {seedata_transformed}")
                    # print(f"📌 최종 데이터 추출 결과 → Data: {data_value}, Daily Data: {daily_data}, QoS: {qos}")
                    converted_parts = []
                    data_value, daily_data, qos = "", "", ""

                    # QoS 를 split 하기 전에 원본에서 먼저 추출
                    # QoS: Mbps 또는 kbps
                    qos_match = re.search(r"(\d+(?:\.\d+)?)(k|K|M|m)bps", seedata_original)
                    if qos_match:
                        speed = qos_match.group(1)
                        unit = qos_match.group(2).upper()

                        # k → kbps, M → Mbps
                        if unit == "K":
                            qos = f"{speed}kbps"
                        else:
                            qos = f"{speed}Mbps"

                    # 기존 split 로직 그대로 유지
                    for part in seedata_original.split("+"):
                        part = part.strip()

                        # G → GB (예: 7G → 7GB)
                        if re.match(r"^\d+(\.\d+)?(G|GB)$", part, re.IGNORECASE):
                            # G로 끝나는 경우만 GB로 변환
                            if part.lower().endswith("g") and not part.lower().endswith("gb"):
                                part = part[:-1] + "GB"

                            data_value = part

                        # M → Mbps (예: 1M → 1Mbps)
                        elif re.match(r"^\d+M$", part):
                            part = part.replace("M", "Mbps")
                            # qos = part   #  기존 코드 유지하지만 덮어쓰지는 않음

                        # 일XG → 일XGB
                        elif re.match(r"^일\d+G$", part):
                            part = part.replace("G", "GB")
                            daily_data = part.replace("일", "")

                        converted_parts.append(part)

                    # 변환된 값 합치기
                    seedata_transformed = "+".join(converted_parts)

                    # "결합시" 처리
                    if "결합시" in seedata_transformed:
                        data_value, daily_data, qos = "0GB", "0GB", ""

                    #  기본 데이터 없는 경우
                    if not data_value:
                        data_match = re.search(r"(\d+GB)", seedata_transformed)
                        if data_match:
                            data_value = data_match.group(1)

                    # "일XGB+XMbps" 패턴 처리
                    if re.match(r"^일\d+GB\+\d+Mbps$", seedata_transformed):
                        data_value = "0GB"
                        daily_data_match = re.match(r"일(\d+)GB\+(\d+)Mbps", seedata_transformed)
                        if daily_data_match:
                            daily_data = f"{daily_data_match.group(1)}GB"
                            if not qos:
                                qos = f"{daily_data_match.group(2)}Mbps"

                    # "XGB+XMbps" 패턴 처리
                    if re.match(r"^\d+GB\+\d+Mbps$", seedata_transformed):
                        match = re.match(r"(\d+)GB\+(\d+)Mbps", seedata_transformed)
                        if match:
                            data_value = f"{match.group(1)}GB"
                            if not qos:
                                qos = f"{match.group(2)}Mbps"

                    # "XGB+일XGB+XMbps" 복합 패턴 처리
                    complex_match = re.match(r"(\d+)GB\+일(\d+)GB\+(\d+)Mbps", seedata_transformed)
                    if complex_match:
                        data_value = f"{complex_match.group(1)}GB"
                        daily_data = f"{complex_match.group(2)}GB"
                        if not qos:
                            qos = f"{complex_match.group(3)}Mbps"


                    # ✅ SEEVOICE 분석 (voice_call, buga_call 추출)
                    seevoice = plan.get("SEEVOICE", "").strip()
                    voice_call, buga_call = "0분", ""

                    if "무제한" in seevoice:
                        voice_call = "무제한"

                    elif "분" in seevoice:
                        match = re.search(r"(\d+)분", seevoice)
                        if match:
                            voice_call = f"{match.group(1)}분"

                    buga_match = re.search(r"부가통화\s?(\d+)분", seevoice)
                    if buga_match:
                        buga_call = f"{buga_match.group(1)}분"

                    # ✅ SEELETTER 분석 (message 값 추출)
                    seeletter = plan.get("SEELETTER", "").strip()
                    message = "0건"  # 기본값

                    if "무제한" in seeletter:
                        message = "무제한"

                    elif "건" in seeletter:
                        match = re.search(r"(\d+)건", seeletter)
                        if match:
                            message = f"{match.group(1)}건"

                    
                    # ✅ 가격 정보 계산 (콤마 제거 후 int 변환)
                    seeamt = plan.get("SEEAMT", "0").replace(",", "").strip()
                    # discount = plan.get("DISCOUNT", "0").replace(",", "").strip()
                    discount = plan.get("AMT_MONTHLY", "0").replace(",", "").strip()
                    # tt_amt = plan.get("TT_AMT", "0").replace(",", "").strip()
                    tt_amt = plan.get("AMT_DISCOUNT", "0").replace(",", "").strip()

                    # ✅ 빈 문자열("")이나 None을 0으로 변환
                    seeamt = int(seeamt) if seeamt else 0
                    discount = int(discount) if discount else 0
                    tt_amt = int(tt_amt) if tt_amt else 0

                    # ✅ 최종가(after_price) 먼저 설정
                    after_price = tt_amt

                    # ✅ 정상가(normal_price) 설정
                    normal_price = seeamt if seeamt > 0 else after_price

                    # ✅ 할인가(sale_price) 설정
                    sale_price = discount if discount > 0 else after_price


                    # ✅ 이모지를 제거하는 정규 표현식
                    emoji_pattern = re.compile(
                        "["
                        "\U0001F600-\U0001F64F"  # 이모지 (스마일, 손 모양 등)
                        "\U0001F300-\U0001F5FF"  # 기호, 도형 등
                        "\U0001F680-\U0001F6FF"  # 교통, 기계류
                        "\U0001F700-\U0001F77F"  # 과학 기호
                        "\U0001F780-\U0001F7FF"  # 추가된 기호
                        "\U0001F800-\U0001F8FF"  # 기타 추가 심볼
                        "\U0001F900-\U0001F9FF"  # 손짓, 동물, 식물
                        "\U0001FA00-\U0001FA6F"  # 기호, 도구
                        "\U0001FA70-\U0001FAFF"  # 기타
                        "]+", flags=re.UNICODE
                    )

                    # ✅ benefit 값 추출 및 이모지 제거
                    benefit_parts = [
                        plan.get("H_GDDESC1", "").strip(),
                        plan.get("H_GDDESC2", "").strip(),
                        plan.get("H_GDDESC3", "").strip()
                    ]

                    # ✅ 빈 값 제거 + 이모지 제거 + '|'로 결합
                    benefit = " | ".join(filter(None, [emoji_pattern.sub("", part) for part in benefit_parts]))
                    
                    plan_code = plan.get("GDCD", "").strip()  # ✅ GDCD 값을 가져와 적용

                    # ✅ PERIODNM 분석 및 변환 (1개월 차감)
                    # periodnm = plan.get("PERIODNM", "").strip()
                    periodnm = plan.get("PERIOD", "").strip()
                    promotion_period = ""  # 기본값

                    # "n개월 차 부터"에서 n개월 추출
                    match = re.search(r"(\d+)개월", periodnm)
                    if match:
                        n_months = int(match.group(1))
                        promotion_period = f"{n_months - 1}개월" if n_months > 1 else "평생"

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


                    # ✅ DTO 객체 생성
                    dto = PlanData(
                        uuid=f"SPTIZ_{uuid}",
                        mno=mno,
                        telecom=SiteTargetListType.SPTIZ_MOBILE_LIST.value,
                        company_id=SiteTargetListIDType.SPTIZ_MOBILE_LIST.value,
                        url=url,
                        plan_type=plan_type,
                        plan_name=plan_name,
                        data=data_value,  # ✅ 변환된 데이터량 적용
                        voice_call=voice_call,
                        message=message,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        after_price=after_price,
                        benefit=benefit,
                        qos=qos,  # ✅ QoS 속도 적용
                        business_name="스피츠모바일 주식회사",
                        promotion_period=promotion_period,
                        buga_call=buga_call,
                        plan_code=plan_code,
                        daily_data=daily_data,  # ✅ 일별 데이터 적용
                        m12_price=m12_price,
                        m24_price=m24_price,
                        combination=False,
                        freebies="",
                        etc=""
                    )

                    result.append(dto)
                    success_count += 1

                  except Exception as e:
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()
                    error_count += 1

            is_end = True

        except Exception as e:
            print(f"{TAG} [ERROR] 오류 발생: {e}")
            traceback.print_exc()
            error_count += 1

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")

        return result, is_end