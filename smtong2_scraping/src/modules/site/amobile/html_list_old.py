import time
import traceback
import requests
import json
import re
from bs4 import BeautifulSoup
import os

# ✅ DTO 관련 클래스 임포트 (PlanData, SiteTargetListType, SiteTargetListIDType)
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData

class AMobileListAction:
    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result = []  # ✅ PlanData DTO 객체를 저장할 리스트

        save_dir = "./response_files"
        os.makedirs(save_dir, exist_ok=True)

        parsed_json_file_path = os.path.join(save_dir, "parsed_data.json")

        common_headers = {
            "User-Agent": (
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                "AppleWebKit/537.36 (KHTML, like Gecko) "
                "Chrome/133.0.0.0 Safari/537.36"
            )
        }

        url_list = [
            {
                "telecom": "KT",
                "url": "https://amobile.co.kr/semo/plan?cp=K",
                "headers": common_headers
            },
            {
                "telecom": "LGU",
                "url": "https://amobile.co.kr/semo/plan?cp=L",
                "headers": common_headers
            }
        ]

        for base_url in url_list:
            telecom = base_url["telecom"]
            file_name = os.path.join(save_dir, f"list_amobile_{telecom}.html")

            try:
                print(f"[INFO] '{telecom}' 통신사의 요금제 데이터를 요청 중: {base_url['url']}")
                response = requests.get(base_url["url"], headers=base_url["headers"], timeout=30)

                if response.status_code != 200:
                    print(f"[ERROR] 응답 상태 코드 오류: {response.status_code}")
                    continue
                if not response.text.strip():
                    print(f"[ERROR] 응답이 비어 있음: {base_url['url']}")
                    continue

                with open(file_name, "w", encoding="utf-8") as file:
                    file.write(response.text)
                print(f"[INFO] 응답 HTML 저장 완료: {file_name}")

            except requests.exceptions.Timeout:
                print(f"[ERROR] 요청 타임아웃: {base_url['url']}")
            except Exception as e:
                print(f"[ERROR] 요청 중 예외 발생: {e}")
                traceback.print_exc()

        json_data = []  # ✅ JSON 저장을 위한 리스트

        for base_url in url_list:
            telecom = base_url["telecom"]
            file_name = os.path.join(save_dir, f"list_amobile_{telecom}.html")

            try:
                print(f"\n🔍 [INFO] '{telecom}' 통신사의 저장된 HTML 파일을 분석 중: {file_name}")

                with open(file_name, "r", encoding="utf-8") as file:
                    html = file.read()

                soup = BeautifulSoup(html, "html.parser")

                plan_list_box = soup.find("div", class_="plan_list_box")
                if not plan_list_box:
                    print(f"⚠️ [WARNING] '{telecom}' 요금제 페이지에서 'plan_list_box'를 찾을 수 없음!")
                    continue

                table_list = [plan_list_box] + soup.find_all("div", class_="sub_plan_list")

                for table in table_list:
                    plan_type = "5G" if table.find("h3") and "5G" in table.find("h3").text else "LTE"

                    for item in table.find_all("tr")[1:]:
                        try:
                            td_list = item.find_all("td")

                            plan_name = item.find("p").text.strip()
                            voice_call = td_list[1].text.strip()
                            message = td_list[2].text.strip()
                            data = td_list[3].text.strip()
                            basic_price = td_list[4].text.strip()
                            lifetime_discount = td_list[5].text.strip()
                            additional_discount = td_list[6].text.strip()
                            monthly_payment = td_list[7].text.strip()
                            discount_months = td_list[8].text.strip()
                            after_discount_price = td_list[9].text.strip()

                            # ✅ 상세보기 링크 오류 방지
                            detail_td = td_list[10] if len(td_list) > 10 else None
                            detail_a_tag = detail_td.find("a") if detail_td else None
                            detail_link = detail_a_tag["href"].strip() if detail_a_tag and "href" in detail_a_tag.attrs else ""

                            # ✅ 상대경로를 `https://amobile.co.kr/semo/` 기준으로 변환
                            if detail_link and not detail_link.startswith("http"):
                                detail_link = "https://amobile.co.kr/semo/" + detail_link.lstrip("/")

                            # ✅ UUID 생성 (detail_link의 숫자 값에 "AMOBILE_" 접두사 추가)
                            uuid_match = re.search(r"(\d+)$", detail_link)
                            uuid = f"AMOBILE_{uuid_match.group(1)}" if uuid_match else "AMOBILE_UNKNOWN"

                            # ✅ JSON 저장 데이터 추가
                            json_data.append({
                                "uuid": uuid,
                                "통신사": telecom,
                                "요금제 구분": plan_type,
                                "요금제명": plan_name,
                                "음성": voice_call,
                                "문자": message,
                                "데이터": data,
                                "기본료": basic_price,
                                "평생할인": lifetime_discount,
                                "추가할인": additional_discount,
                                "월납부요금": monthly_payment,
                                "할인개월": discount_months,
                                "할인 종료 후 요금": after_discount_price,
                                "상세보기": detail_link
                            })

                            # ✅ DTO 생성 및 `result` 리스트에 추가
                            dto = PlanData(
                                uuid=uuid,
                                mno=telecom,
                                telecom=SiteTargetListType.AMOBILE_LIST.value,
                                company_id=SiteTargetListIDType.AMOBILE_LIST.value,
                                url=detail_link,
                                plan_type=plan_type,
                                plan_name=plan_name,
                                data=data,
                                voice_call=voice_call,
                                message=message,
                                normal_price=basic_price,
                                sale_price=monthly_payment,
                                benefit='',
                                qos='',
                                business_name='(주)에넥스텔레콤',
                                after_price=after_discount_price,
                                combination='',
                                freebies='',
                                etc='',
                                promotion_period=discount_months,
                                buga_call='',
                                plan_code='',
                            )
                            result.append(dto)

                        except Exception as e:
                            print(f"[ERROR] 데이터 처리 중 예외 발생: {e}")
                            traceback.print_exc()

            except Exception as e:
                print(f"[ERROR] 저장된 HTML을 확인하는 중 예외 발생: {e}")
                traceback.print_exc()

        try:
            with open(parsed_json_file_path, "w", encoding="utf-8") as file:
                json.dump(json_data, file, ensure_ascii=False, indent=4)
            print(f"✅ [INFO] 결과가 '{parsed_json_file_path}'에 JSON 형식으로 저장되었습니다!")
        except Exception as e:
            print(f"❌ [ERROR] JSON 파일 저장 중 오류 발생: {e}")

        return result, is_end  # ✅ DTO 리스트 반환
