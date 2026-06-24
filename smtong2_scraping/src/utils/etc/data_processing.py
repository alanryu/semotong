# db에 저장하기 전 데이터를 변환하는 로직


import re

def convert_data_to_mb(data_str, uuid=None, mno=None, telecom=None):
    """
    문자열 형태의 데이터 값을 MB 단위의 정수로 변환.
    - GB 단위는 MB로 변환.
    - "무제한"과 같은 키워드는 0으로 처리.
    """
    if not data_str or not isinstance(data_str, str):
        return 0

    try:
        match = re.search(r"(\d+(\.\d+)?)(GB|gb|MB|mb)", data_str, re.IGNORECASE)
        if match:
            value, _, unit = match.groups()
            value = float(value)
            if unit.lower() == "gb":
                return int(value * 1024)  # GB -> MB
            elif unit.lower() == "mb":
                return int(value)  # 그대로 MB
        elif "무제한" in data_str or "unlimited" in data_str.lower():
            return 0  # 무제한은 0으로 처리
    except Exception as e:
        # 예외 발생 시 로깅 추가 (개발 중에 디버깅에 유용)
        print(f"[WARNING] Failed to convert data to MB: {data_str}, Error: {e}")

    return 0


def convert_qos_to_kbps(qos_str, uuid=None, mno=None, telecom=None):
    """
    문자열 형태의 QoS 값을 Kbps 단위의 정수로 변환.
    - Mbps 단위는 Kbps로 변환.
    """
    if not qos_str or not isinstance(qos_str, str):
        return 0

    try:
        match = re.search(r"(\d+(\.\d+)?)(Mbps|mbps|Kbps|kbps)", qos_str, re.IGNORECASE)
        if match:
            value, _, unit = match.groups()
            value = float(value)
            if unit.lower() == "mbps":
                return int(value * 1024)  # Mbps -> Kbps
            elif unit.lower() == "kbps":
                return int(value)  # 그대로 Kbps
    except Exception as e:
        # 예외 발생 시 로깅 추가
        print(f"[WARNING] Failed to convert QoS to Kbps: {qos_str}, Error: {e}")

    return 0


def safe_int(value):
    """
    문자열이나 None 값을 안전하게 정수로 변환
    - 변환 불가능한 값은 0 반환
    """
    try:
        return int(value) if value else 0  # None이나 빈 값이면 0 반환
    except (ValueError, TypeError) as e:
        # 예외 발생 시 로깅 추가
        print(f"[WARNING] Failed to convert value to int: {value}, Error: {e}")
        return 0  # 변환 실패 시 0 반환


def safe_str(data):
    """문자열 데이터를 안전하게 UTF-8로 변환"""
    if isinstance(data, str):
        return data.encode('utf-8', 'replace').decode('utf-8', 'replace')
    return str(data)

def truncate_message(msg, max_length=64000):
    """메시지를 지정된 길이로 자르기"""
    if isinstance(msg, str):
        if len(msg.encode('utf-8')) > max_length:
            truncated = msg.encode('utf-8')[:max_length].decode('utf-8', 'ignore')
            return truncated + "...(truncated)"
    return msg