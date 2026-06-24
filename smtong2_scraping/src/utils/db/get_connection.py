import dataclasses as dc
import os
from functools import wraps
import pymysql
from dotenv import load_dotenv
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import padding
import base64

# .env 파일에서 환경 변수를 로드합니다.
load_dotenv(override=True)  # 환경파일 캐싱 방지

@dc.dataclass(unsafe_hash=True)
class ConnectionInfo:
    host: str
    port: int
    user: str
    password: str
    db: str

# AES-256 복호화 함수
def decrypt_aes256(key: str, encrypted_data: str) -> str:
    try:
        # Base64로 인코딩된 키와 암호화된 데이터를 디코딩합니다.
        key_bytes = base64.urlsafe_b64decode(key)
        encrypted_data_bytes = base64.urlsafe_b64decode(encrypted_data)

        # IV(첫 16바이트)와 암호화된 텍스트를 분리합니다.
        iv = encrypted_data_bytes[:16]
        ciphertext = encrypted_data_bytes[16:]

        # CBC 모드로 AES 암호화를 생성합니다.
        cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv))
        decryptor = cipher.decryptor()

        # 복호화하고 패딩을 제거합니다.
        padded_plaintext = decryptor.update(ciphertext) + decryptor.finalize()
        unpadder = padding.PKCS7(algorithms.AES.block_size).unpadder()
        plaintext = unpadder.update(padded_plaintext) + unpadder.finalize()

        return plaintext.decode()
    except Exception as e:
        raise ValueError(f"복호화 실패: {e}")

# 환경변수에서 데이터베이스 연결 정보를 읽고 복호화합니다.
def _get_connection_info() -> ConnectionInfo:
    load_dotenv()

    # 암호화 키를 시스템 환경변수에서 읽어옵니다.
    encryption_key = os.getenv("ENCRYPTION_KEY")

    # 콘솔에 encryption_key 출력 (주의: 실제 환경에서는 민감한 정보 출력 금지)
    # print(f"DEBUG: ENCRYPTION_KEY = {encryption_key}")

    if encryption_key is None:
        raise ValueError("ENCRYPTION_KEY가 환경 변수에 설정되지 않았습니다.")

    # .env에서 암호화된 값을 읽어옵니다.
    encrypted_host = os.getenv("DB_HOST")
    encrypted_port = os.getenv("DB_PORT")  # 포트는 암호화되지 않음
    encrypted_user = os.getenv("DB_USER")
    encrypted_password = os.getenv("DB_PASSWORD")
    encrypted_db = os.getenv("DB_NAME")

    # 암호화된 데이터를 복호화합니다.
    try:
        host = decrypt_aes256(encryption_key, encrypted_host)
        port = int(encrypted_port)  # 포트는 숫자로 변환
        user = decrypt_aes256(encryption_key, encrypted_user)
        password = decrypt_aes256(encryption_key, encrypted_password)
        db = decrypt_aes256(encryption_key, encrypted_db)

        # 복호화된 정보를 콘솔에 출력
        # print(f"DEBUG: DB_HOST = {host}")
        # print(f"DEBUG: DB_USER = {user}")
        # print(f"DEBUG: DB_PASSWORD = {password}")
        # print(f"DEBUG: DB_NAME = {db}")



    except Exception as e:
        raise ValueError(f"환경 변수 복호화 중 오류 발생: {e}")

    if not host or not port or not user or not password or not db:
        raise ValueError(f"데이터베이스 연결 정보가 올바르게 설정되지 않았습니다. ['host': {host}, 'port': {port}, 'user': {user}, 'password': {password}, 'db': {db}]")

    return ConnectionInfo(host=host, port=port, user=user, password=password, db=db)

# 데이터베이스 연결을 위한 데코레이터
def db_connection(f):
    @wraps(f)
    def db_connection_(*args, **kwargs):
        conn = None
        conn_info = _get_connection_info()

        try:
            conn = pymysql.connect(host=conn_info.host,
                                   port=conn_info.port,
                                   user=conn_info.user,
                                   password=conn_info.password,
                                   db=conn_info.db,
                                   charset='utf8')

            result = f(*args, connection=conn, **kwargs)
        except Exception as e:
            print(f"데이터베이스 예외 오류: {e} / 연결 정보: {conn_info}")
            raise
        else:
            if conn:
                conn.commit()
        finally:
            if conn:
                conn.close()

        return result

    return db_connection_