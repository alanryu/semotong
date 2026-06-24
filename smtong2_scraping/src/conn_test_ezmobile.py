import requests
import json

# API URL 및 요청 헤더 설정
url = "https://api.egmobile.co.kr/plan/list.php"
headers = {
    "Content-Type": "application/json; charset=UTF-8"
}

# 요청 바디
data = {
    "acckey": "2E1C40F809994CF71DBF1775B5B982A8299FC157"
}

# API 호출
response = requests.post(url, headers=headers, json=data)

# 응답 확인 및 JSON 저장
if response.status_code == 200:
    json_data = response.json()  # 응답 JSON 파싱
    with open("ara_plan_list.json", "w", encoding="utf-8") as json_file:
        json.dump(json_data, json_file, indent=4, ensure_ascii=False)
    print("JSON 데이터가 성공적으로 저장되었습니다.")
else:
    print(f"API 요청 실패: {response.status_code}, 응답: {response.text}")
