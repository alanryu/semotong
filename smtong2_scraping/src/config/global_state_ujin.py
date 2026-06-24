# 전역 상태 및 설정

# 스크래핑 엔진 상태 변수, 전역 상태 변수
is_running = False
current_cycle_id = 0
wait_time = 20
scraping_thread = None

# IP 접근 허용 설정
ALLOWED_IPS = [
    "127.0.0.1",  # 로컬호스트
    "106.248.255.18",
    "106.248.255.19",
]
ALLOWED_RANGES = [
    "172.25.",         # 기존 허용 범위
    "182.228.212.",    # 추가 범위
    "211.192.222."     # 추가 범위
]
ALLOWED_CIDR_RANGES = [
    "182.228.212.0/24",
    "211.192.222.0/24",
]

webdriver_instances = []

# 사이트 리스트 기본값


from modules.site.kgmobile.html_list import KgMobileListAction        # 우선 순위 : 1
from modules.site.insmobile.html_list import InsMobileListAction      # 우선 순위 : 2
from modules.site.erel.html_list import ErelListAction                # 우선 순위 : 3
from modules.site.eyesmobile.html_list import EyesMobileListAction    # 우선 순위 : 4
from modules.site.tplus.html_list import TPlusListAction              # 우선 순위 : 5
from modules.site.amobile.html_list import AMobileListAction          # 우선 순위 : 6
from modules.site.egmobile.html_list import EGMobileListAction        # 우선 순위 : 7
from modules.site.gogomobile.html_list import GogoMobileListAction    # 우선 순위 : 8
from modules.site.shake_mobile.html_list import ShakeMobileListAction # 우선 순위 : 9
from modules.site.mobing.html_list import MobingListAction            # 우선 순위 : 10
from modules.site.eyagi.html_list import EyagiListAction              # 우선 순위 : 10
from modules.site.marvelring.html_list import MarvelringListAction    # 우선 순위 : 12
from modules.site.sugarmobile.html_list import SugarMobileListAction  # 우선 순위 : 13
from modules.site.s1.html_list import S1MobileListAction              # 우선 순위 : 14  
from modules.site.liivm.html_list import LiivmListAction              # 우선 순위 : 15
from modules.site.mona.html_list import MonaListAction               # 우선 순위 : 16
from modules.site.pindirect.html_list import PindirectListAction    # 우선 순위 : 17
from modules.site.smartel.html_list import SmartelListAction
from modules.site.winnerstel.html_list import WinnerstelListAction
from modules.site.chancemobile.html_list import ChanceMobileListAction 
from modules.site.kt.html_list import KtListAction                # 우선 순위 : 18
from modules.site.skt.html_list import SktListAction 
from modules.site.lgu.html_list import LguListAction 
from modules.site.hello_mobile.html_list import HelloMobileListAction
from modules.site.sevenmobile.html_list import SevenMobileListAction
from modules.site.uplusmobile.html_list import UplusMobileListAction
from modules.site.ktmmobile.html_list import KtmMobileListAction
from modules.site.skylife.html_list import SkylifeListAction
from modules.site.kctvmobile.html_list import KctvMobileListAction



# 사이트 리스트 기본값
site_list_default = [
    LguListAction(),           # 우선 순위 : 0
    HelloMobileListAction(),   # 우선 순위 : 0
    SevenMobileListAction(),   # 우선 순위 : 0
    KctvMobileListAction(),    # 우선 순위 : 0
    KtmMobileListAction(),     # 우선 순위 : 0
    SkylifeListAction(),       # 우선 순위 : 0
    UplusMobileListAction(),   # 우선 순위 : 0
    SktListAction(),           # 우선 순위 : 0
    KgMobileListAction(),      # 우선 순위 : 1
    InsMobileListAction(),     # 우선 순위 : 2
    ErelListAction(),          # 우선 순위 : 3
    EyesMobileListAction(),    # 우선 순위 : 4
    TPlusListAction(),         # 우선 순위 : 5
    AMobileListAction(),       # 우선 순위 : 6
    EGMobileListAction(),      # 우선 순위 : 7
    GogoMobileListAction(),    # 우선 순위 : 8
    ShakeMobileListAction(),   # 우선 순위 : 9
    MobingListAction(),        # 우선 순위 : 10
    EyagiListAction(),         # 우선 순위 : 11
    MarvelringListAction(),    # 우선 순위 : 12
    SugarMobileListAction(),   # 우선 순위 : 13
    S1MobileListAction(),      # 우선 순위 : 14
    LiivmListAction(),          # 우선 순위 : 15
    MonaListAction(),          # 우선 순위 : 16
    PindirectListAction(),     # 우선 순위 : 17
    KtListAction(),             # 우선 순위 : 18
    SmartelListAction(),    # 우선 순위 : 19
    WinnerstelListAction(),    # 우선 순위 : 20
    ChanceMobileListAction()    # 우선 순위 : 21
]




