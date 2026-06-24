import time
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import requests
import traceback
import os
import json
import re
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData

TAG = '[Mobing]'

class MobingListAction:

    def __init__(self):
        # ✅ API 응답 저장 폴더 설정 (오류 수정)
        self.response_folder = os.path.abspath("./response_files")
        os.makedirs(self.response_folder, exist_ok=True)
        
    def get_total_pages(self, url: str):
        """
        셀레니움을 사용하여 총 페이지 수를 가져옴
        """
        driver = None
        try:
            chrome_options = Options()
            chrome_options.add_argument("--headless")
            chrome_options.add_argument("--no-sandbox")
            chrome_options.add_argument("--disable-dev-shm-usage")
            chrome_options.add_argument("--no-zygote")
            chrome_options.add_argument("--disable-gpu")

            print(f"{TAG} Selenium 드라이버 생성 중...")
            service = Service(ChromeDriverManager().install())
            driver = webdriver.Chrome(service=service, options=chrome_options)
            print(f"{TAG} Selenium 드라이버 생성 완료")

            t0 = time.time()
            print(f"{TAG} 페이지 로드 중: {url}")
            driver.get(url)
            driver.implicitly_wait(3)
            elapsed = time.time() - t0
            print(f"{TAG} 페이지 로드 완료 ({elapsed:.1f}초)")

            self.cookies = {cookie["name"]: cookie["value"] for cookie in driver.get_cookies()}

            page_info = driver.find_element(By.CSS_SELECTOR, "div.page-more__num").text

            match = re.search(r"/\s*(\d+)", page_info)
            if match:
                total_pages = int(match.group(1))
            else:
                total_pages = 1

            print(f"{TAG} Selenium 드라이버 종료, 총 {total_pages} 페이지 확인")
            return total_pages

        except Exception as e:
            print(f"{TAG} [ERROR] 페이지 수 가져오기 실패: {e}")
            traceback.print_exc()
            return 1
        finally:
            if driver:
                try:
                    driver.quit()
                except Exception:
                    pass

    def fetch_api_data(self, page: int, total_pages: int = 0):
        """
        API를 통해 Mobing 요금제 데이터 수집 (페이지별 요청)
        """
        base_url = "https://www.mobing.co.kr/api/product/getPromoPlanList"
        params = {"page": page, "limit": 10}

        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
            "Accept": "application/json, text/plain, */*",
            "Referer": "https://www.mobing.co.kr/product/plan/promotion",
            "Origin": "https://www.mobing.co.kr",
            "X-Requested-With": "XMLHttpRequest",
            "Cookie": "; ".join([f"{k}={v}" for k, v in self.cookies.items()]),
        }

        try:
            t0 = time.time()
            response = requests.get(base_url, params=params, headers=headers)
            elapsed = time.time() - t0
            if response.status_code != 200:
                print(f"{TAG} [ERROR] API 요청 실패 (페이지 {page}/{total_pages}, 상태 코드: {response.status_code})")
                return []

            data = response.json()
            items = data.get("entity", {}).get("list", [])
            print(f"{TAG} API 페이지 [{page}/{total_pages}] 응답 완료 ({elapsed:.1f}초) - {len(items)}건")
            time.sleep(1)  # ✅ 요청 간격 조절 (차단 방지)
            return items

        except Exception as e:
            print(f"{TAG} [ERROR] API 요청 예외 (페이지 {page}): {e}")
            traceback.print_exc()
            return []

    def root(self, *args, **kwargs):
        """
        전체 프로세스 실행 (API 데이터 수집 → DTO 생성)
        """
        start_time = time.time()
        print(f"{TAG} 스크래핑 시작")
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = True
        url = "https://www.mobing.co.kr/product/plan/promotion"

        # ✅ 셀레니움으로 총 페이지 수 가져오기
        total_pages = self.get_total_pages(url)
        print(f"{TAG} 총 {total_pages} 페이지 데이터 수집 시작")

        api_data = []
        for page in range(1, total_pages + 1):
            page_data = self.fetch_api_data(page, total_pages)
            if page_data:
                api_data.extend(page_data)

        print(f"{TAG} API 데이터 수집 완료: 총 {len(api_data)}건")

        api_data_json_path = os.path.join(self.response_folder, "mobing_all_data.json")
        with open(api_data_json_path, "w", encoding="utf-8") as f:
            json.dump(api_data, f, ensure_ascii=False, indent=4)

        # ✅ DTO 변환 (이전 코드 유지)
        total_count = len(api_data)
        for idx, item in enumerate(api_data):
            try:
                # ✅ 기본 데이터 추출
                plan_id = item.get("planID", "")
                promo_seq = item.get("promoSeq", "")
                url = f"https://www.mobing.co.kr/product/plan/view?planID={plan_id}&promoSeq={promo_seq}"
                plan_name = item.get("planNM", "")
                print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {plan_name}")
                telecom = item.get("telco", "")
                # ✅ "LGT"를 "LGU"로 변환
                if telecom == "LGT":
                    telecom = "LGU"

                # ✅ 데이터 & QoS 정보
                data = f"{item.get('basicDataMon', '0') or '0'}{item.get('basicDataMonUnit', '')}"
                basic_qos = item.get("basicQos", "0") or "0"
                basic_qos_unit = item.get("basicQosUnit", "")
                qos = f"{basic_qos}{basic_qos_unit}"
                daily_data = f"{item.get('basicDataDay', '0') or '0'}{item.get('basicDataDayUnit', '')}"

                # ✅ 음성 / 영상 / 문자 제공량
                basic_voice = item.get('basicVoice', 0)
                basic_video = item.get('basicVideo', 0)
                buga_call = f"{basic_video}분" if basic_video != 0 else ""
                voice_call = "무제한" if basic_voice == 99999 else f"{basic_voice}분"
                basic_sms = item.get('basicSms', 0)
                message = "무제한" if basic_sms == 99999 else f"{basic_sms}건"

                # ✅ 가격 정보
                normal_price = item.get("amountMon", 0)
                sale_price = normal_price - item.get("totalSaleAmount", 0)

                # ✅ saleAmount 처리 및 애프터프라이스 계산
                term_month_str = item.get("termMonth", "")
                sale_amount_str = item.get("saleAmount", "")
                term_months = [int(month) for month in term_month_str.split("|") if month.isdigit()]
                sale_amounts = [int(amount) for amount in sale_amount_str.split("|") if amount.isdigit()]

                # ✅ 1200개월(평생)에 해당하는 saleAmount만 추출하여 총 할인 금액 계산
                total_discount = 0
                for i in range(len(term_months)):
                    if term_months[i] == 1200:
                        total_discount += sale_amounts[i]

                # ✅ 애프터프라이스 계산
                after_price = normal_price - total_discount

                # ✅ 프로모션 기간 계산 (빈값이면 "평생"으로 설정)
                if "|" in term_month_str:
                    term_months = [int(month) for month in term_month_str.split("|") if month.isdigit()]
                    if term_months:
                        min_month = min(term_months)
                        if min_month == 1200:
                            promotion_period = "평생"
                        else:
                            promotion_period = f"{min_month}개월"
                    else:
                        promotion_period = "평생"
                elif term_month_str.isdigit() and int(term_month_str) <= 100:
                    promotion_period = f"{int(term_month_str)}개월"
                else:
                    promotion_period = "평생"

                # ✅ plan_type 치환
                plan_type = item.get("networkID", "LTE")
                if plan_type == "4G":
                    plan_type = "LTE"

                combination = True if item.get("iconUplusPack", "") == "Y" else ""  # "iconUplusPack"이 "Y"이면 True, 아니면 False

                # ✅ sale_price 및 after_price 변환
                sale_price = int(sale_price) if isinstance(sale_price, str) and sale_price.isdigit() else sale_price
                after_price = int(after_price) if isinstance(after_price, str) and after_price.isdigit() else after_price

                # ✅ 12개월 & 24개월 총 요금(m12_price, m24_price) 계산
                if not promotion_period or promotion_period == "평생":
                    m12_price = sale_price * 12
                    m24_price = sale_price * 24
                elif "개월" in promotion_period:
                    match = re.search(r"(\d+)", promotion_period)
                    if match:
                        months = int(match.group(1))
                    else:
                        months = 0

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
                    m12_price = normal_price * 12
                    m24_price = normal_price * 24

                # ✅ DTO 객체 생성
                dto = PlanData(
                    uuid=f"MOBING_{plan_id}_{promo_seq}",
                    telecom=SiteTargetListType.MOBING_LIST.value,
                    company_id=SiteTargetListIDType.MOBING_LIST.value,
                    mno=telecom,
                    url=url,
                    plan_type=plan_type,
                    plan_name=plan_name,
                    data=data,
                    voice_call=voice_call,
                    message=message,
                    normal_price=normal_price,
                    sale_price=sale_price,
                    qos=qos,
                    business_name="(주) 유니컴즈",
                    after_price=after_price,
                    combination=combination,
                    freebies="",
                    etc="",
                    benefit="",
                    promotion_period=promotion_period,
                    buga_call=buga_call,
                    plan_code="",
                    daily_data=daily_data,
                    m12_price=m12_price,
                    m24_price=m24_price,
                )

                # ✅ 리스트에 추가
                result.append(dto)
                success_count += 1

            except Exception as e:
                error_count += 1
                print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                traceback.print_exc()

        elapsed_total = time.time() - start_time
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초) | 성공: {success_count}, 스킵: {skip_count}, 에러: {error_count}")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
