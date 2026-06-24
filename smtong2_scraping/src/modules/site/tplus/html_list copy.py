
import ssl
import requests
from requests.adapters import HTTPAdapter
from urllib3.poolmanager import PoolManager
from bs4 import BeautifulSoup
import traceback
import warnings
import urllib3
import json
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import AllData, DetailInfo, PlanData, Telecom
from utils.etc.functions import filter_detail
from utils.site.url_maker import UrlParm, make_url
import re

warnings.simplefilter('ignore', urllib3.exceptions.InsecureRequestWarning)

# SSL Error 해결을 위한 TLSAdapter
class TLSAdapter(HTTPAdapter):
    def init_poolmanager(self, connections, maxsize, block=False):
        """Create and initialize the urllib3 PoolManager."""
        ctx = ssl.create_default_context()
        ctx.set_ciphers('DEFAULT@SECLEVEL=1')  # SSL/TLS 보안 레벨 조정
        ctx.check_hostname = False  # 인증서의 호스트 이름 검증을 비활성화
        ctx.verify_mode = ssl.CERT_NONE  # 인증서 검증 비활성화

        # PoolManager에 SSLContext 적용
        self.poolmanager = PoolManager(
            num_pools=connections,
            maxsize=maxsize,
            block=block,
            ssl_context=ctx
        )

# 경고 메시지 출력 억제
warnings.filterwarnings("ignore", category=UserWarning)

class TPlusListAction:
    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result = []
        detail_result = []
        key = ''

        # requests 세션 생성 및 TLSAdapter 장착
        _session = requests.Session()
        _session.mount('https://', TLSAdapter())  # TLSAdapter를 https 연결에 사용

        while True:
            try:
                # 리스트 url 응답 (인증서 검증을 무시하고 TLSAdapter 사용)
                response = _session.post(
                    "https://www.tplusmobile.com/BackBone/rate/rate_list",
                    headers={
                        "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
                        "Host": "www.tplusmobile.com",
                        "referer": "https://www.tplusmobile.com/main/rate/join",
                        "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"
                    },
                    data=f"tp=N&key={key}&keyword=&seloptiontp=&selcompanytp=&recommendtp=&filter_data_s=0&filter_data_e=10001&filter_voice_s=0&filter_voice_e=501&filter_price_s=0&filter_price_e=50001&selsorttp=",
                    verify=False  # 인증서 검증 무시
                )

                html = response.text
                                
                if not key:
                    # BeautifulSoup을 사용하여 HTML을 파싱
                    bs = BeautifulSoup(html, 'html.parser')
                    prettified_html = bs.prettify()  # 가독성을 높인 HTML로 변환
                    
                    # 가독성 좋은 HTML을 파일에 저장
                    with open('./response_files/list_tplus.html', 'w', encoding='utf-8') as file:
                        file.write(prettified_html)  # 파일에 prettified HTML 저장
                   

                if "조회된 데이터가 없습니다" in html:
                    break

                key = html.split('ㅹㆄ')[-1] # 해당 값이 의미하는 바가 먼지 모르겠음 (양성훈)

                bs = BeautifulSoup(html, 'html.parser')
                li_list = bs.find_all('li')

                for item in li_list:
                    try:
                        href = item.find('button', 'plan-view-more')['onclick'].split('\'')[-2]
                        uuid = href.split('seq=')[-1]
                        
                        # print(f"Generated UUID: {uuid}")


                        plan_name = item.find('div', 'plan-name').find('p').text
                        plan_type = item.find('button', 'plan-signup')['data-networktp']

                        telecom = ''
                        telecomText = item.find('div', 'plan-service').find('span').text
                        if telecomText == 'LGU+':
                            telecom = Telecom.LGU.name
                        elif telecomText == 'KT':
                            telecom = Telecom.KT.name
                        elif telecomText == 'SKT':
                            telecom = Telecom.SKT.name
                      
                        
                        # plan-data 텍스트 가져오기
                        data_text = item.find('p', 'plan-data').text

                        # 데이터 값 분리
                        data_split = data_text.split('+')

                        # 초기값 설정
                        data = ''
                        daily_data = ''
                        qos = ''

                        for part in data_split:
                            part = part.strip()

                            # QoS (속도 제한) 정보 추출 (Mbps, Kbps 값이 있는 경우)
                            match_qos = re.search(r'(\d+(?:\.\d+)?)\s*(Mbps|Kbps)', part, re.IGNORECASE)
                            if match_qos:
                                qos = match_qos.group(0).strip()  # 예: "5Mbps", "3Mbps" 등
                                continue  # QoS는 data 값이 아니므로 제외하고 넘어감

                            # 일일 제공 데이터 추출
                            if "일" in part:  
                                match_daily = re.search(r'(\d+(?:\.\d+)?)\s*(GB|MB)', part, re.IGNORECASE)
                                if match_daily:
                                    value, unit = match_daily.groups()
                                    if value.endswith('.0'):
                                        value = value[:-2]
                                    daily_data = f"{value}{unit}"
                            else:
                                # 기본 데이터 (최대값 유지)
                                match_data = re.search(r'(\d+(?:\.\d+)?)\s*(GB|MB)', part, re.IGNORECASE)
                                if match_data:
                                    value, unit = match_data.groups()
                                    if value.endswith('.0'):
                                        value = value[:-2]
                                    # 현재 저장된 data 값과 비교하여 큰 값 유지
                                    if not data or float(value) > float(data.replace("GB", "").replace("MB", "")):
                                        data = f"{value}{unit}"

                        # 만약 데이터가 없는 경우 기본값 설정
                        if not data:
                            data = "0GB"




                        

                        voice_call = item.find('p', 'plan-call').find('span').text
                        message = item.find('p', 'plan-message').find('span').text

                        plan_before_price = item.find('span', 'plan-before-price')
                        sale_price = item.find('span', 'plan-final-price').text.split(' ')[-1].replace(',', '').replace('원', '')

                        if plan_before_price is None:
                            normal_price = sale_price
                        else:
                            normal_price = item.find('span', 'plan-before-price').text.split(' ')[-1].replace(',', '').replace('원', '')

                        after_text_split = item.find('p', 'plan-af-price').text.split(' 후 ')
                        if len(after_text_split) < 2:
                            promotion_period = "평생"
                            after_price = after_text_split[0].split(' ')[-1].replace(',', '').replace('원', '')
                        else:
                            promotion_period = after_text_split[0]
                            after_price = after_text_split[1].split(' ')[-1].replace(',', '').replace('원', '')

                        if not after_price.isdigit():
                            after_price = sale_price
                            
                        # "ext-info-box" 클래스에서 "부가통화" 텍스트를 포함한 <p> 태그 찾기
                        ext_info_box = item.find('div', class_='ext-info-box')
                        buga_call = ''  # 초기값 설정

                        if ext_info_box:
                            # "부가통화" 텍스트를 포함한 <p> 태그 찾기
                            p_tags = ext_info_box.find_all('p', text=lambda t: t and '부가통화' in t)
                            for p_tag in p_tags:
                                # "부가통화" 다음의 숫자와 "분" 추출
                                match = re.search(r'부가통화\s*(\d+분)', p_tag.text)
                                if match:
                                    buga_call = match.group(1)  # 예: "300분" 추출
                                    break  # 첫 번째 매칭된 값만 사용
                        
                        benefits = []
                        benefit_p_tags = item.find('div', 'ext-info-box').find_all('p')
                        for ptag in benefit_p_tags:
                            benefits.append(ptag.text)
                        if len(benefits) > 0:
                            benefit = '|'.join(benefits)
                        else:
                            benefit = ''

                        # 인증서 검증을 무시하고 상세 정보 요청
                        response = _session.get(
                            f'https://www.tplusmobile.com/main/rate/plan_details?seq={uuid}',
                            verify=False  # 인증서 검증 무시
                        ).text

                        filter_result = filter_detail(response)

                        combination = False
                        if '결합가능' in item.text:
                            combination = True

                        bs2 = BeautifulSoup(response, 'html.parser')
                        plan_desc = bs2.find('div', 'plan-desc')
                        plan_desc_tit = plan_desc.find_all('div', 'con-tit')
                        plan_desc_txt = plan_desc.find_all('div', 'con-txt')

                        data_detail = ''
                        voice_call_detail = ''
                        message_detail = ''
                        over_fee_detail = ''

                        for idx, div in enumerate(plan_desc_tit):
                            if div.text == '데이터 이용 안내':
                                data_detail = plan_desc_txt[idx].text.strip()
                            elif div.text == '통화 이용 안내':
                                voice_call_detail = plan_desc_txt[idx].text.strip()
                            elif div.text == '문자 이용 안내':
                                message_detail = plan_desc_txt[idx].text.strip()
                            elif div.text == '요금제 초과 요율':
                                over_fee_detail = plan_desc.find('div', 'con-table').find('table')


                        
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


                        detail_dto = DetailInfo(
                            uuid= 'TPLUS_' + uuid,
                            data=data_detail,
                            voice_call=voice_call_detail,
                            message=message_detail,
                            precautions='',
                            over_fee=over_fee_detail,
                            event='',
                            micropayment=filter_result[0],
                            overseas_roaming=filter_result[1],
                            mobile_hotspot=filter_result[2],
                            data_sharing=filter_result[3],
                        )
                        detail_result.append(detail_dto)

                        dto = PlanData(
                            uuid= 'TPLUS_' + uuid,
                            mno = telecom,
                            telecom = SiteTargetListType.TPLUS_LIST.value,
                            company_id = SiteTargetListIDType.TPLUS_LIST.value,  # company_id 추가
                            url='https://www.tplusmobile.com/SMTONG/rate/plan_details?seq=' + uuid,
                            plan_type=plan_type,
                            plan_name=plan_name,
                            data=data,
                            voice_call=voice_call,
                            message=message,
                            normal_price=normal_price,
                            sale_price=sale_price,
                            benefit=benefit,
                            qos=qos,
                            business_name='㈜한국케이블텔레콤',
                            after_price=after_price,
                            combination=combination,
                            freebies='',
                            etc='',
                            promotion_period=promotion_period,
                            buga_call = buga_call,
                            plan_code = '',
                            daily_data=daily_data,
                            m12_price=m12_price,
                            m24_price=m24_price,
                        )
                        result.append(dto)

                    except Exception as e:
                        traceback.print_exc()

            except Exception as e:  # try 문에 대한 전체 예외 처리 추가
                traceback.print_exc()  # 에러 추적

            is_end = True

        # AllData 객체를 콘솔에 JSON으로 출력
        all_data = AllData(planData=result, detailInfo=detail_result)
        # print(json.dumps(all_data, default=str, ensure_ascii=False, indent=4))  # JSON 형식으로 출력

        return all_data, is_end
