import re


def preprocessAfterContentOriginResponse(text: str) -> str:
    # 개행 문자 제거
    text = re.sub(r'\n', ' ', text)

    # 추가적인 공백 제거 (연속된 공백을 단일 공백으로 변환)
    text = re.sub(r'\s+', ' ', text).strip()

    # HTML 태그 제거
    text = re.sub(r'<[^>]*>', '', text)

    # 특수문자 제거
    text = re.sub(r'[^\w\s]', '', text)

    return text
