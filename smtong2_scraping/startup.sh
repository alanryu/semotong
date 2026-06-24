#!/bin/bash

# 현재 실행 중인 프로세스를 확인하고 중복 실행 방지
PID_FILE="scrapingApp.pid"

if [ -f "$PID_FILE" ] && kill -0 $(cat "$PID_FILE") 2>/dev/null; then
  echo "scrapingApp is already running. (PID: $(cat $PID_FILE))"
  exit 1
fi

# 백그라운드 실행 (nohup 로그 제거)
nohup ./scrapingApp > /dev/null 2>&1 &

# PID 저장
echo $! > "$PID_FILE"
echo "scrapingApp started with PID $(cat $PID_FILE)."
