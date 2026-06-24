package kr.co.ucomp.web.plan.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class PlanEntity {

    private int id;
    private String uuid;
    private String planCode;
    private String planName;
    private String planNameSmt;
    private String mno;
    private Integer host;
    private String hostNm;
    private String companyMngCode;
    private String planType;
    private Integer supDataVal;
    private Integer supQos;
    private Integer supCallVal;
    private Integer supSmsVal;
    private Integer normalPrice;
    private Integer salePrice;
    private Integer afterPrice;
    private String promotionPeriod;
    private String promotionPeriodVal;
    private Boolean combinationEnable;
    private String benefit;
    private String freebies;
    private String linkUrl;
    private String urlSmt;
    private String businessName;
    private String etc;
    private Boolean saleStatus;
    private Boolean newYn;
    private Boolean dispYn;
    private Boolean imageBadgeSpecial;
    private Boolean imageBadgeItComb;
    private Boolean imageBadgeAddData;
    private String imageBadgeAddDataNm;
    private Boolean imageBadgeSimpleOpen;
    private Boolean imageBadgeLowest;
    private String suppContent;
    private String noSuppContent;
    private String eventBannerImagePc;
    private String eventBannerImageMo;
    private String eventBannerImageUrl;
    private String eventBannerImageTarget;
    private String planLogoImg;
    private LocalDateTime createDate;
    private Integer createId;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;
    private int planBenefitCnt;
    private List<PlanBenefitMappingEntity> benefitList;
    private int planFreebieCnt;
    private List<PlanFreebieMappingEntity> freebieList;
    private Integer planZzimCnt;
    private Integer planLoginUserZzimCnt;
    private Integer planReqCnt;
    // 2차 개발 추가
    private Integer pointPlanYn;
    private Integer m12Price;
    private Integer m24Price;
    private String supCallBugaVal;
    private Integer intQos;
    private Integer dailyData;
    private String planNameSmtSub;
    private String secondPrice;

    private String orderState;
    private String orderStateNm;
    private String orderStateDttm;
    private String openCompDttm;

    private float reviewAvg;
    private float mvnoReviewAvg;

    private String newNumReqUrl;
    private String numMoveReqUrl;

    private String noticeContent; // 유의사항 추가 20250402

	private String pcUrl;
	private String moUrl;
	
	private String planTag1;
	private String planTag2;
	private String planTag3;
	private String planTag4;
	private String planTag5;
	
	
    // 챗봇 요금제 리스트 전용
    private String chatbotPlanMeno;
    private Boolean chatbotPlanRecomYn;
    private Boolean chatbotPlanLowPriceYn;
    private Boolean chatbotPlanMaxBenefitYn;
    private Integer chatbotPlanOrderNo;

    private Integer supDataTotal;
}
