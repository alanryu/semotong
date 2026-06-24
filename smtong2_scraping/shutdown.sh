#!/bin/bash

# 프로세스 종료
PID_FILE="scrapingApp.pid"

if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")

  # PID가 숫자인지 확인
  if [[ ! "$PID" =~ ^[0-9]+$ ]]; then
    echo "Invalid PID found in $PID_FILE. Cleaning up."
    rm -f "$PID_FILE"
    exit 1
  fi

  # 프로세스가 실행 중인지 확인
  if kill -0 "$PID" 2>/dev/null; then
    echo "Stopping scrapingApp... (PID: $PID)"
    kill "$PID"

    # 프로세스가 종료될 때까지 대기
    for i in {1..5}; do
      if kill -0 "$PID" 2>/dev/null; then
        sleep 1
      else
        break
      fi
    done

    # 아직 실행 중이면 강제 종료
    if kill -0 "$PID" 2>/dev/null; then
      echo "Force stopping scrapingApp... (PID: $PID)"
      kill -9 "$PID"
    fi

    echo "scrapingApp stopped successfully."
    rm -f "$PID_FILE"
  else
    echo "scrapingApp is not running."
    rm -f "$PID_FILE"
  fi
else
  echo "scrapingApp is not running or PID file is missing."
fi
