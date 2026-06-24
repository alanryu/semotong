import json
import re
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
from utils.site.data_preprocess import preprocessAfterContentOriginResponse
from utils.site.url_maker import UrlParm, make_url
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.support.ui import WebDriverWait

class LiivmListAction():
    
    def initialize_browser(self):
        options = webdriver.ChromeOptions()
        options.add_argument('--headless=new')  # 최신 headless 모드
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        return webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

    def root(self,
        page: int,
        *args,
        **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result = []
        
        # 브라우저 초기화 및 안전한 종료 처리
        try:
            browser = self.initialize_browser()
       
            # 리스트 URL
            param_list = ['01', '02', '03']
            mno_mapping = {
                '01': 'LGU',
                '02': 'KT',
                '03': 'SKT'
            }
    
            for param in param_list:
                url = make_url(base_url='https://www.liivm.com/appIf/v1/ratePlan/LMPM000001')

                # 리스트 URL 응답
                response = requests.post(url, data={'soId': param})        
                li_list = response.json()["searchProdList"]

                for item in li_list:
                    sold = item["soId"]
                    prodGrpCd = item["prodGrpCd"]

                    # ✅ dataUnit에서 데이터, 일일 데이터, QoS 추출
                    data = "0GB"  # 기본 데이터량이 없는 경우 0GB로 설정
                    daily_data = ""
                    qos = ""

                    data_unit = item["dataUnit"]

                    # QoS 속도 추출 (예: "3Mbps", "1Mbps", "5Mbps")
                    qos_match = re.search(r"(\d+Mbps)", data_unit)
                    if qos_match:
                        qos = qos_match.group(1)
                        data_unit = data_unit.replace(qos, "").strip()  # QoS 제거

                    # 친구결합데이터 제거 (예: "+20GB(친구결합데이터)")
                    data_unit = re.sub(r"\+\s*\d+GB\(친구결합데이터\)", "", data_unit).strip()

                    # 일일 데이터 추출 (예: "일2GB", "일5GB", "매일 5GB")
                    daily_match = re.search(r"(일\s?\d+GB|매일\s?\d+GB)", data_unit)
                    if daily_match:
                        daily_data = re.search(r"(\d+GB)", daily_match.group(0)).group(1)  # "5GB" 부분만 추출
                        data_unit = data_unit.replace(daily_match.group(0), "").strip()  # 일일 데이터 제거

                    # 남은 값이 있으면 기본 데이터량으로 설정 (예: "25GB", "30GB" 같은 값이 남아 있는 경우)
                    if data_unit:
                        data = data_unit.strip()

                    # ✅ qosDesc에서 QoS 속도 제한, 일일 데이터 제공량 추가 추출
                    qos_from_qosdesc = ""
                    daily_data_from_qosdesc = ""

                    qos_match_qosdesc = re.search(r"최대(\d+Mbps)", item["qosDesc"])
                    if qos_match_qosdesc:
                        qos_from_qosdesc = qos_match_qosdesc.group(1)

                    daily_match_qosdesc = re.search(r"일\s?(\d+GB)", item["qosDesc"])
                    if daily_match_qosdesc:
                        daily_data_from_qosdesc = daily_match_qosdesc.group(1)

                    # 최종 QoS와 daily_data 결정 (우선순위 적용)
                    daily_data = daily_data if daily_data else daily_data_from_qosdesc
                    qos = qos_from_qosdesc if qos_from_qosdesc else qos

                    plan_name = item["prodNm"]
                    plan_type = "LTE" if "LTE" in plan_name else "5G" if "5G" in plan_name else ""
                    if not plan_type:
                        continue
                    
                    # `mno`를 param 값에 따라 매핑
                    mno = mno_mapping.get(param, '')
                    normal_price = item["prodPrice"]

                    sale_price = int(item["prodPrice"]) - int(item["eventAmt"]) if item["eventAmt"] != '' else item["prodPrice"]
                    sale_price = sale_price if sale_price else normal_price

                    after_price = int(item["prodPrice"]) - int(item["eventAmt"]) if item["eventAmt"] != '' else item["prodPrice"]
                    after_price = after_price if after_price else normal_price
                        
                    uuid = 'LIIVM_' + item["prodCd"]
                    
                    url = f"https://www.liivm.com/info/rateplan/rateplan/infoProdDetail?soId={sold}&prodGrpCd={prodGrpCd}"
                    
                    browser.get(url)
                            
                    # 페이지가 완전히 로드될 때까지 기다림
                    WebDriverWait(browser, 10).until(
                        lambda driver: driver.execute_script("return document.readyState") == "complete"
                    )
                    browser.execute_script("window.scrollTo(0, document.body.scrollHeight);")
                    
                    page_source = browser.page_source
                    detail_soup = BeautifulSoup(page_source, 'html.parser')
                    
                    # 부가통화 값 추출
                    buga_call = ''
                    grp_srv_desc = detail_soup.find('div', id='grpSrvDesc', class_='txt_help')
                    if grp_srv_desc:
                        paragraphs = grp_srv_desc.find_all('p')
                        for paragraph in paragraphs:
                            text = paragraph.text.strip()
                            if '부가음성' in text or '영상통화' in text:
                                if re.search(r'\d+GB.*:\s*\d+\s*분', text):
                                    continue
                                match = re.search(r'(부가음성.*?(\d+\s*분))|(매월\s*(\d+\s*분))', text)
                                if match:
                                    if match.group(2):
                                        buga_call = match.group(2).strip()
                                    elif match.group(4):
                                        buga_call = match.group(4).strip()
                                    break

                    # 12개월 & 24개월 총 요금 계산
                    sale_price, after_price = int(sale_price), int(after_price)
                    promotion_period = ''

                    m12_price, m24_price = sale_price * 12, sale_price * 24 if not promotion_period or promotion_period == "평생" else (sale_price * 12, sale_price * 24)

                    try:                            
                        dto = PlanData(
                                        uuid=uuid,
                                        mno=mno,
                                        telecom=SiteTargetListType.LIIVM_LIST.value,
                                        company_id=SiteTargetListIDType.LIIVM_LIST.value,
                                        url=url,
                                        plan_type=plan_type,
                                        plan_name=item["prodNm"],
                                        data=data,
                                        daily_data=daily_data,
                                        voice_call=item["voiceUnit"],
                                        message=item["smsUnit"],
                                        normal_price=normal_price,
                                        sale_price=sale_price,
                                        after_price=after_price,
                                        qos=qos,
                                        business_name='(주)국민은행',
                                        buga_call=buga_call,
                                        m12_price=m12_price,
                                        m24_price=m24_price,
                                        
                                        # ✅ 누락된 필드 추가 (기본값은 빈 문자열 '')
                                        benefit="",
                                        freebies="",
                                        etc="",
                                        promotion_period="",
                                        plan_code=""
                                    )
                        result.append(dto)
                        
                    except Exception as e:
                        traceback.print_exc()
        
        finally:
            browser.quit()

        is_end = True   
        return result, is_end
