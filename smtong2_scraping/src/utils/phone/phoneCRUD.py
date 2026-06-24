from datetime import datetime
from typing import List
from modules.dto.input_queue_dto import DetailInfo, PlanData
from utils.etc.functions import DATA_DETAIL_FIX, MESSAGE_DETAIL_FIX, OVER_FEE_FIX, VOICE_CALL_DETAIL_FIX
from utils.site.data_preprocess import preprocessAfterContentOriginResponse
from utils.db.get_connection import db_connection
import pytz
import re

@db_connection
def savePhone(plan_list: List[PlanData], platform: str, connection=None, **kwargs):
    cursor = connection.cursor()
    # 한국 표준시 (KST) 시간대 설정
    kst = pytz.timezone('Asia/Seoul')

    # 현재 시간을 KST로 가져오기
    nowDateTime = datetime.now(kst).strftime('%Y-%m-%d %H:%M:%S')

    def convert_data_to_mb(data_str):
        """
        문자열 형태의 data 값을 MB 단위의 정수로 변환
        예:
        - '11GB' -> 11264
        - '100MB' -> 100
        - '1.5GB' -> 1536
        """
        if not data_str:
            return 0

        # 정규식을 사용하여 숫자와 단위를 분리
        match = re.match(r"(\d+(\.\d+)?)(GB|gb|MB|mb)", data_str, re.IGNORECASE)
        if match:
            value, _, unit = match.groups()
            value = float(value)
            if unit.lower() == "gb":
                return int(value * 1024)  # GB -> MB 변환
            elif unit.lower() == "mb":
                return int(value)        # MB 그대로 사용
        return 0  # 변환 실패 시 기본값

    def convert_qos_to_kbps(qos_str):
        """
        문자열 형태의 qos 값을 Kbps 단위의 정수로 변환
        예:
        - '100Mbps' -> 102400
        - '100Kbps' -> 100
        """
        if not qos_str:
            return 0

        # 정규식을 사용하여 숫자와 단위를 분리
        match = re.match(r"(\d+(\.\d+)?)(Mbps|mbps|Kbps|kbps)", qos_str, re.IGNORECASE)
        if match:
            value, _, unit = match.groups()
            value = float(value)
            if unit.lower() == "mbps":
                return int(value * 1024)  # Mbps -> Kbps 변환
            elif unit.lower() == "kbps":
                return int(value)        # Kbps 그대로 사용
        return 0  # 변환 실패 시 기본값

    # plan_type 값 정리: 4G -> LTE
    for plan in plan_list:
        if plan.plan_type == "4G":
            plan.plan_type = "LTE"

    # 쿼리 문자열 생성
    sql = """INSERT INTO tb_pmb_plan_data2 
    (uuid, mno, telecom, url, plan_type,
    plan_name, data, voice_call, message,
    normal_price, sale_price, benefit, qos, 
    business_name, after_price, combination, freebies, promotion_period, etc,
    sale_status, create_date, modified_date) 
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) 
    ON DUPLICATE KEY UPDATE 
    modified_date = 
        CASE 
            WHEN telecom <> VALUES(telecom) 
                 OR url <> VALUES(url) 
                 OR plan_type <> VALUES(plan_type) 
                 OR plan_name <> VALUES(plan_name) 
                 OR data <> VALUES(data) 
                 OR voice_call <> VALUES(voice_call) 
                 OR message <> VALUES(message) 
                 OR normal_price <> VALUES(normal_price) 
                 OR sale_price <> VALUES(sale_price) 
                 OR benefit <> VALUES(benefit) 
                 OR qos <> VALUES(qos) 
                 OR business_name <> VALUES(business_name) 
                 OR after_price <> VALUES(after_price) 
                 OR combination <> VALUES(combination) 
                 OR freebies <> VALUES(freebies) 
                 OR promotion_period <> VALUES(promotion_period) 
                 OR etc <> VALUES(etc)
                 OR modified_date is null
            THEN VALUES(modified_date)
            ELSE modified_date
        END,
    telecom = VALUES(telecom),
    url = VALUES(url),
    plan_type = VALUES(plan_type),
    plan_name = VALUES(plan_name),
    data = VALUES(data),
    voice_call = VALUES(voice_call),
    message = VALUES(message),
    normal_price = VALUES(normal_price),
    sale_price = VALUES(sale_price),
    benefit = VALUES(benefit),
    qos = VALUES(qos),
    business_name = VALUES(business_name),
    after_price = VALUES(after_price),
    combination = VALUES(combination),
    freebies = VALUES(freebies),
    promotion_period = VALUES(promotion_period),
    etc = VALUES(etc),
    sale_status = VALUES(sale_status),
    create_date = COALESCE(create_date, VALUES(create_date));"""

    # 값 목록 생성
    values = [
        (
            plan.uuid, plan.mno, plan.telecom ,plan.url,
            plan.plan_type, plan.plan_name,
            convert_data_to_mb(plan.data), plan.voice_call,
            plan.message, plan.normal_price,
            plan.sale_price, plan.benefit, convert_qos_to_kbps(plan.qos),
            plan.business_name,plan.after_price,plan.combination,plan.freebies,plan.promotion_period,plan.etc,
            "1", nowDateTime, nowDateTime
        )
        for plan in plan_list
    ]

    # 실행
    cursor.executemany(sql, values)

    phone_name = [plan.uuid for plan in plan_list]
    format_strings = ','.join(['%s'] * len(phone_name))

    # 테스트용 plan_data에서 tb_pmb_plan_data 변경 (12/06)
    update_sql = f"""UPDATE tb_pmb_plan_data2
    SET sale_status = '0'
    WHERE mno=%s and uuid NOT IN ({format_strings})"""

    cursor.execute(update_sql, tuple([platform] + phone_name))

    connection.commit()

@db_connection
def saveDetail(detail_list: List[DetailInfo], connection=None, **kwargs):
    cursor = connection.cursor()

    # 쿼리 문자열 생성
    sql = """INSERT INTO detail_info (uuid, data_detail, voice_call_detail, message_detail, precautions, over_fee, event,
    micropayment, overseas_roaming, mobile_hotspot, data_sharing) 
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) 
    ON DUPLICATE KEY UPDATE 
    data_detail = VALUES(data_detail),
    voice_call_detail = VALUES(voice_call_detail),
    message_detail = VALUES(message_detail),
    precautions = VALUES(precautions),
    over_fee = VALUES(over_fee),
    event = VALUES(event),
    micropayment = VALUES(micropayment),
    overseas_roaming = VALUES(overseas_roaming),
    mobile_hotspot = VALUES(mobile_hotspot),
    data_sharing = VALUES(data_sharing)
    """

    # 값 목록 생성
    values = [
        (
            detail.uuid, 
            DATA_DETAIL_FIX, VOICE_CALL_DETAIL_FIX,
            MESSAGE_DETAIL_FIX, detail.precautions, OVER_FEE_FIX, detail.event,
            detail.micropayment, detail.overseas_roaming,
            detail.mobile_hotspot, detail.data_sharing
        )
        for detail in detail_list
    ]
    # 테스트용으로 쿼리 실행 안함 (12/06 양성훈) => 향후에는 필요한지 검토 필요함
    # cursor.executemany(sql, values)
    connection.commit()
