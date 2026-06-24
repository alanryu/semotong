
import json  # 추가
from datetime import datetime
from typing import List
from modules.dto.input_queue_dto import DetailInfo, PlanData
from utils.etc.functions import DATA_DETAIL_FIX, MESSAGE_DETAIL_FIX, OVER_FEE_FIX, VOICE_CALL_DETAIL_FIX
# from utils.site.data_preprocess import preprocessAfterContentOriginResponse
from utils.db.get_connection import db_connection
import pytz
import re
from utils.etc.data_processing import safe_str, convert_data_to_mb, convert_qos_to_kbps  # 데이터 형변환 처리
from utils.db.logger import log_to_db   # 로그를 DB에 저장하는 함수


@db_connection
def savePhone(plan_list: List[PlanData], platform: str, cycle_id: int, connection=None, table_name="tb_pmb_plan_data", **kwargs):
# def savePhone(plan_list: List[PlanData], platform: str, cycle_id: int, connection=None, **kwargs):

    cursor = connection.cursor()
    kst = pytz.timezone('Asia/Seoul')
    nowDateTime = datetime.now(kst).strftime('%Y-%m-%d %H:%M:%S')
    
    sql = f"""
    INSERT INTO {table_name} 
    (uuid, mno, telecom, url, plan_type, plan_name, data, voice_call, message,
    normal_price, sale_price, benefit, qos, business_name, after_price, combination,
    freebies, promotion_period, etc, sale_status, create_date, modified_date, 
    company_id, buga_call, plan_code, daily_data, m12_price, m24_price, contract_option, hidden_yn,
    special_category, datasharing_yn, micropayment_yn, data_tethering, agreement_period,qos_blocked, 
    data_daily_tethering) 
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 
    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    ON DUPLICATE KEY UPDATE 
    modified_date = CASE 
        WHEN telecom <> VALUES(telecom)
            OR mno <> VALUES(mno)  
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
            OR buga_call <> VALUES(buga_call)
            OR plan_code <> VALUES(plan_code)
            OR daily_data <> VALUES(daily_data)
            OR m12_price <> VALUES(m12_price)
            OR m24_price <> VALUES(m24_price)
            OR contract_option <> VALUES(contract_option)
            OR hidden_yn <> VALUES(hidden_yn)
            OR special_category <> VALUES(special_category)
            OR datasharing_yn <> VALUES(datasharing_yn)
            OR micropayment_yn <> VALUES(micropayment_yn)
            OR data_tethering <> VALUES(data_tethering)
            OR agreement_period <> VALUES(agreement_period)
            OR qos_blocked <> VALUES(qos_blocked)
            OR data_daily_tethering <> VALUES(data_daily_tethering)
        THEN VALUES(modified_date)
        ELSE modified_date
    END,
    telecom = CASE WHEN telecom <> VALUES(telecom) THEN VALUES(telecom) ELSE telecom END,
    mno = CASE WHEN mno <> VALUES(mno) THEN VALUES(mno) ELSE mno END,
    url = CASE WHEN url <> VALUES(url) THEN VALUES(url) ELSE url END,
    plan_type = CASE WHEN plan_type <> VALUES(plan_type) THEN VALUES(plan_type) ELSE plan_type END,
    plan_name = CASE WHEN plan_name <> VALUES(plan_name) THEN VALUES(plan_name) ELSE plan_name END,
    data = CASE WHEN data <> VALUES(data) THEN VALUES(data) ELSE data END,
    voice_call = CASE WHEN voice_call <> VALUES(voice_call) THEN VALUES(voice_call) ELSE voice_call END,
    message = CASE WHEN message <> VALUES(message) THEN VALUES(message) ELSE message END,
    normal_price = CASE WHEN normal_price <> VALUES(normal_price) THEN VALUES(normal_price) ELSE normal_price END,
    sale_price = CASE WHEN sale_price <> VALUES(sale_price) THEN VALUES(sale_price) ELSE sale_price END,
    benefit = CASE WHEN benefit <> VALUES(benefit) THEN VALUES(benefit) ELSE benefit END,
    qos = CASE WHEN qos <> VALUES(qos) THEN VALUES(qos) ELSE qos END,
    business_name = CASE WHEN business_name <> VALUES(business_name) THEN VALUES(business_name) ELSE business_name END,
    after_price = CASE WHEN after_price <> VALUES(after_price) THEN VALUES(after_price) ELSE after_price END,
    combination = CASE WHEN combination <> VALUES(combination) THEN VALUES(combination) ELSE combination END,
    freebies = CASE WHEN freebies <> VALUES(freebies) THEN VALUES(freebies) ELSE freebies END,
    promotion_period = CASE WHEN promotion_period <> VALUES(promotion_period) THEN VALUES(promotion_period) ELSE promotion_period END,
    etc = CASE WHEN etc <> VALUES(etc) THEN VALUES(etc) ELSE etc END,
    sale_status = CASE WHEN sale_status <> VALUES(sale_status) THEN VALUES(sale_status) ELSE sale_status END,
    buga_call = CASE WHEN buga_call <> VALUES(buga_call) THEN VALUES(buga_call) ELSE buga_call END,
    plan_code = CASE WHEN plan_code <> VALUES(plan_code) THEN VALUES(plan_code) ELSE plan_code END,
    daily_data = CASE WHEN daily_data <> VALUES(daily_data) THEN VALUES(daily_data) ELSE daily_data END,
    m12_price = CASE WHEN m12_price <> VALUES(m12_price) THEN VALUES(m12_price) ELSE m12_price END,
    m24_price = CASE WHEN m24_price <> VALUES(m24_price) THEN VALUES(m24_price) ELSE m24_price END,
    contract_option = CASE WHEN contract_option <> VALUES(contract_option) THEN VALUES(contract_option) ELSE contract_option END,
    hidden_yn = CASE WHEN hidden_yn <> VALUES(hidden_yn) THEN VALUES(hidden_yn) ELSE hidden_yn END,
    special_category = CASE WHEN special_category <> VALUES(special_category) THEN VALUES(special_category) ELSE special_category END,
    datasharing_yn = CASE WHEN datasharing_yn <> VALUES(datasharing_yn) THEN VALUES(datasharing_yn) ELSE datasharing_yn END,
    micropayment_yn = CASE WHEN micropayment_yn <> VALUES(micropayment_yn) THEN VALUES(micropayment_yn) ELSE micropayment_yn END,
    data_tethering = CASE WHEN data_tethering <> VALUES(data_tethering) THEN VALUES(data_tethering) ELSE data_tethering END,
    agreement_period = CASE WHEN agreement_period <> VALUES(agreement_period) THEN VALUES(agreement_period) ELSE agreement_period END,
    qos_blocked = CASE WHEN qos_blocked <> VALUES(qos_blocked) THEN VALUES(qos_blocked) ELSE qos_blocked END,
    data_daily_tethering = CASE WHEN data_daily_tethering <> VALUES(data_daily_tethering) THEN VALUES(data_daily_tethering) ELSE data_daily_tethering END,
    create_date = COALESCE(create_date, VALUES(create_date));
    """

    for plan in plan_list:
        try:
            
            # 디버깅을 위해 현재 plan 데이터를 출력 : 특정 UUID에 대한 데이터만 출력
            # if plan.uuid == 'gogo2032':
            #    print(f"[DEBUG] Plan Data for uuid=gogo2032: {plan.__dict__}")
            
    
            # 1. 비교를 위해 plan_data 준비하기 (telecom과 mno를 교환)
            plan_data = {
                "uuid": safe_str(plan.uuid),
                "telecom": safe_str(plan.telecom),  
                "mno": safe_str(plan.mno),  
                "url": safe_str(plan.url),
                "plan_type": safe_str(plan.plan_type),
                "plan_name": safe_str(plan.plan_name),
                "data": convert_data_to_mb(plan.data, uuid=plan.uuid, mno=plan.mno, telecom=plan.telecom),
                "voice_call": safe_str(plan.voice_call),
                "message": safe_str(plan.message),
                "normal_price": plan.normal_price,
                "sale_price": plan.sale_price,
                "benefit": safe_str(plan.benefit),
                "qos": convert_qos_to_kbps(plan.qos, uuid=plan.uuid, mno=plan.mno, telecom=plan.telecom),
                "business_name": safe_str(plan.business_name),
                "after_price": plan.after_price,
                "combination": safe_str(plan.combination),
                "freebies": safe_str(plan.freebies),
                "promotion_period": safe_str(plan.promotion_period),
                "etc": safe_str(plan.etc),
                "sale_status": 1,  # Compare as integer
                "company_id": plan.company_id,
                "buga_call": safe_str(plan.buga_call),
                "plan_code": safe_str(plan.plan_code),
                "daily_data": convert_data_to_mb(plan.daily_data, uuid=plan.uuid, mno=plan.mno, telecom=plan.telecom),
                "m12_price": plan.m12_price,
                "m24_price": plan.m24_price,
                "contract_option": plan.contract_option,
                "hidden_yn": plan.hidden_yn,
                "special_category": plan.special_category,
                "datasharing_yn": plan.datasharing_yn,
                "micropayment_yn": plan.micropayment_yn,
                "data_tethering": plan.data_tethering,
                "agreement_period": plan.agreement_period,
                "qos_blocked": plan.qos_blocked,
                "data_daily_tethering": plan.data_daily_tethering
            }

            # print(f"[테스트] Plan Data for uuid={plan.uuid}: {plan_data}")

            # 2. DB에서 기존 데이터 가져오기
            cursor.execute(f"SELECT * FROM {table_name} WHERE uuid = %s", (safe_str(plan.uuid),))
            existing_row = cursor.fetchone()
            if existing_row:
                db_columns = [desc[0] for desc in cursor.description]
                db_data = dict(zip(db_columns, existing_row))

                # is_protected가 1이면 업데이트를 스킵
                if db_data.get("is_protected") == 1:
                    print(f"[SKIP] uuid={plan.uuid} is protected. Skipping update.")
                    continue  # 다음 plan으로 넘어감
                
                # 1) 데이터 타입을 정규화하기
                if "sale_status" in db_data:
                    db_data["sale_status"] = int(db_data["sale_status"]) if db_data["sale_status"] is not None else 0
                

                # 2) 비교를 위해 무시할 컬럼을 정의하기
                ignore_columns = {"id", "create_date", "modified_date"}
                changes = {
                    column_name: {"old": db_data[column_name], "new": plan_data[column_name]}
                    for column_name in db_columns
                    if column_name in plan_data
                    and column_name not in ignore_columns
                    and str(db_data[column_name]) != str(plan_data[column_name])  # Ensure string comparison for consistency
                }

                # 3) 변경 사항이 있을 경우 로그를 남기기
                if changes:
                    log_message = {
                        "uuid": safe_str(plan.uuid),
                        "changes": [
                            {"column": column, "old": str(change["old"]), "new": str(change["new"])}
                            for column, change in changes.items()
                        ]
                    }
                    log_to_db(cycle_id, "update_data", function_name=plan.telecom, message=json.dumps(log_message, ensure_ascii=False))
                    print(f"[DEBUG] Detected changes for uuid={plan.uuid}: {json.dumps(log_message, ensure_ascii=False)}")

            # 3. 쿼리 실행을 위해 값 준비하기
            values = (
                safe_str(plan.uuid),
                safe_str(plan.mno),
                safe_str(plan.telecom),
                safe_str(plan.url),
                safe_str(plan.plan_type),
                safe_str(plan.plan_name),
                convert_data_to_mb(plan.data, uuid=plan.uuid, mno=plan.mno, telecom=plan.telecom),
                safe_str(plan.voice_call),
                safe_str(plan.message),
                plan.normal_price if plan.normal_price is not None else 0,  # null일 경우 0으로 설정
                plan.sale_price if plan.sale_price is not None else 0,      # null일 경우 0으로 설정
                safe_str(plan.benefit),
                convert_qos_to_kbps(plan.qos, uuid=plan.uuid, mno=plan.mno, telecom=plan.telecom),
                safe_str(plan.business_name),
                plan.after_price if plan.after_price is not None else 0,    # null일 경우 0으로 설정
                safe_str(plan.combination),
                safe_str(plan.freebies),
                safe_str(plan.promotion_period),
                safe_str(plan.etc),
                1,  # Always set sale_status to 1
                nowDateTime,
                nowDateTime,
                plan.company_id,
                plan.buga_call,
                plan.plan_code,
                convert_data_to_mb(plan.daily_data, uuid=plan.uuid, mno=plan.mno, telecom=plan.telecom),
                plan.m12_price,
                plan.m24_price,
                plan.contract_option,
                plan.hidden_yn,
                plan.special_category,
                plan.datasharing_yn,
                plan.micropayment_yn,
                plan.data_tethering,
                plan.agreement_period,
                plan.qos_blocked,
                plan.data_daily_tethering
            )

            # 4. 쿼리 실행 : Execute SQL to insert or update the data
            cursor.execute(sql, values)
            if cursor.rowcount == 1:  # Insert
                # Prepare new_data as a dictionary mapping columns to their values
                columns = [
                    "uuid", "mno", "telecom", "url", "plan_type", "plan_name", "data", "voice_call", "message",
                    "normal_price", "sale_price", "benefit", "qos", "business_name", "after_price", "combination",
                    "freebies", "promotion_period", "etc", "sale_status", "create_date", "modified_date", "company_id", 
                    "buga_call", "plan_code", "daily_data", "m12_price", "m24_price", "contract_option", "hidden_yn",
                    "special_category", "datasharing_yn", "micropayment_yn", "data_tethering", "agreement_period",
                    "qos_blocked", "data_daily_tethering"
                ]
                new_data = dict(zip(columns, values))  # Create a dictionary mapping columns to values

                log_to_db(
                    cycle_id, "add_data",
                    function_name=plan.telecom,
                    message=json.dumps(new_data, ensure_ascii=False)
                )
                # print(f"[DEBUG] Successfully processed uuid={plan.uuid}")

        # 예외 처리 : 오류 발생 시 로그를 남기고 다음 데이터로 넘어가기
        except Exception as e:
            log_to_db(
                cycle_id,
                "error",
                function_name=platform,
                message=json.dumps({"uuid": plan.uuid, "error": str(e)}, ensure_ascii=False)
            )
            print(f"[ERROR] Failed to process uuid={plan.uuid}: {e}")

    phone_names = [safe_str(plan.uuid) for plan in plan_list]
    # 디버깅용 출력 구문 추가
    # print(f"[DEBUG] Platform value: {platform}")
    # print(f" (phone_names): {phone_names}")

    # SELECT로 업데이트 대상 확인 (sale_status가 0이 아닌 경우만 선택)
    cursor.execute(f"""
    SELECT uuid 
    FROM {table_name} 
    WHERE telecom = %s 
    AND uuid NOT IN ({','.join(['%s'] * len(phone_names))})
    AND sale_status != 0
    AND is_protected != 1
    """, tuple([platform] + phone_names))
    rows = cursor.fetchall()

    # 업데이트 대상 UUID 추출
    if rows:
        updated_uuids = [row[0] for row in rows]
        
        # 로그 테이블에 기록
        log_to_db(
            cycle_id, 
            "update_sale_status", 
            function_name=platform, 
            message=json.dumps({"updated_uuids": updated_uuids}, ensure_ascii=False)
        )
        print(f"[DEBUG] UUIDs with sale_status updated to 0: {json.dumps(updated_uuids, ensure_ascii=False)}")

        # UPDATE 쿼리 실행
        update_sql = f"""
        UPDATE {table_name}
        SET sale_status = 0
        WHERE telecom = %s 
        AND uuid IN ({','.join(['%s'] * len(updated_uuids))})
        """
        cursor.execute(update_sql, tuple([platform] + updated_uuids))
        connection.commit()
    #else:
        # print("[DEBUG] No UUIDs require sale_status update.")


#  detail_info 테이블에 데이터를 저장하는 함수, 그러나 현재 안쓰이고 있음
@db_connection
def saveDetail(detail_list: List[DetailInfo], connection=None, **kwargs):
    """
    detail_info 테이블에 데이터를 저장하며, 현재 쿼리 실행은 주석 처리되어 있음.
    """
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

    #  쿼리 실행 주석 처리
    # cursor.executemany(sql, values)
    connection.commit()
