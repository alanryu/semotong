import json
import os
import traceback
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
from utils.site.url_maker import UrlParm, make_url

class SktListAction:
    
    def save_json(self, data, filename="skt_data.json"):
        """크롤링한 데이터를 JSON 파일로 저장하는 함수"""
        os.makedirs("response_files", exist_ok=True)  # response_files 폴더 생성
        filepath = os.path.join("response_files", filename)

        with open(filepath, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=4)

        # print(f"✅ JSON 데이터가 {filepath} 파일에 저장되었습니다.")

    def root(self, page: int, *args, **kwargs) -> tuple[list[PlanData], bool]:
        is_end = False
        result = []
        crawled_data = []  # JSON 저장용 리스트

        paramList = [
            {"type": "LTE", "code": "F01121", "opClCd": "02"},
            {"type": "5G", "code": "F01713", "opClCd": "02"},
            {"type": "3G", "code": "F01122", "opClCd": "02"},
        ]

        for param in paramList:
            # 리스트 URL
            base_url = "https://www.tworld.co.kr/core-product/v1/product/mobile/plan-device-list"
            idxCtgCd = param["code"]
            opClCd = param["opClCd"]

            if idxCtgCd == "F01180":
                base_url = "https://www.tworld.co.kr/core-product/v1/submain/overall-product"

            url = make_url(
                base_url=base_url,
                url_params=[
                    UrlParm(key="idxCtgCd", value=idxCtgCd),
                    UrlParm(key="opClCd", value=opClCd),
                    UrlParm(key="size", value="10000"),
                    UrlParm(key="page", value=f"{page}"),
                    UrlParm(key="order", value="recommend"),
                    UrlParm(key="searchFltIds", value="null"),
                ],
            )

            # 리스트 URL 응답
            response = requests.get(
                url,
                headers={
                    "Host": "www.tworld.co.kr",
                    "referer": "https://www.tworld.co.kr/web/product/plan/list",
                },
            )
            data = response.json().get("result", {}).get("mobilePlanList", []) or \
                   response.json().get("result", {}).get("separateProductList", [])

            if not data:
                break

            for item in data:
                try:
                    uuid = item["prodId"]

                     # basFeeInfo 값이 숫자로 변환되지 않으면 해당 item을 건너뛰기
                    bas_fee_info = item.get("basFeeInfo", "").replace(",", "").strip()
                    try:
                        bas_fee_info = int(bas_fee_info)  # 숫자로 변환 시도
                    except ValueError:
                        #print(f"[INFO] Skipping item {uuid} due to non-numeric basFeeInfo: {item.get('basFeeInfo')}")
                        continue  # 변환 실패 시 다음 item으로 넘어감

                    detail_response = requests.get(
                        f"https://www.tworld.co.kr/core-product/v1/benefits/{uuid}/price-plan",
                        headers={
                            "Host": "www.tworld.co.kr",
                            "referer": f"https://www.tworld.co.kr/web/product/callplan/{uuid}",
                        },
                    )

                    benefits = []
                    benefitList = detail_response.json().get("result", [])

                    for benefit_unit in benefitList:
                        if "commPhrs" in benefit_unit:
                            benefits.append(benefit_unit["commPhrs"])

                    if not benefits:
                        detail_response = requests.get(
                            f"https://www.tworld.co.kr/core-product/v1/ledger/{uuid}/summaries",
                            headers={
                                "Host": "www.tworld.co.kr",
                                "referer": f"https://www.tworld.co.kr/web/product/callplan/{uuid}",
                            },
                        )
                        benefitList = detail_response.json().get("result", {}).get("prodBenfAreaList", [])
                        for benefit_unit in benefitList:
                            benefits.extend(prodBen["prodBenfNm"] for prodBen in benefit_unit.get("prodBenfList", []))

                    benefit = "|".join(benefits) if benefits else ""

                    # 데이터 용량 변환
                    data = item.get("basOfrGbDataQtyCtt", "").strip()
                    if data and data != "무제한":
                        data += "GB"
                    elif not data:
                        data = item.get("basOfrMbDataQtyCtt", "").strip()
                        if data and data != "함께쓰기":
                            data += "MB"

                    # 가격 데이터 변환
                    sale_price = item.get("basFeeInfo", 0)
                    after_price = item.get("basFeeInfo", 0)

                    if isinstance(item["selAgrmtAplyMfixAmt"], (int, float)):
                        sale_price = item["selAgrmtAplyMfixAmt"] if item["selAgrmtAplyMfixAmt"] > 0 else item["basFeeInfo"]
                    elif isinstance(item["selAgrmtAplyMfixAmt"], str):
                        sanitized_value = item["selAgrmtAplyMfixAmt"].replace(",", "").strip()
                        if sanitized_value.isdigit():
                            sale_price = int(sanitized_value) if int(sanitized_value) > 0 else item["basFeeInfo"]
                        else:
                            print(f"[WARNING] Non-numeric value found: {item['selAgrmtAplyMfixAmt']} → Using basFeeInfo ({item['basFeeInfo']})")
                            sale_price = item["basFeeInfo"]

                    after_price = sale_price

                    # JSON 저장용 데이터
                    extracted_info = {
                        "uuid": uuid,
                        "plan_name": item["prodNm"],
                        "data": data,
                        "voice_call": item["basOfrVcallTmsCtt"],
                        "message": item["basOfrCharCntCtt"],
                        "normal_price": item["basFeeInfo"],
                        "sale_price": sale_price,
                        "benefit": benefit,
                    }
                    crawled_data.append(extracted_info)

                    url=f"https://www.tworld.co.kr/web/product/callplan/{uuid}"

                    # 기존 uuid 값 앞에 "SKT_" 추가
                    uuid = f"SKT_{item['prodId']}"

                    normal_price=item["basFeeInfo"]
                    promotion_period=""
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

                    # DTO 변환
                    dto = PlanData(
                        uuid=uuid,
                        mno="SKT",
                        telecom=SiteTargetListType.SKT_LIST.value,
                        company_id=SiteTargetListIDType.SKT_LIST.value,
                        url=url,
                        plan_type=param["type"],
                        plan_name=item["prodNm"],
                        data=data,
                        voice_call=item["basOfrVcallTmsCtt"] + "분" if item["basOfrVcallTmsCtt"].isdigit() else item["basOfrVcallTmsCtt"],
                        message=item["basOfrCharCntCtt"] + "건" if item["basOfrCharCntCtt"].isdigit() else item["basOfrCharCntCtt"],
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit=benefit,
                        qos="",
                        business_name="에스케이텔레콤(주)",
                        after_price=after_price,
                        combination="",
                        freebies="",
                        etc="",
                        promotion_period=promotion_period,
                        buga_call="",
                        plan_code="",
                        daily_data='',
                        m12_price=m12_price,
                        m24_price=m24_price,
                    )

                    result.append(dto)

                except Exception as e:
                    traceback.print_exc()

        # JSON 데이터 저장
        self.save_json(crawled_data)

        # print(f"✅ Processed {len(result)} plans. Is end: {is_end}")
        if not result:
            is_end = True





        return result, is_end
