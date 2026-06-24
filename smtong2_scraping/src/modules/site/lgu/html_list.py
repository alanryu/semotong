import json
import os
import time
import traceback
import requests
import re
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
from utils.site.url_maker import make_url

TAG = '[LGU]'

class LguListAction():

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        print(f"{TAG} 스크래핑 시작")
        total_start = time.time()
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0
        
        #paramList = [
        #    {'type': 'LTE', 'code': 'M10045'},
        #    {'type': '5G', 'code': 'M20395'},
        #]
        
        paramList = [
            {'type': 'ALL', 'code': 'M20162'},
        ]
        
        all_data = []  # JSON 데이터를 저장할 리스트

        for param in paramList:
            try:
                # 리스트 URL
                base_url = 'https://www.lguplus.com/uhdc/fo/prdv/mblppexhi/v1/list:get'
                url = make_url(base_url=base_url)

                # API 요청 수행
                t0 = time.time()
                print(f"{TAG} API 요청 시작: type={param['type']}")
                response = requests.post(url, json={
                    "ageGrpCd": "",
                    "mblPpExhiFilterCondList": [],
                    "menuId": "M20162",
                    "ppSortType": "SALE_ASC",
                })
                print(f"{TAG} API 응답 완료 ({time.time() - t0:.1f}초)")

                # API 응답 데이터를 JSON 파일에 저장
                data = response.json()
                # JSON 파일 경로 설정 (폴더가 존재한다고 가정)
                file_path = f'./response_files/lgu_direct_data.json'

                # API 응답 데이터를 JSON 파일로 저장
                with open(file_path, 'w', encoding='utf-8') as f:
                    json.dump(data, f, ensure_ascii=False, indent=4)

                price_plan_list = data.get('pricePlanList', [])
                total_count = len(price_plan_list)
                print(f"{TAG} 파싱 대상 요금제 수: {total_count}건")

                for idx, item in enumerate(price_plan_list):
                    try:
                        uuid = item['urcMblPpCd']
                        print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {item.get('mblProdPpNm', '')}")
                        menuEngNmPath = item['mblPpExhiMenu']['menuEngNmPath']
                        mblUrcMenuEngNm = item['mblPpExhiMenu']['mblUrcMenuEngNm']

                        data_value = "0GB"
                        daily_data_value = "0GB"
                        detail_voice_call = ""
                        detail_message = ""
                        detail_benefit = ""
                        detail_qos = "0Kbps"  # 기본값
                        detail_buga_call = "0분"  # 부가통화 기본값

                        for info in item['mblPpExhiMajrInfoList']:
                            if info['urcMblProdPpMajrItemCdNm'] == '데이터':
                                value = info['mblProdPpScrnOfqnNm'].strip()

                                # "무제한" → 데이터 = 9999GB
                                if "무제한" in value:
                                    data_value = "9999GB"
                                
                                # "월XGB+매일YGB" 패턴 (예: "월2GB+매일2GB")
                                elif match := re.search(r"월\s*(\d+)\s*GB\s*\+\s*매일\s*(\d+)\s*GB", value):
                                    data_value = f"{match.group(1)}GB"
                                    daily_data_value = f"{match.group(2)}GB"

                                # "일 XGB" 또는 "매일 XGB" 형태 (예: "일 5GB", "매일 5GB")
                                elif match := re.search(r"(?:일|매일)\s*(\d+)\s*GB", value):
                                    data_value = "0GB"
                                    daily_data_value = f"{match.group(1)}GB"

                                # 범위 값이 있는 경우 (예: "3GB~6GB", "250MB~1GB")
                                elif match := re.search(r"(\d+\.?\d*)\s*(MB|GB)\s*~\s*(\d+\.?\d*)\s*(MB|GB)", value):
                                    data_value = match.group(1) + match.group(2)  # 하한값 적용
                                    daily_data_value = "0GB"

                                # 일반적인 GB 단위 데이터 추출 (예: "10GB", "1.7GB")
                                elif match := re.search(r"(\d+\.?\d*)\s*GB", value):
                                    data_value = f"{match.group(1)}GB"
                                    daily_data_value = "0GB"

                                # MB 단위 데이터를 변환하지 않고 원본 그대로 저장
                                elif match := re.search(r"(\d+\.?\d*)\s*MB", value):
                                    data_value = f"{match.group(1)}MB"  # 변환 없이 MB 그대로 저장
                                    daily_data_value = "0GB"

                                # QoS 값 추출
                                if 'mblProdPpOfqnCntn' in info and info['mblProdPpOfqnCntn']:
                                    match = re.search(r'(\d+\.?\d*\s*(?:Mbps|Kbps))', info['mblProdPpOfqnCntn'])
                                    if match:
                                        detail_qos = match.group(1)

                            elif info['urcMblProdPpMajrItemCdNm'] == '음성통화':
                                detail_voice_call = info['mblProdPpScrnOfqnNm']

                                # 부가통화 값 추출
                                if 'mblProdPpOfqnCntn' in info and info['mblProdPpOfqnCntn']:
                                    match = re.search(r'(\d+)\s*분', info['mblProdPpOfqnCntn'])
                                    if match:
                                        detail_buga_call = match.group(1) + "분"

                            elif info['urcMblProdPpMajrItemCdNm'] == '문자메시지':
                                detail_message = info['mblProdPpScrnOfqnNm']

                            elif info['urcMblProdPpMajrItemCdNm'] == '기본혜택':
                                detail_benefit = info['mblProdPpScrnOfqnNm']

                        normal_price = item['urcPpBasfAmt']
                        sale_price = item['finalDcntAmt']
                        after_price = item['finalDcntAmt']
                        promotion_period = ''

                        # 12개월 & 24개월 총 요금(m12_price, m24_price) 계산
                        if not promotion_period or promotion_period == "평생":  # 빈 값, None, "평생" 동일 처리
                            m12_price = int(normal_price * 12)
                            m24_price = int(normal_price * 24)  # 평생일 때도 24개월 가격 계산
                        elif "개월" in promotion_period:
                            months = int(re.search(r"(\d+)", promotion_period).group(1))  # 개월 수 추출
                            
                            if months >= 24:  # 24개월 이상일 경우 24개월 할인 적용
                                m12_price = int(sale_price * 12)
                                m24_price = int(sale_price * 24)
                            elif months >= 12:  # 12개월 초과 24개월 이하일 경우
                                m12_price = int(sale_price * 12)
                                m24_price = int((sale_price * months) + (after_price * (24 - months)))
                            else:  # 12개월 이하일 경우
                                m12_price = int((sale_price * months) + (after_price * (12 - months)))
                                m24_price = int((sale_price * months) + (after_price * (24 - months)))
                        else:
                            m12_price = int(normal_price * 12)  # 기본값
                            m24_price = int(normal_price * 24)  # 기본값

                        dto = PlanData(
                            uuid='LGU_' + uuid,
                            mno='LGU',
                            telecom=SiteTargetListType.LGU_LIST.value,
                            company_id=SiteTargetListIDType.LGU_LIST.value,
                            url=f'https://www.lguplus.com/mobile/plan/mplan/direct/{mblUrcMenuEngNm}/{uuid}',
                            plan_type=param['type'],
                            plan_name=item['mblProdPpNm'],
                            data=data_value,  # 월 데이터
                            daily_data=daily_data_value,  # 일일 데이터
                            voice_call=detail_voice_call,
                            message=detail_message,
                            normal_price=normal_price,
                            sale_price=normal_price,
                            after_price=normal_price,
                            benefit=detail_benefit,
                            qos=detail_qos,
                            business_name='㈜엘지유플러스',
                            combination=True,
                            freebies='',
                            etc='',
                            promotion_period=promotion_period,
                            plan_code='',
                            buga_call=detail_buga_call,
                            m12_price=m12_price,
                            m24_price=m24_price,
                        )
                        result.append(dto)
                        success_count += 1

                    except Exception as e:
                        error_count += 1
                        print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                        traceback.print_exc()

                all_data.append({
                    "type": param['type'],
                    "data": [dto.__dict__ for dto in result]
                })

            except json.JSONDecodeError:
                error_count += 1
                print(f"{TAG} [ERROR] JSON 디코딩 오류 발생")
                traceback.print_exc()

        is_end = True
        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
