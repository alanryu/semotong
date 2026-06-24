import json
import html
import re
import traceback
import time
import os
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from bs4 import BeautifulSoup
from modules.dto.input_queue_dto import PlanData  # ✅ DTO 불러오기
from modules.site.target import SiteTargetListType, SiteTargetListIDType

class SkylifeListAction:
    def root(self, page: int, *args, **kwargs) -> tuple[list, bool]:
        is_end = False
        result = []

        # 파일 경로 설정
        json_file_path = "./response_files/list_skylife_json.json"
        debug_json_path = "./response_files/error_json_debug.json"

        # Selenium 브라우저 옵션 설정
        options = webdriver.ChromeOptions()
        options.add_argument('--headless=new')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--disable-blink-features=AutomationControlled')

        try:
            # ChromeDriverManager를 이용한 WebDriver 초기화
            browser = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
            url = "https://m.skylife.co.kr/product/mobile/all"
            browser.get(url)

            # 페이지 로딩 대기
            time.sleep(1)

            # BeautifulSoup으로 HTML 파싱
            soup = BeautifulSoup(browser.page_source, "html.parser")

            # 현재 페이지의 script 태그 찾기
            script_tags = soup.find_all("script")
            
            filtered_scripts = []
            
            for script in script_tags:
                script_text = script.text.strip()

                # "self.__next_f.push" 포함 여부 확인
                if "self.__next_f.push" in script_text:
                    if script_text.startswith('self.__next_f.push([1,"8'):
                        filtered_scripts.append(script_text)
                        

            # JSON 변환 시작
            raw_data = "\n\n".join(filtered_scripts)

            # 앞부분 (`self.__next_f.push([1,"8:[\"$\",\"$L1e\",null,`) 제거
            prefix = 'self.__next_f.push([1,"8:[\\"$\\",\\"$L1e\\",null,'
            start_idx = raw_data.find(prefix)
            if start_idx != -1:
                raw_data = raw_data[start_idx + len(prefix):]
            
            raw_data = raw_data[:-6]  # 마지막 6글자 제거
                                                
            # HTML 엔터티 변환 (`\u003c`, `\u003e` → `<`, `>`)
            clean_json_text = html.unescape(raw_data)
            clean_json_text = clean_json_text.replace("\\n", "<br>")
            clean_json_text = clean_json_text.replace("\\", "")
            
            delete_msg= 'class="text-skylife-primary underline" data-action="openDrawer" data-drawer-name="s-money-info"u003eⓘ S-머니가 뭔가요?u003c/au003e"'
            delete_msg2='u003ca href="/product/combi/any" class="text-skylife-primary underline"u003eⓘ 혜택 받을 수 있는 조건 알아보기u003c/au003e'
                      
            clean_json_text = clean_json_text.replace(delete_msg, '"')
            clean_json_text = clean_json_text.replace(delete_msg2, '')
            clean_json_text = clean_json_text.replace("nu003ca", "")
            
            try:
                # 5️⃣ **JSON 변환 시도**
                json_data = json.loads(clean_json_text)
                
                # ✅ **'mobilePlanAll' 키만 남기고 나머지는 제거**
                if "mobilePlanAll" in json_data:
                    json_data = {"mobilePlanAll": json_data["mobilePlanAll"]}
                else:
                    raise ValueError("JSON 데이터에 'mobilePlanAll' 키가 없습니다.")

                

                # JSON 데이터 저장
                with open(json_file_path, "w", encoding="utf-8") as file:
                    json.dump(json_data, file, indent=4, ensure_ascii=False)
                # print(f"✅ JSON 데이터가 '{json_file_path}' 에 저장되었습니다.")

            except json.JSONDecodeError as e:
                print("❌ JSON 변환 오류 발생! 일부 데이터 출력:")
                print(clean_json_text[:1000])  # 1000자 출력하여 문제 확인

                # 디버깅용 JSON 저장
                with open(debug_json_path, "w", encoding="utf-8") as debug_file:
                    debug_file.write(clean_json_text)
                # print(f"⚠ JSON 변환 오류 데이터가 '{debug_json_path}' 에 저장되었습니다.")

                raise e  # 오류 다시 발생
            

            
            
            ####### 데이터 정리 및 DTO 저장 시작 #######

            

            # ✅ 'mobilePlanAll'이 존재하는지 확인
            mobile_plan_list = json_data.get("mobilePlanAll", [])  # 없으면 빈 리스트 반환

            if isinstance(mobile_plan_list, list) and mobile_plan_list:
                # print(f"✅ 'mobilePlanAll' 키가 존재하며, 총 {len(mobile_plan_list)}개의 항목이 있습니다.")

                for item in mobile_plan_list:  # ✅ 존재하는 경우에만 반복문 실행

                    # ✅ "properties" → "data" → "limit" 값 가져와서 GB 변환 (소수점 포함)
                    data_limit = item.get("properties", {}).get("data", {}).get("limit", 0)  # 기본값 0
                    data = f"{data_limit / 1000:.1f}GB" if data_limit else "0GB"  # ✅ 소수점 1자리까지 유지

                    # ✅ "properties" → "voice" → "unit" 값 가져오기
                    voice_properties = item.get("properties", {}).get("voice", {})  # "voice" 딕셔너리 가져오기
                    voice_unit = voice_properties.get("unit", "")  # unit 값
                    voice_limit = voice_properties.get("limit", 0)  # limit 값 (기본값 0)

                    # ✅ voice_call 값 설정
                    if voice_unit == "기본제공":
                        voice_call = "기본제공"
                    else:
                        voice_call = f"{voice_limit}분"  # limit 값 뒤에 "분" 추가

                    # ✅ "properties" → "message" 값 가져오기
                    message_properties = item.get("properties", {}).get("message", {})  # "message" 딕셔너리 가져오기
                    message_unit = message_properties.get("unit", "")  # unit 값
                    message_limit = message_properties.get("limit", 0)  # limit 값 (기본값 0)

                    # ✅ message 값 설정
                    if message_unit == "기본제공":
                        message = "기본제공"
                    else:
                        message = f"{message_limit}건"  # limit 값 뒤에 "건" 추가


                    # ✅ price_data가 리스트인지 확인 후 첫 번째 요소 사용
                    price_data = item.get("price", {})

                    # ✅ "default" 키가 리스트이면 첫 번째 요소를 사용, 그렇지 않으면 빈 딕셔너리 반환
                    default_price = price_data.get("default", [{}])[0] if isinstance(price_data.get("default"), list) else {}

                    # ✅ normal_price, sale_price, after_price 설정
                    normal_price = int(price_data.get("baseFee", 0))  # 기본 가격
                    sale_price = int(default_price.get("fee", 0))  # 할인가
                    after_price = int(default_price.get("baseFee") or sale_price)  # 할인 후 가격 (기본 가격이 없으면 할인가로 대체)

                    # ✅ benefit 값 설정
                    benefit_list = item.get("benefit", [])
                    if isinstance(benefit_list, list):  # 리스트인지 확인
                        benefit = " | ".join([b.get("heading", "").strip() for b in benefit_list if isinstance(b, dict) and "heading" in b])
                    else:
                        benefit = ""  # 리스트가 아니면 빈 문자열

                    # ✅ "properties" → "data" → "description" 값 가져오기
                    data_description = item.get("properties", {}).get("data", {}).get("description", "")

                    # ✅ 정규식으로 *G 또는 *.M 속도 값 추출
                    qos_match = re.search(r"(\d+\.?\d*)\s*(G|M)bps", data_description)

                   # ✅ "properties" → "data" → "description" 값 가져오기
                    data_description = item.get("properties", {}).get("data", {}).get("description", "")

                    # ✅ "Mbps" 또는 "Kbps"가 포함되어 있다면 그대로 추출
                    qos_match = re.search(r"(\d+\.?\d*\s*(?:Kbps|Mbps))", data_description)

                    # ✅ qos 값 설정 (없으면 빈 문자열)
                    qos = qos_match.group(1) if qos_match else ""


                    # ✅ "tags" 값 가져오기 (기본값: 빈 리스트)
                    tags_list = item.get("tags", [])

                    # ✅ "properties" → "voice" → "description" 값 가져오기
                    voice_description = item.get("properties", {}).get("voice", {}).get("description", "")

                    # ✅ "properties" → "voice" → "description" 값 가져오기
                    voice_description = item.get("properties", {}).get("voice", {}).get("description", "")

                    # ✅ 기본값 설정
                    buga_call = ""

                    # ✅ 부가통화 여부 확인 후 "*분" 값 추출 (예: "영상/부가 300분", "부가통화 200분")
                    if isinstance(voice_description, str) and "부가" in voice_description:  # "부가"라는 단어가 포함된 경우
                        match = re.search(r"부가\s*(\d+)분|부가통화\s*(\d+)분", voice_description)
                        if match:
                            buga_call = f"{match.group(1) or match.group(2)}분"  # ✅ 숫자 + "분" 형태로 저장

                    # ✅ voice_description이 None이거나 부가통화 정보가 없으면 기본값("") 유지


                    dto = PlanData(
                        uuid=item.get("slug", ""),  
                        mno="KT",
                        telecom=SiteTargetListType.SKYLIFE_LIST.value,
                        url="https://m.skylife.co.kr/product/mobile/goods/"+item.get("slug", ""),
                        company_id=SiteTargetListIDType.SKYLIFE_LIST.value,
                        plan_type=item.get("network", {}).get("type", ""),  
                        plan_name=item.get("name", ""),  
                        data=data,  
                        voice_call=voice_call,
                        message=message,  
                        normal_price=normal_price,  
                        sale_price=sale_price,  
                        after_price=after_price,  
                        benefit='',  
                        qos=qos,  
                        business_name="케이티스카이라이프",
                        combination=False,  
                        freebies="",  
                        etc=item.get("shortDescription", ""),  
                        promotion_period="평생",  
                        buga_call=buga_call,  
                        plan_code = item.get("slug", "").upper()  
                    )
                    result.append(dto)

                # print(f"✅ 총 {len(result)}개의 PlanData 객체가 생성되었습니다.")

            else:
                print("❌ 'mobilePlanAll' 키가 존재하지 않거나 리스트 형식이 아닙니다. JSON 구조를 확인하세요.")
                print(json.dumps(json_data, indent=4, ensure_ascii=False))  # ✅ JSON 데이터 출력

                ####### 데이터 정리 및 DTO 저장 완료 #######

        

        except Exception as e:
            # 오류 발생 시 처리
            print(f"오류 발생: {e}")
            traceback.print_exc()

        finally:
            # 브라우저 종료
            browser.quit()

        is_end = True
        return result, is_end
