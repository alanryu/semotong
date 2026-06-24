# SMTONG Admin

SMTONG 플랫폼의 관리자 시스템입니다. 요금제, 회원, 주문, 정산, 포인트, 고객서비스 등을 관리합니다.

## 목차

- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [사전 요구사항](#사전-요구사항)
- [환경 설정](#환경-설정)
- [빌드 및 실행](#빌드-및-실행)
- [프로파일 설정](#프로파일-설정)
- [주요 설정 파일](#주요-설정-파일)
- [모듈 구조](#모듈-구조)
- [배포](#배포)
- [보안](#보안)
- [트러블슈팅](#트러블슈팅)

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **Framework** | Spring Boot 3.3.3 |
| **Language** | Java 21 |
| **Build Tool** | Gradle |
| **Database** | MySQL |
| **ORM** | MyBatis 3.0.3 |
| **Session Store** | Redis (Spring Session) |
| **Template Engine** | Thymeleaf |
| **Security** | Spring Security + OTP |
| **Property Encryption** | Jasypt |

### 주요 라이브러리

- **Spring Boot Starters**: web, webflux, validation, security, data-redis, thymeleaf
- **Apache POI 5.2.3**: Excel 파일 처리
- **Apache HttpClient 5.3**: 외부 API 호출
- **ModelMapper 3.0.0**: 객체 매핑
- **Lombok**: 보일러플레이트 코드 제거
- **Log4jdbc**: SQL 로깅

---

## 프로젝트 구조

```
smtong2_admin/
├── build.gradle                 # Gradle 빌드 설정
├── settings.gradle              # Gradle 설정
├── gradlew / gradlew.bat        # Gradle Wrapper
│
├── src/main/java/kr/co/ucomp/
│   ├── SmtongAdminApplication.java    # 메인 애플리케이션
│   ├── ServletInitializer.java        # WAR 배포용 초기화
│   │
│   ├── common/                        # 공통 모듈
│   │   ├── auth/                      # 인증 에러 처리
│   │   ├── biztalk/                   # 카카오 메시지 유틸
│   │   ├── config/                    # 설정 클래스
│   │   │   ├── security/              # 보안 관련 설정
│   │   │   ├── SecurityConfig.java    # Spring Security 설정
│   │   │   ├── RedisConfig.java       # Redis 설정
│   │   │   ├── JasyptConfig.java      # 암호화 설정
│   │   │   └── WebConfig.java         # 웹 MVC 설정
│   │   ├── encrypt/                   # 암호화 유틸 (AES, SEED)
│   │   ├── exception/                 # 예외 처리
│   │   ├── global/                    # 전역 DTO, Enum, Response
│   │   ├── restapi/                   # REST API 로깅
│   │   └── util/                      # 공통 유틸리티
│   │
│   └── web/                           # 웹 모듈
│       ├── mbm/      # 회원/관리자 관리
│       ├── csm/      # 고객서비스 (FAQ, 공지사항)
│       ├── pmb/      # 요금제/상품 관리
│       ├── order/    # 주문 관리
│       ├── point/    # 포인트 시스템
│       ├── stl/      # 정산 관리
│       ├── svc/      # 서비스 관리 (배너, 챗봇, 이벤트)
│       ├── bizpurio/ # 카카오/SMS 메시징
│       ├── cmm/      # 공통코드, 파일관리
│       └── kakao/    # 카카오 OAuth
│
├── src/main/resources/
│   ├── application.yml              # 기본 설정
│   ├── application-local.yml        # 로컬 개발 환경
│   ├── application-dev.yml          # 개발 서버 환경
│   ├── application-staging.yml      # 스테이징 환경
│   ├── application-prod.yml         # 운영 환경
│   ├── log4jdbc.log4j2.properties   # SQL 로깅 설정
│   │
│   ├── mapper/                       # MyBatis SQL 매퍼
│   │   ├── BizPurioIFMapper.xml
│   │   ├── AdminUserMapper.xml
│   │   ├── PlanMngMapper.xml
│   │   └── ... (31+ 매퍼 파일)
│   │
│   ├── static/                       # 정적 리소스
│   │   ├── css/
│   │   ├── js/
│   │   ├── images/
│   │   └── fonts/
│   │
│   └── templates/                    # Thymeleaf 템플릿
│       ├── common/                   # 레이아웃, 공통 조각
│       └── pages/                    # 기능별 페이지
│
└── build/                            # 빌드 출력 디렉토리
```

---

## 사전 요구사항

### 필수 소프트웨어

| 소프트웨어 | 버전 | 비고 |
|-----------|------|------|
| **JDK** | 21 이상 | OpenJDK 또는 Oracle JDK |
| **Gradle** | 8.x | Wrapper 사용 가능 |
| **MySQL** | 8.x | 데이터베이스 |
| **Redis** | 6.x 이상 | 세션 저장소 |

### 환경 변수

| 변수명 | 필수 | 설명 |
|--------|------|------|
| `ENCRYPTION_KEY` | **필수** | Jasypt 암호화 키 (DB 비밀번호 등 복호화용) |
| `SPRING_PROFILES_ACTIVE` | 선택 | 프로파일 지정 (기본값: local) |

---

## 환경 설정

### 1. JDK 설치 및 설정

```bash
# Windows (환경변수 설정)
setx JAVA_HOME "C:\Program Files\Java\jdk-21"
setx PATH "%PATH%;%JAVA_HOME%\bin"

# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-21
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. 환경 변수 설정

```bash
# Windows
setx ENCRYPTION_KEY "your-encryption-key-here"
setx SPRING_PROFILES_ACTIVE "local"

# Linux/Mac
export ENCRYPTION_KEY="your-encryption-key-here"
export SPRING_PROFILES_ACTIVE="local"
```

### 3. Redis 설치 (로컬 개발용)

```bash
# Windows (WSL 또는 Docker 사용 권장)
docker run -d --name redis -p 6380:6379 redis:latest

# Linux
sudo apt-get install redis-server
sudo systemctl start redis

# Mac
brew install redis
brew services start redis
```

### 4. 로컬 업로드 디렉토리 생성 (Windows)

```bash
mkdir C:\uploadfile
```

---

## 빌드 및 실행

### 로컬 개발 실행

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

기본적으로 `local` 프로파일로 실행됩니다.

### 특정 프로파일로 실행

```bash
# Windows
set SPRING_PROFILES_ACTIVE=dev && gradlew.bat bootRun

# Linux/Mac
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

### WAR 파일 빌드

```bash
# Windows
gradlew.bat bootWar

# Linux/Mac
./gradlew bootWar
```

빌드 결과: `build/libs/admin.war`

### 테스트 실행

```bash
# Windows
gradlew.bat test

# Linux/Mac
./gradlew test
```

### 클린 빌드

```bash
# Windows
gradlew.bat clean build

# Linux/Mac
./gradlew clean build
```

---

## 프로파일 설정

| 프로파일 | 용도 | Redis 포트 | 업로드 경로 |
|---------|------|-----------|------------|
| `local` | 로컬 Windows 개발 | 6380 | `C:\uploadfile` |
| `dev` | 개발 서버 | 6380 | `/smtong_project/upload_img` |
| `staging` | 스테이징 서버 | 6380 | `/smtong_was/upload_img` |
| `prod` | 운영 서버 | 6380 | `/smtong_was/upload_img` |

### 프로파일별 특징

#### local
- Thymeleaf 캐시 비활성화
- DevTools 라이브 리로드 활성화
- 카카오 리다이렉트: `http://127.0.0.1:8080`

#### dev
- 개발 서버 DB 연결
- 카카오 리다이렉트: `https://tb.smtong.co.kr`

#### staging
- 스테이징 DB 연결
- DAOU API: 테스트 환경

#### prod
- 운영 DB 연결
- 모든 외부 API: 운영 엔드포인트
- DAOU, MiniGate: 운영 환경

---

## 주요 설정 파일

### application.yml (공통 설정)

```yaml
# 서버 설정
server:
  port: 8081
  servlet:
    context-path: /

# 세션 설정
spring:
  session:
    store-type: redis
    redis:
      namespace: semotong:session
    timeout: 18000  # 5시간

# MyBatis 설정
mybatis:
  type-aliases-package: kr.co.ucomp.domain
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

### 암호화된 속성 (Jasypt)

민감한 정보는 `ENC(...)` 형식으로 암호화되어 있습니다:

```yaml
spring:
  datasource:
    url: ENC(암호화된_URL)
    username: ENC(암호화된_사용자명)
    password: ENC(암호화된_비밀번호)
```

복호화를 위해 반드시 `ENCRYPTION_KEY` 환경변수가 설정되어야 합니다.

---

## 모듈 구조

### 아키텍처 패턴

```
Controller → Service → Mapper (MyBatis XML)
     ↓          ↓
   DTO      Entity
```

### 주요 모듈

| 패키지 | 설명 | 주요 기능 |
|--------|------|----------|
| `web.mbm` | 회원 관리 | 관리자 계정, 회사 관리, 사용자 관리 |
| `web.csm` | 고객 서비스 | FAQ, 공지사항, 정책, 리뷰 |
| `web.pmb` | 요금제 관리 | 인터넷 요금제, 혜택, 카테고리, 인기 요금제 |
| `web.order` | 주문 관리 | 요금제 주문, 일련번호 관리 |
| `web.point` | 포인트 | 적립, 현금전환, 네이버페이 연동 |
| `web.stl` | 정산 | 정산 관리, 수익 리포트 |
| `web.svc` | 서비스 | 배너, 팝업, 챗봇, 이벤트, 추천 요금제 |
| `web.bizpurio` | 메시징 | 카카오톡/SMS 발송 |
| `web.cmm` | 공통 | 공통코드, 파일 관리 |
| `web.kakao` | 카카오 | OAuth 로그인 |

### 외부 API 연동

| API | 용도 |
|-----|------|
| **Kakao** | OAuth 로그인, 카카오톡 메시지 |
| **GOGO Factory** | 주문 관리 |
| **MiniGate** | 모바일 결제 |
| **DAOU** | 통신사 API |
| **BizPurio** | SMS/알림톡 발송 |

---

## 배포

### WAR 파일 배포 (Tomcat)

#### 1. WAR 빌드

```bash
./gradlew bootWar
```

#### 2. Tomcat 설정

```bash
# 환경변수 설정 (setenv.sh 또는 setenv.bat)
export SPRING_PROFILES_ACTIVE=prod
export ENCRYPTION_KEY=your-encryption-key
export JAVA_OPTS="-Xms512m -Xmx1024m"
```

#### 3. WAR 배포

```bash
cp build/libs/admin.war $TOMCAT_HOME/webapps/
```

### 서버 요구사항

| 구분 | 최소 사양 | 권장 사양 |
|------|----------|----------|
| CPU | 2 Core | 4 Core |
| Memory | 2GB | 4GB |
| Disk | 20GB | 50GB |

### 체크리스트

- [ ] `ENCRYPTION_KEY` 환경변수 설정
- [ ] `SPRING_PROFILES_ACTIVE` 환경변수 설정
- [ ] Redis 서버 접근 가능 여부
- [ ] MySQL 데이터베이스 접근 가능 여부
- [ ] 업로드 디렉토리 생성 및 권한 설정
- [ ] 방화벽 포트 오픈 (8081, Redis 6380, MySQL 3306)

---

## 보안

### 인증 흐름

1. Form 로그인 (`/login`)
2. `CustomUserDetailsService`로 사용자 조회
3. OTP 검증 (`OtpVerificationFilter`)
4. 세션 생성 (Redis 저장)

### 권한 체계

| 역할 | 설명 |
|------|------|
| `ADMIN` | 시스템 관리자 |
| `HOST` | 호스트 관리자 |
| `USER` | 일반 사용자 |

### 화이트리스트 URL

인증 없이 접근 가능:
- `/login/**`, `/auth/**`, `/logincert/**`
- `/css/**`, `/js/**`, `/images/**`
- `/robots.txt`, `/favicon.ico`
- `/kakao/**` (웹훅)

### 암호화

- **비밀번호**: BCrypt 해싱
- **속성 암호화**: Jasypt (PBEWithHMACSHA512AndAES)
- **외부 API 암호화**: AES, SEED-CBC (서비스별 상이)

### CORS 설정

```java
// 모든 Origin 허용 (필요시 제한 권장)
allowedOriginPatterns: *
allowCredentials: true
allowedMethods: GET, POST, PUT, PATCH, DELETE, OPTIONS
```

---

## 트러블슈팅

### 1. 애플리케이션 시작 실패

**증상**: `Failed to configure a DataSource` 오류

**해결**:
```bash
# ENCRYPTION_KEY 환경변수 확인
echo $ENCRYPTION_KEY  # Linux/Mac
echo %ENCRYPTION_KEY% # Windows
```

### 2. Redis 연결 실패

**증상**: `Unable to connect to Redis` 오류

**해결**:
```bash
# Redis 실행 상태 확인
redis-cli -p 6380 ping
# 응답: PONG

# Docker로 Redis 실행
docker run -d -p 6380:6379 redis:latest
```

### 3. 파일 업로드 실패

**증상**: 업로드 디렉토리 관련 오류

**해결**:
```bash
# Windows
mkdir C:\uploadfile

# Linux
mkdir -p /smtong_project/upload_img
chmod 755 /smtong_project/upload_img
```

### 4. 세션 타임아웃

**증상**: 빈번한 로그아웃

**원인**: 기본 세션 타임아웃 5시간 (18000초)

**확인**:
```yaml
# application.yml
spring:
  session:
    timeout: 18000
```

### 5. SQL 로그 확인

**파일**: `src/main/resources/log4jdbc.log4j2.properties`

```properties
log4jdbc.spylogdelegator.name=net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator
log4jdbc.dump.sql.maxlinelength=0
```

---

## 개발 팁

### IDE 설정 (IntelliJ IDEA)

1. **Lombok 플러그인** 설치
2. **Annotation Processing** 활성화
   - Settings → Build → Compiler → Annotation Processors → Enable annotation processing

### Gradle 명령어 모음

| 명령어 | 설명 |
|--------|------|
| `./gradlew bootRun` | 개발 서버 실행 |
| `./gradlew bootWar` | WAR 파일 빌드 |
| `./gradlew test` | 테스트 실행 |
| `./gradlew clean` | 빌드 결과물 삭제 |
| `./gradlew dependencies` | 의존성 트리 출력 |

### 로컬 개발 시 유용한 설정

```yaml
# application-local.yml에서 Thymeleaf 캐시 비활성화
spring:
  thymeleaf:
    cache: false
  devtools:
    livereload:
      enabled: true
```

---

## 참고 문서

- [Spring Boot 3.3 Documentation](https://docs.spring.io/spring-boot/docs/3.3.x/reference/html/)
- [MyBatis Spring Boot Starter](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Jasypt Spring Boot](https://github.com/ulisesbocchio/jasypt-spring-boot)

---

## 라이선스

이 프로젝트는 내부용 소프트웨어입니다.

---

*마지막 업데이트: 2025-01*
