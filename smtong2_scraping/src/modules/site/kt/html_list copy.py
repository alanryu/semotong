import json
import re
import traceback
from bs4 import BeautifulSoup
import requests
from modules.site.target import SiteTargetListType, SiteTargetListIDType
from modules.dto.input_queue_dto import PlanData
from utils.site.url_maker import UrlParm, make_url

def clean_text(text):
    """
    Cleans up unnecessary whitespaces and special characters from the text.
    """
    return re.sub(r"(자세히보기|\r|\n|\t)", "", text.strip())

def clean_plan_name(plan_name):
    """
    Cleans the plan_name by removing leading \r\n\t characters and trailing \n.
    """
    plan_name = re.sub(r"^\s*[\r\n\t]+", "", plan_name)  # Remove leading \r\n\t
    plan_name = plan_name.rstrip("\n")  # Remove trailing \n
    return plan_name




def parse_table(bs):
    """
    Parses an HTML table into a list of dictionaries where each dictionary represents a row.
    """
    tables = bs.find_all('table')
    all_results = []

    for table in tables:
        headers = []
        rows = []
        data_dict = []

        # Extract headers
        thead = table.find('thead')
        if thead:
            header_rows = thead.find_all('tr')
            for tr in header_rows:
                cells = tr.find_all(['th', 'td'])
                headers = [clean_text(cell.get_text()) for cell in cells]
        else:
            # If no thead, use the first row as header
            first_row = table.find('tr')
            if first_row:
                headers = [clean_text(cell.get_text()) for cell in first_row.find_all(['th', 'td'])]

        # Extract body rows
        tbody = table.find('tbody')
        if tbody:
            rows = tbody.find_all('tr')
        else:
            # If no tbody, take all rows
            rows = table.find_all('tr')

        for row in rows:
            if row == table.find('tr'):  # Skip header row if used as headers
                continue

            # Extract both th and td from the row
            cells = row.find_all(['th', 'td'])
            row_data = [clean_text(cell.get_text()) for cell in cells]

            if headers and len(row_data) == len(headers):
                # Create dict if row matches header length
                data_dict.append(dict(zip(headers, row_data)))
            else:
                # If row length doesn't match header length, fill missing data with empty strings
                while len(row_data) < len(headers):
                    row_data.append('')
                data_dict.append(dict(zip(headers, row_data)))

        all_results.extend(data_dict)

    return all_results

class KtListAction:
    def __init__(self):
        self.base_url = 'https://product.kt.com/wDic/getOptionItemListAjax.ajax'
        self.detail_base_url = 'https://product.kt.com/static/prodetail/{uuid}/web/itemAccordion/html/{accordion_file}'
        self.business_name = '(주)케이티'
        self.site_type = SiteTargetListType.KT_LIST.value
        self.site_id = SiteTargetListIDType.KT_LIST.value

    def get_accordion_file(self, html):
        """
        Extracts accordion file value from HTML.
        """
        bs = BeautifulSoup(html, 'html.parser')
        script_tags = bs.find_all('script')
        pattern = re.compile(r"var\s+accordion_File\s*=\s*'([^']*)'")
        for script in script_tags:
            match = pattern.search(str(script))
            if match:
                return match.group(1)
        return ''

    def create_dto(self, item, detail_url, param, plan_all_name, uuid):
        if not isinstance(item, dict):
            print(f"Item is not a dict: {item}")
            return None

        detail_plan_name = clean_plan_name(item.get('요금제', plan_all_name))
        detail_data = item.get('데이터', '')
        detail_voice_call = item.get('음성', '')
        detail_message = item.get('문자', '')
        detail_normal_price = re.sub(r"[^\d]", "", item.get('월정액', '0'))
        detail_benefit = item.get('혜택', '')
        # detail_sale_price = re.sub(r"[^\d]", "", item.get('할인가격', '0'))  # '할인가격' 예시 키

        try:
            dto = PlanData(
                uuid='KT' + uuid,
                mno = 'KT',
                telecom = SiteTargetListType.KT_LIST.value,
                company_id = SiteTargetListIDType.KT_LIST.value,  # company_id 추가
                url=detail_url,
                plan_type=param.get('type', ''),
                plan_name=detail_plan_name,
                data=detail_data,
                voice_call=detail_voice_call,
                message=detail_message,
                normal_price=int(detail_normal_price) if detail_normal_price.isdigit() else 0,
                # sale_price=int(detail_sale_price) if detail_sale_price.isdigit() else 0,
                sale_price = '',
                benefit=detail_benefit,
                qos='',
                business_name=self.business_name,
                after_price=int(detail_normal_price) if detail_normal_price.isdigit() else 0,
                combination='',
                freebies='',
                etc='',
                promotion_period='',
                buga_call='',
            )
            return dto
        except Exception as e:
            traceback.print_exc()
            return None

    def root(self, page: int = 1, *args, **kwargs) -> tuple[list[PlanData], bool]:
        """
        Fetches KT List data and returns PlanData DTOs.
        """
        is_end = False
        result = []

        param_list = [
            {'type': 'LTE', 'code': '2'},
            {'type': '5G', 'code': '81'},
            {'type': '3G', 'code': '3'},
        ]

        for param in param_list:
            url = make_url(
                base_url=self.base_url,
                url_params=[
                    UrlParm(key='cate_code', value='6002'),
                    UrlParm(key='pageNo', value=str(page)),
                    UrlParm(key='listSize', value='10000'),
                    UrlParm(key='filter_code', value=param['code'])
                ]
            )
            try:
                response = requests.get(url, headers={"Host": "product.kt.com"})
                response.raise_for_status()
                html = response.text
                bs = BeautifulSoup(html, 'html.parser')
                plan_list = bs.find_all('table', 'plan-list')

                for item in plan_list:
                    tbody = item.find('tbody')
                    if tbody:
                        first_row = tbody.find('tr')
                        if first_row:
                            plan_all_name = first_row.find('th').find('strong').text
                            detail_url = 'https://product.kt.com' + first_row.find_all('td')[2].find('a')['href']
                            uuid = detail_url.split('ItemCode=')[1].split('&')[0]

                            first_detail = requests.get(detail_url, headers={"Host": "product.kt.com"}).text
                            accordion_file = self.get_accordion_file(first_detail)
                            if not accordion_file:
                                continue

                            detail_url2 = self.detail_base_url.format(uuid=uuid, accordion_file=accordion_file)
                            response = requests.get(detail_url2, headers={"Host": "product.kt.com"})
                            response.raise_for_status()
                            bs = BeautifulSoup(response.text, 'html.parser')
                            table_data = parse_table(bs)

                            for item in table_data:
                                dto = self.create_dto(item, detail_url, param, plan_all_name, uuid)
                                if dto:
                                    result.append(dto)

            except requests.RequestException as e:
                print(f"Request error: {e}")
            except Exception as e:
                print(f"Error: {e}")
                traceback.print_exc()

        if not result:
            is_end = True

        return result, is_end
