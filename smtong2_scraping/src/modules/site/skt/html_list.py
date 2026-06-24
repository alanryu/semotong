import json
import os
import time
import traceback
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
from utils.site.url_maker import UrlParm, make_url
from bs4 import BeautifulSoup
import re
import unicodedata

TAG = '[SKT]'

class SktListAction:
    def save_json(self, data, filename):
        """JSON 데이터를 저장하는 함수"""
        os.makedirs("response_files", exist_ok=True)  # 저장 폴더 생성
        filepath = os.path.join("response_files", filename)

        with open(filepath, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=4)

        # print(f"✅ JSON 데이터가 {filepath} 파일에 저장되었습니다.")

    def fetch_detail_info(self, uuid):
        """prodId를 이용하여 상세 페이지 정보를 가져옴"""
        url = f"https://www.tworld.co.kr/core-product/v1/ledger/{uuid}/summaries"
        headers = {
            "Host": "www.tworld.co.kr",
            "Referer": f"https://www.tworld.co.kr/web/product/callplan/{uuid}",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
            "Accept": "application/json"
        }
        
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            return response.json().get("result", {})
        return {}
    
    

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:

        def remove_emojis(text):
            """이모지만 제거하고 일반 텍스트는 유지하는 함수"""
            return ''.join(char for char in text if unicodedata.category(char) not in ["So", "Sk"])

        """ SKT 요금제 크롤링 및 상세 정보 저장 """
        start_time = time.time()
        print(f"{TAG} 스크래핑 시작")
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0
        detailed_data_list = []  # 상세 정보 저장용 리스트

        paramList = [
            {"type": "LTE", "code": "F01121", "opClCd": "02"},
            {"type": "5G", "code": "F01713", "opClCd": "02"},
            
        ]

        for param in paramList:
            base_url = "https://www.tworld.co.kr/core-product/v1/product/mobile/plan-device-list"
            idxCtgCd = param["code"]
            opClCd = param["opClCd"]
            plan_type = param["type"]  # ✅ paramList에서 plan_type 가져오기

            url = make_url(
                base_url=base_url,
                url_params=[
                    UrlParm(key="idxCtgCd", value=idxCtgCd),
                    UrlParm(key="opClCd", value=opClCd),
                    UrlParm(key="size", value="10000"),
                    UrlParm(key="page", value=f"{page}"),
                    UrlParm(key="order", value="recommend"),
                    UrlParm(key="searchFltIds", value="null"),
                ],
            )

            t0 = time.time()
            print(f"{TAG} API 요청: type={plan_type}, code={idxCtgCd}")
            response = requests.get(
                url,
                headers={
                    "Host": "www.tworld.co.kr",
                    "referer": "https://www.tworld.co.kr/web/product/plan/list",
                },
            )
            elapsed = time.time() - t0
            print(f"{TAG} API 응답 완료 ({elapsed:.1f}초)")
            data = response.json().get("result", {}).get("mobilePlanList", []) or \
                   response.json().get("result", {}).get("separateProductList", [])

            if not data:
                print(f"{TAG} type={plan_type}: 데이터 없음, 종료")
                break

            print(f"{TAG} type={plan_type}: 요금제 {len(data)}건 발견")
            for item_idx, item in enumerate(data):
                try:
                    uuid = item["prodId"]

                    # ✅ 상세 정보 가져오기
                    t1 = time.time()
                    detail_info = self.fetch_detail_info(uuid)
                    elapsed_detail = time.time() - t1
                    detail_info["plan_type"] = plan_type  # ✅ 새 JSON에 plan_type 추가
                    detailed_data_list.append(detail_info)  # 상세 정보 리스트에 추가
                    plan_name = item["prodNm"]
                    print(f"{TAG} [{item_idx+1}/{len(data)}] 파싱 중: {plan_name} (상세 {elapsed_detail:.1f}초)")


                    # ✅ 데이터 값 가져오기 (GB와 MB 둘 다 확인)
                    data_gb = item.get("basOfrGbDataQtyCtt", "").strip()  # GB 단위 데이터
                    data_mb = item.get("basOfrMbDataQtyCtt", "").strip()  # MB 단위 데이터

                    if data_gb == "무제한":
                        data = "9999GB"  # ✅ "무제한" → "9999GB"로 변환

                    elif data_gb.replace(".", "", 1).isdigit():  # ✅ GB 단위 데이터가 있는 경우
                        data = f"{data_gb}GB"

                    elif data_mb.replace(",", "").isdigit():  # ✅ MB 단위 데이터가 있는 경우
                        data = f"{data_mb}MB"  # ✅ 변환 없이 그대로 저장

                    else:
                        data = "0GB"  # ✅ 데이터 값이 없거나 인식 불가하면 기본값 "0GB"



                    # ✅ 통화 값 처리 (문자 그대로 저장)
                    voice_call_raw = item.get("basOfrVcallTmsCtt", "").strip()

                    if voice_call_raw in ["무제한", "기본 제공"]:
                        voice_call = voice_call_raw  # ✅ 그대로 저장
                    else:
                        voice_call_match = re.search(r'(\d+)', voice_call_raw.replace(",", "")) if voice_call_raw else None
                        voice_call = f"{voice_call_match.group(1)}분" if voice_call_match else "0분"


                    # ✅ 문자 값 처리 (문자 그대로 저장)
                    message_raw = item.get("basOfrCharCntCtt", "").strip()

                    # 기본 제공, 기본제공, 무제한 모두 동일하게 처리
                    message_raw = message_raw.replace("기본 제공", "기본제공").replace("무제한", "기본제공")

                    if message_raw == "기본제공":
                        message = "기본제공"  # 그대로 저장
                    else:
                        message_match = re.search(r'(\d+)', message_raw.replace(",", "")) if message_raw else None
                        message = f"{message_match.group(1)}건" if message_match else "0건"



                    
                    
                    def safe_int(value):
                        """숫자로 변환 가능한 경우 변환, 그렇지 않으면 0 반환"""
                        try:
                            return int(value.replace(",", "").strip()) if value.replace(",", "").strip().isdigit() else 0
                        except Exception as e:
                            print(f"[WARNING] Failed to convert value to int: {value}, Error: {e}")
                            return 0  # 변환 실패 시 기본값 0 반환
                        
                    # ✅ 숫자 추출 함수 개선
                    def extract_number(value):
                        if not value or not isinstance(value, str):
                            return 0  
                        match = re.search(r'(\d+)', value.replace(",", ""))
                        return int(match.group(1)) if match else 0





                    # ✅ 상세 정보에서 prodBenfAreaList 가져오기
                    benefit_titles = []
                    for benefit_area in detail_info.get("prodBenfAreaList", []):
                        title = benefit_area.get("prodBenfAreaTitleNm", "").strip()
                        if title:
                            clean_title = BeautifulSoup(title, "html.parser").text  # HTML 태그 제거
                            clean_title = remove_emojis(clean_title)  # ✅ 이모지 제거 추가
                            benefit_titles.append(clean_title)

                    # ✅ 리스트를 | 로 합쳐서 저장
                    benefit = " | ".join(benefit_titles) if benefit_titles else ""


                    def extract_qos(value):
                        """QoS 값을 '5Mbps', '400Kbps' 등만 추출하는 함수"""
                        if not value:
                            return ""  # 값이 없으면 빈 문자열 반환

                        match = re.search(r'(\d+)\s*(Mbps|Kbps)', value, re.IGNORECASE)
                        return match.group(0) if match else ""  # 패턴이 맞으면 반환, 없으면 빈 문자열 반환

                    # ✅ QoS 값 가져오기
                    qos_raw = detail_info.get("qosDataQtyCtt", "").strip()  # 원본 값 가져오기
                    qos = extract_qos(qos_raw)  # 정제된 QoS 값 추출



                    # ✅ 부가통화 값 가져오기 (item에서 먼저 찾고, 없으면 detail_info에서 찾기)
                    buga_call_raw = str(item.get("addTcCtt", detail_info.get("addTcCtt", ""))).strip()

                    # ✅ 숫자만 추출 (ex. "부가통화 150분 제공" -> "150")
                    buga_call_match = re.search(r'(\d+)', buga_call_raw.replace(",", "")) if buga_call_raw else None

                    if "무제한" in buga_call_raw:
                        buga_call = "9999분"
                    elif buga_call_match:
                        buga_call = f"{buga_call_match.group(1)}분"
                    else:
                        buga_call = "0분"



                    # ✅ 가격 필드 추출
                    normal_price = extract_number(item.get("basFeeInfo", ""))
                    sale_price = extract_number(item.get("selAgrmtAplyMfixAmt", ""))

                    if sale_price == 0:
                        sale_price = normal_price  

                    after_price = sale_price  

                    # ✅ 프로모션 기간 가져오기
                    promotion_period = ""

                    # ✅ 12개월 & 24개월 가격 계산 로직 개선
                    if not promotion_period or promotion_period.strip() == "":
                        m12_price = normal_price * 12  # ✅ 정상 가격 기준으로 계산
                        m24_price = normal_price * 24
                    elif "개월" in promotion_period:
                        months = int(re.search(r"(\d+)", promotion_period).group(1)) if re.search(r"(\d+)", promotion_period) else 0
                        if months >= 24:
                            m12_price = sale_price * 12
                            m24_price = sale_price * 24
                        elif months >= 12:
                            m12_price = sale_price * 12
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                        else:
                            m12_price = (sale_price * months) + (after_price * (12 - months))
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                    else:
                        m12_price = normal_price * 12  # ✅ 기본값
                        m24_price = normal_price * 24  # ✅ 기본값





                    # ✅ PlanData DTO 객체 생성 (가격만 0, 나머지는 빈 문자열)
                    dto = PlanData(
                        uuid=f"SKT_{uuid}",
                        mno="SKT",
                        telecom=SiteTargetListType.SKT_LIST.value,
                        company_id=SiteTargetListIDType.SKT_LIST.value,
                        url=f"https://www.tworld.co.kr/web/product/callplan/{uuid}",
                        plan_type=plan_type,  # ✅ DTO에도 plan_type 반영
                        plan_name=plan_name,
                        data=data,
                        voice_call=voice_call,
                        message=message,
                        normal_price = normal_price,
                        sale_price = normal_price,
                        after_price = normal_price,       
                        benefit=benefit,
                        qos=qos,
                        business_name="에스케이텔레콤(주)",
                        promotion_period="",
                        buga_call=buga_call,
                        plan_code="",
                        daily_data="",
                        m12_price=m12_price,
                        m24_price=m24_price,
                        combination=True,
                        freebies="",
                        etc=""
                    )

                    result.append(dto)
                    success_count += 1

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{item_idx+1}/{len(data)}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

        # ✅ 상세 정보 JSON 저장
        self.save_json(detailed_data_list, filename="skt_detail_data.json")

        if not result:
            is_end = True

        elapsed_total = time.time() - start_time
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초) | 성공: {success_count}, 스킵: {skip_count}, 에러: {error_count}")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
