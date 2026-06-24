from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import os
import traceback
import re
from bs4 import BeautifulSoup
from modules.dto.input_queue_dto import PlanData
from modules.site.target import SiteTargetListType, SiteTargetListIDType

class WinnerstelListAction:
    def __init__(self):
        # WebDriver 설정
        self.chrome_options = Options()
        self.chrome_options.add_argument("--headless")
        self.chrome_options.add_argument("--no-sandbox")
        self.chrome_options.add_argument("--disable-dev-shm-usage")
        self.service = Service(ChromeDriverManager().install())

        # 결과 저장 디렉토리 생성
        self.output_dir = "./response_files"
        os.makedirs(self.output_dir, exist_ok=True)

    def save_html_with_webdriver(self, url: str) -> str:
        """
        WebDriver를 사용하여 URL의 HTML을 반환하며 <div class="product"> 내부의 코드만 저장
        """
        try:
            driver = webdriver.Chrome(service=self.service, options=self.chrome_options)

            # URL 접속
            driver.get(url)

            # 페이지 소스 반환
            page_source = driver.page_source

            #print(f"HTML 로드 완료: {url}")

            # BeautifulSoup으로 HTML 파싱
            soup = BeautifulSoup(page_source, 'html.parser')

            # <div class="product"> 태그만 추출
            product_content = soup.find("div", class_="product")
            if product_content:
                formatted_product = product_content.prettify()

                # <div class="product"> 내용만 파일에 저장
                output_file = os.path.join(self.output_dir, f"list_winnerstel_{url.split('page=')[-1]}.html")
                with open(output_file, 'w', encoding='utf-8') as f:
                    f.write(formatted_product)

                #print(f"<div class='product'> 저장 완료: {output_file}")
            else:
                print(f"<div class='product'> 태그를 찾을 수 없음: {url}")

            return page_source
        except Exception as e:
            print(f"오류 발생 - URL: {url}")
            traceback.print_exc()
            return ""
        finally:
            driver.quit()

    def extract_plan_data(self, product_html: str) -> list[PlanData]:
        """
        HTML에서 요금제 데이터를 추출하여 PlanData 리스트 반환
        """
        soup = BeautifulSoup(product_html, 'html.parser')
        plans = []

        # <tr> 태그를 순회하며 데이터 추출
        for row in soup.select("tbody > tr"):
            try:
                # MNO, plan_type, plan_name, uuid, URL 추출
                sbj_td = row.find("td", class_="sbj")
                if sbj_td:
                    # MNO 값 추출 (prcon_dat4 또는 prcon_dat1 검사)
                    mno_div = sbj_td.find("div", class_="prcon_dat4") or sbj_td.find("div", class_="prcon_dat1")
                    mno = mno_div.text.strip() if mno_div else "N/A"
                    if mno == "LGU+":
                        mno = "LGU"

                    plan_type = sbj_td.find("div", class_="prcon_dat3")
                    plan_type = plan_type.text.strip() if plan_type else "N/A"

                    plan_name_tag = sbj_td.find("a")
                    plan_name = plan_name_tag.text.strip() if plan_name_tag else "N/A"
                    uuid = (
                        f"winnerstel_{plan_name_tag['href'].split('no=')[-1].split('&')[0]}"
                        if plan_name_tag and plan_name_tag.has_attr("href")
                        else "N/A"
                    )
                    url = (
                        f"https://www.idowell.co.kr/home/contents.php?sn1=2&sn2=1&spn=view&no={plan_name_tag['href'].split('no=')[-1].split('&')[0]}"
                        if plan_name_tag and plan_name_tag.has_attr("href")
                        else "N/A"
                    )
                else:
                    continue  # 필수 데이터가 없으면 다음 row로 이동

                # 가격 정보 추출
                org_td = row.find("td", class_="org")
                if org_td:
                    normal_price_div = org_td.find("div", class_="product_txt_s2")
                    normal_price = (
                        normal_price_div.text.strip().replace(",", "").replace("원", "")
                        if normal_price_div
                        else "0"
                    )
                    sale_price = (
                        org_td.text.strip().split()[-1].replace(",", "").replace("원", "")
                        if org_td.text.strip()
                        else "0"
                    )
                else:
                    normal_price = "0"
                    sale_price = "0"

                # Voice call, message, data, qos, buga_call 추출
                voice_call_td = row.find_all("td")[2]
                voice_call_text = voice_call_td.text.strip() if voice_call_td else ""
                if "(" in voice_call_text:
                    voice_call, buga_call = voice_call_text.split("(")
                    voice_call = voice_call.strip()
                    buga_call = buga_call.strip(")")
                else:
                    voice_call = voice_call_text
                    buga_call = ""

                message_td = row.find_all("td")[3]
                message = message_td.text.strip() if message_td else ""

              # 데이터 추출 부분 수정
                data_td = row.find_all("td")[4]
                data_text = data_td.text.strip() if data_td else ""

                # 괄호 안의 qos 값 추출
                qos_match = re.search(r"\((.*?)\)", data_text)
                qos = qos_match.group(1).strip() if qos_match else ""

                # "안심차단" 또는 "자동차단"이면 qos를 빈 값으로 설정
                if qos in ["안심차단", "자동차단"]:
                    qos = ""

                # 괄호 안의 값을 제거한 데이터 값
                data_text = re.sub(r"\(.*?\)", "", data_text).strip()

                # "일XGB" 패턴 찾기
                daily_data_match = re.search(r"일\d+GB", data_text)

                # "+"가 있는 경우 분리
                if "+" in data_text:
                    parts = data_text.split("+")
                    data = ""
                    daily_data = ""
                    
                    for part in parts:
                        part = part.strip()
                        if "일" in part:  # "일XGB" 패턴은 daily_data로 저장
                            daily_data = part.replace("일", "").strip()
                        else:  # 일반 데이터는 data로 저장
                            data = part
                else:
                    if daily_data_match:
                        daily_data = daily_data_match.group().replace("일", "").strip()
                        data = ""  # 일반 데이터가 없고 일 데이터만 있는 경우
                    else:
                        data = data_text
                        daily_data = ""

                # "무제한"이면 9999GB로 설정
                if data == "무제한":
                    data = "9999GB"

                # "400Kbps"이면 9999GB로 설정하고 qos도 "400Kbps"로 설정
                if "400Kbps" in data_text:
                    data = "9999GB"
                    qos = "400Kbps"

                # `qos` 값에서 "+" 제거
                if qos.startswith("+"):
                    qos = qos[1:]


                    
                # 추가 WebDriver를 통해 promotion_period 추출
                promotion_period = ""
                try:
                    driver = webdriver.Chrome(service=self.service, options=self.chrome_options)
                    driver.get(url)
                    detail_page = driver.page_source
                    detail_soup = BeautifulSoup(detail_page, 'html.parser')
                    promotion_span = detail_soup.find("span", string=lambda text: text and "기본료" in text and "할인이벤트" in text)
                    if promotion_span:
                        match = re.search(r"\d+개월", promotion_span.text)
                        promotion_period = match.group(0) if match else ""
                except Exception as e:
                    print("promotion_period 추출 중 오류 발생:", e)
                finally:
                    driver.quit()
                    
                if normal_price == "0":
                    normal_price = sale_price
                
                if "부가" in buga_call:
                    buga_call = buga_call.replace("부가", "").strip()
                    
                after_price = normal_price
                
                # 할인 기간에서 숫자만 추출 (promotion_period가 없으면 기본값 0)
                if promotion_period and re.search(r"\d+", promotion_period):
                    promotion_period_value = int(re.search(r"\d+", promotion_period).group())
                elif promotion_period == "평생":
                    promotion_period_value = -1  # 특별한 값으로 설정
                else:
                    promotion_period_value = 0

                # m12_price 및 m24_price 계산
                if promotion_period_value > 0:  # 정상적인 숫자 값이 있는 경우
                    m12_price = (12 - promotion_period_value) * int(sale_price) + (promotion_period_value * int(after_price))
                    m24_price = (24 - promotion_period_value) * int(sale_price) + (promotion_period_value * int(after_price))
                elif promotion_period_value == -1:  # "평생"인 경우
                    m12_price = 12 * int(sale_price)
                    m24_price = 24 * int(sale_price)
                else:  # promotion_period가 빈 값이거나 None인 경우
                    m12_price = 12 * int(after_price)
                    m24_price = 24 * int(after_price)
                    
                    
                                
                # PlanData 객체 생성
                dto = PlanData(
                    uuid=uuid,
                    telecom=SiteTargetListType.WINNERSTEL_LIST.value,
                    company_id=SiteTargetListIDType.WINNERSTEL_LIST.value,
                    mno=mno,
                    plan_name=plan_name,
                    data=data,
                    voice_call=voice_call,
                    message=message,
                    sale_price=sale_price,
                    after_price=normal_price,
                    normal_price=normal_price,
                    promotion_period=promotion_period,
                    url=url,
                    plan_type=plan_type,
                    qos=qos,
                    business_name="(주)위너스텔",
                    combination="",
                    freebies="",
                    etc="",
                    benefit="",
                    buga_call=buga_call,
                    plan_code = '',
                    daily_data=daily_data, # 일XGB 추가함 (일2GB, 일5GB)
                    m12_price=m12_price,
                    m24_price=m24_price,
                )
                plans.append(dto)

            except Exception as e:
                print("데이터 추출 중 오류 발생:", e)
                traceback.print_exc()

        return plans

    def root(self, page: int = 1) -> tuple[list[PlanData], bool]:
        """
        URL 범위를 순회하며 HTML 저장 및 PlanData 추출
        """
        base_url = "https://www.idowell.co.kr/home/contents.php?sn1=2&sn2=1&page={page_num}"
        result = []

        try:
            for page_num in range(1, 4):
                url = base_url.format(page_num=page_num)
                html_content = self.save_html_with_webdriver(url)

                if html_content:
                    product_html = BeautifulSoup(html_content, 'html.parser').find("div", class_="product")
                    if product_html:
                        plans = self.extract_plan_data(str(product_html))
                        result.extend(plans)

        except Exception as e:
            print("root 메서드 실행 중 오류 발생")
            traceback.print_exc()

        is_end = True
        return result, is_end
