from config.global_state import current_cycle_id, is_running
from utils.db.get_connection import db_connection


@db_connection
def log_to_db(cycle_id, log_type, function_name=None, message=None, connection=None):
    """로그를 데이터베이스에 저장."""
    with connection.cursor() as cursor:
        sql = """
        INSERT INTO tb_pmb_scraping_logs (cycle_id, log_type, function_name, message)
        VALUES (%s, %s, %s, %s)
        """
        cursor.execute(sql, (cycle_id, log_type, function_name, message))
    connection.commit()