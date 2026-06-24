import json
import time
import traceback
import re
import requests
from modules.dto.input_queue_dto import PlanData
from modules.site.target import SiteTargetListType, SiteTargetListIDType

TAG = '[ChanceMobile]'

class ChanceMobileListAction:
    def root(self, *args, **kwargs) -> tuple[list[PlanData], bool]:
        """
        Chancemobile 데이터를 요청하고 PlanData DTO로 변환
        """
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result = []
        url = "https://chancemobile.co.kr/common/component/plan/AjaxPhone_plan.aspx"
        headers = {
            "Content-Type": "application/json; charset=UTF-8",
            "X-Requested-With": "XMLHttpRequest",
        }

        # POST 요청 payload
        payload = {
            "header": [{"type": "02"}],
            "body": [{"seq": "", "order_type": "AMT", "order_align": "ASC"}]
        }

        try:
            # POST 요청
            print(f"{TAG} HTTP POST 요청 시작: {url}")
            req_start = time.time()
            response = requests.post(url, json=payload, headers=headers)
            response.raise_for_status()
            req_elapsed = time.time() - req_start
            print(f"{TAG} HTTP 응답 완료 ({req_elapsed:.1f}초), 상태코드: {response.status_code}")

            # 응답 데이터 처리
            response_data = response.json()

            # 응답 데이터를 파일로 저장
            try:
                filename = "./response_files/list_chancemobile.json"
                with open(filename, "w", encoding="utf-8") as file:
                    json.dump(response_data, file, ensure_ascii=False, indent=4)
                # print(f"Response saved to {filename}")
            except Exception as e:
                print(f"{TAG} [ERROR] 응답 파일 저장 실패: {e}")

            # 응답 데이터 파싱
            if response_data.get("RESULT") == "Y" and "DATA" in response_data:
                plan_list = response_data["DATA"]
            else:
                plan_list = []

            total_count = len(plan_list)
            print(f"{TAG} 요금제 {total_count}건 발견")

            # PlanData 객체 생성
            for idx, item in enumerate(plan_list):
                try:
                    # 필드 매핑 및 값 처리
                    tt_amt = int(item.get("TT_AMT", "0").replace(",", ""))
                    discount = int(item.get("DISCOUNT", "0").replace(",", ""))
                    orgcharge = int(item.get("ORGCHARGE", "0").replace(",", ""))
                    gdcd = item.get("GDCD", "")
                    agcd = item.get("AGCD", "")
                    gdnm = item.get("GDNM", "")
                    gddesc = item.get("GDDESC", "")
                    mno_cd = item.get("MNO_CD", "")
                    netdiv = item.get("NETDIV", "")
                    blockyn = item.get("BLOCKYN", "")
                    dataamount = item.get("DATAAMOUNT", "0MB").replace("+", "")
                    add_data = item.get("ADD_DATA", "")
                    voiceamount = item.get("VOICEAMOUNT", "0분")
                    letteramount = item.get("LETTERAMOUNT", "0건")
                    limperiod = item.get("LIMPERIOD", "")
                    qosfg = item.get("QOSFG", "")
                    qosamt = item.get("QOSAMT", "").strip()
                    voice_add_amt = item.get("VOICE_ADD_AMT", "")

                    # LGU+ 표기 변경
                    if mno_cd == "LGU+":
                        mno_cd = "LGU"

                    # QoS 처리
                    qos_match = re.search(r"(\d+)(Mbps|Kbps)", qosamt)
                    qos = f"{qos_match.group(1)}{qos_match.group(2)}" if qos_match else ""
                    
                    # buga_call 숫자 처리
                    if voice_add_amt.isdigit() and int(voice_add_amt) > 0:
                        voice_add_amt = f"{voice_add_amt}분"
                    else:
                        voice_add_amt = ""

                    # voice_call 0 처리
                    if voiceamount == "0분":
                        voiceamount = ""

                    normal_price=tt_amt
                    sale_price=discount
                    after_price=orgcharge
                    promotion_period=limperiod


                    # 2025년 5월 12일 수정 : 정상가, 할인금액, 약정금액이 0인 경우 정상가로 처리
                    if sale_price == 0:
                        sale_price = normal_price
                    if after_price == 0:
                        after_price = normal_price


                     # 12개월 & 24개월 총 요금 계산
                    sale_price, after_price = int(sale_price), int(after_price)
                    if not promotion_period or promotion_period == "평생":
                        m12_price, m24_price = sale_price * 12, sale_price * 24
                    elif "개월" in promotion_period:
                        match = re.search(r"(\d+)", promotion_period)
                        months = int(match.group(1)) if match else 0

                        if months >= 24:
                            m12_price, m24_price = sale_price * 12, sale_price * 24
                        elif months >= 12:
                            m12_price = sale_price * 12
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                        else:
                            m12_price = (sale_price * months) + (after_price * (12 - months))
                            m24_price = (sale_price * months) + (after_price * (24 - months))
                    else:
                        m12_price, m24_price = normal_price * 12, normal_price * 24

                    daily_data = ""
                    if add_data:
                        match = re.search(r"(매일|하루)\s*(\d+)(GB|MB)", add_data)
                        if match:
                            daily_data = f"{match.group(2)}{match.group(3)}"
                    

                    # PlanData 생성
                    dto = PlanData(
                        uuid=f"CHANCE_{gdcd}",
                        telecom=SiteTargetListType.CHANCE_MOBILE_LIST.value,
                        company_id=SiteTargetListIDType.CHANCE_MOBILE_LIST.value,  # company_id 추가
                        mno=mno_cd,
                        url=f"https://chancemobile.co.kr/view/plan/phone_plan_detail.aspx?gdcd={gdcd}&poscd={agcd}",
                        plan_type=netdiv,
                        plan_name=gdnm,
                        data=dataamount,
                        voice_call=voiceamount,
                        message=letteramount,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        after_price=after_price,
                        qos=qos,
                        business_name="찬스모바일",                       
                        combination="",
                        freebies=add_data,
                        etc=gddesc,
                        benefit="",
                        promotion_period=promotion_period,
                        buga_call=voice_add_amt,
                        plan_code = '',
                        daily_data=daily_data,
                        m12_price=m12_price,
                        m24_price=m24_price,
                    )
                    result.append(dto)
                    success_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] 파싱 완료: {mno_cd} | {gdnm}")
                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{idx+1}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

        except Exception as e:
            error_count += 1
            print(f"{TAG} [ERROR] 데이터 요청 중 오류 발생: {e}")
            traceback.print_exc()

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        is_end = True
        return result, is_end
