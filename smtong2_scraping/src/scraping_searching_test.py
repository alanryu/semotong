from modules.site.skylife.html_list import SkylifeListAction

if __name__ == "__main__":
    # skylifeListAction 클래스의 인스턴스 생성
    skylife_scraper = SkylifeListAction()
    
    # root 메서드 실행 (페이지 번호 1 전달)
    result, is_end = skylife_scraper.root(page=1)

    # 실행 결과 출력
    print(f"데이터 수집 완료 여부: {is_end}")
    print(f"수집된 데이터 개수: {len(result)}")

