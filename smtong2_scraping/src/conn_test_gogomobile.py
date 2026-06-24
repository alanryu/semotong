import requests
import json
import urllib3

# InsecureRequestWarning 경고 무시
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# 헤더 설정
headers = {
    "Authorization": "057485accc44c076e27b7fa2328232e4"
}

# POST 데이터 (빈 데이터)
post_data = {}

# 요청 보내기
url = "https://api.gogofactory.co.kr/info/semo/online_plan2.php"
response = requests.post(url, headers=headers, data=post_data, verify=False)

# JSON 파일 저장
filename = "./response_files/list_gogomobile_planlist.json"
try:
    with open(filename, "w", encoding="utf-8") as json_file:
        json.dump(response.json(), json_file, ensure_ascii=False, indent=4)
    print(f"JSON 데이터가 {filename} 파일로 저장되었습니다.")
except ValueError:
    print("Invalid JSON response:", response.text)