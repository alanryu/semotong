
import json
import traceback
from bs4 import BeautifulSoup
import re
import requests
import time
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import AllData, DetailInfo, PlanData, Telecom

from utils.etc.functions import fetch_url_with_retry, filter_detail
from utils.site.url_maker import UrlParm, make_url

TAG = '[EyesMobile]'

class EyesMobileListAction():

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

        url = "https://eyes.co.kr/payplan/get_PlanList"

        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                        "AppleWebKit/537.36 (KHTML, like Gecko) "
                        "Chrome/120.0.0.0 Safari/537.36",
            "Accept": "application/json, text/javascript, */*; q=0.01",
            "Referer": "https://eyes.co.kr/",
            "X-Requested-With": "XMLHttpRequest",
        }

        print(f"{TAG} HTTP GET 요청 시작: {url}")
        req_start = time.time()
        response = requests.get(url, headers=headers)
        req_elapsed = time.time() - req_start
        print(f"{TAG} HTTP 응답 완료 ({req_elapsed:.1f}초), 상태코드: {response.status_code}")

        json_data = response.json()['planList']
        total_count = len(json_data)
        print(f"{TAG} 요금제 {total_count}건 발견")
        
        # json_data 변수에 담긴 내용을 파일로 저장
        
        try:
            with open("./response_files/list_eyesmobile.json", "w", encoding="utf-8") as file:
                json.dump(json_data, file, ensure_ascii=False, indent=4)
        except Exception as e:
            print("Error saving JSON to file:", e)
            traceback.print_exc()
        

        for idx, item in enumerate(json_data):
            try:
                uuid = item['ori']['PROD_CD']
                plan_name = item['ori']['SERVICE_NAME']
                plan_type = '5G' if (("5G" in plan_name) and ("5GB" not in plan_name)) else 'LTE'
                
                telecom =''
                telecomText = item['telco']
                if telecomText == 'LGT':
                    telecom = Telecom.LGU.name
                elif telecomText == 'KT':
                    telecom = Telecom.KT.name
                elif telecomText == 'SKT':
                    telecom = Telecom.SKT.name
                
                
               # 기본값 설정
                data = "0GB"
                daily_data = ""

                basic_data_txt = item.get("basic_data_txt", "").strip()
                basic_data_add_unit = item.get("basic_data_add_unit", "").strip()

                # 기본값 설정
                data = ''
                daily_data = ''

                # 데이터 관련 필드 가져오기
                basic_data_txt = item.get("basic_data_txt", "").strip()
                basic_data_add_txt = item.get("basic_data_add_txt", "").strip()

                # 기본 제공 데이터 처리 (basic_data_txt)
                if basic_data_txt:
                    # "5GB/일" 같은 형태인지 확인
                    if "일" in basic_data_txt:
                        match = re.search(r"(\d+(?:\.\d+)?)\s*(GB|MB)", basic_data_txt, re.IGNORECASE)
                        if match:
                            value, unit = match.groups()
                            daily_data = f"{value.rstrip('.0')}{unit}"  # 소수점 제거 후 저장
                            data = "0GB"  # 매일 제공되는 데이터라면 기본 데이터는 없음
                    else:
                        data = basic_data_txt  # 일반적인 기본 데이터

                # 기본 제공 데이터가 "없음"이면 0GB 처리
                if data == "없음":
                    data = "0GB"

                # 매일 제공 데이터 처리 (basic_data_add_txt)
                if "일" in basic_data_add_txt:
                    match = re.search(r"(\d+(?:\.\d+)?)\s*(GB|MB)", basic_data_add_txt, re.IGNORECASE)
                    if match:
                        value, unit = match.groups()
                        daily_data = f"{value.rstrip('.0')}{unit}"  # 소수점 제거 후 저장


                normal_price = item['basic_fee']
                after_price = item['basic_fee']
                sale_price = item['sale_price']
                
                dc_period_txt = item.get('dc_period_txt', '')
                
                qos = item.get("data_qos", "").strip()

                # data_qos 값이 빈 문자열이거나 "0"일 때, goods_explain_text2에서 속도 정보 추출
                if not qos or qos == "0":
                    goods_explain = item.get("goods_explain_text2", "")

                    if goods_explain:  # None 체크 후 strip() 호출
                        goods_explain = goods_explain.strip().lower()
                        match = re.search(r"(\d+(?:\.\d+)?)\s*(kbps|mbps)", goods_explain, re.IGNORECASE)
                        
                        if match:
                            value, unit = match.groups()
                            unit = unit.capitalize()  # "kbps" -> "Kbps", "mbps" -> "Mbps"
                            qos = f"{value}{unit}"  # "200Kbps" 형식으로 저장
                        else:
                            qos = ""  # 패턴이 없으면 빈 문자열로 설정
                    else:
                        qos = ""  # goods_explain_text2가 None 또는 빈 문자열일 경우

                          
                payplan_gb = item['payplan_gb']
                payplan_seq = item['payplan_seq']
                url = f'https://eyes.co.kr/payplan/info_view/{payplan_seq}/{payplan_gb}'
                print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 요청: {plan_name}")
                detail_start = time.time()
                time.sleep(5) # 단시간 여러번의 페이지요청시 아이피 차단됨..
                detail_response = fetch_url_with_retry(url)
                detail_elapsed = time.time() - detail_start
                print(f"{TAG} [{idx+1}/{total_count}] 상세 페이지 응답 완료 ({detail_elapsed:.1f}초)")

                # 기본값 설정
                combination = False  

                # combi_flag 값 체크
                combi_flag = item.get('combi_flag', '')  # 기본값: 빈 문자열
                if combi_flag in ['SKT', 'LGT', 'KT']:
                    combination = True
                else:
                    combination = False  # combi_flag 조건이 맞지 않으면 False로 초기화

                # goods_explain_text1 값 체크 (None 방지)
                goods_explain_text1 = item.get('goods_explain_text1', '') or ''  # None이면 빈 문자열로 변환
                if re.search(r'인터넷|결합', goods_explain_text1, re.IGNORECASE):  # "인터넷" 또는 "결합" 포함 여부 확인
                    combination = True

                filter_result = filter_detail(detail_response)
                if ('let tethering_type = \'Y\'')in detail_response:
                    filter_result[2] = True
                    
                
                goods_explain = item.get('goods_explain_text1', '')  # 키가 없을 경우 기본값으로 빈 문자열 할당

                # goods_explain이 문자열이고 빈 값이 아니면 benefit에 goods_explain 할당
                if isinstance(goods_explain, str) and goods_explain.strip():
                    benefit = goods_explain
                else:
                    benefit = ''
                
                promotion_period =''   
                # item['dc_period'] 값이 0이 아닌 경우 promotion_period 설정
                if int(item['dc_period']) != 0:
                    if int(item['dc_period']) >= 100:
                        promotion_period = '평생'
                    else:
                        promotion_period = str(item['dc_period']) + '개월'
                        
                if promotion_period == '평생':
                    after_price = sale_price
                
                if promotion_period == '':
                    promotion_period = dc_period_txt
                    if promotion_period == '':
                        promotion_period = '평생'
                        
                buga_call = item.get('basic_add_txt', '')  # 키가 없을 경우 기본값으로 빈 문자열 할당
                if  buga_call == '없음':
                    buga_call = ''


            
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
                        uuid= 'EYES_'+ uuid,
                        mno = telecom,
                        telecom = SiteTargetListType.EYES_MOBILE_LIST.value,
                        company_id=SiteTargetListIDType.EYES_MOBILE_LIST.value,  # company_id 추가
                        url = url,
                        plan_type= plan_type,
                        plan_name= plan_name,
                        data = data,
                        voice_call = item['basic_voice_txt'].replace(',',''),
                        message = item['basic_sms_txt'].replace(',',''),
                        normal_price = normal_price,
                        sale_price =sale_price,
                        benefit = benefit,
                        qos= qos,
                        business_name='(주)아이즈비전',
                        after_price= after_price,
                        combination= combination,
                        freebies='',
                        etc='',
                        promotion_period = promotion_period,
                        buga_call= buga_call,
                        plan_code = '',
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price,
                    )
                result.append(dto)
                success_count += 1
                print(f"{TAG} [{idx+1}/{total_count}] 파싱 완료: {telecom} | {plan_name}")

            except Exception as e:
                error_count += 1
                print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
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




