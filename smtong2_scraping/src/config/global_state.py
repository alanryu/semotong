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
    "172.17.",         # Docker 네트워크 범위
    "172.18.",
    "172.19.",
    "172.20.",
    "172.21.",
    "172.22.",
    "172.23.",
    "172.24.",
    "172.25.",
    "172.26.",
    "172.27.",
    "172.28.",
    "172.29.",
    "172.30.",
    "172.31.",
    "182.228.212.",    # 추가 범위
    "211.192.222."     # 추가 범위
]
ALLOWED_CIDR_RANGES = [
    "182.228.212.0/24",
    "211.192.222.0/24",
    
]

webdriver_instances = []
# 사이트 리스트 기본값 (임포트 순서에 맞춰 정렬)
from modules.site.kgmobile.html_list import KgMobileListAction        # 우선 순위 : 1
from modules.site.insmobile.html_list import InsMobileListAction      # 우선 순위 : 2
from modules.site.erel.html_list import ErelListAction                # 우선 순위 : 3
from modules.site.eyesmobile.html_list import EyesMobileListAction    # 우선 순위 : 4 #2-21일 확인 여기까지 
from modules.site.tplus.html_list import TPlusListAction              # 우선 순위 : 5
from modules.site.amobile.html_list import AMobileListAction          # 우선 순위 : 6
from modules.site.egmobile.html_list import EGMobileListAction        # 우선 순위 : 7
from modules.site.gogomobile.html_list import GogoMobileListAction    # 우선 순위 : 8
from modules.site.shake_mobile.html_list import ShakeMobileListAction # 우선 순위 : 9
from modules.site.mobing.html_list import MobingListAction            # 우선 순위 : 10
from modules.site.eyagi.html_list import EyagiListAction              # 우선 순위 : 10
from modules.site.marvelring.html_list import MarvelringListAction    # 우선 순위 : 12 
from modules.site.sugarmobile.html_list import SugarMobileListAction  # 우선 순위 : 13 3월1일 확인
from modules.site.s1.html_list import S1MobileListAction              # 우선 순위 : 14  
from modules.site.liivm.html_list import LiivmListAction              # 우선 순위 : 15
from modules.site.mona.html_list import MonaListAction                # 우선 순위 : 16
from modules.site.pindirect.html_list import PindirectListAction      # 우선 순위 : 17
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
from modules.site.kctvmobile.html_list import KctvMobileListAction # 3/5일 확인
# from modules.site.sptizmobile.html_list import SptizMobileListAction 스피츠모바일 서비스종료 251128


# 사이트 리스트 기본값 (임포트 순서에 맞춰 정렬)
site_list_default = [
    TPlusListAction(),         # 우선 순위 : 1
    KgMobileListAction(),      # 우선 순위 : 2
    InsMobileListAction(),     # 우선 순위 : 3
    ErelListAction(),          # 우선 순위 : 4
    EyesMobileListAction(),    # 우선 순위 : 5
    AMobileListAction(),       # 우선 순위 : 6
    EGMobileListAction(),      # 우선 순위 : 7
    GogoMobileListAction(),    # 우선 순위 : 8
    ShakeMobileListAction(),   # 우선 순위 : 9
    EyagiListAction(),         # 우선 순위 : 10
    MobingListAction(),        # 우선 순위 : 10
    MarvelringListAction(),    # 우선 순위 : 12
    SugarMobileListAction(),   # 우선 순위 : 13
    S1MobileListAction(),      # 우선 순위 : 14
    LiivmListAction(),         # 우선 순위 : 15
    MonaListAction(),          # 우선 순위 : 16
    PindirectListAction(),     # 우선 순위 : 17
    SmartelListAction(),       # 우선 순위 : 18
    WinnerstelListAction(),    # 우선 순위 : 19
    ChanceMobileListAction(),  # 우선 순위 : 20
    KtListAction(),            # 우선 순위 : 21
    SktListAction(),           # 우선 순위 : 22
    LguListAction(),           # 우선 순위 : 23
    HelloMobileListAction(),   # 우선 순위 : 24
    SevenMobileListAction(),   # 우선 순위 : 25
    UplusMobileListAction(),   # 우선 순위 : 26
    KtmMobileListAction(),     # 우선 순위 : 27
    SkylifeListAction(),       # 우선 순위 : 28
    KctvMobileListAction(),    # 우선 순위 : 29
    # SptizMobileListAction(),   # 우선 순위 : 30 스피츠모바일 서비스종료 251128
]




