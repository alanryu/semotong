import requests
from bs4 import BeautifulSoup
import time
import re
import json
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from modules.dto.input_queue_dto import PlanData  # ✅ DTO 불러오기
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[SevenMobile]'

class SevenMobileListAction:
    def initialize_browser(self):
        """Chrome WebDriver 설정"""
        print(f"{TAG} Selenium 드라이버 생성 중...")
        options = webdriver.ChromeOptions()
        options.add_argument('--headless=new')  # 백그라운드 실행
        options.add_argument('--disable-gpu')
        options.add_argument('--window-size=1920x1080')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--no-zygote')
        options.add_argument('--disable-blink-features=AutomationControlled')
        driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
        print(f"{TAG} Selenium 드라이버 생성 완료")
        return driver

    def get_prodCd_list(self):
        """요금제 목록에서 prodCd 값 추출"""
        browser = self.initialize_browser()
        t0 = time.time()
        print(f"{TAG} 목록 페이지 로드 중...")
        browser.get("https://www.sk7mobile.com/prod/data/callingPlanList.do?refCode=USIM")
        time.sleep(1)
        elapsed = time.time() - t0
        print(f"{TAG} 목록 페이지 로드 완료 ({elapsed:.1f}초)")

        # HTML 파싱
        soup = BeautifulSoup(browser.page_source, "html.parser")
        browser.quit()
        print(f"{TAG} Selenium 드라이버 종료")

        # ✅ prodCd 값만 정확히 추출하는 정규표현식 수정
        prodCd_pattern = re.findall(r"fnSearchView\('([A-Z0-9]+)'\)", str(soup))
        prodCd_list = list(set(prodCd_pattern))  # 중복 제거

        print(f"{TAG} prodCd {len(prodCd_list)}건 수집 완료")
        return prodCd_list

    def get_plan_details(self, prodCd, log_prefix=""):
        """요금제 상세페이지 크롤링"""
        BASE_URL = "https://www.sk7mobile.com/prod/data/callingPlanView.do?prodCd={}"
        url = BASE_URL.format(prodCd)

        HEADERS = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36",
            "Referer": "https://www.sk7mobile.com/",
            "Accept-Language": "ko-KR,ko;q=0.9",
        }

        session = requests.Session()
        session.headers.update(HEADERS)

        try:
            t0 = time.time()
            response = session.get(url, timeout=10)  # 타임아웃 설정
            elapsed = time.time() - t0
            print(f"{TAG} {log_prefix}상세 페이지 응답 완료 ({elapsed:.1f}초)")

            if response.status_code == 200:
                soup = BeautifulSoup(response.text, "html.parser")

                # 상세페이지 진입 여부 확인
                title_tag = soup.find("h2", class_="title")
                if not title_tag:
                    return {"prodCd": prodCd, "error": "상세페이지 로드 실패"}
                
                # 요금제 이름
                name = title_tag.text.strip() if title_tag else ""

                
                # ✅ 계약 기간 정보 가져오기
                contract_tag = soup.find("div", class_="contract")
                contract_period = contract_tag.text.strip() if contract_tag else "평생"

                # ✅ 원래 가격 (쉼표 및 원 제거 후 숫자로 변환)
                original_price_tag = soup.select_one(".item1 p b")
                original_price = original_price_tag.text.strip() if original_price_tag else "N/A"

                try:
                    original_price = int(re.sub(r"[^\d]", "", original_price)) if original_price != "N/A" else 0
                except ValueError as e:
                    print(f"[WARNING] Failed to convert value to int: {original_price}, Error: {e}")
                    original_price = 0  # 변환 실패 시 0으로 설정

                # ✅ 할인 유형 가져오기
                discount_type_tag = soup.select_one(".item2 em")
                discount_type = discount_type_tag.text.strip() if discount_type_tag else "N/A"

                # ✅ 할인 가격 가져오기
                discount_price_tag = soup.select_one(".item2 p b")
                discount_price = discount_price_tag.text.strip() if discount_price_tag else "N/A"

                # ✅ 숫자로 변환
                try:
                    discount_price = int(re.sub(r"[^\d]", "", discount_price)) if discount_price != "N/A" else 0
                except ValueError as e:
                    print(f"[WARNING] Failed to convert value to int: {discount_price}, Error: {e}")
                    discount_price = 0

                # ✅ 기본 설정값
                promotion_period = "평생"
                after_price = discount_price  # 기본값은 할인된 가격과 동일

                # ✅ "n개월 프로모션 할인 기본료" 패턴 감지 (예: "12개월 프로모션 할인 기본료")
                match = re.search(r"(\d+)개월 프로모션 할인 기본료", discount_type)
                if match:
                    promotion_period = f"{match.group(1)}개월"  # n개월로 설정
                    after_price = original_price  # 프로모션 적용 후 원래 가격으로 복귀


                # 데이터 제공량
                data_tag = soup.select_one("i.icon-data + p")
                data = data_tag.text.strip() if data_tag else ""

                # 음성 제공량
                call_tag = soup.select_one("i.icon-call + p")
                call = call_tag.text.strip() if call_tag else ""

                # 문자 제공량
                sms_tag = soup.select_one("i.icon-sms + p")
                sms = sms_tag.text.strip() if sms_tag else ""


                # ✅ "N/A"일 경우에도 "평생"으로 설정
                if contract_period == "N/A":
                    contract_period = "평생"


                # 할인 정보
                discount_tag = soup.find("div", class_="discount")
                discount_info = discount_tag.text.strip() if discount_tag else ""

                #  LTE 등 badge1 정보 가져오기
                badge1_tag = soup.find("span", class_="badge1")
                plan_type = badge1_tag.text.strip() if badge1_tag else ""

                #  "3G"를 "5G"로 변경
                if "3G" in plan_type:
                    plan_type = "5G"


                #  혜택 (benefit) 가져오기
                benefit_tag = soup.find("p", class_="tit-sub")
                benefit = benefit_tag.text.strip() if benefit_tag else ""
                # 특정 단어가 포함된 경우 빈 문자열로 설정
                if "기본제공" in benefit and "소진" in benefit:
                    benefit = ""

                # ✅ QOS (속도 제한 정보) 추출
                qos = ""

                # ✅ 1단계: `.plan-sect` 내부에서 "데이터" 관련 `tit-sub` 찾기
                plan_sect_divs = soup.find_all("div", class_="plan-sect")

                for div in plan_sect_divs:
                    strong_tag = div.find("strong", class_="tit")
                    if strong_tag and "데이터" in strong_tag.get_text():  # ✅ "데이터" 섹션만 처리
                        qos_tag = div.find("p", class_="tit-sub")
                        if qos_tag:
                            break  # ✅ 가장 먼저 찾은 데이터 관련 `qos` 정보 사용

                # ✅ 2단계: `.data-plus .item2` 내부에서 찾기 (백업)
                if not qos_tag:
                    data_plus_div = soup.find("div", class_="data-plus")
                    if data_plus_div:
                        qos_tag = data_plus_div.find("div", class_="item2")

                # ✅ QOS 값을 정제하여 가져오기
                if qos_tag:
                    qos_text = qos_tag.get_text(separator=" ", strip=True)  # ✅ <br> 태그를 공백으로 변환하여 가져옴

                    # ✅ 기본 제공 데이터 (예: "15GB 기본제공")
                    base_data_match = re.search(r"(\d+\.?\d*\s?(?:GB|MB))\s*기본제공", qos_text)
                    base_data = base_data_match.group(1) if base_data_match else ""

                    # ✅ 매일 추가 제공 데이터 (예: "매일 2GB 추가 제공")
                    daily_data_match = re.search(r"매일\s*(\d+\.?\d*\s?(?:GB|MB))\s*추가 제공", qos_text)
                    daily_data = daily_data_match.group(1) if daily_data_match else ""

                    # ✅ QOS 속도 제한 값 (예: "최대 3Mbps 속도로 무제한 이용 가능")
                    qos_match = re.search(r"최대\s*(\d+\.?\d*\s?(?:Mbps|Kbps))\s*속도로", qos_text)
                    qos = qos_match.group(1) if qos_match else ""

                # ✅ 음성통화 제공량 (부가통화 정보) 추출
                voice_call = ""

                # ✅ `.plan-sect` 내부에서 "통화" 관련된 `tit-sub` 찾기
                plan_sect_divs = soup.find_all("div", class_="plan-sect")

                for div in plan_sect_divs:
                    strong_tag = div.find("strong", class_="tit")
                    if strong_tag and "통화" in strong_tag.get_text():  # ✅ "통화" 섹션만 처리
                        voice_call_tag = div.find_all("p", class_="tit-sub")  # ✅ 모든 `tit-sub` 찾기
                        
                        for tag in voice_call_tag:
                            text = tag.get_text(strip=True)
                            match = re.search(r"(\d+)\s*분", text)  # ✅ "숫자+분" 패턴 찾기
                            if match:
                                voice_call = f"{match.group(1)}분"
                                break  # ✅ 가장 먼저 찾은 값 사용

                # ✅ 결과 저장
                return {
                    "prodCd": prodCd,
                    "name": name,
                    "original_price": original_price,
                    "discount_price": discount_price,
                    "data": data,
                    "call": call,
                    "sms": sms,
                    "contract_period": contract_period,
                    "discount_info": discount_info,
                    "url": url,
                    "plan_type": plan_type,
                    "benefit": benefit,
                    "qos": qos,  # ✅ QOS 값 추가
                    "buga_call" : voice_call,
                    "daily_data": daily_data,  # ✅ 매일 추가 제공 데이터
                    "promotion_period": promotion_period,  # ✅ 프로모션 적용 기간
                    "after_price": after_price,  # ✅ 애프터 프라이스

                }

            else:
                return {"prodCd": prodCd, "error": f"HTTP {response.status_code} 오류"}

        except requests.exceptions.RequestException as e:
            print(f"{TAG} {log_prefix}[ERROR] 상세 페이지 요청 실패: {e}")
            return {"prodCd": prodCd, "error": str(e)}
    def root(self, *args, **kwargs):
        """전체 크롤링 실행"""
        start_time = time.time()
        print(f"{TAG} 스크래핑 시작")
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0
        details_list = []

        # ✅ 브라우저 실행
        browser = self.initialize_browser()

        try:
            # ✅ prodCd 목록 가져오기
            prodCd_list = self.get_prodCd_list()
            total_count = len(prodCd_list)

            # ✅ 요금제 상세정보 가져오기
            for idx, prodCd in enumerate(prodCd_list):
                log_prefix = f"[{idx+1}/{total_count}] "
                print(f"{TAG} {log_prefix}상세 페이지 요청 중: {prodCd}")
                plan_details = self.get_plan_details(prodCd, log_prefix=log_prefix)
                details_list.append(plan_details)  # 새로운 리스트에 저장

            print(f"{TAG} 상세 페이지 수집 완료: {len(details_list)}건")
            # ✅ 개별 데이터 상세 출력 (JSON 형식 그대로)
            for plan_idx, plan in enumerate(details_list):
                normal_price = int(plan["original_price"]) if isinstance(plan["original_price"], (int, float, str)) and str(plan["original_price"]).isdigit() else 0
                sale_price = int(plan["discount_price"]) if isinstance(plan["discount_price"], (int, float, str)) and str(plan["discount_price"]).isdigit() else 0
                after_price = int(plan["after_price"]) if isinstance(plan["after_price"], (int, float, str)) and str(plan["after_price"]).isdigit() else 0

                promotion_period=plan["promotion_period"]

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
                        
                print(f"{TAG} [{plan_idx+1}/{len(details_list)}] DTO 생성: {plan.get('name', plan.get('prodCd', ''))}")
                dto = PlanData(
                                uuid="SEVEN_" +plan["prodCd"],
                                mno="SKT", 
                                telecom=SiteTargetListType.SK7MOBILE_LIST.value,
                                url = plan["url"],
                                company_id=SiteTargetListIDType.SK7MOBILE_LIST.value,
                                plan_type=plan["plan_type"],
                                plan_name=plan["name"], 
                                data=plan["data"], 
                                voice_call=plan["call"], 
                                message=plan["sms"],
                                normal_price=normal_price, 
                                sale_price=sale_price,
                                after_price=after_price,  
                                benefit=plan["benefit"], 
                                qos=plan["qos"], 
                                business_name="에스케이텔링크 주식회사",
                                combination=False, 
                                freebies="", 
                                etc="", 
                                promotion_period=promotion_period,
                                buga_call=plan["buga_call"], 
                                plan_code="",
                                daily_data=plan["daily_data"],
                                m12_price=m12_price,
                                m24_price=m24_price,
                             )
                result.append(dto)
                success_count += 1

            is_end = True
            elapsed_total = time.time() - start_time
            print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초) | 성공: {success_count}, 스킵: {skip_count}, 에러: {error_count}")
            print(f"{TAG} 최종 결과: PlanData {len(result)}건")
            return result, is_end

        finally:
            # ✅ 크롤링 완료 후 브라우저 종료
            browser.quit()
            print(f"{TAG} 브라우저 종료")