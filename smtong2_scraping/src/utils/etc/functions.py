# 수정일: 2024년 12월 08일
# 수정자: 양성훈
# 수정사유: 빈번하게 접속 오류 발생 
# 수정사항: 보다 안정적으로 연결되도록 연결 로직 변경


from datetime import datetime, timedelta
import concurrent.futures
from concurrent.futures import ThreadPoolExecutor, TimeoutError
import errno
from functools import wraps
import multiprocessing
import os
import signal
import time
from typing import List

import requests

from modules.dto.input_queue_dto import PlanData

OVER_FEE_FIX = """음성 1.98원/초
영상 3.3원/초
문자 22원/건
데이터 22.53원/MB"""
DATA_DETAIL_FIX = """데이터 무제한 혜택을 상업적으로 이용하여 네트워크 품질을 영향을 미치거나 네트워크 상황에 따라 장애 발생 우려가 있는 경우애는 일반 사용자 보호를 위해 데이터의 속도제어, 이용제한, 차단, 해지가 될 수 있어요. 

기본 제공 데이터내에서 m-Voip(무선 인터넷망을 활용해 무료로 음성통화를 이용하는 서비스. 예를 들면 카카오 보이스톡, 네이버 라인 등이 있어요) 사용이 가능해요.

기본제공량 초과 시 별도 요금은 부과되지 않지만 속도가 제어되면서 네트워크 환경에 따라 속도가 일시적으로 느려질 수 있어요. QoS 이용 시 속도는 평균 속도이며, 사용 환경에 따라 다를 수 있어요.

QoS(Quality of Service : 통신 서비스 품질, 보통 데이터 속도를 말해요) 체감 속도는 다음과 같아요.
<table class="serina-hanae">
    <thead>
        <tr>
            <th colspan="2">구분</th>
            <th>400Kbps</th>
            <th>1Mbps</th>
            <th>3Mbps</th>
            <th>5Mbps</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan="4">YouTube</td>
            <td>240p</td>
            <td>O<br>되감기 5초 로딩</td>
            <td>O<br>되감기 2초 로딩</td>
            <td>O</td>
            <td>O</td>
        </tr>
        <tr>
            <td>360p</td>
            <td>X<br>5초 재생 후 로딩</td>
            <td>O<br>되감기 5초 로딩</td>
            <td>O</td>
            <td>O</td>
        </tr>
        <tr>
            <td>720p</td>
            <td>X<br>재생시 5초 로딩</td>
            <td>X<br>30초 재생 후 로딩</td>
            <td>O<br>되감기 2초 로딩</td>
            <td>O</td>
        </tr>
        <tr>
            <td>720p</td>
            <td>X<br>재생시 10초 로딩</td>
            <td>X<br>30초 재생 후 로딩</td>
            <td>O<br>되감기 5초 로딩</td>
            <td>O<br>되감기 2초 로딩</td>
        </tr>
        <tr>
            <td>카카오톡</td>
            <td>사진(원본)</td>
            <td>약 1분정도 로딩</td>
            <td>약 15초정도 로딩</td>
            <td>약 8초정도 로딩</td>
            <td>약 5초정도 로딩</td>
        </tr>
        <tr>
            <td>SNS</td>
            <td>페이스북<br>인스타그램</td>
            <td>다소불편함</td>
            <td>5초 이상 로딩</td>
            <td>O</td>
            <td>O</td>
        </tr>
        <tr>
            <td colspan="2">네이버 접속 로딩(크롬브라우저)</td>
            <td>약 6초</td>
            <td>약 5초</td>
            <td>O</td>
            <td>O</td>
        </tr>
        <tr>
            <td colspan="2">음악 스트리밍<br>네비게이션, 게임플레이</td>
            <td>O</td>
            <td>O</td>
            <td>O</td>
            <td>O</td>
        </tr>
    </tbody>
</table>"""

VOICE_CALL_DETAIL_FIX ="""상업적 목적이 아닌 일반 통화 용도의 국내 음성 통화에 한하여, 무선(010, 011, 016, 017, 018, 019), 유선(지역02, 03X, 04X, 05X, 06X 등), 인터넷 전화(070)와 특수번호(1335, 120, 122 등)를 기본 제공해요.

그외 부가통화 전국대표번호(15XX,16XX), 평생개인번호(050X), 주파수공용통신(013등) 및 영상통화의 경우는 별도 부가통화 제공량에서 차감돼요(부가음성통화 1초당 1초, 영상통화 1초당 1.66초 차감).

부가통화 이용시 부가통화 사업자가 정하는 바에 따라 별도의 정보이용료가 발생될 수 있어요.

일부 정보이용료 상품, 소액결제이용료, KT114 직접연결수수료 및 통화료, KT114 전화번호 안내 서비스, 타사착신요금(콜렉트콜), 전화정보서비스(060) 정보이용료, 수신자부담통화, 부가서비스 월 이용료, 각종 후원금, 세금(부가세) 단말기대금, 번호이동요금, 가입비 등은 별도로 청구돼요.

불법스팸/상업적 용도 사용/비정상적인 과도한 사용을 방지하기 위해 경우에 따라 서비스를 제한할 수 있어요.
1. 음성통화량이 일 600분을 초과하는 횟수가 월 중 3회가 넘는 경우(영상통화는 음성의 1.66배로 차감)
2. 음성통화량이 월 6,000분을 초과할 경우(USIM프리티데이터선택11G의 경우는 월 10,000분 초과 시)
3. 음성/영상통화량 수신처가 월 1천 회선(합산) 초과하는 경우는 광고 및 상업적 목적으로 이용하는 것으로 간주해요."""

MESSAGE_DETAIL_FIX = """문자의 '기본제공' 혜택 제한기준은 아래를 참고하세요.

SMS, MMS(텍스트형, 멀티미디어형) 포함
1. 일 SMS & MMS 사용량이 (SKT망 : 500건 / KT망 : 500건 / LG망 : 500건)을 초과하는 경우
2. 일 SMS & MMS 사용량이 (SKT : 200건 & KT/LG U+망 : 150건)을 초과하는 횟수가 월 중 10회를 넘는 경우
3. 광고성 스팸 메시지 발송과 같이 상업적 목적으로 이용하거나 물리적 장치 또는 자동발송 프로그램을 이용하는 경우
4. 수신처가(SKT망 : 월 3,000개 & KT/LG U+망 : 월 2,000개) 회선을 초과하는 경우
※ 기본제공되는 문자 미소진시, 잔여량은 이월되지 않아요. 

문자메세지 기본 제공량 소진시 문자 SMS 22원/건, LMS 44원/건, MMS(사진/그림/배경음악 첨부) 220원/건, MMS(동영상 첨부) 440원 청구돼요."""
#'10분 전'과 같이 날짜가 안나오는 경우 날짜를 계산
def checkDate(dateString):
    now = datetime.now()
    if '분 전' in dateString:
        minuteMinus = dateString.split('분 전')[0].strip()
        date = now - timedelta(minutes=int(minuteMinus))
        return date.strftime("%Y.%m.%d")
    elif '시간 전' in dateString:
        hourMinus = dateString.split('시간 전')[0].strip()
        date = now - timedelta(hours=int(hourMinus))
        return date.strftime("%Y.%m.%d")
    elif '일 전' in dateString:
        daysMinus = dateString.split('일 전')[0].strip()
        date = now - timedelta(days=int(daysMinus))
        return date.strftime("%Y.%m.%d")
    elif '어제' in dateString:
        daysMinus = '1'
        date = now - timedelta(days=int(daysMinus))
        return date.strftime("%Y.%m.%d")
    else:
        return now

#숫자로만 나타나는 날짜를 연월일 형식으로 변경
def changeDate(time):
    unix_timestamp = int(time)
    date = datetime.utcfromtimestamp(unix_timestamp / 1000)
    # UTC에서 한국 시간(KST)으로 변환
    kst_date = date + timedelta(hours=9)
    year = kst_date.year
    month = kst_date.month
    day = kst_date.day
    return f"{year}-{month}-{day}"

def convert_date_format(date_str):
    
    checkedD = checkDate(date_str)
    
    # 입력 문자열의 형식 정의
    input_format = "%y. %m. %d"
    input_format2 = "%Y.%m.%d"
    input_format3 = "%y.%m.%d %H:%M"
    input_format4 = "%Y.%m.%d %H:%M"
    input_format5 = "%Y.%m.%d."
    
    # 출력 문자열의 형식 정의
    output_format = "%Y-%m-%d %H:%M:%S"
    
    # 문자열을 datetime 객체로 변환
    try:
        dt = datetime.strptime(checkedD, input_format)
    except ValueError:
        try:
            dt = datetime.strptime(checkedD, input_format2)
        except ValueError:
            try:
                dt = datetime.strptime(checkedD, input_format3)
            except ValueError:
                try:
                    dt = datetime.strptime(checkedD, input_format4)
                except ValueError:
                    try:
                        dt = datetime.strptime(checkedD, input_format5)
                    except ValueError:
                        return checkedD

    # 원하는 형식으로 포맷팅하여 반환
    return dt.strftime(output_format)

def run_with_timeout(func, args=(), kwargs={}, timeout=10):
    pool = multiprocessing.Pool(processes=1)
    result = pool.apply_async(func, args, kwargs)
    try:
        return result.get(timeout=timeout)
    except multiprocessing.TimeoutError:
        pool.terminate()
        print("Function timed out")
        return None
    finally:
        pool.close()
        pool.join()


def exract_qos(qos_text):
    qos =''
    if qos_text!= '':
        if 'Kbps' in qos_text:
            qos = qos_text.split('Kbps')[0].split(' ')[-1]+'Kbps' 
        elif 'Mbps' in qos_text:
            qos = qos_text.split('Mbps')[0].split(' ')[-1]+'Mbps'
    return qos

def fetch_url_with_retry(url, max_retries=5, delay=3):
    attempt = 0
    while attempt < max_retries:
        try:
            response = requests.get(
                url,
                # headers={
                #     "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0 Safari/537.36"
                # },
                headers = {
                    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                    "Accept": "application/json, text/javascript, */*; q=0.01",
                    "Referer": "https://eyes.co.kr/",
                    "X-Requested-With": "XMLHttpRequest",
                },
                timeout=10  # 타임아웃 설정
            )
            response.raise_for_status()  # HTTP 상태 코드 확인
            return response.text
        except requests.exceptions.RequestException as e:
            attempt += 1
            print(f"시도 {attempt} 실패: {e}")
            if attempt >= max_retries:
                print("최대 재시도 횟수 초과. 요청을 종료합니다.")
                raise
            time.sleep(delay)

def filter_detail(text:str):
    filter_list = [['소액결제','소액 결제', '소액결제이용료', '소액결제 이용료'],
                   ['해외로밍'],
                   ['모바일 핫스팟', '핫스팟 제공량', '데이터 테더링', '테더링은 제공량 한도', '테더링(핫스팟)은', '테더링 및', '* 테더링', '핫스팟 및 테더링은', '핫스팟/테더링 사용은', '테더링 제공량', '테더링은', '테더링의 경우'],
                   ['데이터쉐어링','데이터 쉐어링','쉐어링 제공량']]
    not_filter_list = [[],[],[],['데이터 쉐어링은 이용 불가']]
    filter_result = [False, False, False, False]
    for i in range(len(filter_list)):
        for filter in filter_list[i]:
            if (filter in text):
                filter_result[i] = True
                break
        for not_filter in not_filter_list[i]:
            if (not_filter in text):
                filter_result[i] = False
                break
            
    return filter_result

def filter_equal(text: str, filter_list:list, edit_list:list):
    if(text != None):
        final_text = text
        for i in range(len(filter_list)):
            if text == filter_list[i]:
                final_text = edit_list[i]
                
        return final_text
    else:
        return text

def filter_contain(text: str, filter_list:list, edit_list:list):
    if(text != None):
        final_text = text
        for i in range(len(filter_list)):
            if filter_list[i] in text:
                final_text = text.replace(filter_list[i],edit_list[i])
                
        return final_text
    else:
        return text
        
def filter_dto(dto_list: List[PlanData]):
    filtered_dto_list =[]
    
    for dto in dto_list:
        filtered_dto = dto
        filtered_dto.data = filter_contain(filtered_dto.data,[" GB"," MB"],["GB", "MB"])
        filtered_dto.qos = filter_equal(filtered_dto.qos,['-'],[''])
        
        filtered_dto.voice_call = filter_equal(filtered_dto.voice_call,
                                            ['미제공','없음','집/이동전화 무제한','음성/문자 기본제공','-','기본 제공','상세 참고']
                                            ,['0분','0분','무제한','기본제공','0분','기본제공','상세참고'])
        filtered_dto.voice_call =filter_contain(filtered_dto.voice_call,[' 분','링'],['분','링 (1초에 2.5링)'])
        
        filtered_dto.message = filter_equal(filtered_dto.message,['기본 제공','-','없음',''],['기본제공','0건','0건','0건'])
        filtered_dto.message = filter_contain(filtered_dto.message,[' 건'],['건'])
        
        filtered_dto.benefit = filter_equal(filtered_dto.benefit,['매일 3시간,주말,심야', '심야', '주말', '히어로 전용'],
                                            ['주말/심야/매일 3시간 중 1개 데이터 무제한', '매일 00시~07시 사용 데이터 할인', '주말 전용 데이터',
                                             '평일 오후 6시~10시+주말/공휴일 데이터 무제한'])
        
        
        filtered_dto_list.append(filtered_dto)
    
    return filtered_dto_list