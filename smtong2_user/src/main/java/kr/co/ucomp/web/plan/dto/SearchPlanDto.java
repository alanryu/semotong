package kr.co.ucomp.web.plan.dto;

import java.util.List;
import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchPlanDto  extends BaseSearchDto {
	private String searchType; // 1: 기본 ,2 : 최근본 요금제, 3 : 추천요금제 71G
	private Integer searchDispYn;
	private String searchSaleSp;
	private String searchMno;
	private String searchNetrok;
	private String searchCompanyList;
	private String searchplanIds;
	private List<String> searchplanIdList;
	private int searchCompany;
	private String searchBasicPrice;
	private String searchBasicPriceTermFrom;
	private String searchBasicPriceTermTo;
	private String searchData;
	private String searchDataTermFrom;
	private String searchDataTermTo;
	private String searchCall;
	private String searchCallTermFrom;
	private String searchCallTermTo;
	private String searchSms;
	private String searchSmsTermFrom;
	private String searchSmsTermTo;
	private String searchQos;
	private String searchQosTermFrom;
	private String searchQosTermTo;
	private String searchPromoPeriod;
	private String searchPromoPeriodTermFrom;
	private String searchPromoPeriodTermTo;
	private String searchAfterPrice;
	private Integer searchAfterPriceTermFrom;
	private Integer searchAfterPriceTermTo;
	private String searchSalePrice;
	private Integer searchSalePriceTermFrom;
	private Integer searchSalePriceTermTo;
	
	private Integer searchBannerId;
	private Integer searchEventId;
	
	private Integer searchCombination;
	private Integer searchUserId;
	
	private String searchCookieIds;
	
	private String searchSection;
	
	private long searchSalesId;
	
	private String searchOrderState;
	
	private Integer searchChatbotGroupId;
	
	private Integer recomMngId; // 추천요금제 관리 아이디
	
	private String searchPlanTag;

}
