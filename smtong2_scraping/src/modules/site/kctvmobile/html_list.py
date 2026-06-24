import json
import os
import time
import traceback
import requests
import re
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from modules.dto.input_queue_dto import PlanData
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[KctvMobile]'


class KctvMobileListAction:
    def initialize_browser(self):
        """Chrome WebDriver 설정"""
        print(f"{TAG} Selenium 드라이버 생성 중")
        options = webdriver.ChromeOptions()
        options.add_argument('--headless=new')
        options.add_argument('--disable-gpu')
        options.add_argument('--window-size=1920x1080')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--no-zygote')
        options.add_argument('--disable-blink-features=AutomationControlled')
        browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
        print(f"{TAG} Selenium 드라이버 생성 완료")
        return browser

    def fetch_mobile_rates(self):
        """KCTV 모바일 요금제 JSON 데이터 요청"""
        url = "https://www.kctvjeju.com/product/ajaxGetMobileRateAllList.kctv"
        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
            "Accept": "application/json, text/javascript, */*; q=0.01",
            "Accept-Encoding": "gzip, deflate, br, zstd",
            "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "X-Requested-With": "XMLHttpRequest",
            "Referer": "https://www.kctvjeju.com/product/kctvm_view.kctv",
            "Origin": "https://www.kctvjeju.com",
            "Connection": "keep-alive",
        }

        try:
            t0 = time.time()
            print(f"{TAG} 요금제 API 요청 시작")
            response = requests.post(url, headers=headers)
            response.raise_for_status()
            elapsed = time.time() - t0
            print(f"{TAG} 요금제 API 응답 완료 ({elapsed:.1f}초)")
            json_data = response.json()

            self.save_json_response(json_data)

            return json_data
        except requests.exceptions.RequestException as e:
            print(f"{TAG} [ERROR] 요금제 API 요청 실패: {e}")
            return None

    def save_json_response(self, data):
        """JSON 응답 데이터를 response_files 폴더에 저장"""
        os.makedirs("response_files", exist_ok=True)
        file_path = "response_files/kctv_mobile_rates.json"

        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=4, ensure_ascii=False)

    def root(self, *args, **kwargs):
        """크롤링 실행 후 JSON 데이터를 PlanData 객체로 변환"""
        print(f"{TAG} 스크래핑 시작")
        total_start = time.time()
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0

        browser = self.initialize_browser()
        print(f"{TAG} 페이지 로드 시작")
        t0 = time.time()
        browser.get("https://www.kctvjeju.com/product/kctvm_view.kctv")
        time.sleep(1)
        print(f"{TAG} 페이지 로드 완료 ({time.time() - t0:.1f}초)")

        try:
            mobile_rates = self.fetch_mobile_rates()

            if not mobile_rates or "list" not in mobile_rates:
                print(f"{TAG} 데이터 없음, 종료")
                return result, is_end

            items = mobile_rates["list"]
            total_count = len(items)
            print(f"{TAG} 파싱 대상 요금제 수: {total_count}건")

            for idx, item in enumerate(items):
                rate_code = item.get("rate_code", "").strip()
                uuid = f"KCTV_{rate_code}"

                plan_name = item.get("rate_nm", "").strip()
                mno = item.get("use_line_nm", "").strip()
                mno = "LGU" if mno == "LG U+" else mno
                print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {mno} | {plan_name}")

                plan_type = "5G" if re.search(r"\b5G\b", item.get("rate_nm", "")) else "LTE"

                default_data = item.get("default_data", "").strip()
                data, daily_data = "0GB", ""

                if default_data:
                    data_match = re.search(r"(\d+\.?\d*)GB\s*\+?", default_data)
                    if data_match:
                        data = f"{data_match.group(1)}GB"

                    daily_data_match = re.search(r"매일\s*(\d+)\s*GB", default_data)
                    if daily_data_match:
                        daily_data = f"{daily_data_match.group(1)}GB"

                    has_sojinhu_daily = bool(re.search(r"소진 후 매일\s*\d+\s*GB", default_data))
                    has_just_daily = bool(re.search(r"^매일\s*\d+\s*GB", default_data))

                    if has_sojinhu_daily:
                        data = f"{data_match.group(1)}GB" if data_match else "0GB"
                    elif has_just_daily:
                        data = "0GB"

                qos_match = re.search(r"(?:\(|소진 후)\s*(\d+\.?\d*)\s*(Mbps|Mpbs|Kbps)", default_data)
                qos = f"{qos_match.group(1)}Mbps" if qos_match else "0Mbps"

                default_voice = item.get("default_voice", "").strip()
                voice_match = re.search(r"([^<]+)<br/>", default_voice)
                voice_call = voice_match.group(1).strip() if voice_match else default_voice
                voice_call = voice_call if voice_call else "0분"

                default_message = item.get("default_message", "").strip()
                message_match = re.search(r"([^<]+)<br/>", default_message)
                message = message_match.group(1).strip() if message_match else default_message
                message = message if message else "0건"

                buga_match = re.search(r"<br/>\s*\(?부가\s*(\d+)분", default_voice)
                buga_call = buga_match.group(1).strip() + "분" if buga_match else "0분"

                price = item.get("comm_charge", "").strip()
                price = int(price) if price.isdigit() else 0

                normal_price = sale_price = after_price = price

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
                    uuid=uuid,
                    mno=mno,
                    telecom=SiteTargetListType.KCTV_LIST.value,
                    url="https://www.kctvjeju.com/product/kctvm_view.kctv",
                    company_id=SiteTargetListIDType.KCTV_LIST.value,
                    plan_type=plan_type,
                    plan_name=item.get("rate_nm", "").strip(),
                    data=data,
                    daily_data=daily_data,
                    qos=qos,
                    voice_call=voice_call,
                    message=message,
                    normal_price=normal_price,
                    sale_price=sale_price,
                    after_price=after_price,
                    benefit="",
                    business_name="(주)KCTV제주방송",
                    combination=False,
                    freebies="",
                    etc="",
                    promotion_period=promotion_period,
                    buga_call=buga_call,
                    plan_code="",
                    m12_price=m12_price,
                    m24_price=m24_price,
                )

                result.append(dto)
                success_count += 1

        except Exception as e:
            error_count += 1
            print(f"{TAG} [ERROR] 크롤링 중 오류 발생: {e}")
            traceback.print_exc()

        finally:
            is_end = True
            print(f"{TAG} Selenium 드라이버 종료")
            browser.quit()

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
