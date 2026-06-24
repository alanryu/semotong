
import json
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData

from utils.site.url_maker import UrlParm, make_url

class MarvelringListAction():

    def root(self,
        page: int,
        *args,
        **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result =[]
        file_counter = 0  # 파일 번호를 위한 카운터 초기화
       
        
        #리스트 url
        url_list = [
            {
                'type':'LTE',
                'url': 'https://www.marvelring.com/rate_plan.do?type=T003'
            },
            {
                'type':'5G',
                'url': 'https://www.marvelring.com/rate_plan.do?type=T002'
            },
        ]
        for base_url in url_list:
            #리스트 url 응답
            response = requests.get(base_url['url'], 
                #                     headers={
                # "Host":"www.uplusumobile.com",
                # "user-agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
                # }
            )        
            html = response.text
            
            bs = BeautifulSoup(html, 'html.parser')
            li_list = bs.find_all('li','card_list_item')
            
            # print(li_list)    

            
            for item in li_list:
                try:
                    # <a> 태그 확인 및 href 값 추출
                    a_tag = item.find('a')
                    if not a_tag or 'href' not in a_tag.attrs:
                        # <a> 태그가 없거나 href 속성이 없으면 해당 item을 건너뜀
                        continue

                    href = a_tag['href']
                    
                    # ;로 시작하는 세션 ID 제거
                    if ';' in href:
                        href = href.split(';')[0] + '?' + href.split('?', 1)[1]
                    
                    aType = href.split('type=')[1].split('&')[0]
                    aNo = href.split('no=')[1]
                    uuid = aType + aNo

                    title = item.find('p', 'title')
                    title_span = title.find('span') if title else None
                    plan_name = title.text.replace(title_span.text, '') if title and title_span else "Unknown"
                    # title에서 QOS 부분 추출
                    qos = ''
                    if '+' in title.text:
                        qos = title.text.split('+')[-1].strip()  # + 이후의 값 추출 및 공백 제거
                    
                    
                    desc = item.find('ul', 'desc')
                    li_list2 = desc.find_all('li') if desc else []

                    data = ''
                    voice_call = ''
                    message = ''

                    mno_span = item.find('span', class_='text_light_color me-2')
                    mno = mno_span.text.strip() if mno_span else "Unknown"  # 값이 없으면 'Unknown'

                    # LGU+ 값을 LGU로 치환
                    if mno == "LGU+":
                        mno = "LGU"

                    if len(li_list2) == 3:
                        data = li_list2[0].text.split('월 데이터 ')[1]
                        voice_call = li_list2[1].text.split('음성통화 ')[1]
                        message = li_list2[2].text.split('문자 ')[1]
                    elif len(li_list2) == 4:
                        data = li_list2[0].text.split('월 데이터 ')[1]
                        voice_call = li_list2[2].text.split('음성통화 ')[1]
                        message = li_list2[3].text.split('문자 ')[1]
                    else:
                        print('exception')

                    price_full_text = item.find('div', 'price').text if item.find('div', 'price') else ""
                    if '이후' in price_full_text:
                        split_price = price_full_text.split('이후')
                        normal_price = split_price[0].split('원')[0].split('월')[1].strip().replace(',', '')
                        sale_price = split_price[1].split('원')[0].strip().replace(',', '')
                        promotion_period = split_price[0].split('*')[1].strip()
                    else:
                        normal_price = price_full_text.split(' ')[1].replace(',', '') if price_full_text else "0"
                        sale_price = normal_price
                        promotion_period = ''

                    combination = False
                    badge_text = item.find('span', 'badge').text if item.find('span', 'badge') else ""
                    if '결합 가능' in badge_text:
                        combination = True

                    dto = PlanData(
                        uuid=uuid,
                        mno=mno,
                        telecom=SiteTargetListType.MARVELRING_LIST.value,
                        company_id=SiteTargetListIDType.MARVELRING_LIST.value,  # company_id 추가
                        url='https://www.marvelring.com/' + href,
                        plan_type=base_url['type'],
                        plan_name=plan_name,
                        data=data,
                        voice_call=voice_call,
                        message=message,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit='',
                        qos= qos,
                        business_name='(주)마블프로듀스',
                        after_price=normal_price,
                        combination=combination,
                        freebies='',
                        etc='',
                        promotion_period=promotion_period,
                    )
                    result.append(dto)

                except Exception as e:
                    traceback.print_exc()
            
        is_end = True 
        
        return result,is_end




