# 메인 스크래핑 프로그램 (ver 0.4)  - 2024.12.21
from flask import Flask, request, jsonify, abort
import time
from datetime import datetime
import config.global_state as global_state  # 전역 상태 모듈 전체 import
from utils.db.logger import log_to_db   # 로그를 DB에 저장하는 함수
from modules.dto.input_queue_dto import AllData # DTO 클래스 import
from utils.phone.save_data import saveDetail, savePhone # 전화번호 저장 함수 import
from utils.etc.functions import filter_dto # DTO 필터링 함수 import
from utils.etc.data_processing import safe_str, truncate_message # 문자열 처리 함수 import
import logging # 로깅 모듈 import
import sys
import os
from api.scraping_api import scraping_api  # 새로 분리한 파일에서 가져옴
from tasks.scraping_tasks import start_scraping_task  # 새로 분리한 파일에서 가져옴
from selenium.webdriver.remote.remote_connection import LOGGER as selenium_logger

# 경로 설정 (PyInstaller 실행 시에도 config 등 내부 모듈을 찾을 수 있도록 추가)
sys.path.append(os.path.abspath(os.path.dirname(__file__)))  # 현재 파일 경로 추가
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))  # src 폴더 상위 경로 추가



# Flask 앱 초기화
app = Flask(__name__)
app.register_blueprint(scraping_api)  # Blueprint 등록


# 로깅 설정 및 Flask 실행
if __name__ == '__main__':
    # 로깅 설정
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(message)s',
        handlers=[
            logging.FileHandler("scrapingLog.log", mode='w', encoding='utf-8'),
            logging.StreamHandler()
        ]
    )

    # print() 출력 리디렉션
    class PrintLogger:
        def write(self, message):
            if message.strip():  # 내용이 있는 줄 처리
                for line in message.splitlines():
                    logging.info(line)  # 줄 단위로 기록
            elif message == "\n":  # 줄바꿈만 있는 경우
                pass  # 무시
        def flush(self):
            pass

    sys.stdout = PrintLogger()
    sys.stderr = PrintLogger()

    # Flask 로거 설정
    flask_log = logging.getLogger('werkzeug')
    flask_log.setLevel(logging.WARNING)

    # Selenium 및 webdriver_manager 로그 비활성화



    # Selenium 로거 설정
    selenium_logger.setLevel(logging.CRITICAL)
    for handler in selenium_logger.handlers:
        selenium_logger.removeHandler(handler)

    # WebDriver Manager 로거 설정
    webdriver_manager_logger = logging.getLogger("WDM")  # WebDriver Manager의 기본 로거 이름
    webdriver_manager_logger.setLevel(logging.CRITICAL)
    for handler in webdriver_manager_logger.handlers:
        webdriver_manager_logger.removeHandler(handler)

    # 프로그램 시작
    print("=== 스크래핑 프로그램 준비 완료 ===")
    print("다음 작업을 준비 중입니다.\n여러 작업을 순차적으로 실행합니다.")
    app.run(host="0.0.0.0", port=5001)


