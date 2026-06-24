from enum import Enum

class SiteTargetListType(Enum):
    AMOBILE_LIST='에이모바일'
    INS_MOBILE_LIST ='인스모바일'
    KCTV_LIST ='KCTV'
    KG_MOBILE_LIST ='KG모바일'
    KT_LIST='KT'
    KTMMOBILE_LIST ='KTM모바일'
    SKYLIFE_LIST ='KT스카이라이프'
    LGU_LIST = 'LGU'
    MARVELRING_LIST = '마블링'
    MOBING_LIST = '모빙'
    SK7MOBILE_LIST = '세븐모바일'
    SKT_LIST='SKT'
    SMARTEL_LIST='스마텔'
    SNOWMAN_LIST ='스노우맨'
    SUGAR_MOBILE_LIST='슈가모바일'
    TPLUS_LIST ='티플러스'
    UPLUS_MOBILE_LIST ='U+유모바일'
    YYMOBILE_LIST ='여유모바일'
    GOGOMOBILE_LIST = '고고모바일'
    LIIVM_LIST ='리브엠모바일'
    MONA_LIST='모나'
    SHAKE_MOBILE_LIST ='쉐이크모바일'
    EYES_MOBILE_LIST = '아이즈모바일'
    EREL_LIST = '에르엘'
    S1_LIST ='에스원안심모바일'
    WINNERSTEL_LIST ='위너스텔'
    EYAGI_MOBILE_LIST = '이야기모바일'
    EGMOBILE_LIST = '이지모바일'
    CHANCE_MOBILE_LIST = '찬스모바일'
    FREET_LIST ='프리티'
    PINDIRECT_LIST ='핀다이렉트'
    HELLO_MOBILE_LIST='헬로모바일'
    SPTIZ_MOBILE_LIST = '스피츠모바일'

class SiteTargetListIDType(Enum):
    AMOBILE_LIST = 1
    INS_MOBILE_LIST = 2
    KCTV_LIST = 3
    KG_MOBILE_LIST = 4
    KT_LIST = 5
    KTMMOBILE_LIST = 6
    SKYLIFE_LIST = 7
    LGU_LIST = 8
    MARVELRING_LIST = 9
    MOBING_LIST = 10
    SK7MOBILE_LIST = 11
    SKT_LIST = 12
    SMARTEL_LIST = 13
    SNOWMAN_LIST = 14
    SUGAR_MOBILE_LIST = 15
    TPLUS_LIST = 16
    UPLUS_MOBILE_LIST = 17
    YYMOBILE_LIST = 18
    GOGOMOBILE_LIST = 19
    LIIVM_LIST = 20
    MONA_LIST = 21
    SHAKE_MOBILE_LIST = 22
    EYES_MOBILE_LIST = 23
    EREL_LIST = 24
    S1_LIST = 25
    WINNERSTEL_LIST = 26
    EYAGI_MOBILE_LIST = 27
    EGMOBILE_LIST = 28
    CHANCE_MOBILE_LIST = 29
    FREET_LIST = 30
    PINDIRECT_LIST = 31
    HELLO_MOBILE_LIST = 32
    SPTIZ_MOBILE_LIST = 33

def parse_str_to_site_target_detail_type(enum_type, value):
    for enum in enum_type:
        if enum.value == value:
            return enum
    return None
  