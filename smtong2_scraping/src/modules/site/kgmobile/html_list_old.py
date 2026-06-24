
import json
import re
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import AllData, DetailInfo, PlanData, Telecom

from utils.etc.functions import filter_detail
from utils.site.data_preprocess import preprocessAfterContentOriginResponse
from utils.site.url_maker import UrlParm, make_url

class KgMobileListAction():
    # print("KG모바일 시작\n")

    def root(self,
        page: int,
        *args,
        **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result =[]
        detail_result =[]
        
        #리스트 url
        url_list = [
            f'https://www.kgmobile.co.kr/api/product/plan?block=5&isAdult=&isUser=true&limit=-1&network=&orderType=RECOMMEND&page={page}&searchAmount=&searchData=&useFlag=Y',
        ]
        for base_url in url_list:
            
            url = make_url(base_url=base_url)
            # 리스트 url 응답
            response = requests.get(url)   

            # 파일 이름 생성
            filename = f"response_kgmobile_list.json"

            # 응답 데이터를 JSON 형식으로 변환
            response_json = response.json()

            # 가독성 좋게 JSON 파일로 저장
            with open(filename, "w", encoding="utf-8") as file:
                json.dump(response_json, file, ensure_ascii=False, indent=4)
            print(f"Response saved to {filename}")
                 
            li_list = response.json()["entity"]["list"]
            for item in li_list:
                try:
                    planNo = str(item["planNo"])
                    
                    telecom = ''
                    if item['telco'] == 'LGT':
                        telecom = Telecom.LGU.name
                    elif item['telco'] == 'SKT':
                        telecom = Telecom.SKT.name
                    elif item['telco'] == 'KT':
                        telecom = Telecom.KT.name
                    
                                    
                    # detail_url 에서 html 내려받고 특정 내용을 찾는 로직 추가
                    detail_url = f"https://www.kgmobile.co.kr/plan/{planNo}"
                    detail_response = requests.get(detail_url)  # detail_url 요청
                   
                    
                    # HTML 파싱
                    detail_soup = BeautifulSoup(detail_response.text, 'html.parser')

                    # 파싱된 내용을 가독성 있게 출력
                    print("Parsed HTML (prettified):")
                    print(detail_soup.prettify())
                    

                    # <div class="calling_bottom"> 찾기
                    combination = False
                    calling_bottom_div = detail_soup.find('div', class_='calling_bottom')

                    # '인터넷 결합 안내' 또는 '인터넷결합안내' 포함 여부 확인
                    if calling_bottom_div and ('인터넷 결합 안내' in calling_bottom_div.text or '인터넷결합안내' in calling_bottom_div.text):
                        combination = True
                    
                    
                    
                    bs = BeautifulSoup(item["contents"], 'html.parser')
                    benefit = preprocessAfterContentOriginResponse(bs.text)
                    bs = BeautifulSoup(item["notice"], 'html.parser')
                    notice = preprocessAfterContentOriginResponse(bs.text)

                    h1 = bs.find('h1')
                    if h1 != None:
                        pass      
                    
                    
                    
                    data =''
                    if item["basicMonthData"]!="-1":
                        data = item["basicMonthData"] + item["basicMonthDataUnit"]
                    else:
                        if item["basicDayData"] !="-1":
                            data = str(item["basicDayData"])+item["basicDayDataUnit"]
                        else:
                            data = '무제한'
                            
                    message = item["basicSms"]
                    if message=="-1":
                        message = "무제한"
                    elif message=="-":
                        message = ""
                    else:
                        message += "건"
                        
                    uuid = 'KG_'+planNo
                    filter_result = filter_detail(benefit+notice)          
                    
                    voice_call_detail = ''
                    message_detail = ''
                    data_detail =''
                    
                    pattern = r'\[([^\]]+)\]'
                    bs_notice = BeautifulSoup(item["notice"], 'html.parser').text
                    split_notice = re.split(pattern, bs_notice)  
                                        
                    for idx, matche in enumerate(split_notice):
                        if matche == '음성통화':
                            voice_call_detail = split_notice[idx+1].strip()  
                        elif matche == '메시지':
                            message_detail = split_notice[idx + 1].strip()
                        elif matche == '데이터':
                            data_detail = split_notice[idx + 1].strip()
                            
                            
                    # Plan type 처리
                    plan_type = item["network"]
                    if plan_type == "4G":
                        plan_type = "LTE"
                    
                    detail_dto = DetailInfo(
                        uuid=uuid,
                        data= data_detail,
                        voice_call= voice_call_detail,
                        message= message_detail,
                        precautions= '',
                        over_fee='',
                        event=benefit,
                        micropayment=filter_result[0],
                        overseas_roaming=filter_result[1],
                        mobile_hotspot=filter_result[2],
                        data_sharing=filter_result[3],
                    )
                    detail_result.append(detail_dto)
                    
                    normal_price = item["basicAmount"]
                    
                    sale_price = int(item["basicAmount"]) - int(item["saleList"][0]["prSaleAmount"]) if len(item["saleList"])>0 else item["basicAmount"]
                    if not sale_price:  # sale_price가 None, 0 또는 빈 문자열인 경우
                        sale_price = normal_price
                                
                    after_price = int(item["basicAmount"]) - int(item["saleList"][0]["prSaleAmount"]) if len(item["saleList"])>0 else item["basicAmount"]
                    if not after_price:  # after_price가 None, 0 또는 빈 문자열인 경우
                        after_price = normal_price
                        
                    # promotion_period 값을 contents에서 추출 (1개월 이상 모든 개월 수 및 "평생" 포함)
                    contents_text = item.get("contents", "")

                    # "평생"이 있는지 먼저 확인하고, 있으면 "평생" 처리
                    if "할인기간 제한이 없는 평생 할인 요금제입니다." in contents_text:
                        promotion_period = "평생"
                    else:
                        # "가입월 포함 *개월간 적용" 형태로 개월 수 추출
                        match = re.search(r"가입월 포함\s*(\d+)개월간 적용됩니다", contents_text)
                        if match:
                            promotion_period = f"{match.group(1)}개월"
                        else:
                            promotion_period = ""  # 해당하는 기간이 없을 경우 빈 문자열

                    
                    
                    dto = PlanData(
                            uuid= uuid,
                            mno = telecom,
                            telecom= SiteTargetListType.KG_MOBILE_LIST.value,
                            company_id=SiteTargetListIDType.KG_MOBILE_LIST.value,  # company_id 추가
                            url = detail_url,
                            plan_type= plan_type,
                            plan_name= item["planName"],
                            data = data,
                            voice_call = item["basicVoice"]+"분" if item["basicVoice"]!="-1" else "무제한",
                            message = message,
                            normal_price = normal_price,
                            after_price= normal_price,
                            sale_price = sale_price,
                            benefit = '',
                            qos=item["basicQos"].strip(),
                            business_name='주식회사 케이지모빌리언스',
                            combination = combination,
                            freebies='',
                            etc='',
                            promotion_period = promotion_period # promotion_period_text의 내용중에서 찾아야함
                        )
                    result.append(dto)
                        
                except Exception as e:
                        traceback.print_exc()
            

        is_end = True 
                
        return AllData(
                planData=result,
                detailInfo= detail_result
            ),is_end




