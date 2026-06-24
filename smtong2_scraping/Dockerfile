# =============================================================================
# Multi-stage Dockerfile for Python Scraping Application
# Python 3.11 + Chrome + Selenium
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Builder - 의존성 설치
# -----------------------------------------------------------------------------
FROM python:3.11-slim AS builder

WORKDIR /app

# 빌드에 필요한 시스템 패키지 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    libffi-dev \
    && rm -rf /var/lib/apt/lists/*

# requirements.txt 복사 및 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir --user -r requirements.txt

# -----------------------------------------------------------------------------
# Stage 2: Runtime - 실행 환경
# -----------------------------------------------------------------------------
FROM python:3.11-slim

# 환경변수 설정
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    DISPLAY=:99 \
    DEBIAN_FRONTEND=noninteractive

# Chrome 및 필수 패키지 설치 (단일 RUN으로 레이어 최소화)
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    gnupg \
    curl \
    unzip \
    ca-certificates \
    # Chrome 실행에 필요한 라이브러리
    libnss3 \
    libxss1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libdrm2 \
    libgbm1 \
    libx11-xcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    # 폰트 (한글 지원)
    fonts-nanum \
    # Google Chrome 설치
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends google-chrome-stable \
    # 정리
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# non-root 사용자 생성 (보안)
RUN useradd -m -u 1000 appuser

WORKDIR /app

# Builder stage에서 설치한 Python 패키지 복사
COPY --from=builder /root/.local /home/appuser/.local

# 애플리케이션 코드 복사 및 전체 권한 부여
COPY . .
RUN chown -R appuser:appuser /app

# 사용자 전환 및 PATH 설정
USER appuser
ENV PATH=/home/appuser/.local/bin:$PATH

# 포트 노출
EXPOSE 5001

# 헬스체크
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:5001/status || exit 1

# 애플리케이션 실행
CMD ["python", "src/scrapingApp.py"]
