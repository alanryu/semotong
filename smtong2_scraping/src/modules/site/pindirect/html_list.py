import requests
import json
import time
import traceback
import re
import math
from modules.dto.input_queue_dto import PlanData
from modules.site.target import SiteTargetListType, SiteTargetListIDType
# from utils.db.logger import log_to_db       # DB에 로그 저장하는 함수 추가
# from config.global_state import current_cycle_id    # 글로벌 상태에서 current_cycle_id 가져오기

TAG = '[Pindirect]'

class PindirectListAction:
    def root(self, *args, **kwargs) -> tuple[list[PlanData], bool]:
        """
        Z-Series 데이터를 요청하고 PlanData DTO로 변환
        """
        start_time = time.time()
        print(f"{TAG} 스크래핑 시작")
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0

        url = "https://z-api.pindirectshop.com/product/series"
        headers = {
            "Content-Type": "application/json",
        }

        try:
            # GET 요청
            t0 = time.time()
            print(f"{TAG} API 요청: {url}")
            response = requests.get(url, headers=headers)
            response.raise_for_status()
            elapsed = time.time() - t0
            print(f"{TAG} API 응답 완료 ({elapsed:.1f}초)")
            
            # ✅ JSON 파일 저장 (요금제 목록)
            list_filename = "./response_files/list_Pindirect.json"
            try:
                with open(list_filename, "w", encoding="utf-8") as file:
                    json.dump(response.json(), file, ensure_ascii=False, indent=4)
                # print(f"Response JSON 저장 완료: {list_filename}")
            except Exception as e:
                print("JSON 파일 저장 중 오류 발생:")
                traceback.print_exc()
          
            # 응답 데이터 처리
            response_data = response.json()
            
            # 응답 데이터에서 list 추출
            series_list = response_data.get("list", [])
            print(f"{TAG} 시리즈 {len(series_list)}개 발견")
            plan_counter = 0
            for series in series_list:
                series_title = series.get("title", "")
                plan_view_groups = series.get("planViewGroups", [])

                for group in plan_view_groups:
                    group_name = group.get("name", "")
                    plan_view_list = group.get("planViews", [])

                    for plan in plan_view_list:
                        try:
                            plan_counter += 1
                            # Plan 정보를 처리
                            plan_code = plan.get("plan", {}).get("code", "")
                            plan_provider = plan.get("plan", {}).get("provider", "")
                            if plan_provider == "LGT":
                                plan_provider = "LGU"

                            plan_name = f"{group_name} ({plan_provider})"
                            plan_network = plan.get("plan", {}).get("network", "")
                            plan_basic_data_mb = plan.get("plan", {}).get("basicData", 0)
                            plan_daily_data_mb = plan.get("plan", {}).get("dailyData", 0)
                            plan_qos_kbps = plan.get("plan", {}).get("qos", 0)

                            deal = plan.get("deal", {})
                            deal_price = deal.get("price", 0)
                            deal_discount_price = deal.get("discountPrice", 0)
                            deal_discount_period = deal.get("discountPeriod", 0)

                            # 데이터 변환
                            total_data_gb = math.ceil(plan_basic_data_mb / 1024 + plan_daily_data_mb * 30.5 / 1024)
                            plan_qos_mbps = plan_qos_kbps // 1000  # Kbps를 Mbps로 변환 (정수값)
                            
                            if total_data_gb in [149, 98, 88, 93, 97, 108, 196]:
                                total_data_gb = {149: 150, 98: 100, 88: 90, 93: 95, 97: 99, 108: 110, 196: 200}[total_data_gb]

                            # 메시지와 음성 처리
                            message = "무제한" if plan.get("plan", {}).get("message", -1) == -1 else f"{plan.get('plan', {}).get('message')}건"
                            voice_call = "무제한" if plan.get("plan", {}).get("voice", -1) == -1 else f"{plan.get('plan', {}).get('voice')}분"

                            # URL 생성
                            plan_url = f"https://www.pindirectshop.com/plan-view/{plan.get('code', '')}"

                            # UUID 설정
                            uuid = f"PINDIRECT_{plan_code}"

                            # Plan 이름 업데이트
                            plan_name = f"{group_name} ({plan_provider})"
                            print(f"{TAG} [{plan_counter}] 파싱 중: {plan_name}")

                            if deal_discount_period == -1:
                                promotion_period = "평생"
                            elif deal_discount_period > 0:
                                promotion_period = f"{deal_discount_period}개월"
                            elif deal_discount_period == 0 or deal_discount_period is None:
                                promotion_period = "평생"  # 0 또는 None이면 "평생"으로 설정

                            normal_price = deal_price
                            sale_price = deal_price - deal_discount_price
                            after_price = deal_price

                            # ✅ 12개월 & 24개월 총 요금 계산 (m12_price, m24_price)
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

                           # dailyData 변환 로직 수정 (1000으로 나눠 GB 단위 변환)
                            plan_daily_data_mb = plan.get("plan", {}).get("dailyData", 0)
                            if plan_daily_data_mb > 0:
                                daily_data = f"{plan_daily_data_mb // 1000}GB" if plan_daily_data_mb >= 1000 else f"{plan_daily_data_mb}MB"
                            else:
                                daily_data = "0GB"  # 값이 없거나 0이면 0GB로 설정


                            # DTO 생성
                            dto = PlanData(
                                uuid=uuid,
                                telecom=SiteTargetListType.PINDIRECT_LIST.value,
                                company_id=SiteTargetListIDType.PINDIRECT_LIST.value,
                                mno=plan_provider,
                                url=plan_url,
                                plan_type=plan_network,
                                plan_name=plan_name,
                                data=f"{total_data_gb}GB",
                                voice_call=voice_call,
                                message=message,
                                normal_price=normal_price,
                                sale_price=sale_price,
                                qos=f"{plan_qos_mbps}Mbps",
                                business_name="핀다이렉트",
                                after_price=after_price,
                                combination="",
                                freebies="",
                                etc="",
                                benefit="",
                                promotion_period=promotion_period,
                                buga_call="",
                                plan_code='',
                                daily_data=daily_data,
                                m12_price=m12_price,
                                m24_price=m24_price,
                            )
                            result.append(dto)
                            success_count += 1

                        except Exception as e:
                            error_count += 1
                            print(f"{TAG} [{plan_counter}] [ERROR] 요금제 파싱 실패: {e}")
                            traceback.print_exc()

        except Exception as e:
            error_count += 1
            print(f"{TAG} [ERROR] 사이트 탐색 실패: {e}")
            traceback.print_exc()

        is_end = True
        elapsed_total = time.time() - start_time
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초) | 성공: {success_count}, 스킵: {skip_count}, 에러: {error_count}")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, is_end
