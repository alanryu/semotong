import time
import traceback
import re  # 정규 표현식 모듈 추가
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData, Telecom

TAG = '[Eyagi]'

class EyagiListAction:
    def root(self, page: int = 0, *args, **kwargs) -> tuple[bool, list]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result = []

        # URL 설정
        urls = [
            "https://www.eyagi.co.kr/shop/plan/list.php?tag=skt",
            "https://www.eyagi.co.kr/shop/plan/list.php?tag=lgt",
            "https://www.eyagi.co.kr/shop/plan/list.php?tag=kt"
        ]

        # Selenium ChromeOptions 설정
        options = webdriver.ChromeOptions()
        options.add_argument('--headless')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--no-zygote')
        options.add_argument('--disable-gpu')
        print(f"{TAG} Selenium 드라이버 생성 중...")
        browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
        print(f"{TAG} Selenium 드라이버 생성 완료")

        try:
            for url in urls:
                try:
                    print(f"{TAG} 페이지 로드 시작: {url}")
                    page_start = time.time()
                    browser.get(url)
                    time.sleep(2)  # 페이지 로드 대기
                    page_elapsed = time.time() - page_start
                    print(f"{TAG} 페이지 로드 완료 ({page_elapsed:.1f}초)")

                    # HTML 파싱 및 <section id="listWrap"> 안의 내용 추출
                    soup = BeautifulSoup(browser.page_source, 'html.parser')
                    plans = soup.find_all('div', class_='plan-list')
                    print(f"{TAG} 요금제 {len(plans)}건 발견 ({url})")
                    
                    
                    # 파일 이름 결정
                    if "skt" in url:
                        filename = "./response_files/list_Eyagi_SKT.html"
                    elif "lgt" in url:
                        filename = "./response_files/list_Eyagi_LGU.html"
                    elif "kt" in url:
                        filename = "./response_files/list_Eyagi_KT.html"
                    else:
                        filename = "./response_files/list_Eyagi_unknown.html"

                    # plans 내용을 보기 좋게 저장
                    
                    with open(filename, "w", encoding="utf-8") as file:
                        for plan in plans:
                            pretty_plan = BeautifulSoup(str(plan), "html.parser").prettify()
                            file.write(pretty_plan)
                            file.write("\n")
                    #print(f"[INFO] 데이터 저
                    # 장 완료: {filename}")
                                     

                    for idx, plan in enumerate(plans):
                        try:
                            item = plan.find('a', class_='plan-item')
                            uuid = item.get('data-comm-code', '').strip()
                            mno = item.get('data-mno-gubun', '').strip()
                            # ✅ "LGT"를 "LGU"로 변환
                            if mno == "LGT":
                                mno = "LGU"
                            url_detail = f"https://www.eyagi.co.kr/shop/plan/detail.php?agent_code=KSD0002&comm_code={uuid}"

                            plan_name_tag = plan.find('p', class_='name')
                            plan_name = plan_name_tag.text.strip() if plan_name_tag else ''
                            plan_type = "5G" if plan_name.startswith("5G") else "LTE"

                           # ✅ 데이터 파싱 로직 개선 (QoS 처리 X)
                            data_tag = plan.find('div', class_='data').find('p', class_='free')
                            data_text = data_tag.text.strip() if data_tag else ''
                            # print(f"[DEBUG] data_text: {data_text}")

                            # 기본 제공 데이터와 매일 제공 데이터를 저장할 변수
                            data = ""  # 기본 제공 데이터
                            daily_data = ""  # 매일 제공 데이터

                            # ✅ (1) 매일 제공 데이터 추출 (가장 먼저 처리)
                            match_daily = re.search(r'\(?\+?\s*매일\s*(\d+(?:\.\d+)?)\s*([GgMm][Bb]?)\)?', data_text, re.IGNORECASE)
                            if match_daily:
                                daily_data = f"{match_daily.group(1)}{match_daily.group(2).upper()}".replace(".0", "")

                            # ✅ (2) 기본 제공 데이터 추출 (매일 제공 데이터가 제거된 후)
                            # "매일" 키워드가 포함되지 않은 데이터만 추출
                            cleaned_data_text = re.sub(r'\(?\+?\s*매일\s*\d+(?:\.\d+)?\s*[GgMm][Bb]?\)?', '', data_text, flags=re.IGNORECASE).strip()

                            data_match = re.search(r'(\d+(?:\.\d+)?)\s*([GgMmBb]?)', cleaned_data_text, re.IGNORECASE)
                            if data_match:
                                unit = data_match.group(2).upper()
                                
                                # 단위 보정: 'G' -> 'GB', 'M' -> 'MB'
                                if unit == "G":
                                    unit = "GB"
                                elif unit == "M":
                                    unit = "MB"
                                
                                data = f"{data_match.group(1)}{unit}".replace(".0", "")

                            # ✅ (3) 매일 제공 데이터만 있는 경우, 기본 데이터(`data`)를 `"0GB"`로 설정
                            if daily_data and not data:
                                data = "0GB"

                            # ✅ (4) 매일 제공 데이터만 존재하는 경우, 기본 데이터(`data`)를 `"0GB"`로 설정
                            if daily_data and not data:
                                data = "0GB"



                            voice_call_tag = plan.find('div', class_='call').find('p', class_='free')
                            voice_call = voice_call_tag.text.strip() if voice_call_tag else ''

                            message_tag = plan.find('div', class_='sms').find('p', class_='free')
                            message = message_tag.text.strip() if message_tag else ''
                            
                            # 기본적으로 normal_price를 None으로 설정
                            normal_price = None

                            normal_price_tag = plan.find('p', class_='basic-price')
                            if normal_price_tag:
                                # 숫자만 추출하여 normal_price 설정
                                normal_price = int(''.join(filter(str.isdigit, normal_price_tag.text)))

                            sale_price_tag = plan.find('p', class_='current-price')
                            sale_price = int(''.join(filter(str.isdigit, sale_price_tag.text))) if sale_price_tag else 0
                            
                            
                            

                            qos_tag = plan.find('p', class_='qos')
                            qos = ''

                            if qos_tag:
                                qos_text = qos_tag.text.strip()
                                
                                # QoS 속도 추출 (정규식 개선)
                                qos_match = re.search(r'(최대\s*)?(\d+(?:\.\d+)?)\s*(Mbps|Kbps)', qos_text, re.IGNORECASE)
                                
                                if qos_match:
                                    qos = f"{qos_match.group(2)}{qos_match.group(3)}"  # 숫자와 단위를 결합
                                    
                            # 기본적으로 after_price를 None으로 설정
                            after_price = None
                            
                            # 기본적으로 promotion_period를 빈 문자열로 초기화
                            promotion_period = ''        

                           # "평생 할인"을 포함하는 모든 period 클래스를 가진 span 태그를 찾기
                            badge_box = plan.find('div', class_='badge-box')
                            if badge_box:
                                # 'badge period' 클래스를 포함하는 모든 span 태그를 찾음
                                period_badges = badge_box.find_all('span', class_=re.compile('badge period'))
                                for period_badge in period_badges:
                                    # '평생'이 텍스트에 포함되어 있는지 확인
                                    if "평생" in period_badge.text.strip():
                                        promotion_period = "평생"
                                        
                            after_price_tag = plan.find('p', class_='orgin-price')
                            if after_price_tag:
                                after_price_text = after_price_tag.text.strip()

                                # '개월 후' 패턴과 '~개월간' 패턴 처리
                                after_price_text = re.sub(r'(\d+)\s*개월\s*후', r'\1개월', after_price_text)  # 'n개월 후'를 'n개월'로 변환
                                price_match = re.search(r'(\d{1,3}(,\d{3})*)원', after_price_text)  # 금액 패턴 매칭
                                if price_match:
                                    after_price = int(price_match.group(1).replace(',', ''))  # 금액 추출 및 쉼표 제거

                                # promotion_period 처리
                                if '개월간' in after_price_text:
                                    period_match = re.search(r'(\d+)\s*~\s*(\d+)\s*개월', after_price_text)  # 'n~m개월' 패턴 매칭
                                    if period_match:
                                        min_period = int(period_match.group(1)) - 1  # 최소 기간
                                        promotion_period = f"{min_period}개월"
                                    else:
                                        promotion_period = ''  # 매칭되지 않으면 빈값
                                elif '개월' in after_price_text:
                                    period_match = re.search(r'(\d+)\s*개월', after_price_text)  # 'n개월' 패턴 매칭
                                    if period_match:
                                        promotion_period = f"{period_match.group(1)}개월"

                            # normal_price가 None인 경우 sale_price 값을 대입
                            if normal_price is None:
                                normal_price = after_price

                            # after_price가 None인 경우 sale_price 값을 대입
                            if after_price is None:
                                after_price = sale_price

                            # ✅ normal_price가 None이거나 0이면 보정
                            if normal_price is None or normal_price == 0:
                                if after_price and after_price > 0:
                                    normal_price = after_price
                                elif sale_price and sale_price > 0:
                                    normal_price = sale_price

                            # ✅ 컴비네이션 여부 판별
                            combination_tag = plan.find('span', class_=lambda x: x and "combine" in x)
                            combination = True if combination_tag else False        
                           

                            uuid = 'EYAGI_' + uuid
                            
                            # 부가통화 값 추출
                            additional_tag = plan.find('p', class_='additional')
                            buga_call = ''
                            if additional_tag:
                                additional_text = additional_tag.text.strip()
                                # '부가통화'로 시작하는 텍스트에서 '50분' 같은 값을 추출
                                match = re.search(r'부가통화\s*(\d+\s*분)', additional_text)
                                if match:
                                    buga_call = match.group(1).strip()
                                    
                            # "평생 할인" 탐색 및 promotion_period 설정
                            badge_box = plan.find('div', class_='badge-box')
                            if badge_box:
                                badge_period = badge_box.find('span', class_='badge period')
                                if badge_period and "평생" in badge_period.text:
                                    promotion_period = "평생"
                        

                            # sale_price 및 after_price 변환
                            sale_price = int(sale_price) if isinstance(sale_price, str) and sale_price.isdigit() else sale_price
                            after_price = int(after_price) if isinstance(after_price, str) and after_price.isdigit() else after_price

                            # 12개월 & 24개월 총 요금(m12_price, m24_price) 계산
                            if not promotion_period or promotion_period == "평생":  # 빈 값, None, "평생" 동일 처리
                                m12_price = sale_price * 12
                                m24_price = sale_price * 24  # 평생일 때도 24개월 가격 계산
                            elif "개월" in promotion_period:
                                match = re.search(r"(\d+)", promotion_period)
                                if match:
                                    months = int(match.group(1))  # 개월 수 추출
                                else:
                                    months = 0  # 기본값 설정

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
                                uuid = uuid,
                                mno = mno,
                                telecom = SiteTargetListType.EYAGI_MOBILE_LIST.value,
                                company_id = SiteTargetListIDType.EYAGI_MOBILE_LIST.value,  # company_id 추가
                                url = url_detail,
                                plan_type = plan_type,
                                plan_name = plan_name,
                                data = data,
                                voice_call = voice_call,
                                message =  message,
                                normal_price = normal_price,
                                sale_price = sale_price,
                                benefit = '',
                                qos = qos,
                                business_name = '이야기모바일',
                                after_price = after_price,
                                combination = combination,
                                freebies = '',
                                etc = '',
                                promotion_period = promotion_period,
                                buga_call= buga_call,
                                plan_code = '',
                                daily_data=daily_data,
                                m12_price=  m12_price,
                                m24_price=  m24_price,
                            )
                            result.append(dto)
                            success_count += 1
                            print(f"{TAG} [{idx+1}/{len(plans)}] 파싱 완료: {mno} | {plan_name}")

                        except Exception as e:
                            error_count += 1
                            print(f"{TAG} [{idx+1}/{len(plans)}] [ERROR] 요금제 파싱 실패: {e}")
                            traceback.print_exc()

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [ERROR] URL 처리 중 오류 발생: {url} - {e}")
                    traceback.print_exc()

        finally:
            print(f"{TAG} Selenium 드라이버 종료")
            browser.quit()

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        is_end = True
        return result, is_end

