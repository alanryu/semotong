# ScrapingApp

ScrapingApp은 Python 기반 웹 스크래핑 엔진입니다. Flask를 API 계층으로 사용하며, 데이터 스크래핑, 로깅 및 데이터베이스 작업을 지원하는 모듈형 구조로 설계되었습니다.

---

## 목차
- [기능](#기능)
- [폴더 구조](#폴더-구조)
- [요구 사항](#요구-사항)
- [설치](#설치)
- [사용법](#사용법)
  - [애플리케이션 실행](#애플리케이션-실행)
  - [API 엔드포인트](#api-엔드포인트)
- [사이트 탐색](#사이트-탐색)
- [라이선스](#라이선스)

---

## 기능
- 스크래핑 작업, API 처리 및 데이터베이스 작업을 분리한 모듈형 아키텍처.
- 스크래핑 엔진을 제어하기 위한 Flask 기반 API.
- 데이터베이스 및 파일에 대한 고급 로깅.
- 여러 사이트에 대한 맞춤형 스크래핑 주기.
- WebDriver 프로세스의 자동 관리 및 종료.

---

## 폴더 구조
```
.
├── src/
│   ├── scrapingApp.py           # 메인 애플리케이션 파일
│   ├── modules/
│       ├── site/                # 각 사이트별 데이터 스크래핑 모듈
│           ├── amobile/         # A-Mobile 관련 모듈
│               └── html_list.py # A-Mobile 스크래핑 구현
│           ├── egmobile/        # EG-Mobile 관련 모듈
│               └── html_list.py # EG-Mobile 스크래핑 구현
│           ├── erel/            # Erel 관련 모듈
│           └── ...              # 기타 사이트 모듈 (총 10개)
│   ├── utils/                   # 유틸리티 모듈
│   ├── api/                     # API 엔드포인트 관리
│   ├── tasks/                   # 주기 작업 로직
│   ├── config/                  # 환경설정 및 전역 상태 관리
└── README.md                    # 프로젝트 문서
```

---

## 요구 사항
- Python 3.10+
- Flask
- psutil
- pytz
- MySQL 또는 호환 가능한 데이터베이스

필요한 패키지를 설치하려면 다음 명령을 실행하세요:
```bash
pip install -r requirements.txt
```

---

## 설치
1. 저장소를 클론합니다:
   ```bash
   git clone https://github.com/your-repository/scrapingApp.git
   ```
2. 프로젝트 디렉토리로 이동합니다:
   ```bash
   cd scrapingApp
   ```
3. 의존성을 설치합니다:
   ```bash
   pip install -r requirements.txt
   ```
4. 실행 파일을 빌드합니다:
   ```bash
   PYTHONPATH=src pyinstaller --onefile --name scrapingApp src/scrapingApp.py
   ```
5. 생성된 실행 파일을 프로젝트 루트 디렉토리로 이동합니다:
   ```bash
   mv dist/scrapingApp ./
   ```

---

## 사용법

### 애플리케이션 실행
1. 실행 스크립트를 사용하여 애플리케이션을 시작합니다:
   ```bash
   ./startup.sh
   ```

### 애플리케이션 종료
1. 실행 중인 애플리케이션을 종료하려면 다음을 실행하세요:
   ```bash
   ./shutdown.sh
   ```

---

## 사이트 탐색

`src/modules/site/` 디렉토리에는 10개의 사이트 관련 스크래핑 파일이 포함되어 있으며, 각 사이트에서 데이터를 스크래핑하는 로직을 제공합니다.

### 주요 사이트 예시
#### `src/modules/site/amobile/html_list.py`
A-Mobile 사이트에서 데이터를 스크래핑하는 파일입니다. Selenium과 BeautifulSoup을 활용하여 요금제 데이터를 추출합니다.

#### `src/modules/site/egmobile/html_list.py`
EG-Mobile 사이트에서 데이터를 스크래핑하는 파일입니다. 중복된 UUID 방지를 위한 로직이 포함되어 있습니다.

---

## 라이선스
이 프로젝트는 smtong.co.kr의 내부 용도로만 사용되며, 외부 배포 및 재사용은 엄격히 금지됩니다.
