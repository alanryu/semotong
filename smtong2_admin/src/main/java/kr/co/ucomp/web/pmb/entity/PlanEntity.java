package kr.co.ucomp.web.pmb.entity;



import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class PlanEntity  implements Serializable {

	private static final long serialVersionUID = -2701631513962518754L;
	
	private int id;
    private String uuid;
    private String planCode;
    private String planName;
    private String planNameSmt;
    private String mno;
    private Integer host;
    private String hostNm;
    private String planType;
    private Integer supDataVal;
    private Integer supQos;

    private Integer supCallVal;
    private Integer supSmsVal;
    private Integer normalPrice;
    private Integer salePrice;
    private Integer afterPrice;
    private String promotionPeriod;
    private Boolean combinationEnable;
    private String benefit;
    private String freebies;
    private String linkUrl;
    private String urlSmt;
    private String businessName;
    private String etc;
    private Boolean saleStatus;
    private Boolean useYn;
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
    
    private String eventBannerImagePcUrl;
    private String eventBannerImageMoUrl;    
    
    private String eventBannerImageUrl;
    private String eventBannerImageTarget;
    private LocalDateTime createDate;
    private Integer createId;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;
    private int planBenefitCnt;
    private int planFreebieCnt;
    private Integer planZzimCnt;
    private Integer planReqCnt;
    
    private String pointPlanYn;
    private String planNameSmtSub;
    private Integer dailyData;
    private Integer intQos;
    private String secondPrice;
    
    private Integer orderState;
    private Integer orderStateDttm;
    
    private String newNumReqUrl;
    private String numMoveReqUrl;
    
    private Integer recomOrder1; // 일반 알뜰폰 요금제 정렬 순서
    private Integer recomOrder2; // 추천 요금제 정렬 순서
    private Integer recomOrder3; // 가성비 요금제 정렬 순서
    private Integer recomOrder4; // 인기 요금제 정렬 순서
    
    private String noticeContent;
    
    private String isProtected;
    private String dataCreateSp;
    private String marketingType;
    
    
	private String pcUrl;
	private String moUrl;
	
	private String supCallBugaVal;
	
	private Integer joinCount; // 가입 건수
	private String planTag1;   //요금제 태그 1
	private String planTag2;   //요금제 태그 2
	private String planTag3;   //요금제 태그 3
	private String planTag4;   //요금제 태그 4
	private String planTag5;   //요금제 태그 5
	
	private String planTag1Name;   //요금제 태그 1
	private String planTag2Name;   //요금제 태그 2
	private String planTag3Name;   //요금제 태그 3
	private String planTag4Name;   //요금제 태그 4
	private String planTag5Name;   //요금제 태그 5	

	
    
    // 챗봇 요금제 리스트 전용
    private String chatbotPlanMeno;
    private Boolean chatbotPlanRecomYn;
    private Boolean chatbotPlanLowPriceYn;
    private Boolean chatbotPlanMaxBenefitYn;
    private Integer chatbotPlanOrderNo;
		
}
