import json
import time
import traceback
import re
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData

TAG = '[S1Mobile]'

class S1MobileListAction:
    def root(self, page: int = 1, *args, **kwargs) -> tuple[list[PlanData], bool]:
        """
        S1Mobile 데이터를 요청하고 PlanData DTO로 변환
        """
        start_time = time.time()
        print(f"{TAG} 스크래핑 시작")
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0
        url = "https://www.s1mobile.co.kr:8447/home/plan/getPlanRateList.do"
        headers = {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "X-Requested-With": "XMLHttpRequest",
        }

        # mno 값 리스트 (SKT, KT, LGUP로 호출)
        carriers = ["SKT", "KT", "LGUP"]

        for mno in carriers:
            try:
                # POST 요청 payload
                payload = {
                    "mno": mno,
                    "rateDv": "U",
                    "chargePlanSn": 1,
                    "orderBy": 1,
                    "rateCode": "",
                    "searchMallSp": "mall",
                }

                # POST 요청
                t0 = time.time()
                print(f"{TAG} API 요청: mno={mno}")
                response = requests.post(url, data=payload, headers=headers)
                response.raise_for_status()
                elapsed = time.time() - t0
                print(f"{TAG} API 응답 완료 ({elapsed:.1f}초) - mno={mno}")

                # 응답 데이터 처리
                response_data = response.json()

                # 응답 데이터를 파일로 저장
                try:
                    filename = f"./response_files/list_S1_{mno}.json"
                    with open(filename, "w", encoding="utf-8") as file:
                        json.dump(response_data, file, ensure_ascii=False, indent=4)
                except Exception as e:
                    print(f"Failed to save response for {mno}: {e}")

                if isinstance(response_data, list) and response_data:
                    plan_list = response_data[0].get("list", [])
                else:
                    plan_list = []

                print(f"{TAG} mno={mno}: 요금제 {len(plan_list)}건 발견")

                # PlanData 객체 생성
                for item_idx, item in enumerate(plan_list):
                    try:
                        # useMoblPhonRateCnt 체크
                        if item.get("useMoblPhonRateCnt", "0") != "1":
                            skip_count += 1
                            continue  # "1"이 아닌 경우 건너뛰기

                        chargePlanSn = item.get("chargePlanSn", "")
                        avail = item.get("avail", "")
                        chargePlanNm = item.get("chargePlanNm", "")
                        print(f"{TAG} [{item_idx+1}/{len(plan_list)}] 파싱 중: {mno} | {chargePlanNm}")
                        dataServing = item.get("dataServing", "0GB").replace("+", "")  # '+' 제거
                        dmstcTalkServing = item.get("dmstcTalkServing", "0분")
                        smsServing = item.get("smsServing", "0건")
                        addDispMsg = item.get("addDispMsg", "")
                        addDispMsg4 = item.get("addDispMsg4", "")
                        if addDispMsg4 == "-":  # '-'를 빈 문자열로 처리
                            addDispMsg4 = ""
                        dataServeAddInfo = item.get("dataServeAddInfo", "")

                        # 가격 정보 변환
                        try:
                            bassChrge = int(float(item.get("bassChrge", 0)))  # 기본 요금
                        except ValueError:
                            bassChrge = 0

                        try:
                            promoSalePrice = int(float(item.get("promoSalePrice", 0)))  # 할인 금액
                        except ValueError:
                            promoSalePrice = 0

                        promotion_period = addDispMsg4
                        normal_price = round(bassChrge * 1.1)
                        sale_price = round((bassChrge - promoSalePrice) * 1.1)

                        # After Price 추출
                        after_price_match = re.search(r"(\d+,\d+)원", addDispMsg)
                        after_price = int(after_price_match.group(1).replace(",", "")) if after_price_match else sale_price

                        # After Price가 빈값이거나 0일 경우 sale_price로 대체
                        if not after_price or after_price == 0:
                            after_price = sale_price

                        # LGUP -> LGU 변경
                        if mno == "LGUP":
                            mno = "LGU"

                        # ✅ QoS 및 Daily Data 추출 로직
                        qos_match = re.search(r"(\d+)(Mbps|Kbps)", dataServeAddInfo)
                        qos = f"{qos_match.group(1)}{qos_match.group(2)}" if qos_match else ""

                        # ✅ Daily Data (일 데이터) 추출 로직
                        daily_data = "0GB"  # 기본값 설정

                        # 패턴별 데이터 추출
                        daily_match = re.search(r"(매일|일\/|소진시 매일|소진시 일) ?(\d+(?:\.\d+)?)(GB|MB)", dataServeAddInfo)
                        if daily_match:
                            daily_data = f"{daily_match.group(2)}{daily_match.group(3)}"

                        # "소진시 매일"이 포함된 경우 but 데이터량이 없을 때 처리
                        elif "소진시 매일" in dataServeAddInfo or "매일 데이터 없음" in dataServeAddInfo:
                            daily_data = "0GB"



                        # 부가통화(영상통화) 값 처리
                        vidoTalkServing = item.get("vidoTalkServing", "").replace("-", "").strip()

                        # 12개월 & 24개월 총 요금(m12_price, m24_price) 계산
                        if not promotion_period or promotion_period == "평생":
                            m12_price = sale_price * 12
                            m24_price = sale_price * 24
                        elif "개월" in promotion_period:
                            match = re.search(r"(\d+)", promotion_period)
                            months = int(match.group(1)) if match else 0

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

                        # PlanData 생성
                        dto = PlanData(
                            uuid=f"S1_{chargePlanSn}",
                            telecom=SiteTargetListType.S1_LIST.value,
                            company_id=SiteTargetListIDType.S1_LIST.value,
                            mno=mno,
                            url=f"http://www.s1mobile.co.kr/mall/usim/view.do?searchChargePlanSn={chargePlanSn}",
                            plan_type=avail,
                            plan_name=chargePlanNm,
                            data=dataServing,
                            voice_call=dmstcTalkServing,
                            message=smsServing,
                            normal_price=normal_price,
                            sale_price=sale_price,
                            qos=qos,
                            business_name="에스원",
                            after_price=after_price,
                            combination="",
                            freebies="",
                            etc="",
                            benefit="",
                            promotion_period=promotion_period,
                            buga_call=vidoTalkServing,
                            plan_code="",
                            daily_data=daily_data,
                            m12_price=m12_price,
                            m24_price=m24_price,
                        )
                        result.append(dto)
                        success_count += 1
                    except Exception as e:
                        error_count += 1
                        print(f"{TAG} [{item_idx+1}/{len(plan_list)}] [ERROR] 요금제 파싱 실패: {e}")
                        traceback.print_exc()

            except Exception as e:
                error_count += 1
                print(f"{TAG} [ERROR] mno={mno} API 요청 실패: {e}")
                traceback.print_exc()

        is_end = True
        elapsed_total = time.time() - start_time
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초) | 성공: {success_count}, 스킵: {skip_count}, 에러: {error_count}")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
