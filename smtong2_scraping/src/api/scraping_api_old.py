# src/api/scraping_api.py
from flask import Blueprint, request, jsonify, abort
import threading
import config.global_state as global_state
from utils.db.logger import log_to_db
from tasks.scraping_tasks import start_scraping_task  # 새로 분리한 파일에서 가져옴
# from config.global_state import webdriver_instances
import psutil   # psutil 라이브러리 추가 (pip install psutil), 강제로 프로세스 종료하기 위해 사용 (종료 시점에 사용)
import ipaddress  # IP 주소 처리를 위한 모듈 추가


# Flask Blueprint 설정
scraping_api = Blueprint('scraping_api', __name__)

@scraping_api.before_request
def limit_remote_addr():
    client_ip = request.remote_addr

    # 1. 특정 IP 주소 검사
    if client_ip in global_state.ALLOWED_IPS:
        return

    # 2. 특정 범위 검사
    for prefix in global_state.ALLOWED_RANGES:
        if client_ip.startswith(prefix):
            return

    # 3. CIDR 범위 검사
    try:
        for cidr in global_state.ALLOWED_CIDR_RANGES:
            if ipaddress.IPv4Address(client_ip) in ipaddress.IPv4Network(cidr):
                return
    except ValueError as e:
        print(f"[ERROR] 잘못된 IP 형식: {client_ip}. 에러: {e}")

    # 4. 허용되지 않은 경우
    print(f"[ACCESS DENIED] IP {client_ip} is not allowed. Allowed IPs: {global_state.ALLOWED_IPS}, Allowed Ranges: {global_state.ALLOWED_RANGES}, Allowed CIDRs: {global_state.ALLOWED_CIDR_RANGES}")
    abort(403)

@scraping_api.route('/start', methods=['POST'])
def start_scraping():
    """스크래핑 엔진 시작 API"""
    if global_state.is_running:
        return jsonify({"status": "already running", "message": "이미 실행 중입니다."}), 400

    # 입력값 처리
    data = request.get_json()
    cycle_wait_time = data.get("wait_time", 20)  # 대기 시간
    site_list = global_state.site_list_default

    # 사이클 ID 초기화
    global_state.current_cycle_id = 0  # ID 초기화

    # 메시지 생성
    next_cycle_id = global_state.current_cycle_id + 1  # 표시할 사이클 ID
    log_message = f"대기시간이 {cycle_wait_time}분으로 스크래핑을 시작하였습니다."

    # 로그 테이블 기록
    log_to_db(next_cycle_id, "info", function_name="start_scraping", message=log_message)

    # 콘솔 출력
    print(f"[INFO] 사이클 {next_cycle_id} - {log_message}")
    # logging.info(f"사이클 {global_state.current_cycle_id} - {log_message}")

    # 스크래핑 스레드 시작
    global_state.scraping_thread = threading.Thread(
        target=start_scraping_task,
        args=(site_list, cycle_wait_time),
        daemon=True
    )
    global_state.scraping_thread.start()

    return jsonify({
        "status": "started",
        "cycle_id": next_cycle_id,
        "message": log_message
    }), 200


@scraping_api.route('/shutdown', methods=['POST'])
def shutdown_scraping():
    """스크래핑 엔진 종료 API"""
    if not global_state.is_running:  # global_state에서 is_running 참조
        return jsonify({"status": "not running", "message": "스크래핑 엔진이 실행 중이 아닙니다."}), 400

    # 종료 요청
    global_state.is_running = False
    if global_state.scraping_thread:
        global_state.scraping_thread.join()

    # 실행 중인 WebDriver 프로세스 종료
    try:
        # 모든 실행 중인 프로세스 확인
        for proc in psutil.process_iter(attrs=["pid", "name"]):
            if proc.info["name"] in ["chromedriver", "geckodriver"]:
                try:
                    proc.kill()
                    print(f"[INFO] WebDriver 프로세스 종료: {proc.info['name']} (PID: {proc.info['pid']})")
                except psutil.NoSuchProcess:
                    print(f"[WARNING] 프로세스를 찾을 수 없습니다: {proc.info['pid']}")
                except Exception as e:
                    print(f"[ERROR] 프로세스 종료 중 예외 발생: {e}")
    except Exception as e:
        print(f"[ERROR] WebDriver 프로세스 탐지 및 종료 중 예외 발생: {e}")

    # 종료 로그 기록
    log_message = "사용자에 의해 Scraping Engine이 종료되었습니다."
    log_to_db(global_state.current_cycle_id, "info", message=log_message)

    # 콘솔 출력
    print(f"[INFO] 사이클 {global_state.current_cycle_id} - {log_message}")
    return jsonify({
        "status": "stopped",
        "cycle_id": global_state.current_cycle_id,
        "message": log_message
    }), 200
    
    

@scraping_api.route('/status', methods=['GET'])
def get_status():
    return jsonify({
        "status": "running" if global_state.is_running else "stopped",
        "cycle_id": global_state.current_cycle_id,
        "wait_time": global_state.wait_time
    }), 200

