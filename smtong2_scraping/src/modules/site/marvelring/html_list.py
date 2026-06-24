import json
import traceback
import re
import time
import requests
from bs4 import BeautifulSoup
from concurrent.futures import ThreadPoolExecutor
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData

TAG = '[Marvelring]'

class MarvelringListAction:

    def fetch_detail_page(self, href, log_prefix=""):
        """ 상세 페이지에서 추가 데이터를 가져오는 함수 """
        try:
            t0 = time.time()
            response = requests.get(href)
            elapsed = time.time() - t0
            print(f"{TAG} {log_prefix}상세 페이지 응답 완료 ({elapsed:.1f}초)")
            detail_soup = BeautifulSoup(response.text, 'html.parser')

            # ✅ 부가통화 정보 추출
            rate_caption = detail_soup.find("div", class_="rate_caption")
            buga_call_list = []
            mno_value = ""  # ✅ 기본값을 빈 문자열("")로 설정

            if rate_caption:
                rate_items = rate_caption.find_all("li")
                for rate_item in rate_items:
                    text = rate_item.text.strip()

                    # ✅ 부가통화 정보 추출
                    if "부가통화" in text:
                        buga_call_list.append(text.replace("부가통화", "").strip())

            # ✅ MNO 정보 추출 (SKT, KT, LGU+)
            telco_tag = detail_soup.find("span", class_="telco")
            if telco_tag:
                mno_value = telco_tag.text.strip()

                # ✅ "LGT"를 "LGU"로 변환
                if mno_value == "LGT":
                    mno_value = "LGU"

            return ", ".join(buga_call_list), mno_value  # ✅ MNO 값도 함께 반환

        except Exception as e:
            print(f"{TAG} {log_prefix}[ERROR] 상세 페이지 요청 실패: {e}")
            return "", ""  # ✅ 예외 발생 시에도 빈 문자열 반환

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        start_time = time.time()
        print(f"{TAG} 스크래핑 시작")
        is_end = False
        result = []
        success_count = 0
        skip_count = 0
        error_count = 0
        base_url = "https://www.marvelring.com/"

        try:
            # ✅ 요청 URL 리스트
            url_list = [
                "https://www.marvelring.com/rate_plan.do?type=T007",
                "https://www.marvelring.com/rate_plan.do?type=T003",
                "https://www.marvelring.com/rate_plan.do?type=T002",
                "https://www.marvelring.com/rate_plan.do?type=T010",
                "https://www.marvelring.com/rate_plan.do?type=T006",
            ]

            detail_page_urls = []  # 상세 페이지 URL 저장 리스트

            for url_idx, url in enumerate(url_list):
                try:
                    # ✅ 요금제 목록 페이지 가져오기
                    t0 = time.time()
                    print(f"{TAG} 목록 페이지 요청: {url}")
                    response = requests.get(url)
                    elapsed = time.time() - t0
                    print(f"{TAG} 목록 페이지 응답 완료 ({elapsed:.1f}초)")
                    html = response.text
                    soup = BeautifulSoup(html, 'html.parser')

                    # ✅ 요금제 리스트 찾기
                    plan_items = soup.select("ul.card_list.rate_list > li.card_list_item")
                    print(f"{TAG} 목록 페이지 [{url_idx+1}/{len(url_list)}] 요금제 {len(plan_items)}건 발견")

                    for item_idx, item in enumerate(plan_items):
                        try:
                            # ✅ 요금제 상세 링크 추출
                            a_tag = item.find("a", class_="card_rate_link")
                            if not a_tag or "href" not in a_tag.attrs:
                                continue

                            href = a_tag["href"]

                            # ✅ 상대 경로라면 절대 경로로 변환
                            if not href.startswith("http"):
                                href = base_url + href

                            # ✅ 세션 ID 제거
                            href = re.sub(r";jsessionid=[^?]+", "", href)

                            # ✅ UUID 생성
                            uuid = f"MARVELRING_{href.split('type=')[1].split('&')[0]}{href.split('no=')[1]}"

                            # ✅ 요금제 이름 추출
                            title_tag = item.find("p", class_="title")
                            plan_name = title_tag.text.strip() if title_tag else "Unknown"


                            # ✅ QoS 속도 추출 (예: "+ 3Mbps", "+10Mbps" → "3Mbps", "10Mbps")
                            match_qos = re.search(r"\+ ?(\d+Mbps)", plan_name)
                            qos_value = match_qos.group(1) if match_qos else ""

                            # ✅ 일일 데이터 먼저 추출 (예: "매일 2GB" → "2GB")
                            match_daily = re.search(r"매일 (\d+GB)", plan_name)
                            daily_data_value = match_daily.group(1) if match_daily else "0GB"

                            # ✅ 기본 데이터 추출 (QoS나 일일 데이터가 아닌 첫 번째 GB/MB 값 포함)
                            match_data = re.findall(r"(\d+(\.\d+)?(GB|MB))", plan_name)
                            data_value = "0GB"

                            if match_data:
                                for value in match_data:
                                    extracted_value = value[0]  # 정규식 그룹에서 첫 번째 값 가져오기
                                    if extracted_value != daily_data_value:  # ✅ "매일" 값이 아니라면 기본 데이터로 저장
                                        data_value = extracted_value
                                        break


                           # ✅ 기본 값 설정
                            voice_call, message = None, None  

                            # ✅ 모든 <li> 태그를 검사하면서 직접 텍스트 추출
                            for li in item.find_all("li"):
                                li_text = li.get_text(strip=True).replace(" ", "")  # ✅ 공백 제거 후 텍스트 가져오기

                                # ✅ 음성 통화 데이터 추출 (예: "음성통화 100분", "음성통화 무제한")
                                if "음성통화" in li_text:
                                    match = re.search(r"(\d+)", li_text)  # 숫자 값 찾기
                                    voice_call = match.group(1) + "분" if match else "무제한"

                                # ✅ 문자 데이터 추출 (예: "문자 50건", "문자 무제한")
                                if "문자" in li_text:
                                    match = re.search(r"(\d+)", li_text)  # 숫자 값 찾기
                                    message = match.group(1) + "건" if match else "무제한"

                            # ✅ 값이 여전히 None이면 기본 제공 설정
                            if not voice_call:
                                voice_call = "기본 제공"
                            if not message:
                                message = "기본 제공"


                            # ✅ 가격 정보 추출
                            price_tag = item.find("div", class_="price")
                            normal_price, sale_price, after_price, promotion_period = "0", "0", "0", ""

                            if price_tag:
                                price_text = price_tag.text.strip().replace(",", "")  # ✅ 가격에서 콤마 제거
                                match_price = re.search(r"월\s([\d]+)", price_text)
                                normal_price = match_price.group(1) if match_price else "0"  # ✅ 24,700원 추출

                                ref_tag = price_tag.find("p", class_="ref")
                                if ref_tag:
                                    ref_text = ref_tag.text.strip()

                                    # ✅ "이후" 또는 "개월 후" 뒤에 오는 가격 추출
                                    match_after_price = re.search(r"(?:이후|개월 후|할인 후|정상가|적용 후)\s*(\d{1,3}(?:,\d{3})*)원?", ref_text)
                                    after_price = match_after_price.group(1).replace(",", "") if match_after_price else ""

                                    # ✅ 프로모션 기간 추출
                                    match_promo_period = re.search(r"(\d+)개월", ref_text)
                                    promotion_period = f"{match_promo_period.group(1)}개월" if match_promo_period else ""

                            # ✅ 정상 가격이 없다면, 기본적으로 normal_price를 sale_price로 설정
                            if not sale_price or sale_price == "0":
                                sale_price = normal_price

                            # ✅ after_price가 존재하면 normal_price도 동일하게 변경
                            if after_price and after_price != "0":
                                normal_price = after_price  # ✅ 정상 가격도 after_price와 동일하게 설정

                            # ✅ promotion_period가 없을 때도 after_price가 0이 되지 않도록 처리
                            if not promotion_period:
                                after_price = normal_price  # ✅ 할인 기간이 없으면 정상 가격 유지

                            # ✅ 기본값 설정
                            combination = False  

                           # ✅ 'card_list_item'을 기준으로 정보 탐색
                            # ✅ 'card_list_item'을 기준으로 정보 탐색
                            badge_wrap = item.find("p", class_="badge_wrap")

                            combination = False  # 기본값 설정

                            if badge_wrap:
                                badge_tags = badge_wrap.find_all("span", class_="badge")

                                for badge in badge_tags:
                                    badge_text = badge.get_text(strip=True)

                                    # ✅ "인터넷 결합 가능" 또는 "IP TV 결합 가능"이 포함된 경우 combination = True
                                    if any(keyword in badge_text for keyword in ["인터넷 결합 가능", "IP TV 결합 가능"]):
                                        combination = True
                                        break  # ✅ 한 번 True가 되면 더 이상 검사하지 않음



                            # ✅ 문자열을 정수 변환 (예외 처리 추가)
                            sale_price = int(sale_price) if sale_price.isdigit() else 0
                            after_price = int(after_price) if after_price.isdigit() else normal_price
                            normal_price = int(normal_price) if normal_price.isdigit() else 0

                            # ✅ 12개월 & 24개월 요금 계산
                            if not promotion_period or promotion_period == "평생":
                                m12_price = sale_price * 12
                                m24_price = sale_price * 24
                            else:
                                months = int(re.search(r"(\d+)", promotion_period).group(1)) if promotion_period else 0
                                if months >= 24:
                                    m12_price = sale_price * 12
                                    m24_price = sale_price * 24
                                elif months >= 12:
                                    m12_price = sale_price * 12
                                    m24_price = (sale_price * months) + (after_price * (24 - months))
                                else:
                                    m12_price = (sale_price * months) + (after_price * (12 - months))
                                    m24_price = (sale_price * months) + (after_price * (24 - months))
                            log_prefix = f"[{item_idx+1}/{len(plan_items)}] "
                            print(f"{TAG} {log_prefix}파싱 중: {plan_name}")
                            buga_call_info, mno_value = self.fetch_detail_page(href, log_prefix=log_prefix)
                            # ✅ DTO 생성 및 추가
                            dto = PlanData(
                                uuid=uuid,
                                mno=mno_value,
                                telecom=SiteTargetListType.MARVELRING_LIST.value,
                                company_id=SiteTargetListIDType.MARVELRING_LIST.value,
                                url=href,
                                plan_type="5G" if "5G" in plan_name else "LTE",
                                plan_name=plan_name,
                                data=data_value,  # ✅ plan_name에서 직접 추출한 데이터 값 반영
                                daily_data=daily_data_value,  # ✅ plan_name에서 직접 추출한 일일 데이터 값 반영
                                qos=qos_value,  # ✅ plan_name에서 직접 추출한 QoS 값 반영
                                voice_call=voice_call,
                                message=message,
                                normal_price=normal_price,
                                sale_price=sale_price,
                                after_price=after_price,
                                promotion_period=promotion_period,
                                combination=combination,
                                buga_call=buga_call_info,
                                m12_price=m12_price,
                                m24_price=m24_price,
                                benefit="",
                                business_name="마블링",
                                freebies="",
                                etc="",
                                plan_code="",
                            )
                            result.append(dto)
                            success_count += 1

                        except Exception as e:
                            error_count += 1
                            print(f"{TAG} [{item_idx+1}/{len(plan_items)}] [ERROR] 요금제 파싱 실패: {e}")
                            traceback.print_exc()

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [ERROR] 목록 페이지 요청 실패 ({url}): {e}")
                    traceback.print_exc()

        except Exception as e:
            error_count += 1
            print(f"{TAG} [ERROR] 스크래핑 실패: {e}")
            traceback.print_exc()

        elapsed_total = time.time() - start_time
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초) | 성공: {success_count}, 스킵: {skip_count}, 에러: {error_count}")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        return result, True
