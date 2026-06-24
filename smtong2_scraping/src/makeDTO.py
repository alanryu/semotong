import os
import argparse
from pprint import pprint

# 프로젝트 루트를 src로 설정
# sys.path.append(os.path.abspath(os.path.dirname(__file__)))

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
from modules.site.sptizmobile.html_list import SptizMobileListAction

# 액션 이름과 클래스 매핑 (임포트 순서대로 정렬)
action_classes = {
    "KgMobileListAction": KgMobileListAction,
    "InsMobileListAction": InsMobileListAction,
    "ErelListAction": ErelListAction,
    "EyesMobileListAction": EyesMobileListAction,
    "TPlusListAction": TPlusListAction,
    "AMobileListAction": AMobileListAction,
    "EGMobileListAction": EGMobileListAction,
    "GogoMobileListAction": GogoMobileListAction,
    "ShakeMobileListAction": ShakeMobileListAction,
    "MobingListAction": MobingListAction,
    "EyagiListAction": EyagiListAction,
    "MarvelringListAction": MarvelringListAction,
    "SugarMobileListAction": SugarMobileListAction,
    "S1MobileListAction": S1MobileListAction,
    "LiivmListAction": LiivmListAction,
    "MonaListAction": MonaListAction,
    "PindirectListAction": PindirectListAction,
    "SmartelListAction": SmartelListAction,
    "WinnerstelListAction": WinnerstelListAction,
    "ChanceMobileListAction": ChanceMobileListAction,
    "KtListAction": KtListAction,
    "SktListAction": SktListAction,
    "LguListAction": LguListAction,
    "HelloMobileListAction": HelloMobileListAction,
    "SevenMobileListAction": SevenMobileListAction,
    "UplusMobileListAction": UplusMobileListAction,
    "KtmMobileListAction": KtmMobileListAction,
    "SkylifeListAction": SkylifeListAction,
    "KctvMobileListAction": KctvMobileListAction,
    "SptizMobileListAction": SptizMobileListAction,
}

def main():
    parser = argparse.ArgumentParser(description="Run the specified action.")
    parser.add_argument("action", choices=action_classes.keys(), help="The action class to execute.")
    args = parser.parse_args()

    action_class = action_classes.get(args.action)
    if not action_class:
        print(f"Invalid action: {args.action}")
        return

    action = action_class()
    page = 1  # 페이지는 고정 값

    try:
        result, is_end = action.root(page=page)
        print("=== Writing Results to DTO_result.txt ===")

        with open("DTO_result.txt", "w", encoding="utf-8") as file:
            if hasattr(result, 'planData') and hasattr(result, 'detailInfo'):
                file.write("=== PlanData ===\n")
                for dto in result.planData:
                    pprint(dto.__dict__, stream=file)

                file.write("\n=== DetailInfo ===\n")
                for detail in result.detailInfo:
                    pprint(detail.__dict__, stream=file)

            elif isinstance(result, list):
                file.write("=== PlanData ===\n")
                for dto in result:
                    pprint(dto.__dict__, stream=file)

            else:
                file.write("Unexpected result format.\n")

            file.write(f"\nIs end: {is_end}\n")

        print("Results successfully written to DTO_result.txt.")
    except Exception as e:
        print("An error occurred during the test.")
        print(e)

if __name__ == "__main__":
    main()
