package kr.co.ucomp.web.csm.review.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class SemotongReviewEntity {
    private Integer 	id				;
    private String 		reviewType		;
    private Integer 	companyId		;
    private Integer 	orderId			;
    private String 		planId			;
    private String 		content			;
    private int 		score			;
    private String 		displayYn		;
    private String 		planName		;
    private Integer 	manageId		;
    private String 		manageMemo		;    
    private Date 		createDate		;
    private Integer 	createId		;
    private Date 		modifiedDate	;
    private Integer 	modifiedId		;    
    private String 		username		;
    private String 		kakaoUserId		;
    
    // 간편개통 이후 리뷰 용
    private String 		orderSeq;
    private String 		telecomCd;
    private String 		openCompDttm;
    private String 		companyNm;
    private String 		planNameSmt;
    private String 		planNameSmtSub;
    private String 		planLogoImg;
    private String 		planType;
    private Integer 	supDataVal;
    private Integer		supQos;
    private Integer 	supCallVal;
    private Integer 	supSmsVal;
    private Integer 	dailyData;
    private Integer 	normalPrice;
    private Integer 	salePrice;
    private Integer 	afterPrice;
    private String 		promotionPeriod;
    private String 		promotionPeriodVal;
    private Boolean 	imageBadgeSpecial;
    private Boolean 	imageBadgeItComb;
    private Boolean 	imageBadgeAddData;
    private String 		imageBadgeAddDataNm;
    private Boolean 	imageBadgeSimpleOpen;	
    private Integer		reviewId;
    
    //집계용
    private Integer minScore;
    private Integer maxScore;
    private float avgScore;
    private Integer cnt;
    
    //포인트 사용 확인용
    private String 		pointHistoryAvailAmount;
}
