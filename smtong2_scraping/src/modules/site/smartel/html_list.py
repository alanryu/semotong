# from selenium import webdriver
# from selenium.webdriver.chrome.service import Service
# from selenium.webdriver.chrome.options import Options
# from webdriver_manager.chrome import ChromeDriverManager
# from selenium.webdriver.common.by import By
# from selenium.webdriver.support.ui import WebDriverWait
# from selenium.webdriver.support import expected_conditions as EC
# import os
# import traceback
# import re
# from modules.dto.input_queue_dto import PlanData
# from bs4 import BeautifulSoup
# import json
# from modules.site.target import SiteTargetListType, SiteTargetListIDType
# import time

# class SmartelListAction:
#     def __init__(self):
#         # WebDriver 설정
#         self.chrome_options = Options()
#         self.chrome_options.add_argument("--headless")
#         self.chrome_options.add_argument("--no-sandbox")
#         self.chrome_options.add_argument("--disable-dev-shm-usage")
#         self.service = Service(ChromeDriverManager().install())

#         # 결과 저장 디렉토리 생성
#         self.output_dir = "./response_files"
#         os.makedirs(self.output_dir, exist_ok=True)

#     def save_html_with_webdriver(self, url: str) -> str:
#         """
#         WebDriver를 사용하여 URL의 HTML을 반환
#         """
#         driver = None
#         try:
#             # WebDriver 실행
#             driver = webdriver.Chrome(service=self.service, options=self.chrome_options)

#             # 페이지 로딩 타임아웃 설정 (기본 120초 → 600초)
#             driver.set_page_load_timeout(600)
#             try:
#                 driver.command_executor._conn._conn.timeout = 600  # Remote Connection Timeout 연장
#             except Exception:
#                 pass
            
#             # URL 접속
#             driver.get(url)
            
#             # 페이지 내 요금제 리스트 ul 요소가 로드될 때까지 최대 60초 대기
#             try:
#                 WebDriverWait(driver, 60).until(
#                     EC.presence_of_element_located((By.TAG_NAME, "ul"))
#                 )
#             except:
#                 print(f"요소 로딩 대기 실패: {url}")

#             # 페이지 소스 반환
#             page_source = driver.page_source

#             #print(f"HTML 로드 완료: {url}")
#             return page_source
#         except Exception as e:
#             print(f"오류 발생 - URL: {url}")
#             traceback.print_exc()
#             return ""
#         finally:
#             if driver: 
#                 driver.quit()

#     def extract_plan_data(self, ul_html: str) -> list[PlanData]:
#         """
#         HTML에서 요금제 데이터를 추출하여 PlanData 리스트 반환
#         """
#         soup = BeautifulSoup(ul_html, 'html.parser')
#         plans = []
#         processed_urls = set()  # 중복 제거를 위한 set
#         combination_uuids = []  # 결합 상품인 uuid들 저장
#         skipped_urls = []  # 스킵된 URL들을 추적

#         # phoneplan 리스트에서 개별 요금제 링크들을 찾기
#         plan_links = soup.find_all('a', href=lambda href: href and '/phoneplan/' in href)

#         # 첫 번째 패스: combination 정보 수집
#         for link_element in plan_links:
#             href = link_element.get("href", "")
#             if "/phoneplan/" in href:
#                 plan_id = href.split('/phoneplan/')[-1].split('?')[0]
#                 uuid = f"SMARTEL_{plan_id}"

#                 # combination 확인해서 배열에 추가
#                 combination_element = link_element.find('button')

#                 if combination_element and '결합' in combination_element.get_text():
#                     combination_uuids.append(uuid)


#         for link_element in plan_links:
#             try:
#                 # href에서 uuid와 상세페이지 URL 생성
#                 href = link_element.get("href", "")
#                 if "/phoneplan/" in href:
#                     plan_id = href.split('/phoneplan/')[-1].split('?')[0]
#                     detail_url = f"https://smartel.kr{href.split('?')[0]}"  # 쿼리 파라미터 제거

#                     # 이미 처리한 URL인지 확인
#                     if detail_url in processed_urls:
#                         continue
#                     processed_urls.add(detail_url)

#                     uuid = f"SMARTEL_{plan_id}"
#                     print(f"Processing: {detail_url}")  # 로그 추가


#                     # 상세페이지 크롤링
#                     detail_html = self.save_html_with_webdriver(detail_url)
#                     if not detail_html:
#                         print(f"HTML 로드 실패로 스킵: {detail_url}")
#                         skipped_urls.append(detail_url)
#                         time.sleep(3)
#                         continue



#                     if detail_html:
#                         detail_soup = BeautifulSoup(detail_html, 'html.parser')

#                         # mno 추출 - 특정 span 태그에서 통신사 정보 찾기
#                         mno = ""
#                         mno_element = detail_soup.find('span', class_='undefined')
#                         if mno_element:
#                             mno_text = mno_element.get_text(strip=True)
#                             if mno_text == "LGU+":
#                                 mno = "LGU"
#                             else:
#                                 mno = mno_text  # KT, SKT 그대로 사용
#                         # plan_name 추출 - h2 태그에서 요금제명 찾기
#                         plan_name = ""
#                         plan_name_element = detail_soup.find('h2', class_='text-18')
#                         if plan_name_element:
#                             plan_name = plan_name_element.get_text(strip=True)

#                         data = ""
#                         data_element = detail_soup.find('dt', class_='font-bold')
#                         if data_element:
#                             data_text = data_element.get_text(strip=True)
#                             # 정규식을 사용해서 첫 번째 숫자+GB만 추출
#                             import re
#                             match = re.search(r'\d+GB', data_text)
#                             if match:
#                                 data = match.group(0)
#                             else:
#                                 data = data_text  # 매칭되지 않으면 원본 텍스트 사용



#                         # voice_call과 message 추출 - 전화 아이콘과 문자 아이콘 옆의 h4 태그들에서 추출
#                         voice_call = ""
#                         message = ""

#                         # 전화 아이콘이 있는 img 태그를 찾기
#                         phone_icon = detail_soup.find('img', src='/icons/tele_icon_gray.svg')
#                         if phone_icon:
#                             # 해당 img의 부모 요소에서 h4 태그 찾기
#                             parent_div = phone_icon.find_parent('div')
#                             if parent_div:
#                                 voice_h4 = parent_div.find('h4')
#                                 if voice_h4:
#                                     voice_call = voice_h4.get_text(strip=True)
#                             # 기존 로직에서 못 찾았으면 새로운 구조 확인 (부모 div → dt 찾기)
#                             if not voice_call:
#                                 flex_div = phone_icon.find_parent('div', class_='flex items-center')
#                                 if flex_div:
#                                     voice_dt = flex_div.find('dt')
#                                     if voice_dt:
#                                         voice_call = voice_dt.get_text(strip=True)        

#                         # 문자 아이콘이 있는 img 태그를 찾기
#                         text_icon = detail_soup.find('img', src='/icons/text_icon_gray.svg')
#                         if text_icon:
#                             # 해당 img의 부모 요소에서 h4 태그 찾기
#                             parent_span = text_icon.find_parent('span')
#                             if parent_span:
#                                 # 해당 span 다음에 오는 h4 태그 찾기
#                                 message_h4 = parent_span.find_next_sibling('h4')
#                                 if message_h4:
#                                     message = message_h4.get_text(strip=True)


#                         # message 추출 부분 다음에 추가

#                         # sale_price 추출 - h3 태그에서 가격 정보 찾기
#                         sale_price = 0
#                         price_element = detail_soup.find('h3', class_='mt-1')
#                         if price_element:
#                             price_text = price_element.get_text(strip=True)
#                             # 정규식을 사용해서 숫자만 추출 (월xxxx원에서 숫자 부분)
#                             import re
#                             match = re.search(r'월(\d+(?:,\d{3})*)원', price_text)
#                             if match:
#                                 # 쉼표 제거하고 정수로 변환
#                                 sale_price = int(match.group(1).replace(',', ''))


#                         # after_price 추출 - 프로모션 할인 후 정상가 정보 찾기
#                         after_price = 0
#                         after_price_element = detail_soup.find('p', class_='hidden xl:mt-1 xl:flex')
#                         if after_price_element:
#                             after_price_text = after_price_element.get_text(strip=True)
#                             # 정규식을 사용해서 숫자 부분 추출
#                             import re
#                             if "평생할인" in after_price_text:
#                                 # "정상가 17,600원 / 평생할인 프로모션" 같은 경우 sale_price와 동일하게 설정
#                                 after_price = sale_price
#                             else:
#                                 # "7개월 프로모션 할인 후 정상가 25,300 원으로 변경" 같은 경우
#                                 match = re.search(r'정상가\s*(\d+(?:,\d{3})*)\s*원', after_price_text)
#                                 if match:
#                                     # 쉼표 제거하고 정수로 변환
#                                     after_price = int(match.group(1).replace(',', ''))

#                         normal_price = after_price



#                         # after_price 추출 부분 다음에 추가

#                         # promotion_period 추출 - 프로모션 기간 정보 찾기
#                         promotion_period = ""
#                         promotion_element = detail_soup.find('p', class_='hidden xl:mt-1 xl:flex')
#                         if promotion_element:
#                             promotion_text = promotion_element.get_text(strip=True)
#                             # 정규식을 사용해서 프로모션 기간 추출
#                             import re
#                             if "평생할인" in promotion_text:
#                                 promotion_period = "평생"
#                             else:
#                                 # "7개월 프로모션 할인" 패턴에서 숫자+개월 추출
#                                 match = re.search(r'(\d+개월)', promotion_text)
#                                 if match:
#                                     promotion_period = match.group(1)

#                         url = detail_url

#                         # plan_type 추출 - plan_name에서 5G 문자열 확인
#                         plan_type = ""
#                         if "5G" in plan_name:
#                             plan_type = "5G"
#                         else:
#                             plan_type = "LTE"

#                         # qos 추출 - 속도 제한 정보 찾기
#                         qos = ""
#                         qos_element = detail_soup.find('td', class_='border border-solid border-sub-02 py-6')
#                         if qos_element:
#                             qos_text = qos_element.get_text(strip=True)
#                             # 정규식을 사용해서 Mbps 또는 원/MB 패턴 추출
#                             import re
#                             mbps_match = re.search(r'(\d+(?:\.\d+)?Mbps)', qos_text)
#                             price_match = re.search(r'(\d+(?:\.\d+)?원/MB)', qos_text)

#                             if mbps_match:
#                                 qos = mbps_match.group(1)
#                             elif price_match:
#                                 qos = price_match.group(1)



#                         combination = uuid in combination_uuids


#                         # freebies 추출 - 요금제 혜택 정보 찾기
#                         freebies = ""
#                         freebies_element = detail_soup.find('p', class_='text-14 font-light smd:text-16 md:text-18')
#                         if freebies_element:
#                             freebies_text = freebies_element.get_text(strip=True)
#                             # 괄호 제거
#                             if freebies_text.startswith('(') and freebies_text.endswith(')'):
#                                 freebies = freebies_text[1:-1]  # 첫 번째와 마지막 문자 제거
#                             else:
#                                 freebies = freebies_text


#                         # buga_call 추출 - 부가통화 정보 찾기 (두 번째 매칭되는 것)
#                         buga_call = ""
#                         # tbody 내의 모든 td 태그에서 부가통화 정보 찾기
#                         td_elements = detail_soup.find_all('td', class_='border border-solid border-sub-02 py-6')
#                         matches = []
#                         for td in td_elements:
#                             td_text = td.get_text(strip=True)
#                             # 정규식을 사용해서 "부가통화 xxx분" 패턴에서 숫자+분 추출
#                             import re
#                             match = re.search(r'부가통화\s*(\d+분)', td_text)
#                             if match:
#                                 matches.append(match.group(1))

#                         # 두 번째 매칭되는 것 사용
#                         if len(matches) >= 2:
#                             buga_call = matches[1]  # 두 번째 것
#                         elif len(matches) == 1:
#                             buga_call = matches[0]  # 하나만 있으면 그것 사용



#                         # daily_data 추출 - 매일 제공 데이터 정보 찾기
#                         daily_data = ""
#                         # tr 태그에서 "매일" 패턴 찾기
#                         tr_elements = detail_soup.find_all('tr', class_='text-center')
#                         for tr in tr_elements:
#                             tr_text = tr.get_text(strip=True)
#                             # 정규식을 사용해서 "매일 xxxGB" 패턴에서 숫자+GB 추출
#                             import re
#                             match = re.search(r'매일\s*(\d+GB)', tr_text)
#                             if match:
#                                 daily_data = match.group(1)
#                                 break  # 첫 번째 매칭되는 것만 사용

#                         # m12_price 계산 - 12개월 총 요금
#                         m12_price = 0
#                         if promotion_period and promotion_period != "":
#                             if "평생할인" in promotion_period:
#                                 # 평생할인인 경우 12개월 모두 sale_price
#                                 m12_price = 12 * sale_price
#                             else:
#                                 # "7개월" 같은 경우
#                                 import re
#                                 match = re.search(r'(\d+)', promotion_period)
#                                 if match:
#                                     promo_months = int(match.group(1))
#                                     if promo_months >= 12:
#                                         # 프로모션 기간이 12개월 이상이면 모두 sale_price
#                                         m12_price = 12 * sale_price
#                                     else:
#                                         # 프로모션 기간 + 나머지 기간
#                                         remaining_months = 12 - promo_months
#                                         m12_price = (promo_months * sale_price) + (remaining_months * normal_price)
#                                 else:
#                                     # 숫자를 찾을 수 없으면 normal_price로 계산
#                                     m12_price = 12 * normal_price
#                         else:
#                             # promotion_period가 없으면 normal_price로 계산
#                             m12_price = 12 * normal_price

#                         m24_price = m12_price + (normal_price * 12)


#                 else:
#                     continue

#                 dto = PlanData(
#                     uuid=uuid,
#                     telecom=SiteTargetListType.SMARTEL_LIST.value,
#                     company_id=SiteTargetListIDType.SMARTEL_LIST.value,
#                     mno=mno,
#                     plan_name=plan_name,
#                     data=data,
#                     voice_call=voice_call,
#                     message=message,
#                     sale_price=sale_price,
#                     after_price=after_price,
#                     normal_price=normal_price,
#                     promotion_period=promotion_period,
#                     url=url,
#                     plan_type=plan_type,
#                     qos=qos,
#                     business_name="(주)스마텔",
#                     combination=combination,
#                     freebies=freebies,
#                     etc="",
#                     benefit="",
#                     buga_call=buga_call,
#                     plan_code='',
#                     daily_data=daily_data,
#                     m12_price=m12_price,
#                     m24_price=m24_price,
#                 )
#                 plans.append(dto)
#                 time.sleep(2)

#             except Exception as e:
#                 print(f"URL 처리 중 오류 발생, 스킵함: {detail_url}")
#                 print(f"오류 내용: {str(e)}")
#                 skipped_urls.append(detail_url)
#                 # traceback.print_exc()  # 선택적으로 주석 해제하여 상세한 오류 정보 확인
#                 continue  # 다음 URL로 계속 진행

#             print(f"\n=== 처리 결과 요약 ===")
#             print(f"총 처리된 요금제: {len(plans)}개")
#             print(f"스킵된 URL: {len(skipped_urls)}개")
#             if skipped_urls:
#                 print("스킵된 URL 목록:")
#                 for url in skipped_urls:
#                     print(f"  - {url}")

#         return plans

#     def root(self, page: int = 1) -> tuple[list[PlanData], bool]:
#         """
#         모든 요금제를 한 번에 가져와 저장 및 PlanData 추출
#         """
#         base_url = "https://smartel.kr/phoneplan"  # ✅ 한 번만 요청
#         result = []

#         try:
#             html_content = self.save_html_with_webdriver(base_url)  # ✅ 전체 요금제 가져오기

#             if html_content:
#                 soup = BeautifulSoup(html_content, 'html.parser')
#                 ul_element = soup.select_one("ul.mt-5.w-full")
#                 if ul_element:
#                     output_filename = os.path.join(self.output_dir, f"list_smartel_all.html")  # ✅ 단일 파일 저장
#                     with open(output_filename, "w", encoding="utf-8") as file:
#                         file.write(ul_element.prettify())

#                     plans = self.extract_plan_data(ul_element.prettify())  # ✅ 한 번만 실행
#                     result.extend(plans)
#                 else:
#                     print(f"요금제 목록을 찾을 수 없습니다.")

#         except Exception as e:
#             print("root 메서드 실행 중 오류 발생")
#             traceback.print_exc()

#         is_end = True
#         return result, is_end


import os
import time
import re
import traceback
import requests
from bs4 import BeautifulSoup
from modules.dto.input_queue_dto import PlanData
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[Smartel]'

class SmartelListAction:
    def __init__(self):
        self.base_url = "https://smartel.kr/phoneplan"
        self.output_dir = "./response_files"
        os.makedirs(self.output_dir, exist_ok=True)
        self.headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                          "AppleWebKit/537.36 (KHTML, like Gecko) "
                          "Chrome/128.0.0.0 Safari/537.36"
        }

    def get_html(self, url: str) -> str:
        """
        requests로 HTML 가져오기
        """
        try:
            req_start = time.time()
            response = requests.get(url, headers=self.headers, timeout=60)
            response.raise_for_status()
            html = response.text
            req_elapsed = time.time() - req_start
            print(f"{TAG} HTTP 응답 완료: {url} (length={len(html)}, {req_elapsed:.1f}초)")
            return html
        except Exception as e:
            print(f"{TAG} [ERROR] HTML 가져오기 실패: {url} -> {e}")
            return ""

    def extract_plan_data(self, html: str) -> list[PlanData]:
        """
        HTML에서 요금제 데이터를 추출하여 PlanData 리스트 반환
        """
        soup = BeautifulSoup(html, 'html.parser')
        plans = []
        processed_urls = set()
        success_count = 0
        skip_count = 0
        error_count = 0

        plan_links = soup.find_all('a', href=lambda href: href and '/phoneplan/' in href)
        total_count = len(plan_links)
        print(f"{TAG} 파싱할 링크 수: {total_count}건")

        for idx, link in enumerate(plan_links):
            try:
                href = link.get("href", "")
                plan_id = href.split('/phoneplan/')[-1].split('?')[0]
                detail_url = f"https://smartel.kr/phoneplan/{plan_id}"

                if detail_url in processed_urls:   # 중복 URL 방지
                    print(f"{TAG} [{idx+1}/{total_count}] 중복 URL 스킵: {detail_url}")
                    skip_count += 1
                    continue
                processed_urls.add(detail_url)

                print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 요청: {detail_url}")
                detail_start = time.time()
                detail_html = self.get_html(detail_url)
                detail_elapsed = time.time() - detail_start
                if not detail_html:
                    print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 HTML 비어있음, 스킵")
                    skip_count += 1
                    continue
                print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 응답 완료 ({detail_elapsed:.1f}초)")

                detail_soup = BeautifulSoup(detail_html, 'html.parser')

                # mno 추출
                mno = ""
                mno_element = detail_soup.find('span', class_='undefined')
                if mno_element:
                    mno_text = mno_element.get_text(strip=True)
                    mno = "LGU" if mno_text == "LGU+" else mno_text

                # plan_name 추출
                plan_name_element = detail_soup.find('h2', class_='text-18')
                plan_name = plan_name_element.get_text(strip=True) if plan_name_element else ""

                # data 추출
                data = ""
                data_element = detail_soup.find('dt', class_='font-bold')
                if data_element:
                    match = re.search(r'\d+GB', data_element.get_text(strip=True))
                    data = match.group(0) if match else data_element.get_text(strip=True)

                # voice_call, message 추출
                voice_call, message = "", ""
                phone_icon = detail_soup.find('img', src='/icons/tele_icon_gray.svg')
                if phone_icon:
                    parent = phone_icon.find_parent('div')
                    voice_h4 = parent.find('h4') if parent else None
                    voice_call = voice_h4.get_text(strip=True) if voice_h4 else ""

                    # h4가 없으면 dt 보조 추출
                    if not voice_call:
                        flex_div = phone_icon.find_parent('div', class_='flex items-center')
                        if flex_div:
                            voice_dt = flex_div.find('dt')
                            if voice_dt:
                                voice_call = voice_dt.get_text(strip=True)

                text_icon = detail_soup.find('img', src='/icons/text_icon_gray.svg')
                if text_icon:
                    parent = text_icon.find_parent('span')
                    message_h4 = parent.find_next_sibling('h4') if parent else None
                    message = message_h4.get_text(strip=True) if message_h4 else ""

                # sale_price 추출
                sale_price = 0
                price_element = detail_soup.find('h3', class_='mt-1')
                if price_element:
                    match = re.search(r'월(\d+(?:,\d{3})*)원', price_element.get_text(strip=True))
                    if match:
                        sale_price = int(match.group(1).replace(',', ''))

                # after_price 추출
                after_price = sale_price
                after_element = detail_soup.find('p', class_='hidden xl:mt-1 xl:flex')
                if after_element:
                    text = after_element.get_text(strip=True)
                    match = re.search(r'정상가\s*(\d+(?:,\d{3})*)\s*원', text)
                    if match:
                        after_price = int(match.group(1).replace(',', ''))

                normal_price = after_price

                # promotion_period 추출
                promotion_period = "평생"
                if after_element:
                    text = after_element.get_text(strip=True)
                    if "평생할인" not in text:
                        match = re.search(r'(\d+개월)', text)
                        if match:
                            promotion_period = match.group(1)

                # plan_type 추출
                plan_type = "5G" if "5G" in plan_name else "LTE"

                # qos 추출
                qos = ""
                qos_element = detail_soup.find('td', class_='border border-solid border-sub-02 py-6')
                if qos_element:
                    mbps_match = re.search(r'(\d+(?:\.\d+)?Mbps)', qos_element.get_text(strip=True))
                    price_match = re.search(r'(\d+(?:\.\d+)?원/MB)', qos_element.get_text(strip=True))
                    qos = mbps_match.group(1) if mbps_match else (price_match.group(1) if price_match else "")

                # combination 추출
                combination = False
                button_element = link.find('button')
                if button_element and '결합' in button_element.get_text():
                    combination = True

                # freebies 추출
                freebies = ""
                freebies_element = detail_soup.find('p', class_='text-14 font-light smd:text-16 md:text-18')
                if freebies_element:
                    text = freebies_element.get_text(strip=True)
                    freebies = text[1:-1] if text.startswith('(') and text.endswith(')') else text

                # buga_call 추출
                buga_call = ""
                td_elements = detail_soup.find_all('td', class_='border border-solid border-sub-02 py-6')
                matches = []
                for td in td_elements:
                    match = re.search(r'부가통화\s*(\d+분)', td.get_text(strip=True))
                    if match:
                        matches.append(match.group(1))
                if len(matches) >= 2:
                    buga_call = matches[1]
                elif matches:
                    buga_call = matches[0]

                # daily_data 추출
                daily_data = ""
                tr_elements = detail_soup.find_all('tr', class_='text-center')
                for tr in tr_elements:
                    match = re.search(r'매일\s*(\d+GB)', tr.get_text(strip=True))
                    if match:
                        daily_data = match.group(1)
                        break

                # m12_price, m24_price 계산
                m12_price = m24_price = 12 * sale_price
                if promotion_period != "평생":
                    match = re.search(r'(\d+)', promotion_period)
                    if match:
                        promo_months = int(match.group(1))
                        # 프로모션 개월수 반영한 계산
                        if promo_months < 12:
                            m12_price = promo_months * sale_price + (12 - promo_months) * normal_price
                            m24_price = promo_months * sale_price + (24 - promo_months) * normal_price
                        else:
                            m24_price = 24 * sale_price

                print(f"{TAG} [{idx+1}/{total_count}] 파싱 완료: {plan_name}")

                dto = PlanData(
                    uuid=f"SMARTEL_{plan_id}",
                    telecom=SiteTargetListType.SMARTEL_LIST.value,
                    company_id=SiteTargetListIDType.SMARTEL_LIST.value,
                    mno=mno,
                    plan_name=plan_name,
                    data=data,
                    voice_call=voice_call,
                    message=message,
                    sale_price=sale_price,
                    after_price=after_price,
                    normal_price=normal_price,
                    promotion_period=promotion_period,
                    url=detail_url,
                    plan_type=plan_type,
                    qos=qos,
                    business_name="(주)스마텔",
                    combination=combination,
                    freebies=freebies,
                    etc="",
                    benefit="",
                    buga_call=buga_call,
                    plan_code='',
                    daily_data=daily_data,
                    m12_price=m12_price,
                    m24_price=m24_price
                )
                plans.append(dto)
                success_count += 1
                time.sleep(1)

            except Exception as e:
                print(f"{TAG} [{idx+1}/{total_count}] [ERROR] URL 처리 실패: {href} -> {e}")
                traceback.print_exc()
                error_count += 1
                continue

        print(f"{TAG} extract_plan_data 완료: 성공 {success_count}건, 스킵 {skip_count}건, 에러 {error_count}건")
        return plans

    def root(self, page: int = 1) -> tuple[list[PlanData], bool]:
        """
        Smartel 전체 요금제를 가져와 PlanData 리스트 반환
        """
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        result = []
        try:
            html = self.get_html(self.base_url)
            if not html:
                print(f"{TAG} 목록 페이지 HTML 비어있음, 종료")
                return result, True

            soup = BeautifulSoup(html, 'html.parser')
            ul_element = soup.select_one("ul.mt-5.w-full")
            if ul_element:
                output_file = os.path.join(self.output_dir, "list_smartel_all.html")
                with open(output_file, "w", encoding="utf-8") as f:
                    f.write(ul_element.prettify())

                plans = self.extract_plan_data(ul_element.prettify())
                result.extend(plans)
            else:
                print(f"{TAG} 요금제 목록(ul.mt-5.w-full)을 찾을 수 없음")

        except Exception as e:
            print(f"{TAG} [ERROR] root 실행 중 오류: {e}")
            traceback.print_exc()

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, True
