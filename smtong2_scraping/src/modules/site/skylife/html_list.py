import json
import requests
import traceback
import re
import time
from html import unescape
from modules.dto.input_queue_dto import PlanData  # ✅ DTO 불러오기
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[Skylife]'

class SkylifeListAction:

    def root(self, page: int, *args, **kwargs) -> tuple[dict, bool]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False  # ✅ 처음에는 False로 설정
        result = []  # ✅ 리스트로 변경하면 append() 사용 가능
        json_save_path = "./response_files/fixed_list_skylife.json"  # ✅ JSON 저장 경로

        # ✅ 숫자 변환 공통 함수
        def to_int(value, default=0):
            """주어진 값을 int 형식으로 변환, 숫자가 아니면 기본값 반환"""
            try:
                return int(value) if isinstance(value, (int, float)) else default
            except ValueError:
                return default

        # ✅ API 요청 URL
        url = "https://www.skylife.co.kr/api/api/mobile/plan"

        # ✅ 요청 헤더 설정
        headers = {
            "Accept": "application/json, text/plain, */*",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36 Edg/133.0.0.0",
            "X-Requested-With": "XMLHttpRequest"
        }

        try:
            # ✅ API 요청 및 JSON 응답 받기
            print(f"{TAG} API 요청 시작: {url}")
            req_start = time.time()
            response = requests.get(url, headers=headers)
            req_elapsed = time.time() - req_start
            print(f"{TAG} API 응답 완료 (상태코드: {response.status_code}, {req_elapsed:.1f}초)")

            if response.status_code == 200:
                raw_json_text = response.text.strip()  # ✅ 원본 JSON 텍스트 가져오기

                # ✅ 1. JSON 검증 (API 응답이 JSON인지 확인)
                try:
                    json_data = response.json()  # JSON 변환 시도
                except json.JSONDecodeError:
                    print("❌ API 응답이 JSON이 아님! 복구 시도...")
                    json_data = None

                # ✅ 2. JSON 복구 로직 실행
                if json_data is None:
                    decoded_json_text = unescape(raw_json_text)  # HTML 엔터티 디코딩

                    # ✅ JSON 구조 자동 복구 함수
                    def fix_json_structure(json_text):
                        try:
                            return json.loads(json_text)
                        except json.JSONDecodeError:
                            pass

                        json_text = json_text.strip()

                        # ✅ JSON 배열 감싸기
                        if not json_text.startswith("[") and not json_text.startswith("{"):
                            json_text = "[" + json_text
                        if not json_text.endswith("]") and not json_text.endswith("}"):
                            json_text = json_text + "]"

                        # ✅ 쉼표 오류 수정
                        json_text = json_text.replace(",]", "]").replace(",}", "}")

                        # ✅ 다시 JSON 변환 시도
                        try:
                            return json.loads(json_text)
                        except json.JSONDecodeError:
                            return None  # 복구 실패 시 None 반환

                    json_data = fix_json_structure(decoded_json_text)

                if json_data:
                    # print(f"✅ JSON 데이터 변환 완료, {len(json_data)}개의 항목을 처리합니다.")

                    # ✅ 변환된 JSON을 파일로 저장
                    with open(json_save_path, "w", encoding="utf-8") as json_file:
                        json.dump(json_data, json_file, indent=4, ensure_ascii=False)

                    # print(f"✅ 변환된 JSON 데이터가 '{json_save_path}'에 저장되었습니다.")
                else:
                    print("❌ JSON 변환 실패: 자동 복구 불가! 응답을 확인하세요.")
                    print("🚨 응답 내용 일부 미리 보기:", raw_json_text[:500])  # 처음 500자만 출력
            else:
                print(f"❌ API 요청 실패! 상태 코드: {response.status_code}")

        except Exception as e:
            print(f"오류 발생: {e}")
            traceback.print_exc()

        # ✅ JSON 데이터 (`json_data`)에서 직접 루프 실행
        try:
            if json_data:
                total_count = len(json_data)
                print(f"{TAG} 파싱할 항목 수: {total_count}건")
                # ✅ 루프를 돌면서 각 항목 처리
                for idx, item in enumerate(json_data):
                  try:
                    plan_name_log = str(item.get("name", ""))
                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {plan_name_log}")

                    # ✅ dataProvision 값 가져오기
                    # ✅ dataProvision 값 가져오기
                    data_provision = item.get("dataProvision", None)

                    # ✅ data 값 설정 (MB → GB 변환)
                    # if isinstance(data_provision, int):
                    #     if data_provision >= 1024:
                    #         data = f"{data_provision // 1024}GB"  # 1024MB 이상이면 GB 단위
                    #     else:
                    #         data = f"{data_provision}MB"  # 1024MB 미만이면 MB 유지
                    # else:
                    #     data = "0MB"  # 값이 없거나 숫자가 아니면 "0MB"로 설정
                    if isinstance(data_provision, (int, float)):
                        if data_provision >= 1024:
                            gb_value = data_provision / 1024
                            # 정수에 아주 가까우면 정수 처리
                            if abs(gb_value - round(gb_value)) < 0.25:  
                                data = f"{round(gb_value)}GB"
                            else:
                                data = f"{round(gb_value, 1)}GB"
                        else:
                            data = f"{round(data_provision)}MB"
                    else:
                        data = "0MB"
                   
                   # ✅ voiceProvision 값 가져오기
                    voice_provision = item.get("voiceProvision", None)

                    # ✅ voice_call 값 설정
                    if isinstance(voice_provision, int):  # 값이 숫자일 경우
                        if voice_provision == 10000:
                            voice_call = "집/이동전화 기본제공"
                        else:
                            voice_call = f"{voice_provision}분"  # 숫자 + '분' 추가
                    else:
                        voice_call = "0분"  # 값이 없거나 숫자가 아니면 "0분" 저장

                    # ✅ smsProvision 값 가져오기
                    sms_provision = item.get("smsProvision", None)

                    # ✅ message 값 설정
                    if isinstance(sms_provision, int):  # 값이 숫자일 경우
                        if sms_provision == 10000:
                            message = "기본제공"
                        else:
                            message = f"{sms_provision}건"  # 숫자 + '건' 추가
                    else:
                        message = "0건"  # 값이 없거나 숫자가 아니면 "0건" 저장

                    # ✅ 기본 요금 관련 값 가져오기
                    normal_price = to_int(item.get("basicFee", 0))
                    sale_price = to_int(item.get("charge", 0))
                    after_price = to_int(item.get("charge", 0))

                    data_precaution = str(item.get("dataPrecaution", "")).strip()  # ✅ NoneType 문제 해결

                    # ✅ 데일리 데이터 추출 (매일 XGB)
                    # daily_data_match = re.search(r"매일\s*(\d+)\s*GB", data_precaution)
                    # daily_data = f"{daily_data_match.group(1)}GB" if daily_data_match else ""
                    daily_data_match = re.search(r"매일\s*(\d+(?:\.\d+)?)\s*GB", data_precaution)
                    if daily_data_match:
                        value = daily_data_match.group(1)
                        # 정수면 그냥, 아니면 소수점 유지
                        daily_data = f"{int(float(value))}GB" if float(value).is_integer() else f"{value}GB"
                    else:
                        daily_data = "0GB"

                    # ✅ QOS 값 추출 (X Mbps 또는 X Kbps)
                    # qos_match = re.search(r"(\d+\.?\d*)\s*(Mbps|Kbps)", data_precaution)
                    # qos = f"{qos_match.group(1)}{qos_match.group(2)}" if qos_match else "0Mbps"
                    qos_match = re.findall(r"(\d+\.?\d*)\s*(Mbps|Kbps)", data_precaution)
                    if qos_match:
                        speed, unit = qos_match[-1]  # 마지막 항목 사용
                        qos = f"{speed}{unit}"
                    else:
                        qos = "0Mbps"

                    # ✅ 기본적으로 기존 데이터 유지
                    # original_data = str(item.get("dataProvision", "0GB"))
                    # data = original_data  

                    # ✅ "매일 XGB"가 문장의 맨 앞에 있으면 data 값을 0GB로 변경
                    if re.match(r"^\(?매일\s?\d+\s?GB", data_precaution):
                        data = "0GB"


                    # ✅ voicePrecaution 값 가져오기
                    voice_precaution = item.get("voicePrecaution", None)

                    # ✅ buga_call 값 설정 (분 단위 추출)
                    if isinstance(voice_precaution, str):  # 값이 문자열일 경우
                        match = re.search(r"(\d+)\s*분", voice_precaution)  # ✅ "숫자 + 분" 패턴 찾기
                        buga_call = f"{match.group(1)}분" if match else "0분"  # ✅ 값이 있으면 추출, 없으면 "0분"
                    else:
                        buga_call = "0분"  # 값이 없거나 null이면 "0분"

                    # ✅ categories 값 가져오기
                    categories = item.get("categories", "")

                    # ✅ categories가 리스트라면 문자열로 변환 (", "로 결합)
                    if isinstance(categories, list):
                        etc = ", ".join(categories)  # 리스트를 문자열로 변환
                    else:
                        etc = str(categories) if categories else ""  # 값이 없으면 빈 문자열 저장

                    promotion_period = "평생"

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
                        uuid = "SKYLIFE_" + str(item.get("planId", "")),  # ✅ 문자열 변환
                        mno = "KT",
                        telecom = SiteTargetListType.SKYLIFE_LIST.value,
                        url = f"https://www.skylife.co.kr/product/mobile/goods/{str(item.get('code', '')).lower()}",  # ✅ code 값을 소문자로 변환
                        company_id = SiteTargetListIDType.SKYLIFE_LIST.value,
                        plan_type = str(item.get("sort", "")),  # ✅ 문자열 변환
                        plan_name = str(item.get("name", "")),  # ✅ 문자열 변환
                        data = data,
                        voice_call = voice_call,
                        message = message,
                        normal_price = normal_price,
                        sale_price = sale_price,
                        after_price = after_price,
                        benefit = "",
                        qos = qos,
                        business_name = "케이티스카이라이프",
                        combination = False,
                        freebies = "",
                        etc = etc,
                        promotion_period = promotion_period,
                        buga_call = buga_call,
                        plan_code = "",  # ✅ plan_code도 문자열로 변환
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price,
                    )
                    result.append(dto)
                    success_count += 1

                  except Exception as e:
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    error_count += 1

                # print("✅ JSON 데이터 루프 실행 완료!")

        except Exception as e:
            print(f"{TAG} [ERROR] JSON 처리 중 오류 발생: {e}")
            error_count += 1

        # ✅ 모든 처리가 끝난 후, 마지막에 `is_end = True` 설정
        is_end = True

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")

        return result, is_end  # ✅ is_end는 마지막에만 True로 변경됨
