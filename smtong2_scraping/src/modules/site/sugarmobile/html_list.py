
# import json
# import traceback
# from bs4 import BeautifulSoup
# import requests
# import re
# from modules.site.target import SiteTargetListType, SiteTargetListIDType
# from modules.dto.input_queue_dto import PlanData
# from utils.site.url_maker import UrlParm, make_url
# from selenium import webdriver
# from selenium.webdriver.chrome.service import Service
# from selenium.webdriver.common.by import By
# from webdriver_manager.chrome import ChromeDriverManager
# from selenium.webdriver.support.ui import WebDriverWait
# from selenium.webdriver.support import expected_conditions as EC
# import time

# class SugarMobileListAction():
    
#     def initialize_browser(self):
#         # options = webdriver.ChromeOptions()
#         # options.add_argument('--headless=new')  # 최신 headless 모드
#         # options.add_argument('--no-sandbox')
#         # options.add_argument('--disable-dev-shm-usage')
#         # browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
#         # browser.set_page_load_timeout(600)  # 페이지 로딩 타임아웃 300초로 설정
#         # return browser
#         options = webdriver.ChromeOptions()
#         options.add_argument('--headless=new')  # 최신 headless 모드
#         options.add_argument('--no-sandbox')
#         options.add_argument('--disable-dev-shm-usage')
#         options.add_argument('--disable-gpu')

#         # ChromeDriver 서비스 생성
#         service = Service(ChromeDriverManager().install())
#         service.start()  # 명시적 서비스 시작

#         # 브라우저 생성
#         browser = webdriver.Chrome(service=service, options=options)

#         # 페이지 로딩 타임아웃
#         browser.set_page_load_timeout(600)

#         # Remote connection timeout 연장
#         try:
#             browser.command_executor._conn._conn.timeout = 600
#         except Exception:
#             pass  # 실패하면 무시

#         return browser

#     def root(self,
#         page: int,
#         *args,
#         **kwargs) -> tuple[list[PlanData], bool]:
#         is_end = False
#         result =[]
#         file_counter = 0  # 파일 번호를 위한 카운터 초기화
        
#         browser = None
#         try:
#             browser = self.initialize_browser()
        
#             #리스트 url
#             url_list = [
#                 {
#                     'type':'LTE',
#                     'url': 'https://www.sugarmobile.co.kr/rate_plan.do?type=T006'
#                 },
#                 {
#                     'type':'5G',
#                     'url': 'https://www.sugarmobile.co.kr/rate_plan.do?type=T005'
#                 },
#             ]
#             for base_url in url_list:
#                 #리스트 url 응답
#                 response = requests.get(base_url['url'], 
#                     #                     headers={
#                     # "Host":"www.uplusumobile.com",
#                     # "user-agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
#                     # }
#                 )        
#                 html = response.text
                
#                 # HTML 파일 저장
#                 with open(f'./response_files/list_sugarmobile{file_counter}.html', 'w', encoding='utf-8') as file:
#                     file.write(html)
              
#                 #파일 번호 증가
#                 file_counter += 1
                
#                 bs = BeautifulSoup(html, 'html.parser')
#                 li_list = bs.find_all('li','card_list_item')
                
#                 for item in li_list:
#                     try:
#                         href = item.find('a')['href']
                        
#                         # ;로 시작하는 세션 ID 제거
#                         if ';' in href:
#                             href = href.split(';')[0] + '?' + href.split('?', 1)[1]
                        
#                         aType = href.split('type=')[1].split('&')[0]
                        
#                         aNo = href.split('no=')[1]
#                         uuid = aType + aNo
                        
#                         title = item.find('p','title')
#                         title_span = title.find('span')
                        
#                         # MNO 추출
#                         mno = title_span.text.strip() if title_span else ''
                        
#                         # LGU+ 치환 로직
#                         if mno == 'LGU+':
#                             mno = 'LGU'
                        
#                         plan_name = title.text.replace(title_span.text,'')
#                         desc = item.find('ul','desc')
#                         li_list2 = desc.find_all('li')
                        
#                         data =''
#                         voice_call =''
#                         message =''
#                         qos = ''
                        
#                         # 기본값 설정
#                         data = "0GB"
#                         daily_data = "0GB"
#                         qos = ""
#                         voice_call = ""
#                         message = ""
#                         m12_price = 0
#                         m24_price = 0

#                         # 데이터 관련 정보를 안전하게 추출
#                         for li in li_list2:
#                             text = li.text.replace(" ", "")  # 공백 제거

#                             # ✅ 월 데이터 추출 (QoS 포함)
#                             if "월데이터" in text:
#                                 data_text = text.split("월데이터")[-1]  # "월 데이터" 이후 값 가져오기

#                                 # ✅ 정규식을 사용하여 "무제한 + 5Mbps" 같은 경우 처리
#                                 match = re.search(r'(무제한|\d+GB|\d+MB)\+?\s*(\d+Mbps)?', data_text)
#                                 if match:
#                                     data = match.group(1)  # 데이터 값 저장
#                                     qos = match.group(2) if match.group(2) else ""  # QoS 값 저장

#                                     # ✅ "무제한"이면 0GB로 변경
#                                     if data == "무제한":
#                                         data = "0GB"

#                             # ✅ 일 데이터 추출
#                             if "일데이터" in text:
#                                 daily_data = text.split("일데이터")[-1]

#                             # ✅ 음성통화 추출
#                             if "음성통화" in text:
#                                 voice_call = text.split("음성통화")[-1]

#                             # ✅ 문자 추출
#                             if "문자" in text:
#                                 message = text.split("문자")[-1]

#                         # ✅ "없음"을 "0GB"로 변환
#                         if data == "없음":
#                             data = "0GB"
#                         if daily_data == "없음":
#                             daily_data = "0GB"

                        

                       
#                         price_full_text = item.find('div','price').text
#                         if '이후' in price_full_text:
#                             split_price = price_full_text.split('이후')
#                             sale_price = split_price[0].split('원')[0].split('월')[1].strip().replace(',','')
#                             after_price = split_price[1].split('원')[0].strip().replace(',','')
#                             promotion_period = split_price[0].split('*')[1].strip()
#                             normal_price = after_price
#                         else:
#                             sale_price = price_full_text.split(' ')[1].replace(',','')
#                             after_price = sale_price
#                             promotion_period =''
#                             normal_price = after_price
                        
#                         url = 'https://www.sugarmobile.co.kr/'+href
                        
                        
#                         browser.get(url)
                            
#                         # 페이지가 완전히 로드될 때까지 기다림
#                         # WebDriverWait(browser, 60).until(
#                         #     lambda driver: driver.execute_script("return document.readyState") == "complete"
#                         # )
#                         WebDriverWait(browser, 300).until(
#                             EC.presence_of_element_located((By.CSS_SELECTOR, "ul.card_list"))
#                         )
#                         # 스크롤을 최하단으로 이동하여 추가 데이터 로드
#                         browser.execute_script("window.scrollTo(0, document.body.scrollHeight);")
#                         time.sleep(2)
                        
#                         page_source = browser.page_source
                        
#                         # HTML 파일 저장
#                         """
#                         with open(f'./response_files/detail_sugarmobile_{uuid}.html', 'w', encoding='utf-8') as file:
#                             file.write(page_source)
#                         """
#                         detail_soup = BeautifulSoup(page_source, 'html.parser')
                       
#                         # ✅ 상세페이지에서 "n개월차부터" 조건 확인
#                         price_notice = detail_soup.find("div", class_="desc")  # 가격 공지사항이 있는 부분

#                         if price_notice:
#                             price_text = price_notice.get_text(strip=True)  # 전체 텍스트 추출 후 공백 제거
#                             match = re.search(r'(\d+)개월차부터.*?(\d{1,3}(,\d{3})*)원', price_text)

#                             if match:
#                                 after_price = int(match.group(2).replace(",", ""))  # `after_price` 값 변경
#                                 normal_price = after_price  # `normal_price` 값도 변 경경                     


#                         # ✅ 인터넷결합 여부 추출
#                         header_sub = detail_soup.find("div", class_="header_sub")
#                         combination = False  # 기본값

#                         if header_sub:
#                             badges = header_sub.find_all("span", class_="badge badge_red")
#                             combination = any("인터넷결합" in badge.get_text(strip=True) for badge in badges)


#                          # ✅ 상세페이지에서 "n개월 특가" 조건 확인하여 프로모션 기간 업데이트
#                         promotion_period_match = None

#                         if header_sub:
#                             for badge in header_sub.find_all("span", class_="badge badge_red"):
#                                 promo_match = re.search(r'(\d+)개월 특가', badge.get_text(strip=True))
#                                 if promo_match:
#                                     promotion_period_match = promo_match.group(1)
#                                     break  # 첫 번째 매칭되는 값만 가져옴

#                         if promotion_period_match:
#                             promotion_period = f"{promotion_period_match}개월"

#                         # ✅ 프로모션 값이 빈 문자열이면 "평생"으로 설정
#                         if not promotion_period or promotion_period.strip() == '':
#                             promotion_period = '평생'
#                         elif promotion_period.isdigit():
#                             promotion_period += '개월'


#                         # "rate_caption_list" 클래스에서 "부가통화" 텍스트를 포함한 <li> 태그 찾기
#                         rate_caption_list = detail_soup.find('ul', class_='rate_caption_list')
#                         buga_call = ''

#                         if rate_caption_list:
#                             buga_call_item = rate_caption_list.find('li', text=lambda t: t and '부가통화' in t)
#                             if buga_call_item:
#                                 # "부가통화" 이후의 텍스트만 추출 (예: "300분")
#                                 buga_call = buga_call_item.text.replace('부가통화', '').strip()

                        
#                         # "rate_caption_list" 클래스에서 "혜택" 텍스트를 포함한 <li> 태그 찾기
#                         # sale_price 및 after_price 변환
#                         sale_price = int(sale_price) if isinstance(sale_price, str) and sale_price.isdigit() else sale_price
#                         after_price = int(after_price) if isinstance(after_price, str) and after_price.isdigit() else after_price
#                         normal_price = after_price
                        
#                         # 12개월 & 24개월 총 요금(m12_price, m24_price) 계산
#                         if not promotion_period or promotion_period == "평생":  # 빈 값, None, "평생" 동일 처리
#                             m12_price = sale_price * 12
#                             m24_price = sale_price * 24  # 평생일 때도 24개월 가격 계산
#                         elif "개월" in promotion_period:
#                             match = re.search(r"(\d+)", promotion_period)
#                             if match:
#                                 months = int(match.group(1))  # 개월 수 추출
#                             else:
#                                 months = 0  # 기본값 설정

#                             if months >= 24:  # 24개월 이상일 경우 24개월 할인 적용
#                                 m12_price = sale_price * 12
#                                 m24_price = sale_price * 24
#                             elif months >= 12:  # 12개월 초과 24개월 이하일 경우
#                                 m12_price = sale_price * 12
#                                 m24_price = (sale_price * months) + (after_price * (24 - months))
#                             else:  # 12개월 이하일 경우
#                                 m12_price = (sale_price * months) + (after_price * (12 - months))
#                                 m24_price = (sale_price * months) + (after_price * (24 - months))
#                         else:
#                             m12_price = normal_price * 12  # 기본값
#                             m24_price = normal_price * 24  # 기본값                           

#                         dto = PlanData(
#                                 uuid= 'SUGAR_' + uuid,
#                                 mno = mno,
#                                 telecom = SiteTargetListType.SUGAR_MOBILE_LIST.value,
#                                 company_id = SiteTargetListIDType.SUGAR_MOBILE_LIST.value,  # company_id 추가
#                                 url = url,
#                                 plan_type= base_url['type'],
#                                 plan_name= plan_name,
#                                 data = data,
#                                 voice_call = voice_call,
#                                 message = message,
#                                 normal_price = normal_price,
#                                 sale_price = sale_price,
#                                 benefit = '',
#                                 qos= qos,
#                                 business_name='(주)씨케이커뮤스트리',
#                                 after_price= after_price,
#                                 combination=combination,
#                                 freebies='',
#                                 etc='',
#                                 promotion_period = promotion_period,
#                                 buga_call = buga_call,
#                                 plan_code = '',
#                                 daily_data=daily_data,
#                                 m12_price=  m12_price,
#                                 m24_price=  m24_price,
#                             )
#                         result.append(dto)
                            
#                     except Exception as e:
#                             traceback.print_exc()
#         finally:
#             # 브라우저 종료
#             if browser:
#                 browser.quit()
            
#         is_end = True 
        
#         return result,is_end

import json
import traceback
import requests
import re
import time
from bs4 import BeautifulSoup
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData

TAG = '[SugarMobile]'

class SugarMobileListAction:

    def root(self, page: int = 1, *args, **kwargs) -> tuple[list[PlanData], bool]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result = []
        file_counter = 0

        url_list = [
            {'type':'LTE','url': 'https://www.sugarmobile.co.kr/rate_plan.do?type=T006'},
            {'type':'5G','url': 'https://www.sugarmobile.co.kr/rate_plan.do?type=T005'},
        ]
        
        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                          "(KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
        }

        for base_url in url_list:
            try:
                print(f"{TAG} 목록 페이지 요청: {base_url['type']} - {base_url['url']}")
                req_start = time.time()
                response = requests.get(base_url['url'], headers=headers, timeout=60)
                req_elapsed = time.time() - req_start
                print(f"{TAG} 목록 페이지 응답 완료 ({req_elapsed:.1f}초)")
                html = response.text
                
                with open(f'./response_files/list_sugarmobile{file_counter}.html', 'w', encoding='utf-8') as file:
                    file.write(html)
                
                file_counter += 1
                
                soup = BeautifulSoup(html, 'html.parser')
                li_list = soup.find_all('li', 'card_list_item')
                total_count = len(li_list)
                print(f"{TAG} {base_url['type']} 파싱할 항목 수: {total_count}건")

                for idx, item in enumerate(li_list):
                    try:
                        href = item.find('a')['href']
                        if ';' in href:
                            href = href.split(';')[0] + '?' + href.split('?', 1)[1]
                        
                        aType = href.split('type=')[1].split('&')[0]
                        aNo = href.split('no=')[1]
                        uuid = aType + aNo
                        
                        title = item.find('p', 'title')
                        title_span = title.find('span')
                        mno = title_span.text.strip() if title_span else ''
                        if mno == 'LGU+':
                            mno = 'LGU'
                        plan_name = title.text.replace(title_span.text, '') if title_span else title.text
                        print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {base_url['type']} | {plan_name}")

                        desc = item.find('ul', 'desc')
                        li_list2 = desc.find_all('li') if desc else []
                        
                        data = "0GB"
                        daily_data = "0GB"
                        qos = ""
                        voice_call = ""
                        message = ""
                        m12_price = 0
                        m24_price = 0

                        for li in li_list2:
                            text = li.text.replace(" ", "")
                            if "월데이터" in text:
                                match = re.search(r'(무제한|\d+(?:\.\d+)?GB|\d+(?:\.\d+)?MB)\+?\s*(\d+Mbps)?', text)
                                if match:
                                    data = match.group(1)
                                    qos = match.group(2) if match.group(2) else ""
                                    if data == "무제한":
                                        data = "0GB"
                            if "일데이터" in text:
                                daily_data = text.split("일데이터")[-1]
                            if "음성통화" in text:
                                voice_call = text.split("음성통화")[-1]
                            if "문자" in text:
                                message = text.split("문자")[-1]
                        
                        if data == "없음":
                            data = "0GB"
                        if daily_data == "없음":
                            daily_data = "0GB"
                        
                        price_full_text = item.find('div','price').text if item.find('div','price') else ''
                        if '이후' in price_full_text:
                            split_price = price_full_text.split('이후')
                            sale_price = int(split_price[0].split('원')[0].split('월')[1].strip().replace(',',''))
                            after_price = int(split_price[1].split('원')[0].strip().replace(',',''))
                            promotion_period = split_price[0].split('*')[1].strip()
                            normal_price = after_price
                        else:
                            price_numbers = re.findall(r'\d+', price_full_text.replace(',', ''))
                            if price_numbers:
                                sale_price = int(price_numbers[0])
                                after_price = sale_price
                            else:
                                sale_price = after_price = 0
                            promotion_period = ''
                            normal_price = after_price
                        
                        detail_url = 'https://www.sugarmobile.co.kr/' + href
                        print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 요청: {detail_url}")
                        detail_start = time.time()
                        detail_resp = requests.get(detail_url, headers=headers, timeout=60)
                        detail_elapsed = time.time() - detail_start
                        print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 응답 완료 ({detail_elapsed:.1f}초)")
                        detail_soup = BeautifulSoup(detail_resp.text, 'html.parser')
                        
                        price_notice = detail_soup.find("div", class_="desc")
                        if price_notice:
                            match = re.search(r'(\d+)개월차부터.*?(\d{1,3}(?:,\d{3})*)원', price_notice.get_text(strip=True))
                            if match:
                                after_price = int(match.group(2).replace(",", ""))
                                normal_price = after_price
                        
                        header_sub = detail_soup.find("div", class_="header_sub")
                        combination = False
                        promotion_period_match = None
                        if header_sub:
                            badges = header_sub.find_all("span", class_="badge badge_red")
                            combination = any("인터넷결합" in b.get_text(strip=True) for b in badges)
                            for b in badges:
                                promo_match = re.search(r'(\d+)개월 특가', b.get_text(strip=True))
                                if promo_match:
                                    promotion_period_match = promo_match.group(1)
                                    break
                        if promotion_period_match:
                            promotion_period = f"{promotion_period_match}개월"
                        elif not promotion_period or promotion_period.strip() == '':
                            promotion_period = '평생'
                        elif promotion_period.isdigit():
                            promotion_period += '개월'
                        
                        rate_caption_list = detail_soup.find('ul', class_='rate_caption_list')
                        buga_call = ''
                        if rate_caption_list:
                            b_item = rate_caption_list.find('li', text=lambda t: t and '부가통화' in t)
                            if b_item:
                                buga_call = b_item.text.replace('부가통화','').strip()
                        
                        m12_price = sale_price * 12
                        m24_price = sale_price * 24
                        if promotion_period != "평생" and "개월" in promotion_period:
                            months = int(re.search(r"(\d+)", promotion_period).group(1))
                            if months < 12:
                                m12_price = sale_price * months + after_price * (12 - months)
                                m24_price = sale_price * months + after_price * (24 - months)
                            elif months < 24:
                                m24_price = sale_price * months + after_price * (24 - months)

                        dto = PlanData(
                            uuid='SUGAR_' + uuid,
                            mno=mno,
                            telecom=SiteTargetListType.SUGAR_MOBILE_LIST.value,
                            company_id=SiteTargetListIDType.SUGAR_MOBILE_LIST.value,
                            url=detail_url,
                            plan_type=base_url['type'],
                            plan_name=plan_name,
                            data=data,
                            voice_call=voice_call,
                            message=message,
                            normal_price=normal_price,
                            sale_price=sale_price,
                            benefit='',
                            qos=qos,
                            business_name='(주)씨케이커뮤스트리',
                            after_price=after_price,
                            combination=combination,
                            freebies='',
                            etc='',
                            promotion_period=promotion_period,
                            buga_call=buga_call,
                            plan_code='',
                            daily_data=daily_data,
                            m12_price=m12_price,
                            m24_price=m24_price
                        )
                        result.append(dto)
                        success_count += 1
                    except Exception as e:
                        print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                        traceback.print_exc()
                        error_count += 1
            except Exception as e:
                print(f"{TAG} [ERROR] {base_url['type']} 처리 중 오류: {e}")
                traceback.print_exc()
                error_count += 1

        is_end = True

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")

        return result, is_end



