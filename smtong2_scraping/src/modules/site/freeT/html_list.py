
import json
import time
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import AllData, DetailInfo, PlanData, Telecom

from utils.etc.functions import filter_detail
from utils.site.url_maker import UrlParm, make_url

TAG = '[FreeT]'

class FreeTListAction():

    def root(self,
        page: int,
        *args,
        **kwargs) -> tuple[list[PlanData], bool]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result =[]
        detail_result =[]

        pageNo =1
        while True:
            #리스트 url 응답
            list_url = f'https://api.freet.co.kr/plan/v1/list?rowSize=5&pageNo={pageNo}&onlineAuth=Y&_=1730101169795'
            print(f"{TAG} HTTP GET 요청 시작 (page {pageNo}): {list_url}")
            req_start = time.time()
            response = requests.get(list_url,
                                    headers={
                "Host":"api.freet.co.kr",
                "user-agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
                }
            )
            req_elapsed = time.time() - req_start
            print(f"{TAG} HTTP 응답 완료 ({req_elapsed:.1f}초), 상태코드: {response.status_code}")
            json = response.json()['data']['ratePlans']

            pageNo +=1
            if len(json) == 0:
                print(f"{TAG} 더 이상 데이터 없음, 페이징 종료")
                break

            print(f"{TAG} 페이지 {pageNo-1}에서 요금제 {len(json)}건 발견")
            for idx, item in enumerate(json):
                try:
                    uuid = item['svcCd']
                    plan_type = item['genCd']
                    
                    telecom =''
                    telecomText = item['comType']
                    if telecomText == 'freeC':
                        telecom = Telecom.KT.name
                    elif telecomText == 'freeT':
                        telecom = Telecom.LGU.name
                    elif telecomText == 'freeS':
                        telecom = Telecom.SKT.name
                    
                    data = item['freeData'].replace('월','')
                    basic_price = item['basicFee']
                    discount = item['foreverDiscAmt']
                    after_price = int(basic_price) - int(discount)
                    sale_price = item['monthlyFee']
                    
                    benefits =[]
                    combination = False
                    for benefit_unit in item['benefits']:
                        if '바로가기' in benefit_unit['beneDesc']:
                            continue
                        elif '결합 가능' in benefit_unit['beneDesc']:
                            combination = True
                        else:
                            benefits.append(benefit_unit['beneDesc'])
                    if len(benefits)>0:
                        benefit = '|'.join(benefits)
                    else:
                        benefit = ''
                        
                    api_url = f'https://api.freet.co.kr/plan/v1/detail?svcCd={uuid}'
                    print(f"{TAG} [{idx+1}/{len(json)}] 상세 API 요청: {item['svcName']}")
                    detail_start = time.time()
                    response = requests.get(api_url).json()['data']
                    detail_elapsed = time.time() - detail_start
                    print(f"{TAG} [{idx+1}/{len(json)}] 상세 API 응답 완료 ({detail_elapsed:.1f}초)")
                    
                    data_detail = ''
                    voice_call_detail = ''
                    message_detail = ''
                    over_fee =''
                    
                    full_text =''
                    for info in response['infoSubList']:
                        content_text = BeautifulSoup(info['content'].replace('&nbsp;',' '),'html.parser').text
                        full_text += content_text
                        if '음성' in info['itemName']:
                            voice_call_detail = content_text
                        elif '메시지' in info['itemName']:
                            message_detail = content_text
                        elif '데이터' in info['itemName']:
                            data_detail = content_text
                        elif '초과' in info['itemName']:
                            over_fee = content_text
                        
                    for benefit2 in response['feeBenefitList']:
                        full_text +=BeautifulSoup( benefit2['rateBeneDesc'],'html.parser').text

                    filter_result = filter_detail(full_text)
                    
                    detail_dto = DetailInfo(
                        uuid=uuid,
                        data=data_detail,
                        voice_call= voice_call_detail,
                        message= message_detail,
                        precautions= '',
                        over_fee= over_fee,
                        event='',
                        micropayment=filter_result[0],
                        overseas_roaming=filter_result[1],
                        mobile_hotspot=filter_result[2],
                        data_sharing=filter_result[3],
                    )
                    detail_result.append(detail_dto)
                        
                    dto = PlanData(
                            uuid= uuid,
                            mno = telecom,
                            telecom = SiteTargetListType.FREET_LIST.value,
                            company_id=SiteTargetListIDType.FREET_LIST.value,  # company_id 추가
                            url = f'https://www.freet.co.kr/plan/ratePlan/detail?svcCd={uuid}',
                            plan_type= plan_type,
                            plan_name= item['svcName'],
                            data = data.split('+')[0].replace('매일',''),
                            voice_call = item['freeVoice'],
                            message = item['freeSms'],
                            normal_price = basic_price,
                            sale_price =sale_price,
                            benefit = benefit,
                            qos= item['qos'] if item['qos'] !=None else '',
                            business_name='(주)프리텔레콤',
                            after_price= after_price,
                            combination= combination,
                            freebies='',
                            etc='',
                            promotion_period = str(item['periodDiscMonth'])+'개월',
                            plan_code = '',
                            daily_data='',
                            m12_price='',
                            m24_price='',
                        )
                    result.append(dto)
                    success_count += 1
                    print(f"{TAG} [{idx+1}/{len(json)}] 파싱 완료: {telecom} | {item['svcName']}")

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{idx+1}/{len(json)}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        is_end = True

        return AllData(
                planData=result,
                detailInfo= detail_result
            ),is_end




