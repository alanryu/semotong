import os
from pprint import pprint
import config.global_state as global_state
from utils.db.logger import log_to_db
from utils.etc.data_processing import safe_str, truncate_message
from utils.etc.functions import filter_dto
from utils.phone.save_data import saveDetail, savePhone
from modules.dto.input_queue_dto import AllData, PlanData
from modules.site.eyagi.html_list import EyagiListAction

def save_to_file(result, is_end, file_name="DTO_result.txt"):
    """DTO 데이터를 파일에 저장"""
    try:
        with open(file_name, "w", encoding="utf-8") as file:
            for dto in result:
                # DTO 객체의 속성을 딕셔너리로 변환하고 파일에 기록
                pprint(dto.__dict__, stream=file)
            file.write(f"\nIs end: {is_end}\n")
        print(f"[INFO] Results successfully written to {file_name}.")
    except Exception as e:
        print(f"[ERROR] Failed to write results to file: {e}")

def process_site(site, cycle_id):
    """단일 사이트 스크래핑 작업 (DB 저장 및 파일 저장 포함)"""
    try:
        page = 1
        all_results = []  # 파일로 저장하기 위한 모든 DTO를 저장

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
                    message=f"사이트탐색실패: {safe_str(site_error)[:100]}"  # 앞 100자만 잘라서 저장
                )
                break
            finally:
                try:
                    if hasattr(site, 'browser') and site.browser:
                        site.browser.quit()
                except Exception as e:
                    print(f"[ERROR] WebDriver 종료 중 예외 발생: {e}")

            if result:
                # 파일 저장용으로 결과 누적
                all_results.extend(result)

                # DB에 저장
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

        # 모든 DTO를 파일로 저장
        save_to_file(all_results, is_end)

    except Exception as e:
        log_to_db(
            cycle_id, "error",
            function_name=safe_str(site.__class__.__name__),
            message=truncate_message(safe_str(e))
        )

def main():
    """Main 함수: SnowmanListAction 실행 및 결과 저장 (DB 및 파일)"""
    global_state.is_running = True  # 스크래핑 작업을 활성화
    cycle_id = 1  # 첫 번째 실행 사이클 ID
    site = UplusMobileListAction()

    print(f"[INFO] Scraping 시작 - Cycle ID: {cycle_id}")
    try:
        process_site(site, cycle_id)  # 사이트 데이터 처리 (DB 및 파일 저장)
        print("[INFO] Scraping 완료 및 데이터 저장 완료")
    except Exception as e:
        print("[ERROR] 메인 실행 중 오류 발생")
        log_to_db(
            cycle_id, "error",
            function_name="main",
            message=f"메인 작업 중 예외 발생: {safe_str(e)}"
        )

if __name__ == "__main__":
    main()
