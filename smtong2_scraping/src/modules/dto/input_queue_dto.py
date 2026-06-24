import dataclasses as dc
from enum import Enum
from typing import List
from utils.etc.data_processing import safe_int

class Telecom(Enum):
    def _generate_next_value_(name, start, count, last_values):
        return name
    SKT = 1
    KT = 2
    LGU = 3

@dc.dataclass(unsafe_hash=True)
class PlanData:
    uuid: str
    mno: str
    url: str
    plan_type: str
    plan_name: str
    data: str
    voice_call: str
    message: str
    normal_price: int  # 타입을 int로 변경
    sale_price: int    # 타입을 int로 변경
    benefit: str
    qos: str
    business_name: str
    after_price: int   # 타입을 int로 변경
    combination: str
    freebies: str
    etc: str
    company_id: str
    promotion_period: str
    telecom: Telecom
    buga_call: str
    daily_data: str   # 일일 데이터 제공량
    plan_code: str   # 실제 개통 시 사용되는 요금제 코드
    m12_price: int  # 12개월 요금제 가격
    m24_price: int  # 24개월 요금제 가격

# ❗ 기본값이 있는 필드는 아래 배치 ❗
    data_tethering: int = 0  # 월 기준 핫스팟 제공량 (MB 단위로 저장)
    agreement_period: int = 0  # 약정기간(일) – 0: 약정기간 없음
    data_daily_tethering: int = 0  # 일 기준 핫스팟 제공량 (0 : 제공 안 함)    
    contract_option: int = 0  # 기본값 0으로 설정
    hidden_yn: int = 1 # 기본값 0으로 설정
    special_category: int = 0  # 특수 카테고리 여부 (이지모바일 전용)
    datasharing_yn: int = 0  # 데이터 쉐어링 지원 여부 (true/false)
    micropayment_yn: int = 0  # 소액 결제 가능 여부 (true/false)
    qos_blocked: int = 1  # QoS 차단 여부 (true/false)

    # 생성자에서 safe_int를 적용해 정수로 변환
    def __post_init__(self):
        self.normal_price = safe_int(self.normal_price)
        self.sale_price = safe_int(self.sale_price)
        self.after_price = safe_int(self.after_price)
        self.m12_price = safe_int(self.m12_price) if self.m12_price not in (None, "") else 0
        self.m24_price = safe_int(self.m24_price) if self.m24_price not in (None, "") else 0
        self.contract_option = safe_int(self.contract_option) if self.contract_option not in (None, "") else 0
        self.hidden_yn = safe_int(self.hidden_yn) if self.hidden_yn not in (None, "") else 0
        self.special_category = safe_int(self.special_category) if self.special_category not in (None, "") else 0
        self.datasharing_yn = safe_int(self.datasharing_yn) if self.datasharing_yn not in (None, "") else 0
        self.micropayment_yn = safe_int(self.micropayment_yn) if self.micropayment_yn not in (None, "") else 0
        self.agreement_period = safe_int(self.agreement_period) if self.agreement_period not in (None, "") else 0  
        self.data_tethering = safe_int(self.data_tethering) if self.data_tethering not in (None, "") else 0
        self.data_daily_tethering = safe_int(self.data_daily_tethering) if self.data_daily_tethering not in (None, "") else 0


@dc.dataclass(unsafe_hash=True)
class DetailInfo:
    uuid:str
    data:str
    voice_call:str
    message:str
    precautions:str
    over_fee:str
    event:str
    micropayment:str
    overseas_roaming:str
    mobile_hotspot:str
    data_sharing:str
    
@dc.dataclass(unsafe_hash=True)
class AllData:
    planData: List[PlanData]
    detailInfo: List[DetailInfo]