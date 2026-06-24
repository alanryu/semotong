import os
import time
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from bs4 import BeautifulSoup
import traceback
import re
from modules.dto.input_queue_dto import PlanData  # 모듈 경로에 따라 수정 필요
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[KT]'

class KtListAction:
    @staticmethod
    def extract_numeric_value(raw_text: str) -> int:
        """
        문자열에서 숫자만 추출하여 정수로 반환합니다.
        Args:
            raw_text (str): 숫자를 포함한 문자열.
        Returns:
            int: 추출된 정수 값. 유효하지 않으면 0 반환.
        """
        return int(re.sub(r"[^\d]", "", raw_text)) if raw_text and re.sub(r"[^\d]", "", raw_text).isdigit() else 0

    @staticmethod
    def clean_title(title: str) -> str:
        """
        제목 문자열에서 모든 공백을 제거합니다.
        Args:
            title (str): 원본 제목 문자열.
        Returns:
            str: 공백이 제거된 제목 문자열.
        """
        return title.replace(" ", "")

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        """
        KT 요금제 데이터를 가져와 PlanData DTO 리스트로 변환합니다.
        Args:
            page (int): 가져올 페이지 번호.
        Returns:
            tuple[list[PlanData], bool]: PlanData 리스트와 데이터 종료 여부.
        """
        print(f"{TAG} 스크래핑 시작")
        total_start = time.time()
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0

        # Selenium 브라우저 옵션 설정
        options = webdriver.ChromeOptions()
        options.add_argument('--headless=new')  # 헤드리스 모드로 실행
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--no-zygote')
        options.add_argument('--disable-gpu')
        options.add_argument('--disable-blink-features=AutomationControlled')

        try:
            # ChromeDriverManager를 이용한 WebDriver 초기화
            print(f"{TAG} Selenium 드라이버 생성 중")
            browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
            print(f"{TAG} Selenium 드라이버 생성 완료")
            url = "https://shop.kt.com/direct/directUsim.do"
            t0 = time.time()
            print(f"{TAG} 페이지 로드 시작: {url}")
            browser.get(url)
            print(f"{TAG} 페이지 로드 완료 ({time.time() - t0:.1f}초)")

            # BeautifulSoup을 이용해 HTML 파싱
            soup = BeautifulSoup(browser.page_source, "html.parser")
            target_div = soup.find("div", {"id": "swiper-backup"})
            
            response_dir = "./response_files"
            os.makedirs(response_dir, exist_ok=True)
            with open(os.path.join(response_dir, "kt_list.html"), "w", encoding="utf-8") as f:
                f.write(str(target_div))
            
            if target_div:
                # target_div 내의 모든 슬라이드 요소를 찾습니다.
                slides = target_div.find_all("div", {"class": "swiper-slide"})
                total_count = len(slides)
                print(f"{TAG} 파싱 대상 요금제 수: {total_count}건")
                for idx, slide in enumerate(slides):
                    try:
                        # 제목 추출 및 정리
                        title = slide.find("div", {"class": "title"}).get_text(strip=True)
                        uuid_title = self.clean_title(title)
                        print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {title}")

                        # 데이터 내용 추출
                        data_content = ""
                        data_div = slide.find("div", text="데이터")
                        if data_div:
                            data_content = data_div.find_next_sibling("div").get_text(strip=True)

                        # 요금제 유형 결정 (5G 또는 LTE)
                        # plan_type = "5G" if data_content.startswith("5G") else "LTE"
                        plan_type = "5G"

                        # 데이터 추출 (무제한 또는 GB 단위)
                        data = "무제한" if "무제한" in data_content else re.search(r"(\d+GB)", data_content).group(1) if re.search(r"(\d+GB)", data_content) else ""

                        # QoS 추출 (Mbps 또는 Kbps)
                        qos = re.search(r"(\d+\s*(?:Mbps|Kbps))", data_content).group(1) if re.search(r"(\d+\s*(?:Mbps|Kbps))", data_content) else ""

                        # 전화/문자 정보 추출
                        call_text = ""
                        call_div = slide.find("div", text="전화/문자")
                        if call_div:
                            call_text = call_div.find_next_sibling("div").get_text(strip=True)

                        
                        # 부가 혜택 추출
                        benefit = ""
                        benefit_list = slide.find("ul", {"class": "line-list"})
                        if benefit_list:
                            benefit = "\n".join(item.get_text(strip=True) for item in benefit_list.find_all("li"))
                            # 혜택 문자열 치환
                            benefit = benefit.replace("초이스 혜택", "초이스 혜택 : ")
                            benefit = benefit.replace("플러스 혜택", "플러스 혜택 : ")

                        # 총 가격 추출
                        total_price_raw = slide.find("div", {"class": "total"}).find("strong").get_text(strip=True)
                        total_price = self.extract_numeric_value(total_price_raw)
                        
                        if data=="무제한":
                            data ="9999GB"
                        
                        normal_price=total_price
                        sale_price=total_price
                        after_price=total_price
                        promotion_period=""

                         # 12개월 & 24개월 총 요금 계산
                        sale_price, after_price = int(sale_price), int(after_price)
                        if not promotion_period or promotion_period == "평생":
                            m12_price, m24_price = sale_price * 12, sale_price * 24
                        elif "개월" in promotion_period:
                            match = re.search(r"(\d+)", promotion_period)
                            months = int(match.group(1)) if match else 0

                            if months >= 24:
                                m12_price, m24_price = sale_price * 12, sale_price * 24
                            elif months >= 12:
                                m12_price = sale_price * 12
                                m24_price = (sale_price * months) + (after_price * (24 - months))
                            else:
                                m12_price = (sale_price * months) + (after_price * (12 - months))
                                m24_price = (sale_price * months) + (after_price * (24 - months))
                        else:
                            m12_price, m24_price = normal_price * 12, normal_price * 24

                        # daily_data 값을 항상 "0GB"로 설정
                        daily_data = "0GB"

                        # PlanData 객체 생성
                        plan_data = PlanData(
                            uuid=f"KT_{uuid_title}",
                            mno="KT",
                            telecom=SiteTargetListType.KT_LIST.value,
                            company_id=SiteTargetListIDType.KT_LIST.value,
                            url='https://shop.kt.com/direct/select.do?befIntmUsimUseYn=Y&isSaparDirectPage=U_USE_Y',
                            plan_type=plan_type,
                            plan_name=title,
                            data=data,
                            voice_call=call_text,
                            message=call_text,
                            normal_price=normal_price,
                            sale_price=normal_price, 
                            after_price=normal_price,
                            benefit=benefit,
                            qos=qos,
                            business_name="(주)케이티",                           
                            combination=True,
                            freebies="",
                            etc="",
                            promotion_period=promotion_period,
                            buga_call="",
                            plan_code = '',
                            daily_data=daily_data,
                            m12_price=m12_price,
                            m24_price=m24_price,
                        )

                        # 결과 리스트에 PlanData 추가
                        result.append(plan_data)
                        success_count += 1
                    except Exception as e:
                        error_count += 1
                        print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                        traceback.print_exc()

        except Exception as e:
            error_count += 1
            print(f"{TAG} [ERROR] 전체 실행 중 오류 발생: {e}")
            traceback.print_exc()
        finally:
            # 브라우저 종료
            print(f"{TAG} Selenium 드라이버 종료")
            browser.quit()

        # 데이터 처리가 끝났음을 나타냄
        is_end = True
        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
