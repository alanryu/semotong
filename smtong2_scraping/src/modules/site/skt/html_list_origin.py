
import json
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData

from utils.site.data_preprocess import preprocessAfterContentOriginResponse
from utils.site.url_maker import UrlParm, make_url

class SktListAction():

    def root(self,
        page: int,
        *args,
        **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result =[]
        paramList =[{'type':'LTE',
                     'code':'F01121',
                     'opClCd':'02'
                     },
                    {'type':'5G',
                     'code':'F01713',
                     'opClCd':'02'
                     },
                    {'type':'3G',
                     'code':'F01122',
                     'opClCd':'02'
                     },
                   ]
        for param in paramList:
            #리스트 url
            base_url='https://www.tworld.co.kr/core-product/v1/product/mobile/plan-device-list'
            idxCtgCd = param['code']
            opClCd = param['opClCd']
            
            if idxCtgCd =='F01180':
                base_url = 'https://www.tworld.co.kr/core-product/v1/submain/overall-product'
            
            url = make_url(base_url=base_url,
                        url_params=[
                            UrlParm(
                                key='idxCtgCd',
                                value= idxCtgCd
                            ),
                            UrlParm(
                                key='opClCd',
                                value= opClCd
                            ),
                            UrlParm(
                                key='size',
                                value='10000'
                            ),
                            UrlParm(
                                key='page',
                                value=f'{page}'
                            ),
                            UrlParm(
                                key='order',
                                value='recommend'
                            ),
                            UrlParm(
                                key='searchFltIds',
                                value='null'
                            ),
                        ]
                        )
            #리스트 url 응답
            response = requests.get(url, headers={
                "Host":"www.tworld.co.kr",
                "referer":"https://www.tworld.co.kr/web/product/plan/list",
            })        
            data = []  
            if 'mobilePlanList' in response.json()['result'] :
                data = response.json()['result']['mobilePlanList']
            elif 'separateProductList' in response.json()['result']:
                data = response.json()['result']['separateProductList']
            else:
                break
                
            for item in data:
                try:
                    uuid = item['prodId']                
       
                    detail_response = requests.get(f'https://www.tworld.co.kr/core-product/v1/benefits/{uuid}/price-plan', headers={
                        "Host":"www.tworld.co.kr",
                        "referer": f"https://www.tworld.co.kr/web/product/callplan/{uuid}"
                    })  
                                        
                    
                    benefits =[]
                    
                    #혜택 리스트
                    benefitList = detail_response.json()['result']
                                            
                    for benefit_unit in benefitList:
                        if "commPhrs" in benefit_unit:
                            benefits.append(benefit_unit["commPhrs"])
                        
                            
                    if len(benefits)>0:
                        benefit = '|'.join(benefits)
                    else:
                        #형식이 다른경우
                        detail_response = requests.get(f'https://www.tworld.co.kr/core-product/v1/ledger/{uuid}/summaries?_=1729216407747', headers={
                            "Host":"www.tworld.co.kr",
                            "referer": f"https://www.tworld.co.kr/web/product/callplan/{uuid}"
                        })
                        if  "prodBenfAreaList" in detail_response.json()['result']:  
                            #혜택 리스트
                            benefitList = detail_response.json()['result']['prodBenfAreaList']
                            for benefit_unit in benefitList:
                                for prodBen in benefit_unit["prodBenfList"]:
                                    benefits.append(prodBen["prodBenfNm"])
                        
                        if len(benefits) == 0:
                            benefit = ''      
                        else:    
                            benefit = '|'.join(benefits)
                            
                    data = ''
                    
                    if len(item['basOfrGbDataQtyCtt'])>0:
                        data = item['basOfrGbDataQtyCtt'] +'GB' if item['basOfrGbDataQtyCtt'] !='무제한' else item['basOfrGbDataQtyCtt']
                    elif len(item['basOfrMbDataQtyCtt'])>0:
                        if item['basOfrMbDataQtyCtt'] != '함께쓰기':
                            data = item['basOfrMbDataQtyCtt'] + 'MB'
                        else:
                            data = item['basOfrMbDataQtyCtt']
                    else:
                        data=''
                    
                    dto = PlanData(
                            uuid= uuid,
                            mno = 'SKT',
                            telecom = SiteTargetListType.SKT_LIST.value,
                            company_id=SiteTargetListIDType.SKT_LIST.value,  # company_id 추가
                            url =f'https://www.tworld.co.kr/web/product/callplan/{uuid}',
                            plan_type=param['type'],
                            plan_name=item['prodNm'],
                            data = data,
                            voice_call = item['basOfrVcallTmsCtt']+'분' if item['basOfrVcallTmsCtt'].isdigit() else item['basOfrVcallTmsCtt'],
                            message =item['basOfrCharCntCtt']+'건' if item['basOfrCharCntCtt'].isdigit() else item['basOfrCharCntCtt'],
                            normal_price = item['basFeeInfo'],
                            sale_price = item['selAgrmtAplyMfixAmt'] if int(item['selAgrmtAplyMfixAmt'])>0 else item['basFeeInfo'],
                            benefit = benefit,
                            qos='',
                            business_name='에스케이텔레콤(주)',
                            after_price= item['selAgrmtAplyMfixAmt'] if int(item['selAgrmtAplyMfixAmt'])>0 else item['basFeeInfo'],
                            combination='',
                            freebies='',
                            etc='',
                            promotion_period = '',
                            buga_call= '',
                            plan_code = '',
                        )
                    
                    result.append(dto)
                        
                except Exception as e:
                        traceback.print_exc()
                        
        print(f"Processed {len(result)} plans. Is end: {is_end}")
        if len(result)==0:
            is_end = True 

        return result,is_end




