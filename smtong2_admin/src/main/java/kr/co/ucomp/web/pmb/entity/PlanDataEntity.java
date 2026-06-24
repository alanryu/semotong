package kr.co.ucomp.web.pmb.entity;



import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class PlanDataEntity  implements Serializable {

	private static final long serialVersionUID = -2701631513962518754L;
	
	private int id; 
	private String uuid;
	private String telecom;
	private Integer companyId;
	@NotEmpty(message = "통신사 입력해주세요")	
	private String mno;
	private String planCode;	
	private String planType;
	private String planName;
	private Integer normalPrice;
	private Integer salePrice;
	private Integer afterPrice;
	private String promotionPeriod;
	private Integer data;
	private Integer qos;
	private String voiceCall;
	private String bugaCall;
	private String message;
	private String combination;
	private String benefit;
	private String freebies;
	private String url;
	private String businessName;
	private String etc;
	private String saleStatus;
	private String createDate;
	private String modifiedDate;
	private Integer dailyData;
	private Integer m12Price;
	private Integer m24Price;
	private Boolean contractOption;
	private Boolean hiddenYn;
	private Boolean specialCategory;
	private Boolean dataSharingYn;
	private Boolean microPaymentYn;
	private Integer dataTethering;
	private Integer agreementPeriod;
	private Boolean qosBlocked;
	private Integer dataDailyTethering;
	private Boolean isProtected;
	private String dataCreateSp;
	private String companyNm;
	private Integer planId;
	private String marketingType;
	
	private String pcUrl;
	private String moUrl;
	
	
}
