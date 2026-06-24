import sys
from utils.db.get_connection import db_connection

# 간단한 테스트 함수
@db_connection
def test_db_connection(connection):
    try:
        with connection.cursor() as cursor:
            # 간단한 쿼리 실행 (예: 데이터베이스 버전 확인)
            cursor.execute("SELECT VERSION()")
            version = cursor.fetchone()
            print(f"데이터베이스 버전: {version[0]}")

    except Exception as e:
        print(f"테스트 중 오류 발생: {e}")
        sys.exit(1)

if __name__ == "__main__":
    print("데이터베이스 연결 테스트를 시작합니다...")
    try:
        test_db_connection()
        print("데이터베이스 연결 테스트 성공!")
    except Exception as e:
        print(f"데이터베이스 연결 테스트 실패: {e}")
