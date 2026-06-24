# 세모통 (SMTONG) - 사용자 포털

> 통신 요금제 비교 및 가입 플랫폼

세모통은 다양한 통신사의 요금제를 비교하고, 최적의 요금제를 추천받아 간편하게 가입할 수 있는 서비스입니다.

## 목차

- [배포](#배포)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
  - [사전 요구사항](#사전-요구사항)
  - [환경변수 설정](#환경변수-설정)
  - [빌드 및 실행](#빌드-및-실행)
- [IDE 설정](#ide-설정)
- [로컬 개발 환경 구성](#로컬-개발-환경-구성)
- [환경별 설정](#환경별-설정)
- [주요 기능](#주요-기능)
- [코딩 컨벤션](#코딩-컨벤션)
- [API 구조](#api-구조)
- [인증/세션 관리](#인증세션-관리)
- [외부 API 연동](#외부-api-연동)
- [트러블슈팅](#트러블슈팅)
- [테스트](#테스트)

---

## 배포

> **주의: 배포 전 반드시 `git push`를 먼저 수행하세요.**
> 서버에서 git pull → 빌드 → 배포 순서로 진행되므로, push하지 않은 변경사항은 배포에 반영되지 않습니다.

### 서버 배포 (dev / prod 공통)

배포 경로는 dev, prod 모두 동일합니다.

```bash
# 1. 로컬에서 변경사항 push (필수!)
git push

# 2. 서버 접속 후 배포 스크립트 실행
cd /home/smtong/tomcat-user
./deploy.sh
```

`deploy.sh`가 자동으로 git pull → Gradle 빌드 → WAR 배포 → Tomcat 재시작을 수행합니다.

### 수동 배포 (참고)

스크립트를 사용하지 않고 수동으로 배포할 경우:

1. **WAR 파일 생성**
   ```bash
   ./gradlew bootWar
   ```

2. **배포**
   - `build/libs/ROOT.war` 파일을 Tomcat `webapps/` 디렉토리에 복사

3. **환경변수 설정**
   - 서버에 `ENCRYPTION_KEY` 환경변수가 설정되어 있는지 확인

4. **Tomcat 재시작**

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.3.3 |
| **Build Tool** | Gradle 8.11.1 |
| **Template Engine** | Thymeleaf |
| **ORM** | MyBatis 3.0.3 |
| **Database** | MySQL |
| **Cache/Session** | Redis |
| **Security** | Spring Security, Jasypt (설정 암호화) |

### 주요 라이브러리

- **Spring WebFlux** - 비동기 처리
- **Spring Session Redis** - 분산 세션 관리
- **Apache HttpClient 5.3** - REST API 호출
- **Apache POI 5.2.3** - Excel 처리
- **Lombok** - 보일러플레이트 코드 제거
- **Log4j2** - 로깅

---

## 프로젝트 구조

```
smtong2_user/
├── src/main/
│   ├── java/kr/co/ucomp/
│   │   ├── SmtongFrontApplication.java    # 메인 진입점
│   │   │
│   │   ├── common/                         # 공통 기능 모듈
│   │   │   ├── auth/                       # 인증 관련
│   │   │   │   └── oauth/                  # OAuth 소셜 로그인 (Kakao, Naver)
│   │   │   ├── biztalk/                    # 카카오 비즈톡 메시지 발송
│   │   │   ├── cardCert/                   # 신용카드 인증
│   │   │   ├── config/                     # Spring 설정
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── JasyptConfig.java
│   │   │   │   └── ...
│   │   │   ├── encrypt/                    # 암호화 유틸
│   │   │   ├── exception/                  # 예외 처리
│   │   │   ├── global/                     # 글로벌 유틸
│   │   │   └── util/                       # 공용 유틸리티
│   │   │
│   │   └── web/                            # 비즈니스 기능 모듈
│   │       ├── main/                       # 메인 페이지
│   │       ├── chatbot/                    # 챗봇 상담
│   │       ├── plan/                       # 요금제 관리
│   │       ├── order/                      # 주문/결제
│   │       ├── internet/                   # 인터넷 요금제
│   │       ├── mypage/                     # 마이페이지
│   │       ├── csm/                        # 고객 서비스
│   │       │   ├── banner/                 # 배너
│   │       │   ├── faq/                    # FAQ
│   │       │   ├── notice/                 # 공지사항
│   │       │   ├── review/                 # 리뷰
│   │       │   └── onetoone/               # 1:1 문의
│   │       └── event/                      # 이벤트
│   │
│   └── resources/
│       ├── application.yml                 # 기본 설정
│       ├── application-local.yml           # 로컬 환경
│       ├── application-dev.yml             # 개발 환경
│       ├── application-staging.yml         # 스테이징 환경
│       ├── application-prod.yml            # 상용 환경
│       ├── mapper/                         # MyBatis XML 매퍼 (39개)
│       ├── templates/                      # Thymeleaf 템플릿
│       └── static/                         # 정적 리소스 (CSS, JS, 이미지)
│
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 시작하기

### 사전 요구사항

- **Java 21** 이상
- **Gradle 8.x** 이상
- **MySQL** 데이터베이스
- **Redis** 서버

### 환경변수 설정

프로젝트 실행 전 반드시 `ENCRYPTION_KEY` 환경변수를 설정해야 합니다.

이 키는 Jasypt를 통해 암호화된 설정값(DB 접속 정보, API 키 등)을 복호화하는 데 사용됩니다.

#### Windows (CMD)

```cmd
:: 현재 세션에만 적용
set ENCRYPTION_KEY=your_encryption_key_here

:: 영구 등록 (새 터미널에서 적용)
setx ENCRYPTION_KEY "your_encryption_key_here"
```

#### Windows (PowerShell)

```powershell
# 현재 세션에만 적용
$env:ENCRYPTION_KEY = "your_encryption_key_here"

# 영구 등록
[Environment]::SetEnvironmentVariable("ENCRYPTION_KEY", "your_encryption_key_here", "User")
```

#### Linux / macOS

```bash
# 현재 세션에만 적용
export ENCRYPTION_KEY=your_encryption_key_here

# 영구 등록 (~/.bashrc 또는 ~/.zshrc에 추가)
echo 'export ENCRYPTION_KEY=your_encryption_key_here' >> ~/.bashrc
source ~/.bashrc
```

#### 환경변수 확인

```powershell
# PowerShell (VSCode 기본 터미널)
echo $env:ENCRYPTION_KEY

# CMD
echo %ENCRYPTION_KEY%
```

> **참고**: VSCode 터미널은 PowerShell이 기본이므로 `$env:변수명` 문법을 사용합니다.

### 빌드 및 실행

#### 개발 환경 실행

```bash
./gradlew bootRun --args="--spring.profiles.active=dev"
```

#### 로컬 환경 실행

```bash
./gradlew bootRun --args="--spring.profiles.active=local"
```

#### 스테이징 환경 실행

```bash
./gradlew bootRun --args="--spring.profiles.active=staging"
```

#### 상용 환경 실행

```bash
./gradlew bootRun --args="--spring.profiles.active=prod"
```

#### 빌드만 수행

```bash
./gradlew build
```

#### WAR 파일 생성

```bash
./gradlew bootWar
```

빌드 결과물: `build/libs/ROOT.war`

---

## IDE 설정

### IntelliJ IDEA (권장)

1. **JDK 설정**
   - File → Project Structure → Project SDK → Java 21 선택

2. **Annotation Processing 활성화** (Lombok 필수)
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - ✓ "Enable annotation processing" 체크

3. **인코딩 설정**
   - Settings → Editor → File Encodings → UTF-8

4. **권장 플러그인**
   - Lombok
   - MyBatis Support
   - Spring Boot Assistant

### Eclipse / STS

1. **Lombok 설치**
   ```bash
   java -jar lombok.jar
   ```
   IDE 위치 지정 후 Install

2. **인코딩 설정**
   - Preferences → General → Workspace → Text file encoding → UTF-8

3. **JDK 설정**
   - Preferences → Java → Compiler → Compiler compliance level: 21

---

## 로컬 개발 환경 구성

### MySQL 설치

**Docker 사용 (권장)**
```bash
docker run --name mysql-smtong \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -e MYSQL_DATABASE=smtong_db \
  -p 3306:3306 \
  -d mysql:8.0
```

### Redis 설치

**Docker 사용 (권장)**
```bash
docker run --name redis-smtong \
  -p 6380:6379 \
  -d redis:latest redis-server --requirepass your_password
```

### Docker Compose로 한번에 설정

프로젝트 루트에 `docker-compose.yml` 생성:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: smtong_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:latest
    command: redis-server --requirepass your_password
    ports:
      - "6380:6379"
    volumes:
      - redis_data:/data

volumes:
  mysql_data:
  redis_data:
```

실행:
```bash
docker-compose up -d
```

---

## 환경별 설정

| 환경 | 프로필 | URL | 설명 |
|------|--------|-----|------|
| 로컬 | `local` | http://127.0.0.1:8080 | 로컬 개발 |
| 개발 | `dev` | https://tb.smtong.co.kr | 개발 서버 |
| 스테이징 | `staging` | https://staging.smtong.co.kr | 스테이징 서버 |
| 상용 | `prod` | https://www.smtong.co.kr | 프로덕션 서버 |

### 설정 파일 구조

| 파일 | 설명 | 실행 명령어 |
|------|------|-------------|
| `application.yml` | 공통 설정 (기본값, 모든 환경에서 공유) | - |
| `application-local.yml` | 로컬 개발 환경 (localhost) | `./gradlew bootRun --args="--spring.profiles.active=local"` |
| `application-dev.yml` | 개발 서버 환경 (tb.smtong.co.kr) | `./gradlew bootRun --args="--spring.profiles.active=dev"` |
| `application-staging.yml` | 스테이징 서버 환경 (staging.smtong.co.kr) | `./gradlew bootRun --args="--spring.profiles.active=staging"` |
| `application-prod.yml` | 상용 서버 환경 (www.smtong.co.kr) | `./gradlew bootRun --args="--spring.profiles.active=prod"` |

### 암호화된 설정값

민감한 설정값은 Jasypt로 암호화되어 있습니다:

```yaml
spring:
  datasource:
    url: ENC(암호화된_값)
    username: ENC(암호화된_값)
    password: ENC(암호화된_값)
```

암호화 알고리즘: `PBEWithHMACSHA512AndAES`

---

## 주요 기능

| 모듈 | 설명 |
|------|------|
| **요금제 비교** | 다양한 통신사 요금제 조회, 비교, 추천 |
| **요금제 가입** | 온라인 요금제 가입 신청 및 결제 |
| **인터넷 서비스** | 인터넷 요금제 조회 및 신청 |
| **마이페이지** | 포인트, 캐시, 사용자 정보 관리 |
| **소셜 로그인** | Kakao, Naver OAuth 로그인 |
| **본인 인증** | 신용카드/휴대폰 본인 인증 |
| **고객 서비스** | FAQ, 공지사항, 리뷰, 1:1 문의 |
| **챗봇** | 실시간 상담 챗봇 |
| **이벤트** | 프로모션 및 이벤트 관리 |

---

## 코딩 컨벤션

### 패키지 구조

```
kr.co.ucomp.common.{module}  # 공통 기능 (auth, config, util 등)
kr.co.ucomp.web.{module}     # 비즈니스 기능 (plan, order, mypage 등)
```

### 클래스 네이밍

| 타입 | 네이밍 | 예시 |
|------|--------|------|
| Controller | `*Controller` | `PlanController` |
| Service Interface | `*Service` | `PlanService` |
| Service 구현체 | `*ServiceImpl` | `PlanServiceImpl` |
| DTO | `*DTO` | `PlanSearchDTO` |
| Entity | `*Entity` | `PlanEntity` |
| Mapper | `*Mapper` | `PlanMapper` |

### 메서드 네이밍

| 용도 | 네이밍 | 예시 |
|------|--------|------|
| 단일 조회 | `get*()` | `getPlan()` |
| 목록 조회 | `list()` | `list(param)` |
| 카운트 | `count()` | `count(param)` |
| 생성 | `create()` | `create(entity)` |
| 수정 | `update()` | `update(entity)` |
| 삭제 | `delete()` | `delete(id)` |

### MyBatis Mapper 작성 규칙

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="kr.co.ucomp.web.plan.mapper.PlanMapper">

    <!-- SQL 조각 재사용 -->
    <sql id="baseColumn">
        id, plan_name, price, create_date
    </sql>

    <!-- 동적 WHERE 조건 -->
    <sql id="listWhere">
        <if test='keyword != null and keyword != ""'>
            AND plan_name LIKE CONCAT('%', #{keyword}, '%')
        </if>
    </sql>

    <select id="list" resultType="kr.co.ucomp.web.plan.entity.PlanEntity">
        SELECT <include refid="baseColumn"/>
        FROM tb_plan
        WHERE 1=1
        <include refid="listWhere"/>
        ORDER BY create_date DESC
    </select>
</mapper>
```

---

## API 구조

### 공통 응답 형식

```json
{
  "code": 200,
  "status": "OK",
  "message": "Success",
  "totalCnt": 100,
  "data": [...]
}
```

### Response Codes

| 코드 | 상태 | 용도 |
|------|------|------|
| 200 | OK | 성공 |
| 400 | BAD_REQUEST | 잘못된 요청 |
| 401 | UNAUTHORIZED | 인증 필요 |
| 403 | FORBIDDEN | 권한 없음 |
| 404 | NOT_FOUND | 리소스 없음 |
| 500 | INTERNAL_SERVER_ERROR | 서버 오류 |

### 사용 예시

```java
// 성공 응답
return CustomApiResponse.success(ResponseCode.OK, totalCount, data);

// 에러 응답
return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "잘못된 요청입니다.");
```

---

## 인증/세션 관리

### 소셜 로그인 흐름 (Kakao)

```
1. 사용자 로그인 버튼 클릭
2. /auth/kakao/callback?code={code} 호출
3. OAuthService.getAccessToken(code) → 토큰 획득
4. OAuthService.getKakaoUser(token) → 사용자 정보 조회
5. session.setAttribute("userInfo", userInfo) → 세션 저장
6. 로그인 완료
```

### 세션 저장소 (Redis)

```yaml
spring:
  session:
    store-type: redis
    redis:
      namespace: semotong:session
  servlet:
    session:
      timeout: 36000  # 10시간
```

- 세션은 Redis에 분산 저장됩니다.
- 키 형식: `semotong:session:{SESSION_ID}`

---

## 외부 API 연동

### 소셜 로그인
- **Kakao OAuth 2.0** - 카카오 계정 로그인
- **Naver OAuth 2.0** - 네이버 계정 로그인

### 본인 인증
- **Coocon** - 휴대폰 본인인증
- **OkCert** - 신용카드 본인인증

### 주문/결제
- **GOGO Factory API** - 요금제 주문 처리
- **SHAKE Mobile API** - 통신사 연동

### 배송
- **DAOU** - 택배 배송 연동

### 메시지
- **비즈뿌리오** - 카카오 비즈톡 알림 발송

### 기타
- **도로명 주소 API** - 주소 검색
- **Google Analytics** - 사용자 분석

---

## 트러블슈팅

### ENCRYPTION_KEY 환경변수 미설정

**증상:**
```
Error: JasyptConfig - System.getenv("ENCRYPTION_KEY") returns null
```

**해결:**
```powershell
# PowerShell
$env:ENCRYPTION_KEY = "your_key_here"

# 영구 설정
[Environment]::SetEnvironmentVariable("ENCRYPTION_KEY", "your_key_here", "User")
```

### Redis 연결 오류

**증상:**
```
Error: Cannot connect to Redis at {host}:{port}
```

**확인:**
```bash
redis-cli -h {host} -p 6380 -a {password} ping
# 응답: PONG
```

### MyBatis Mapper 인식 안 됨

**증상:**
```
Error: Consider defining a bean of type 'XxxMapper' in your configuration
```

**해결:** Mapper 인터페이스에 `@Mapper` 애노테이션 추가
```java
@Mapper  // 필수!
public interface PlanMapper {
    // ...
}
```

### javax vs jakarta 패키지 오류

**증상:**
```
Error: Cannot resolve symbol 'javax.servlet.http.HttpSession'
```

**해결:** Spring Boot 3.x는 `jakarta` 패키지 사용
```java
// 올바른 import
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

// 잘못된 import (Spring Boot 2.x)
// import javax.servlet.http.HttpSession;
```

### HTTP 415 Unsupported Media Type

**원인:** Content-Type 헤더 미지정

**해결:**
```javascript
fetch('/api/endpoint', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'  // 필수!
    },
    body: JSON.stringify(data)
})
```

---

## 테스트

> **주의: 로컬에서 테스트 실행 시 반드시 `local` 프로파일을 사용하세요.**
> 프로파일을 지정하지 않으면 기본값 `local`이 적용되지만, 다른 프로파일(dev, staging, prod)로 테스트하면 실서버 DB/Redis에 접속될 수 있습니다.
> ```bash
> # 올바른 사용법
> SPRING_PROFILES_ACTIVE=local ./gradlew test
>
> # 특정 테스트 실행 시에도 동일
> SPRING_PROFILES_ACTIVE=local ./gradlew test --tests "JasyptConfigTest" --info
> ```

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "JasyptConfigTest"

# 테스트 출력(System.out.println) 보기
./gradlew test --tests "JasyptConfigTest" --info

# 캐시 무시하고 강제 재실행
./gradlew test --tests "JasyptConfigTest" --rerun-tasks

# 출력 보기 + 강제 재실행
./gradlew test --tests "JasyptConfigTest" --rerun-tasks --info
```

> **참고**: Gradle은 기본적으로 `System.out.println` 출력을 숨깁니다. `--info` 플래그를 추가해야 테스트 출력을 볼 수 있습니다.

### 테스트 클래스

| 클래스 | 설명 |
|--------|------|
| `SmtongFrontApplicationTests` | 애플리케이션 로드 테스트 |
| `JasyptConfigTest` | 암호화 설정 테스트 |
| `ConnectionTests` | 데이터베이스 연결 테스트 |

---

## 데이터베이스

### 커넥션 풀 설정 (HikariCP)

| 설정 | 값 | 설명 |
|------|-----|------|
| `maximum-pool-size` | 10 | 최대 커넥션 수 |
| `minimum-idle` | 5 | 최소 유휴 커넥션 |
| `idle-timeout` | 250,000ms | 유휴 타임아웃 |
| `max-lifetime` | 280,000ms | 최대 수명 |
| `connection-timeout` | 30,000ms | 연결 타임아웃 |

### 세션 저장소 (Redis)

- 네임스페이스: `semotong:session`
- 세션 타임아웃: 36,000초 (10시간)

---

## 프로젝트 정보

| 항목 | 값 |
|------|-----|
| **조직** | kr.co.ucomp |
| **프로젝트명** | smtong2_user |
| **Java 버전** | 21 |
| **Spring Boot 버전** | 3.3.3 |
| **패키징** | WAR |
| **Git Repository** | https://gitlab.smtong.co.kr/tracer999/user.git |

---

## 환경별 설정 비교

| 설정 | local | dev | staging | prod |
|------|-------|-----|---------|------|
| URL | localhost:8080 | tb.smtong.co.kr | staging.smtong.co.kr | www.smtong.co.kr |
| Thymeleaf 캐시 | false | true | true | true |
| DevTools | 활성화 | 비활성화 | 비활성화 | 비활성화 |
| 파일 업로드 경로 | C:\\uploadfile | /smtong_project/upload_img | /smtong_project/upload_img | /smtong_was/upload_img |

---

## 외부 라이브러리

프로젝트에 포함된 외부 JAR 파일:

```
src/main/resources/lib/
├── OkCert3-java1.5-2.3.3.jar              # OK-NAME 본인인증 라이브러리
└── V22530000094_CID_01_PROD_AES_license.dat  # 신용카드 인증 라이선스
```

build.gradle에서 자동 로드:
```gradle
implementation fileTree(dir: 'src/main/resources/lib', include: ['*.jar'])
```

---

## 빠른 시작 가이드

### 신규 개발자 체크리스트

```bash
# 1. JDK 21 설치 확인
java -version

# 2. 프로젝트 클론
git clone https://gitlab.smtong.co.kr/tracer999/user.git smtong2_user
cd smtong2_user

# 3. 환경변수 설정 (PowerShell)
$env:ENCRYPTION_KEY = "your_key_here"

# 4. Docker로 MySQL, Redis 시작
docker-compose up -d

# 5. 의존성 설치 및 빌드
./gradlew clean build

# 6. 로컬 환경 실행
./gradlew bootRun --args="--spring.profiles.active=local"

# 7. 브라우저에서 확인
# http://localhost:8080
```

### 자주 사용하는 명령어

```bash
# 빌드
./gradlew clean build

# 실행 (환경별)
./gradlew bootRun --args="--spring.profiles.active=local"
./gradlew bootRun --args="--spring.profiles.active=dev"

# 테스트
./gradlew test

# WAR 생성 (배포용)
./gradlew bootWar

# 의존성 확인
./gradlew dependencies

# 캐시 삭제
./gradlew clean
```

---

## 문의

기술적인 문의사항은 개발팀에 연락해주세요.
