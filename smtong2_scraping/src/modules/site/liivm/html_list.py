import traceback
import time
import re
import os
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
from modules.dto.input_queue_dto import PlanData  # ✅ DTO 클래스 임포트
from modules.site.target import SiteTargetListType, SiteTargetListIDType
import requests

TAG = '[Liivm]'

class LiivmListAction():
    
    def initialize_browser(self):
        """ 크롬 브라우저 초기화 및 옵션 설정 """
        print(f"{TAG} Selenium 드라이버 생성 중")
        options = webdriver.ChromeOptions()
        options.add_argument('--headless=chrome')  # ✅ 안정적인 headless 모드
        options.add_argument('--disable-gpu')  # ✅ GPU 가속 비활성화
        options.add_argument('--window-size=1920x1080')  # ✅ 충분한 해상도 확보
        options.add_argument('--disable-extensions')  # ✅ 확장 프로그램 비활성화
        options.add_argument('--disable-popup-blocking')  # ✅ 팝업 차단 해제
        options.add_argument('--disable-infobars')  # ✅ 크롬 정보 표시줄 비활성화
        options.add_argument('--no-sandbox')  # ✅ 샌드박스 모드 비활성화 (서버 환경 대비)
        options.add_argument('--disable-dev-shm-usage')  # ✅ 메모리 사용 최적화
        options.add_argument('--no-zygote')
        options.add_argument('--disable-gpu')
        options.add_argument('--blink-settings=imagesEnabled=false')  # ✅ 이미지 로딩 차단
        options.add_argument('--disable-animations')  # ✅ 애니메이션 제거
        options.add_argument('--disable-extensions')  # ✅ 확장 프로그램 비활성화
        options.add_argument("--user-agent=Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36")

        browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
        print(f"{TAG} Selenium 드라이버 생성 완료")
        return browser

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        """ 요금제 리스트 개수를 확인하는 기능 """
        print(f"{TAG} 스크래핑 시작")
        total_start = time.time()
        is_end = False
        result = []
        browser = None  # ✅ 브라우저 객체 초기화
        os.makedirs('./response_files', exist_ok=True)
        detail_count = 1
        success_count = 0
        skip_count = 0
        error_count = 0

        try:
            # ✅ 크롬 브라우저 실행 (headless)
            browser = self.initialize_browser()

            # ✅ 크롤링할 페이지 이동
            url = "https://m.liivm.com/rateplan/plans/products"
            t0 = time.time()
            print(f"{TAG} 페이지 로드 시작: {url}")
            browser.get(url)

            # ✅ 페이지가 완전히 로드될 때까지 기다림
            time.sleep(3)  # ✅ 10초간 무적적으로 기다리기
            print(f"{TAG} 페이지 로드 완료 ({time.time() - t0:.1f}초)")
            
            # ✅ 페이지 스크롤 (리스트가 동적으로 로딩될 가능성 고려)
            browser.execute_script("window.scrollTo(0, document.body.scrollHeight);")

            # ✅ HTML 가져오기 및 파싱
            page_source = browser.page_source
            soup = BeautifulSoup(page_source, 'html.parser')

            # ✅ 요금제 리스트 추출
            plan_list = soup.find_all("div", class_="card_item")
            total_count = len(plan_list)
            print(f"{TAG} 파싱 대상 요금제 수: {total_count}건")

            # ✅ 데이터 추출
            for idx, plan in enumerate(plan_list):
                try:
                    # ✅ 플랜타입 및 MNO 추출
                    badge_elements = plan.select(".badge_group .badge")
                    if not badge_elements:
                        skip_count += 1
                        continue  # 배지가 없으면 건너뜀

                    plan_type = ""
                    mno = ""
                    buga_call = ""

                    for badge in badge_elements:
                        badge_class = badge.get("class", [])  # 클래스명 리스트
                        badge_text = badge.text.strip()

                        # 플랜타입 설정: LTE 또는 5G가 포함된 경우만 저장
                        # if "lte" in badge_class or "five_g" in badge_class:
                        #     plan_type = badge_text
                        if badge_text in ["LTE", "5G"]:
                            plan_type = badge_text

                        # MNO 설정: 'skt', 'kt', 'lg_u' 중 하나를 찾음
                        if "skt" in badge_class:
                            mno = "SKT"
                        elif "kt" in badge_class:
                            mno = "KT"
                        elif "lg_u" in badge_class:
                            mno = "LGU"  # 'LG U+' → 'LGU'로 변경

                    # LTE 또는 5G 플랜이 아니라면 루프 스킵
                    if not plan_type:
                        skip_count += 1
                        continue


                    # ✅ 플랜 이름 추출
                    plan_name = plan.select_one(".item_tit").text.strip()

                    # ✅ "내맘대로"가 포함된 요금제는 루프에서 제외
                    if "내맘대로" in plan_name:
                        skip_count += 1
                        continue  # 해당 요금제는 저장하지 않고 다음 요금제로 이동

                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {mno} | {plan_name}")


                    normal_price = plan.select_one(".item_price_sub")
                    normal_price = int(re.sub(r"[^\d]", "", normal_price.text)) if normal_price else 0

                    sale_price = plan.select_one(".item_price strong")
                    sale_price = int(re.sub(r"[^\d]", "", sale_price.text)) if sale_price else 0

                    after_price = sale_price

                    
                    # ✅ 데이터 정보 추출
                    data_text_element = plan.select_one("li i.icon_data + span")

                    if data_text_element:
                        raw_data_text = data_text_element.get_text(strip=True)  # 원본 텍스트
                        
                        # print(f"🔍 [디버깅] 원본 data_text: {raw_data_text}")  # ✅ 정제 전 원본 데이터 확인

                        # ✅ "(친구결합데이터)" 같은 불필요한 정보 제거
                        data_text = re.sub(r"\+?\s*\d+GB\(.*?\)", "", raw_data_text)

                        # print(f"🔍 [디버깅] 정제 후 data_text: {data_text}")  # ✅ 정제 후 데이터 확인

                        # ✅ 월 데이터 추출 (ex: "월11GB(10GB+1GB)" 또는 "월11GB")
                        month_match = re.search(r"월\s*(\d+\.?\d*)GB", data_text)
                        month_data = float(month_match.group(1)) if month_match else 0.0

                        # ✅ 일일 데이터 추출 (ex: "일2.5GB" 또는 "매일 5GB")
                        daily_match = re.search(r"(?:일|매일)\s*(\d+\.?\d*)GB", data_text)
                        daily_data = float(daily_match.group(1)) if daily_match else 0.0

                        # ✅ 기본 데이터 (월/일 키워드가 없는 경우, ex: "8GB")
                        base_match = re.search(r"(^|\()\s*(\d+\.?\d*)GB", data_text)
                        base_data = float(base_match.group(2)) if base_match else 0.0

                        # ✅ 최종 데이터 계산
                        final_data = month_data + base_data  # 월 데이터 + 기본 데이터
                        final_daily_data = daily_data  # 일일 데이터는 그대로 유지

                        # ✅ 특별 패턴 "월11GB(10GB+1GB)" 감지 후 강제 덮어쓰기
                        special_pattern = re.search(r"월\s*(\d+\.?\d*)GB\(\d+GB\+\d+GB\)", raw_data_text)
                        if special_pattern:
                            final_data = float(special_pattern.group(1))  # ex: "월11GB(10GB+1GB)" → 11GB

                        # ✅ 데이터 형식 통일
                        data = f"{final_data:.1f}GB" if final_data % 1 != 0 else f"{int(final_data)}GB"
                        daily_data = f"{final_daily_data:.1f}GB" if final_daily_data % 1 != 0 else f"{int(final_daily_data)}GB"

                    else:
                        data = "0GB"
                        daily_data = "0GB"

                    # ✅ 디버깅 출력 추가
                   #  print(f"✅ [최종] 데이터: {data}, 데일리 데이터: {daily_data}")


                    voice_call = plan.select_one(".item_info li:nth-child(2) span").text.strip()
                    message = plan.select_one(".item_info li:nth-child(3) span").text.strip()

                    # ✅ UUID 및 상세페이지 URL 생성
                    link_tag = plan.find("a", href=True)
                    soid = link_tag.get("data-soid", "").strip()
                    prodgrpcd = link_tag.get("data-prodgrpcd", "").strip()
                    prodcd = link_tag.get("data-prodcd", "").strip()

                    uuid = f"LIIVM_{prodcd}"

                    soid = link_tag.get("data-soid", "").strip()
                    prodgrpcd = link_tag.get("data-prodgrpcd", "").strip()
                    prodcd = link_tag.get("data-prodcd", "").strip()

                    if not soid or not prodgrpcd or not prodcd:
                        skip_count += 1
                        print(f"{TAG} [{idx+1}/{total_count}] 스킵 (detail_url 정보 부족)")
                        continue

                    detail_url = f"https://m.liivm.com/rateplan/plans/product-detailed?soId={soid}&prodGrpCd={prodgrpcd}&prodCd={prodcd}"


                    headers = {
                        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36"
                    }

                    qos = ""

                    try:
                        t0 = time.time()
                        browser.get(detail_url)  # ✅ Selenium을 이용해 상세페이지 요청

                        time.sleep(1.5)  # ✅ 상세페이지가 다 로드될 때까지 3초 기다리기
                        print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 로드 완료 ({time.time() - t0:.1f}초)")

                        # ✅ 즉시 데이터 확인 (바로 가져오기 시도)
                        soup = BeautifulSoup(browser.page_source, "html.parser")
                        plan_name_element = soup.select_one(".tit_rateplan_wrap h2.tit_sub")


                        # ✅ 원하는 <div> 요소만 추출
                        content_div = soup.select_one('div.content[style="padding-bottom: 8.4rem;"]')

                        # ✅ 해당 요소가 존재할 경우에만 저장
                        
                        """
                        if content_div:
                            file_path = f'./response_files/liivm_detail_{detail_count}.html'
                            with open(file_path, 'w', encoding='utf-8') as f:
                                f.write(content_div.prettify())  # 원하는 div만 저장
                            print(f"저장완료: ./response_files/liivm_detail_{detail_count}.html")    
                        else:
                            print(f"⚠️ content div를 찾지 못했습니다. 저장 생략: {detail_url}")

                        detail_count += 1  # 다음 파일 번호 증가
                        """

                        # ✅ QOS 데이터 가져오기 (QOS에서 Mbps 숫자만 추출)
                        # print(content_div)
                        qos_element = content_div.select_one("#qosDesc")
                        # print(qos_element)
                        # print(f"🔍 [디버깅] QOS 요소: {qos_element}")  # ✅ QOS 요소 확인

                        if qos_element:
                            qos_text = qos_element.get_text(strip=True)
                            
                            # ✅ `strong` 태그가 있으면 그 텍스트를 가져오기
                            strong_tag = qos_element.select_one("strong")
                            if strong_tag:
                                qos = strong_tag.get_text(strip=True)
                            else:
                                qos = qos_text  # strong이 없으면 전체 텍스트 사용

                            # ✅ "소진시" 같은 불필요한 단어 제거
                            qos = re.sub(r".*소진시\s*", "", qos)

                            # ✅ `nMbps` 숫자만 추출 (정규식)
                            qos_match = re.search(r"(\d+)\s*Mbps", qos)
                            qos = f"{qos_match.group(1)}Mbps" if qos_match else ""

                        # print(f"✅ QOS: {qos}")  # 디버깅 출력 (필요 없으면 삭제 가능)


                        # ✅ 부가통화 데이터 가져오기 (부가음성 통화)
                        buga_call_element = content_div.select_one("#grpSrvDesc")
                        # print(f"🔍 [디버깅] 부가통화 요소: {buga_call_element}")  # ✅ 부가통화 요소 확인
                        buga_call = ""

                        if buga_call_element:
                            buga_call_text = buga_call_element.get_text(strip=True)

                            # ✅ "부가음성"이 포함된 문장 찾기
                            buga_call_sentences = re.findall(r"[^.]*부가음성[^.]*", buga_call_text)

                            # ✅ "부가음성" 문장에서 마지막 `n분` 값 찾기
                            if buga_call_sentences:
                                last_sentence = buga_call_sentences[-1]  # ✅ 마지막 "부가음성" 문장
                                buga_call_matches = re.findall(r"(\d+)\s*분", last_sentence)

                                # ✅ 마지막 `n분` 값 저장 (없으면 빈 문자열)
                                if buga_call_matches:
                                    buga_call = f"{buga_call_matches[-1]}분"

                        # print(f"✅ 부가콜: {buga_call}")  # 디버깅 출력 (필요 없으면 삭제 가능)


                    except Exception as e:
                        print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 상세페이지 요청 실패: {e}")



                    
                    # 12개월 & 24개월 총 요금(m12_price, m24_price) 계산
                    promotion_period = ""
                    if not promotion_period or promotion_period == "평생":
                        m12_price = sale_price * 12
                        m24_price = sale_price * 24
                    elif "개월" in promotion_period:
                        match = re.search(r"(\d+)", promotion_period)
                        months = int(match.group(1)) if match else 0

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
                    
                    

                    # ✅ DTO 생성
                    dto = PlanData(
                        uuid=uuid,
                        mno=mno,
                        telecom=SiteTargetListType.LIIVM_LIST.value,
                        company_id=SiteTargetListIDType.LIIVM_LIST.value,
                        url=detail_url,
                        plan_type=plan_type,
                        plan_name=plan_name,
                        data=data,
                        daily_data=daily_data,
                        voice_call=voice_call,
                        message=message,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        after_price=after_price,
                        qos=qos,
                        business_name='(주)국민은행',
                        buga_call=buga_call,
                        m12_price=m12_price,
                        m24_price=m24_price,
                        benefit="",
                        freebies="",
                        etc="",
                        promotion_period=promotion_period,
                        plan_code="",
                        combination=""
                    )

                    result.append(dto)
                    success_count += 1

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

            is_end = True

        except Exception as e:
            error_count += 1
            print(f"{TAG} [ERROR] 전체 실행 중 오류 발생: {e}")
            traceback.print_exc()

        finally:
            if browser:
                print(f"{TAG} Selenium 드라이버 종료")
                browser.quit()  # ✅ 브라우저 안전 종료

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
