
import json
import time
import traceback
from bs4 import BeautifulSoup
from datetime import datetime
import re
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import AllData, DetailInfo, PlanData, Telecom

from utils.etc.functions import filter_detail
from utils.site.url_maker import UrlParm, make_url

TAG = '[ShakeMobile]'

class ShakeMobileListAction():

    def root(self,
        page: int,
        *args,
        **kwargs) -> tuple[list[PlanData], bool]:
        start_time = time.time()
        print(f"{TAG} 스크래핑 시작")
        is_end = False
        result =[]
        detail_result =[]
        success_count = 0
        skip_count = 0
        error_count = 0

        #리스트 url
        url_list = [
            'https://shakemobile.co.kr/M2Mobile/getTG01ByLtediv'
        ]
        for base_url in url_list:

            url = make_url(base_url=base_url)
            #리스트 url 응답
            t0 = time.time()
            print(f"{TAG} 목록 API 요청: {base_url}")
            response = requests.post(url,
                                    headers={
                "referer":"https://shakemobile.co.kr/M2Mobile/Set3G",
                "content-type":"application/json",
                "user-agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36",
                "x-requested-with":"XMLHttpRequest",
                "content-length":"17",
                "origin":"https://shakemobile.co.kr"
                },
                json= {"sLteDiv":"all"},
            )     
            json_list = response.json()
            elapsed = time.time() - t0
            print(f"{TAG} 목록 API 응답 완료 ({elapsed:.1f}초)")
            total_count = len(json_list)
            print(f"{TAG} 요금제 {total_count}건 발견")

            # JSON 데이터를 가독성 좋게 파일로 저장
            
            filename = "./response_files/list_ShakeMobile.json"
            with open(filename, "w", encoding="utf-8") as file:
                json.dump(json_list, file, ensure_ascii=False, indent=4)  # 가독성을 위해 indent 추가
           #  print(f"Response saved to {filename}") 
            

           
            for idx, item in enumerate(json_list):
                try:
                    plan_type = 'LTE'
                    if item['netdiv'] == '5G':
                        plan_type ='5G'
                
                    uuid = item['gdcd']
                    plan_name = item['gdnm']
                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {plan_name}")
                    plan_type = plan_type
                    

                    # ✅ seedata 분석 로직
                    seedata_raw = item.get('seedata', '').replace('<br>', '+')
                    split_data = seedata_raw.split('+')

                    total_data_gb = 0.0
                    daily_data = ''
                    qos = ''
                    data = ''

                    for part in split_data:
                        part = part.strip()

                        # ✅ QoS 값 추출 (Mbps 또는 Kbps 포함된 경우)
                        match_qos = re.search(r'(\d+(?:\.\d+)?)\s*(Mbps|Kbps)', part, re.IGNORECASE)
                        if match_qos:
                            qos = match_qos.group(0).strip()
                            continue

                        # ✅ 일제공 데이터 추출
                        match_daily = re.search(r'일\s*(\d+(?:\.\d+)?)\s*(GB|MB)', part, re.IGNORECASE)
                        if match_daily:
                            value, unit = match_daily.groups()
                            if value.endswith('.0'):
                                value = value[:-2]
                            daily_data = f"{value}{unit}"
                            continue

                        # ✅ 일반 데이터 추출 (추가10GB, 10GB, 등)
                        match_data = re.search(r'(\d+(?:\.\d+)?)\s*(GB|MB)', part, re.IGNORECASE)
                        if match_data:
                            value, unit = match_data.groups()
                            num = float(value)
                            # MB는 GB로 변환
                            if unit.upper() == 'MB':
                                num /= 1024
                            total_data_gb += num

                    # ✅ 결과 적용
                    if total_data_gb > 0:
                        data = f"{int(total_data_gb) if total_data_gb.is_integer() else round(total_data_gb, 1)}GB"
                    elif re.fullmatch(r'\d+\s*(Kbps|Mbps)', seedata_raw.strip(), re.IGNORECASE):
                        data = seedata_raw.strip()
                    else:
                        data = '0GB'

                    # ✅ daily_data, qos 기본값 설정
                    daily_data = daily_data or '0GB'
                    qos = qos or '0Mbps'



                        
                    voice_call=item['voiceamountv'].split('(')[0]
                    message= item['letteramountv'].replace('-','')
                    
                    normal_price = str(int(item['amt'] * 1.1))
                    



                    # 할인금액  계산 (sale_price)
                    tt_AMT = int( item.get('tt_AMT', 0))
                    sale_price = tt_AMT  * 1.1

                    # 정수 변환
                    sale_price = int(sale_price)

                    
                    # 프로모션 종료 후 계산(after_price)
                    life_TIME_DISCOUNT = int(item.get('life_TIME_DISCOUNT', 0))
                    amt = int(item.get('amt', 0))
                    after_price = (amt - life_TIME_DISCOUNT) * 1.1

                    promotion_period =str(item['promo_MM']) + '개월'
                    
                    detail_url = f'https://shakemobile.co.kr/M2Mobile/feeDetail/{uuid}'
                    t1 = time.time()
                    response = requests.get(detail_url).text
                    elapsed_detail = time.time() - t1
                    print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 응답 완료 ({elapsed_detail:.1f}초)")
                    filter_result = filter_detail(response)
                    bs = BeautifulSoup(response,'html.parser')
                    telecom = bs.find('li', 'primary')
                    if telecom is not None:
                        telecom = telecom.text
                    else:
                        telecom = "Unknown"
                        
                    # "Unknown"인 경우 PlanData 추가하지 않음
                    if telecom == "Unknown":
                        print(f"{TAG} [{idx+1}/{total_count}] 스킵: telecom=Unknown (uuid={uuid})")
                        skip_count += 1
                        continue
                    
                    buga_call = item['free_AMT']
                    # "buga_call" 값을 처리
                    if int(buga_call) == 0:
                        buga_call = ""
                    else:
                        buga_call = f"{buga_call}분"
                 
                    url = f'https://shakemobile.co.kr/M2Mobile/feeDetail/{uuid}'
                    uuid = "SHAKE_" + uuid    

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
                        uuid=uuid,
                        data='',
                        voice_call= '',
                        message= '',
                        precautions= '',
                        over_fee='',
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
                            telecom = SiteTargetListType.SHAKE_MOBILE_LIST.value,
                            company_id=SiteTargetListIDType.SHAKE_MOBILE_LIST.value,  # company_id 추가
                            url = url,
                            plan_type=plan_type,
                            plan_name= plan_name,
                            data = data ,
                            voice_call = voice_call,
                            message = message,
                            normal_price = normal_price ,
                            sale_price =sale_price,
                            benefit = '',
                            qos= qos,
                            business_name='(주)미니게이트',
                            after_price= after_price,
                            combination="",
                            freebies='',
                            etc='',
                            promotion_period = promotion_period,
                            buga_call = buga_call,
                            plan_code = '',
                            daily_data=daily_data,
                            m12_price=m12_price,
                            m24_price=m24_price,
                        )
                    result.append(dto)
                    success_count += 1

                except Exception as e:
                        error_count += 1
                        print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                        traceback.print_exc()

        is_end = True
        elapsed_total = time.time() - start_time
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초) | 성공: {success_count}, 스킵: {skip_count}, 에러: {error_count}")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")

        return AllData(
                planData=result,
                detailInfo= detail_result
            ),is_end




