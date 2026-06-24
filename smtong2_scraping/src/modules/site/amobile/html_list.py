import time
import os
import traceback
import requests
import json
import re
from bs4 import BeautifulSoup
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData


# ✅ DTO 관련 클래스 임포트 (PlanData, SiteTargetListType, SiteTargetListIDType)
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData

TAG = '[AMobile]'

def extract_price(price_text: str) -> int:
    """
    가격 문자열에서 첫 번째 숫자 부분만 추출하여 int 형으로 반환
    예) "63,000원(57,273원)" → 63000
    """
    if not price_text or not isinstance(price_text, str):
        return 0  # 빈 값이거나 문자열이 아닌 경우 0 반환

    price_text = price_text.replace(',', '').strip()  # 쉼표 제거 + 앞뒤 공백 제거
    numbers = re.findall(r'\d+', price_text)  # 숫자만 추출

    return int(numbers[0]) if numbers else 0  # 첫 번째 숫자 반환, 없으면 0

class AMobileListAction:
    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result = []  # ✅ PlanData DTO 객체를 저장할 리스트

        save_dir = './response_files'
        os.makedirs(save_dir, exist_ok=True)

        parsed_json_file_path = os.path.join(save_dir, 'parsed_data.json')

        common_headers = {
            'User-Agent': (
                'Mozilla/5.0 (Windows NT 10.0; Win64; x64) '
                'AppleWebKit/537.36 (KHTML, like Gecko) '
                'Chrome/133.0.0.0 Safari/537.36'
            )
        }

        url_list = [
            {'telecom': 'KT', 'url': 'https://amobile.co.kr/uc/plan?cp=K', 'headers': common_headers},
            {'telecom': 'LGU', 'url': 'https://amobile.co.kr/uc/plan?cp=L', 'headers': common_headers},
            {'telecom': 'SKT', 'url': 'https://amobile.co.kr/uc/plan?cp=s', 'headers': common_headers}
        ]

        for base_url in url_list:
            telecom = base_url['telecom']
            file_name = os.path.join(save_dir, f'list_amobile_{telecom}.html')

            try:
                print(f"{TAG} [{telecom}] HTTP 요청 시작: {base_url['url']}")
                req_start = time.time()
                response = requests.get(base_url['url'], headers=base_url['headers'], timeout=30)
                req_elapsed = time.time() - req_start
                print(f"{TAG} [{telecom}] HTTP 응답 완료 ({req_elapsed:.1f}초), 상태코드: {response.status_code}")

                if response.status_code != 200:
                    print(f"{TAG} [{telecom}] [ERROR] 응답 상태 코드 오류: {response.status_code}")
                    error_count += 1
                    continue
                if not response.text.strip():
                    print(f"{TAG} [{telecom}] [ERROR] 응답이 비어 있음: {base_url['url']}")
                    error_count += 1
                    continue

                with open(file_name, 'w', encoding='utf-8') as file:
                    file.write(response.text)

            except requests.exceptions.Timeout:
                print(f"{TAG} [{telecom}] [ERROR] 요청 타임아웃: {base_url['url']}")
                error_count += 1
            except Exception as e:
                print(f"{TAG} [{telecom}] [ERROR] 요청 중 예외 발생: {e}")
                error_count += 1
                traceback.print_exc()

        json_data = []  # ✅ JSON 저장을 위한 리스트

        for base_url in url_list:
            telecom = base_url['telecom']
            file_name = os.path.join(save_dir, f'list_amobile_{telecom}.html')

            try:
                print(f"{TAG} [{telecom}] HTML 파일 파싱 시작: {file_name}")

                with open(file_name, 'r', encoding='utf-8') as file:
                    html = file.read()

                soup = BeautifulSoup(html, 'html.parser')

                plan_list_box = soup.find('div', class_='plan_list_box')
                if not plan_list_box:
                    print(f"{TAG} [{telecom}] [WARNING] 요금제 페이지에서 'plan_list_box'를 찾을 수 없음")
                    skip_count += 1
                    continue

                table_list = [plan_list_box] + soup.find_all('div', class_='sub_plan_list')

                for table in table_list:
                    plan_type = '5G' if table.find('h3') and '5G' in table.find('h3').text else 'LTE'

                    items = table.find_all('tr')[1:]
                    print(f"{TAG} [{telecom}] {plan_type} 요금제 {len(items)}건 발견")
                    for idx, item in enumerate(items):
                        try:
                            td_list = item.find_all('td')

                            plan_name = item.find('p').text.strip()
                            voice_call = td_list[1].text.strip()
                            message = td_list[2].text.strip()
                            
                            # '기본' → '기본제공'으로 통일 (DB 트리거 호환용)
                            if voice_call == '기본':
                                voice_call = '기본제공'
                            if message == '기본':
                                message = '기본제공'
                            
                          # ✅ 데이터 용량 값 추출 (기존 로직 유지)
                            data_text = td_list[3].text.strip()
                           # print(f"원본 데이터 텍스트: {data_text}")

                            # 데이터 초기화
                            data = ''
                            daily_data = ''
                            qos = ''

                            # "+" 기준으로 데이터 분리
                            data_split = data_text.split('+')

                            for part in data_split:
                                part = part.strip()

                                # ✅ QoS (속도 제한) 정보 추출
                                match_qos = re.search(r'(\d+(?:\.\d+)?)\s*(Mbps|Kbps)', part, re.IGNORECASE)
                                if match_qos:
                                    qos = match_qos.group(0).strip()  # "5Mbps", "3Mbps" 등
                                    continue  # 속도 제한 값이므로 `data`로 저장하지 않음

                                # ✅ 일일 제공 데이터 추출 (예: "매일 5GB")
                                if "일" in part:
                                    match_daily = re.search(r'(\d+(?:\.\d+)?)\s*(GB|MB)', part, re.IGNORECASE)
                                    if match_daily:
                                        value, unit = match_daily.groups()
                                        if value.endswith('.0'):
                                            value = value[:-2]
                                        daily_data = f"{value}{unit}"
                                    continue  # `daily_data` 값이므로 `data`로 저장하지 않음

                                # ✅ 기본 데이터 (가장 큰 값 유지)
                                match_data = re.search(r'(\d+(?:\.\d+)?)\s*(GB|MB)', part, re.IGNORECASE)
                                if match_data:
                                    value, unit = match_data.groups()
                                    if value.endswith('.0'):
                                        value = value[:-2]
                                    if not data or float(value) > float(data.replace("GB", "").replace("MB", "")):
                                        data = f"{value}{unit}"

                            # ✅ 데이터가 없으면 기본값 설정
                            if not data:
                                data = "0GB"

                           # print(f"추출된 데이터: data={data}, daily_data={daily_data}, qos={qos}")


                            # ✅ 가격 값 추출 (숫자만 변환)
                            normal_price  = extract_price(td_list[4].text.strip())  # 기본료
                            sale_price    = extract_price(td_list[7].text.strip())  # 월납부요금
                            after_price   = extract_price(td_list[9].text.strip())  # 할인 종료 후 요금

                            basic_price = td_list[4].text.strip()
                            lifetime_discount = td_list[5].text.strip()
                            additional_discount = td_list[6].text.strip()
                            monthly_payment = td_list[7].text.strip()
                            discount_months = td_list[8].text.strip()

                            # ✅ promotion_period 처리 (숫자면 '개월' 추가)
                            if discount_months.isdigit():  # 숫자라면 '개월' 추가
                                promotion_period = f"{discount_months}개월"
                            else:
                                promotion_period = discount_months  # '평생' 같은 문자열은 그대로 사용
                            after_discount_price = td_list[9].text.strip()

                            # ✅ 상세보기 링크 오류 방지
                            detail_td = td_list[10] if len(td_list) > 10 else None
                            detail_a_tag = detail_td.find('a') if detail_td else None
                            detail_link = detail_a_tag['href'].strip() if detail_a_tag and 'href' in detail_a_tag.attrs else ''

                            # ✅ 상대경로를 `https://amobile.co.kr/semo/` 기준으로 변환    20251022 semo -> uc로 url변경됨.
                            if detail_link and not detail_link.startswith('http'):
                                detail_link = 'https://amobile.co.kr/' + detail_link.lstrip('/')

                            # ✅ UUID 생성 (detail_link의 숫자 값에 'AMOBILE_' 접두사 추가)
                            uuid_match = re.search(r'(\d+)$', detail_link)
                            uuid = f'AMOBILE_{uuid_match.group(1)}' if uuid_match else 'AMOBILE_UNKNOWN'

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

                            # ✅ JSON 저장 데이터 추가
                            json_data.append({
                                'uuid': uuid,
                                '통신사': telecom,
                                '요금제 구분': plan_type,
                                '요금제명': plan_name,
                                '음성': voice_call,
                                '문자': message,
                                '데이터': data,
                                'bps': qos,  # ✅ 속도 제한 값 추가
                                '기본료': basic_price,
                                '평생할인': lifetime_discount,
                                '추가할인': additional_discount,
                                '월납부요금': monthly_payment,
                                '할인개월': discount_months,
                                '할인 종료 후 요금': after_discount_price,
                                '상세보기': detail_link,
                                '일일 데이터': daily_data,  # ✅ 일일 데이터 제공량 추가
                            })

                            #if (promotion_period 

                            # ✅ DTO 생성 (모든 값 `''`으로 설정)
                            dto = PlanData(
                                uuid = uuid,
                                mno = telecom,
                                telecom = SiteTargetListType.AMOBILE_LIST.value,
                                company_id = SiteTargetListIDType.AMOBILE_LIST.value,
                                url  = detail_link,
                                plan_type = plan_type,
                                plan_name = plan_name,
                                data  = data,
                                voice_call = voice_call,
                                message = message,
                                normal_price = normal_price,
                                sale_price = sale_price,
                                after_price = after_price,
                                benefit  = '',
                                qos = qos,
                                business_name='(주)에넥스텔레콤',
                                combination = False,
                                freebies = '',
                                etc = '',
                                promotion_period=promotion_period,
                                buga_call = '',
                                plan_code = '',
                                daily_data=daily_data,  # 일일 데이터 제공량
                                m12_price = m12_price,
                                m24_price= m24_price,
                            )
                            result.append(dto)
                            success_count += 1
                            print(f"{TAG} [{idx+1}/{len(items)}] 파싱 완료: {telecom} | {plan_name}")

                        except Exception as e:
                            error_count += 1
                            print(f"{TAG} [{idx+1}/{len(items)}] [ERROR] 요금제 파싱 실패: {e}")
                            traceback.print_exc()

            except Exception as e:
                error_count += 1
                print(f"{TAG} [{telecom}] [ERROR] 저장된 HTML을 확인하는 중 예외 발생: {e}")
                traceback.print_exc()

        try:
            with open(parsed_json_file_path, 'w', encoding='utf-8') as file:
                json.dump(json_data, file, ensure_ascii=False, indent=4)
                # print(f"✅ [INFO] 결과가 '{parsed_json_file_path}'에 JSON 형식으로 저장되었습니다!")
        except Exception as e:
            print(f"{TAG} [ERROR] JSON 파일 저장 중 오류 발생: {e}")

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        is_end = True
        return result, is_end  # ✅ DTO 리스트 반환
