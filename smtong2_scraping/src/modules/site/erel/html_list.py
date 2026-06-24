import json
import time
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import AllData, DetailInfo, PlanData, Telecom
import re
from utils.etc.functions import filter_detail
from utils.site.url_maker import make_url
from urllib.parse import quote

TAG = '[Erel]'

def save_to_file(filename, content):
    """Helper function to save content to a file."""
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(content)

class ErelListAction():

    def root(self,
             page: int,
             *args,
             **kwargs) -> tuple[list[PlanData], bool]:
        total_start = time.time()
        print(f"{TAG} 스크래핑 시작")
        success_count = 0
        skip_count = 0
        error_count = 0
        is_end = False
        result = []
        detail_result = []

        # 리스트 URL
        url_list = [
            'https://www.erel.co.kr/nf-mvno-user/graphql',
        ]
        payload = {
            "operationName": "getCmSocs",
            "variables": {
                "where": {
                    "workerId": {"not": None},
                    "srvtp": {"equals": "P"},
                    "cnusecd": {"equals": "R"},
                    "isUse": {"equals": True},
                    "isFront": {"equals": True},
                    "isAgent": {"equals": False}
                },
                "orderBy": {"ord": "asc"}
            },
            "query": """query getCmSocs($where: CmSocWhereInput, $orderBy: [CmSocOrderByWithRelationInput!]) {
                getCmSocs(where: $where, orderBy: $orderBy) {
                    mnoGubun
                    code
                    name
                    socDesc
                    thumbnailId
                    thumbnailImage {
                        id
                        uri
                        filename
                        size
                        mimetype
                        encoding
                        uploader {
                            name
                            nickname
                            __typename
                        }
                        uploaderId
                        __typename
                    }
                    planType
                    realCode
                    basePrice
                    salePrice
                    dayPrice
                    priceDesc
                    isAtPeriodTimes
                    discountPeriod
                    discountUnit
                    originalPrice
                    cnusecd
                    isAgent
                    agents
                    isUseSelfOpen
                    isUseDelivery
                    SelfOpenLink
                    DeliveryLink
                    isUseSelfOpenLink
                    isUseDeliveryLink
                    isTabSelfOpenLink
                    isTabDeliveryLink
                    dataDisplayDivision
                    isUseNew
                    isUseTransfer
                    data
                    dataDesc
                    isMonthly
                    isDataQos
                    dataQos
                    isDataInfinity
                    dataInfinityText
                    voc
                    vocDesc
                    isVocInfinity
                    vocInfinityText
                    sms
                    smsDesc
                    isSmsInfinity
                    smsInfinityText
                    tags
                    isFront
                    isExternal
                    isExternalTabLink
                    externalLink
                    ord
                    isUse
                    isUsePay
                    createdAt
                    updatedAt
                    deletedAt
                    __typename
                }
            }"""
        }

        for base_url in url_list:
            url = make_url(base_url=base_url)

            # 리스트 URL 응답
            print(f"{TAG} HTTP POST 요청 시작: {url}")
            req_start = time.time()
            response = requests.post(url, json=payload)
            req_elapsed = time.time() - req_start
            print(f"{TAG} HTTP 응답 완료 ({req_elapsed:.1f}초), 상태코드: {response.status_code}")
            response_data = response.json()

            # 첫 번째 호출 결과 저장
            save_to_file('./response_files/list_Erel.json', json.dumps(response_data, indent=4, ensure_ascii=False))

            data = response_data['data']['getCmSocs']
            total_count = len(data)
            print(f"{TAG} 요금제 {total_count}건 발견")

            for index, item in enumerate(data, start=1):
                try:
                    uuid = item['code']
                    encode_uuid = quote(item['code'])
                    plan_type = item['planType']

                    telecom = ''
                    telecomText = item['mnoGubun']
                    if telecomText == 'LGT':
                        telecom = Telecom.LGU.name
                    elif telecomText == 'SKT':
                        telecom = Telecom.SKT.name
                    elif telecomText == 'KT':
                        telecom = Telecom.KT.name

                    normal_price = item['basePrice']
                    sale_price = item['salePrice']

                    data = item['data']
                    # print(f"data: {data}")
                    after_price = item['originalPrice']
                    if after_price in [None, "", 0]:
                        after_price = item['salePrice']

                    
                    # print(f"originalPrice: {item.get('originalPrice')}, salePrice: {item.get('salePrice')}, after_price: {after_price}")

                    
                    voice_call = str(item['voc']) + '분' if item['voc'] != 0 else '기본제공'
                    message = str(item['sms']) + '건' if item['sms'] != 0 else '기본제공'
                    

                    if data < 1000:
                        data = str(data) + 'MB'
                    elif data < 5000:
                        data = str((data // 100) / 10) + 'GB'
                    elif data < 20000:
                        data = str((data // 1000)) + 'GB'
                    else:
                        if int(str(data // 1000)[-1]) >= 5:
                            data = str((data // 10000) * 10 + 5) + 'GB'
                        else:
                            data = str((data // 10000) * 10) + 'GB'

                    data = data.replace('.0', '')

                    qos = ''
                    if item['isDataQos']:
                        qos = str(item['dataQos']) + 'Mbps'

                    detail_url = f"https://erel.co.kr/soc/view?code={uuid}"
                    detail_payload = {
                        "operationName": "getCmSoc",
                        "variables": {
                            "where": {"code": uuid},
                            "orderBy": [{"ord": "asc"}]
                        },
                        "query": """query getCmSoc($where: CmSocWhereUniqueInput!) {
                            getCmSoc(where: $where) {
                                content
                            }
                        }"""
                    }
                    
                    buga_call = item['vocDesc']
                    
                    if buga_call:  # None이나 빈 값이 아닌 경우
                        buga_call = buga_call.replace("부가통화", "").strip()  # "부가통화" 제거 후 앞뒤 공백 제거
                    else:
                        buga_call = ""  # None이나 빈 값일 경우 빈 문자열로 설정

                    # print(f"buga_call: {buga_call}")
                    
                    
                    # 상세 URL 호출 결과
                    print(f"{TAG} [{index}/{total_count}] 상세 페이지 요청: {item['name']}")
                    detail_start = time.time()
                    detail_response = requests.post(url, json=detail_payload)
                    detail_elapsed = time.time() - detail_start
                    print(f"{TAG} [{index}/{total_count}] 상세 페이지 응답 완료 ({detail_elapsed:.1f}초)")
                    detail_data = detail_response.json()

                    # 상세 호출 결과 저장
                    # save_to_file(f'./response_files/detail_Erel_{encode_uuid}.html', json.dumps(detail_data, indent=4, ensure_ascii=False))

                    # HTML 파싱 및 세부 정보 추출
                    content_html = detail_data['data']['getCmSoc']['content']
                    bs = BeautifulSoup(content_html, 'html.parser')
                    img_tags = bs.find_all('img')

                    data_detail = ''
                    voice_call_detail = ''
                    message_detail = ''
                    precautions_detail = ''

                    dataPTagList = []
                    voiceCallPTagList = []
                    messagePTagList = []
                    precautionsPTagList = []

                    for img in img_tags:
                        imgP_tag = img.find_parent()

                        if img['src'] == 'https://admin.erel.co.kr/upload/e/230216/081447.plan_set1_1.png':
                            self.get_pTags(imgP_tag, dataPTagList, 'data')
                            data_detail = "\n".join(dataPTagList)
                        elif img['src'] == 'https://admin.erel.co.kr/upload/e/230216/081457.plan_set1_2.png':
                            self.get_pTags(imgP_tag, voiceCallPTagList, 'voice')
                            voice_call_detail = "\n".join(voiceCallPTagList)
                        elif img['src'] == 'https://admin.erel.co.kr/upload/e/230216/081504.plan_set1_3.png':
                            self.get_pTags(imgP_tag, messagePTagList, 'msg')
                            message_detail = "\n".join(messagePTagList)
                        elif img['src'] == 'https://admin.erel.co.kr/upload/e/230216/083237.plan_set1_4.png':
                            self.get_pTags(imgP_tag, precautionsPTagList, 'caution')
                            precautions_detail = "\n".join(precautionsPTagList)

                    filter_result = filter_detail(content_html)
                    
                    detail_dto = DetailInfo(
                        uuid=encode_uuid,
                        data=data_detail,
                        voice_call=voice_call_detail.strip(),
                        message=message_detail.strip(),
                        precautions=precautions_detail.strip(),
                        over_fee='',
                        event='',
                        micropayment=filter_result[0],
                        overseas_roaming=filter_result[1],
                        mobile_hotspot=filter_result[2],
                        data_sharing=filter_result[3],
                    )
                    detail_result.append(detail_dto)

                    combination = False
                    if '40' in item['tags'].split(','):
                        combination = True

                    promotion_period=str(item['discountPeriod']) + "개월" if item['discountPeriod'] != None else "평생"

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

                    dto = PlanData(
                        uuid = 'EREL_'+ encode_uuid,
                        telecom=SiteTargetListType.EREL_LIST.value,
                        company_id=SiteTargetListIDType.EREL_LIST.value,  # company_id 추가
                        mno=telecom,
                        # url=detail_url,
                        url=f"https://erel.co.kr/soc/view/?code={encode_uuid}",  # 수정된 URL
                        plan_type=plan_type,
                        plan_name=item['name'],
                        data=data,
                        voice_call=voice_call,
                        message=message,
                        normal_price=normal_price,
                        sale_price=sale_price,
                        benefit='',
                        qos=qos,
                        business_name='주식회사 에르엘',
                        after_price= after_price,
                        combination=combination,
                        freebies='',
                        etc='',
                        promotion_period=promotion_period,
                        buga_call= buga_call,
                        plan_code = '',
                        daily_data='',
                        m12_price=m12_price,
                        m24_price=m24_price,
                    )
                    result.append(dto)
                    success_count += 1
                    print(f"{TAG} [{index}/{total_count}] 파싱 완료: {telecom} | {item['name']}")

                except Exception as e:
                    error_count += 1
                    print(f"{TAG} [{index}/{total_count}] [ERROR] 요금제 파싱 실패: {e}")
                    traceback.print_exc()

        elapsed_total = time.time() - total_start
        print(f"{TAG} 스크래핑 완료 (총 {elapsed_total:.1f}초)")
        print(f"{TAG} 성공: {success_count}건, 스킵: {skip_count}건, 에러: {error_count}건")
        print(f"{TAG} 최종 결과: PlanData {len(result)}건")
        is_end = True

        return AllData(
            planData=result,
            detailInfo=detail_result
        ), is_end

    def get_pTags(self, imgP_tag, pTagList, type):
        next_tag = imgP_tag.find_next_sibling()

        if type == 'caution':
            pTagList.append(next_tag.text)
            for sibling in next_tag.find_next_siblings():
                pTagList.append(sibling.text)

        else:
            if type == 'voice':
                next_tag = next_tag.find('p')
            elif type == 'msg':
                pass

            while True:
                if (next_tag is not None) and (next_tag.find('img') != None):
                    break
                if (next_tag is not None) and (next_tag.name == 'p'):
                    if (next_tag.get('style') != None) and ('padding-left' in next_tag.get('style')):
                        pTagList.append(next_tag.text)
                    next_tag = next_tag.find_next_sibling()
                else:
                    break
