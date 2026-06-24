import os
import time
import psutil
from datetime import datetime
import config.global_state as global_state
from utils.db.logger import log_to_db
from utils.etc.data_processing import safe_str, truncate_message
from utils.etc.functions import filter_dto
from utils.phone.save_data import saveDetail, savePhone
from modules.dto.input_queue_dto import AllData


def reap_zombies():
    """PID 1 방어용: 좀비 프로세스 회수 (init: true 보완)"""
    try:
        while True:
            pid, _ = os.waitpid(-1, os.WNOHANG)
            if pid == 0:
                break
    except ChildProcessError:
        pass


def process_site(site, cycle_id):
    """단일 사이트 스크래핑 작업"""
    try:
        page = 1
        while True:
            if not global_state.is_running:
                print(f"[INFO] 스크래핑 작업 중단 요청 - {site.__class__.__name__}")
                log_to_db(
                    cycle_id, "info",
                    function_name=safe_str(site.__class__.__name__),
                    message="스크래핑 작업이 중단되었습니다."
                )
                break

            try:
                result, is_end = site.root(page)
            except Exception as site_error:
                print(f"[ERROR] '{site.__class__.__name__}'의 탐색에 실패했습니다.")  # 콘솔 출력
                log_to_db(
                    cycle_id, "error",
                    function_name=safe_str(site.__class__.__name__),
                    # message=truncate_message(f"페이지 처리 중 오류: {safe_str(site_error)}")
                    message=f"사이트탐색실패: {safe_str(site_error)[:100]}"  # 앞 100자만 잘라서 저장
                )
                break
            finally:
                pass

            if result:
                if isinstance(result, AllData):
                    filtered_planData = filter_dto(result.planData)
                    saveDetail(result.detailInfo, cycle_id=cycle_id)
                    savePhone(filtered_planData, filtered_planData[0].telecom, cycle_id=cycle_id)
                else:
                    filtered_planData = filter_dto(result)
                    savePhone(filtered_planData, filtered_planData[0].telecom, cycle_id=cycle_id)

            if is_end or page > 200:
                break
            page += 1
    except Exception as e:
        log_to_db(
            cycle_id, "error",
            function_name=safe_str(site.__class__.__name__),
            message=truncate_message(safe_str(e))
        )

def scrapping_cycle(site_list, wait_time, cycle_id):
    """스크래핑 엔진 실행"""
    start_time = datetime.now()
    log_to_db(cycle_id, "start", message=f"Scraping Engine 시작 - {start_time}")
    print(f"[INFO] 사이클 {cycle_id} 시작 - {start_time}")

    for site in site_list:
        if not global_state.is_running:
            print(f"[INFO] 스크래핑 사이클 중단 요청 - {site.__class__.__name__}")
            log_to_db(cycle_id, "info", message="스크래핑 사이클이 중단되었습니다.")
            break

        print(f"[INFO] 실행 중인 사이트: {site.__class__.__name__}")
        time.sleep(1)
        process_site(site, cycle_id)

    
    # Chrome 및 ChromeDriver 강제 종료
    try:
        for proc in psutil.process_iter(attrs=["pid", "name"]):
            proc_name = proc.info["name"].lower()
            if proc_name in ["chromedriver", "chrome", "chrome.exe", "chromedriver.exe",
                             "chrome_crashpad_handler"]:
                try:
                    proc.terminate()
                    proc.wait(timeout=3)
                    if proc.is_running():
                        proc.kill()
                except psutil.NoSuchProcess:
                    pass
                except Exception:
                    pass
    except Exception:
        pass

    # 사이클 완료 후 잔여 좀비 프로세스 회수
    reap_zombies()
    
    end_time = datetime.now()
    duration = end_time - start_time
    log_to_db(cycle_id, "end", message=f"Scraping Engine 종료. 실행 시간: {duration}")
    print(f"[INFO] 사이클 {cycle_id} 종료 - {end_time} (총 실행 시간: {duration})")


def start_scraping_task(site_list, cycle_wait_time):
    """스크래핑 엔진 작업 실행"""
    global_state.is_running = True
    global_state.wait_time = cycle_wait_time

    while global_state.is_running:
        global_state.current_cycle_id += 1
        scrapping_cycle(site_list, global_state.wait_time, global_state.current_cycle_id)
        if not global_state.is_running:
            break
        print(f"[INFO] 대기 {global_state.wait_time}분 후 다음 사이클 시작")
        for _ in range(global_state.wait_time * 60):
            if not global_state.is_running:
                break
            time.sleep(1)