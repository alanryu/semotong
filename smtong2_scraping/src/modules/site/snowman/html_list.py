import time
import traceback
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
from modules.dto.input_queue_dto import PlanData
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[Snowman]'

class SnowmanListAction:
    def root(self, *args, **kwargs) -> tuple[list[PlanData], bool]:
        """
        Snowman 사이트에서 데이터를 가져와 PlanData 리스트로 반환하는 함수.
        """
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result = []

        # Selenium ChromeOptions 설정
        options = webdriver.ChromeOptions()
        options.add_argument('--headless=new')  # 최신 headless 모드
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--no-zygote')
        options.add_argument('--disable-gpu')
        options.add_experimental_option("detach", False)

        # WebDriver Manager를 이용해 ChromeDriver 자동 설치
        print(f"{TAG} Selenium 드라이버 생성 중...")
        browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
        print(f"{TAG} Selenium 드라이버 생성 완료")

        try:
            # 브라우저 실행 및 페이지 접속
            url = "https://www.snowman.co.kr/portal/chageAdtnsvc/ppdChage/list"
            print(f"{TAG} 페이지 로드 시작: {url}")
            page_start = time.time()
            browser.get(url)
            time.sleep(2)  # 페이지 로드 대기
            page_elapsed = time.time() - page_start
            print(f"{TAG} 페이지 로드 완료 ({page_elapsed:.1f}초)")

            # <li class="service-pay-item"> 요소들 찾기
            items = browser.find_elements(By.CLASS_NAME, "service-pay-item")
            total_count = len(items)
            print(f"{TAG} 파싱할 항목 수: {total_count}건")

            if not items:
                print(f"{TAG} [WARNING] 'service-pay-item' 요소를 찾을 수 없습니다.")
                return result, is_end

            for idx, item in enumerate(items):
                try:
                    # 각 항목의 UUID 생성
                    link = item.find_element(By.TAG_NAME, "a")
                    onclick_attr = link.get_attribute("onclick")
                    # print(f"[DEBUG] onclick attribute: {onclick_attr}")

                    # UUID 생성 로직
                    if "popDtl" in onclick_attr:
                        uuid = onclick_attr.split('(')[1].split(',')[0].strip("'")  # 수정된 UUID 생성
                        # print(f"[DEBUG] Generated UUID: {uuid}")
                    else:
                        print("[WARNING] 'popDtl' not found in onclick attribute")
                        continue

                    # 링크 클릭
                    browser.execute_script("arguments[0].scrollIntoView(true);", link)
                    time.sleep(0.5)
                    browser.execute_script("arguments[0].click();", link)

                    # 모달 창 로딩 대기
                    WebDriverWait(browser, 10).until(
                        EC.presence_of_element_located((By.ID, "modalServiceDetail"))
                    )

                    # 모달 데이터 파싱
                    modal_element = browser.find_element(By.ID, "modalServiceDetail")
                    modal_html = modal_element.get_attribute("outerHTML")
                    soup = BeautifulSoup(modal_html, "html.parser")

                    # 필요한 데이터 추출
                    mno = soup.find("em", id="commCmpnNm").get_text(strip=True)
                    plan_name = soup.find("p", id="chageProdNm").get_text(strip=True)
                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {mno} | {plan_name}")
                    plan_type = "5G" if "5G" in plan_name else "LTE"

                    qos = ""
                    qos_element = soup.find("p", id="apdDataCpct")
                    if qos_element:
                        qos_text = qos_element.get_text(strip=True)
                        if "최대" in qos_text:
                            qos = qos_text.split("최대")[-1].replace("+", "").strip()  # '+' 제거

                    try:
                        normal_price_element = soup.find("span", id="basChage")
                        normal_price = (
                            int(normal_price_element.get_text(strip=True).replace(",", ""))
                            if normal_price_element and normal_price_element.get_text(strip=True) else None
                        )
                    except ValueError:
                        normal_price = None

                    # `normal_price`가 None인 경우, `sale_price` 값을 사용
                    sale_price_element = soup.find("span", id="lowstChage")
                    sale_price = (
                        int(sale_price_element.get_text(strip=True).replace(",", "").replace("월", "").replace("원", "").strip())
                        if sale_price_element and sale_price_element.get_text(strip=True) else 0
                    )
                    if normal_price is None:
                        normal_price = sale_price

                    # PlanData 생성
                    dto = PlanData(
                        uuid='snow' + uuid,  # UUID 값 앞에 "snow" 추가
                        mno=mno,
                        telecom=SiteTargetListType.SNOWMAN_LIST.value,
                        company_id=SiteTargetListIDType.SNOWMAN_LIST.value,  # company_id 추가
                        url=url,
                        plan_type=plan_type,
                        plan_name=plan_name,
                        data=soup.find("span", id="basDataCpct").get_text(strip=True),
                        voice_call=soup.find("span", id="basTlk").get_text(strip=True),
                        message=soup.find("span", id="basChr").get_text(strip=True),
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit=soup.find("span", id="promBnfit").get_text(strip=True) if soup.find("span", id="promBnfit") else "",
                        qos=qos,
                        business_name="세종텔레콤",
                        after_price=sale_price,
                        combination="",
                        freebies="",
                        etc="",
                        promotion_period="",
                        plan_code = '',
                        daily_data='',
                        m12_price='',
                        m24_price='',
                    )
                    result.append(dto)
                    success_count += 1

                    # 모달 닫기
                    close_button = browser.find_element(By.CLASS_NAME, "btn-modal-close")
                    browser.execute_script("arguments[0].click();", close_button)
                    time.sleep(1)

                except Exception as e:
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 모달 처리 중 예외 발생: {e}")
                    traceback.print_exc()
                    error_count += 1

            is_end = True

        except Exception as e:
            print(f"{TAG} [ERROR] 데이터 처리 중 예외 발생: {e}")
            traceback.print_exc()
            error_count += 1
        finally:
            if browser:
                browser.quit()
                print(f"{TAG} WebDriver 종료 완료")

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")

        return result, is_end
