import json
import time
import os
import traceback
import requests
import random
import re  # ✅ 정규표현식 사용을 위해 추가
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from modules.dto.input_queue_dto import PlanData  # ✅ DTO 불러오기
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[KtmMobile]'


class KtmMobileListAction:
    def initialize_browser(self):
        """Chrome WebDriver 설정"""
        print(f"{TAG} Selenium 드라이버 생성 중")
        options = webdriver.ChromeOptions()
        options.add_argument('--headless=new')
        options.add_argument('--disable-gpu')
        options.add_argument('--window-size=1920x1080')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--no-zygote')
        options.add_argument('--disable-blink-features=AutomationControlled')
        browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
        print(f"{TAG} Selenium 드라이버 생성 완료")
        return browser
    def extract_number(value):
        """문자열에서 숫자만 추출하여 정수(int)로 변환"""
        if not value:
            return 0  # ✅ 값이 없으면 기본값 0 반환
        match = re.search(r'(\d+)', value)  # ✅ 숫자만 추출
        return int(match.group(1)) if match else 0  # ✅ 변환 후 반환


    def root(self, *args, **kwargs):
        count = 0
        """크롤링 실행 후 JSON 데이터를 리스트(li_list)에 저장 + PlanData 객체 변환"""
        print(f"{TAG} 스크래핑 시작")
        total_start = time.time()
        is_end = False
        result = []  # ✅ 기존 데이터 저장 (변경 X)
        li_list = []  # ✅ PlanData 객체를 저장할 리스트
        uuid_set = set()  # ✅ 중복 체크를 위한 Set
        success_count = 0
        skip_count = 0
        error_count = 0

        browser = self.initialize_browser()
        t0 = time.time()
        print(f"{TAG} 페이지 로드 시작")
        browser.get("https://www.ktmmobile.com/rate/rateList.do")
        time.sleep(1)
        print(f"{TAG} 페이지 로드 완료 ({time.time() - t0:.1f}초)")

        try:
            wait = WebDriverWait(browser, 1)
            wait.until(lambda driver: driver.execute_script("return document.readyState") == "complete")

            # ✅ 쿠키 가져오기
            session_cookies = {cookie['name']: cookie['value'] for cookie in browser.get_cookies()}

            # ✅ 요청 헤더 설정
            headers = {
                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
                "X-Requested-With": "XMLHttpRequest",
                "Referer": "https://www.ktmmobile.com/rate/rateList.do",
                "Origin": "https://www.ktmmobile.com",
                "Accept": "application/json, text/javascript, */*; q=0.01",
                "Connection": "keep-alive"
            }

            # ✅ 랜덤값 생성
            rand_value = str(random.randint(100000000000, 999999999999))

            # ✅ 요금제 ID 가져오기 요청
            rate_plan_ids = []
            filtered_ids = []

            rate_list_url = "https://www.ktmmobile.com/rate/getCtgXmlAllListAjax.do"
            payload = {"rateAdsvcDivCd": "RATE", "rand": rand_value}

            t0 = time.time()
            print(f"{TAG} 요금제 ID 목록 요청 시작")
            response = requests.post(rate_list_url, data=payload, headers=headers, cookies=session_cookies, timeout=5)
            print(f"{TAG} 요금제 ID 목록 응답 완료 ({time.time() - t0:.1f}초)")

            if response.status_code == 200:
                try:
                    json_data = response.json()

                    if isinstance(json_data, list):
                        rate_plan_ids = [str(item["rateAdsvcCtgCd"]) for item in json_data if "rateAdsvcCtgCd" in item]
                        filtered_ids = [str(item["rateAdsvcCtgCd"]) for item in json_data if item.get("depthKey") in [2, 3]]

                except json.JSONDecodeError:
                    pass  # JSON 변환 오류 무시

            # ✅ 필터링된 요금제 데이터 가져오기
            ajax_url = "https://www.ktmmobile.com/rate/rateContentAjax.do"
            print(f"{TAG} 필터링된 요금제 카테고리 수: {len(filtered_ids)}개")

            for rate_plan_id in filtered_ids:
                payload = {"rateAdsvcCtgCd": rate_plan_id}
                t0 = time.time()
                response = requests.post(ajax_url, data=payload, headers=headers, cookies=session_cookies, timeout=5)
                print(f"{TAG} 카테고리 {rate_plan_id} 응답 완료 ({time.time() - t0:.1f}초)")

                if response.status_code == 200:
                    try:
                        json_data = response.json()
                        if isinstance(json_data, list):
                            li_list.extend(json_data)  # ✅ `result` 대신 `li_list`에 추가
                        elif isinstance(json_data, dict) and json_data:
                            li_list.append(json_data)

                    except json.JSONDecodeError:
                        pass  # JSON 변환 오류 무시

        except Exception as e:
            error_count += 1
            print(f"{TAG} [ERROR] 크롤링 중 오류 발생: {e}")
            traceback.print_exc()

        finally:
            print(f"{TAG} Selenium 드라이버 종료")
            browser.quit()
            def extract_number(value):
                """문자열에서 숫자만 추출하여 정수(int)로 변환"""
                if not value:
                    return 0  # ✅ 값이 없으면 기본값 0 반환
                cleaned_value = re.sub(r'[^\d]', '', value)  # ✅ 숫자 이외 문자 제거
                return int(cleaned_value) if cleaned_value.isdigit() else 0  # ✅ 변환 후 반환

            # 기존 저장 경로를 유지 (예: response_files 경로 사용)
            save_path = "response_files/ktm_mobile_data.json"

            # 디렉토리가 존재하지 않으면 생성
            os.makedirs(os.path.dirname(save_path), exist_ok=True)

            # JSON 데이터 저장
            with open(save_path, "w", encoding="utf-8") as json_file:
                json.dump(li_list, json_file, ensure_ascii=False, indent=4)

            #print(f"✅ JSON 데이터 저장 완료: {save_path}")

            
            # ✅ li_list를 순회하며 PlanData 객체 변환 후 저장
            total_count = len(li_list)
            print(f"{TAG} 파싱 대상 요금제 수: {total_count}건")

            for idx, item in enumerate(li_list):
                uuid = "KTM_" + item.get("rateAdsvcCd", "")
                plan_name_log = item.get("rateAdsvcNm", "")
                print(f"{TAG} [{idx+1}/{total_count}] 파싱 중: {plan_name_log}")


               # ✅ 기본 데이터(data)와 일일 제공 데이터(daily_data) 추출
                bnfit_data = item.get("bnfitData", "") or ""

                # ✅ 먼저 "일 XGB" 패턴 추출 및 제거
                daily_match = re.search(r"일\s*(\d+(?:\.\d+)?)\s*GB", bnfit_data)
                if daily_match:
                    val = float(daily_match.group(1))
                    daily_data = f"{int(val)}GB" if val.is_integer() else f"{val}GB"
                    bnfit_data = re.sub(r"일\s*\d+(?:\.\d+)?\s*GB", "", bnfit_data).strip()  # ✅ "일 XGB" 제거
                else:
                    daily_data = ""

                # ✅ 이제 남은 데이터에서 기본 데이터 추출
                data_match = re.search(r"(\d+(?:\.\d+)?)\s*GB", bnfit_data)
                if data_match:
                    val = float(data_match.group(1))
                    data = f"{int(val)}GB" if val.is_integer() else f"{val}GB"
                else:
                    data = "0GB"



                # ✅ 가격 필드 추출 로직 수정
                normal_price = extract_number(item.get("mmBasAmtVatDesc", ""))
                sale_price = extract_number(item.get("promotionAmtVatDesc", ""))

                if sale_price == 0:
                    sale_price = normal_price  # ✅ 할인 가격이 없으면 정상 가격 유지

                # ✅ 프로모션 기간이 끝나도 세일프라이스를 유지지
                after_price = sale_price  # ✅ 할인 종료 후 적용되는 가격
                promotion_period = "평생"



                # ✅ 중복된 uuid는 저장하지 않음
                if uuid and uuid not in uuid_set:
                    uuid_set.add(uuid)  # ✅ 중복 체크용 Set에 추가
                    
                    # ✅ 요금제 이름이 "5G" 또는 "e 5G"로 시작하는 경우 plan_type을 '5G'로 설정
                    plan_type = "5G" if re.match(r"^(5G|e 5G)", item.get("rateAdsvcNm", ""), re.IGNORECASE) else "LTE"


                    # ✅ bnfitList 및 bnfitList2에서 rateAdsvcItemDesc 값 추출
                    benefit_list = [
                        benefit["rateAdsvcItemDesc"] for benefit in item.get("bnfitList", []) if "rateAdsvcItemDesc" in benefit
                    ]
                    benefit_list2 = [
                        benefit["rateAdsvcItemDesc"] for benefit in item.get("bnfitList2", []) if "rateAdsvcItemDesc" in benefit
                    ]

                    # ✅ 두 리스트의 데이터를 합치고, "|" 로 구분된 문자열 생성
                    benefit = "|".join(benefit_list + benefit_list2) if benefit_list or benefit_list2 else ""

                    # ✅ bnfitData에서 QoS 속도(예: "최대1Mbps") 추출
                    bnfit_data = item.get("bnfitData", "") or ""
                    qos = ""
                    match = re.search(r"최대\s*([\d.]+(?:[MG]bps))", bnfit_data)
                    if match:
                        qos = match.group(1).strip()  # ✅ QoS 속도값 저장 (예: "1Mbps", "2Gbps")

                    # ✅ promotionBnfitVoice에서 "*분" 패턴 추출 (예: "300분")
                    buga_call = "0분"  # 기본값 설정
                    promotion_bnfit_voice = item.get("promotionBnfitVoice", "") or ""

                    match = re.search(r"(\d+)\s*분", promotion_bnfit_voice)  # ✅ 숫자+공백+분 패턴 찾기
                    if match:
                        buga_call = match.group(1) + "분"  # ✅ 매칭된 숫자에 "분" 추가


                    # ✅ 12개월 & 24개월 요금 계산
                    if not promotion_period or promotion_period == "평생":  
                        m12_price = sale_price * 12
                        m24_price = sale_price * 24
                    elif "개월" in promotion_period:
                        months = int(re.search(r"(\d+)", promotion_period).group(1))
                        if months >= 24:
                            m12_price = sale_price * 12
                            m24_price = sale_price * 24
                        elif months >= 12:
                            m12_price = sale_price * 12
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                        else:
                            m12_price = (sale_price * months) + (after_price * (12 - months))
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                    else:
                        m12_price = normal_price * 12
                        m24_price = normal_price * 24

                    dto = PlanData(
                        uuid=uuid,  # ✅ rateAdsvcCd 값 사용
                        mno="KT",
                        telecom=SiteTargetListType.KTMMOBILE_LIST.value,
                        url = "https://www.ktmmobile.com/rate/rateList.do",
                        company_id=SiteTargetListIDType.KTMMOBILE_LIST.value,
                        plan_type=plan_type,
                        plan_name=item.get("rateAdsvcNm", ""),
                        data=data,
                        voice_call=item.get("bnfitVoice", "") or "",
                        message=item.get("bnfitSms", "") or "",
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit=benefit,
                        qos=qos,
                        business_name="주식회사 케이티엠모바일",
                        after_price=after_price,
                        combination=False,
                        freebies="",
                        etc="",
                        promotion_period=promotion_period,
                        buga_call=buga_call,
                        plan_code = "",
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price,  
                        
                    )
                    result.append(dto)
                    success_count += 1
                else:
                    skip_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] 스킵 (중복 uuid): {uuid}")

        is_end = True
        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end