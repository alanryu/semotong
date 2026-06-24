package kr.co.ucomp.web.pmb.dto;

import java.util.List;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPlanDto extends BaseSearchDto {

	private String searchDispYn;
	private String searchSaleSp;
	private String searchNewSp;
	private String searchMno;
	private String searchNetrok;
	private String searchCompanyList;
	private String searchplanIds;
	private List<String> searchplanIdList;
	private int searchCompany;
	private Integer searchBasicPrice;
	private Integer searchBasicPriceTermFrom;
	private Integer searchBasicPriceTermTo;
	private Integer searchData;
	private Integer searchDataTermFrom;
	private Integer searchDataTermTo;
	private Integer searchCall;
	private Integer searchCallTermFrom;
	private Integer searchCallTermTo;
	private Integer searchSms;
	private Integer searchSmsTermFrom;
	private Integer searchSmsTermTo;
	private Integer searchQos;
	private Integer searchQosTermFrom;
	private Integer searchQosTermTo;
	private Integer searchPromoPeriod;
	private Integer searchPromoPeriodTermFrom;
	private Integer searchPromoPeriodTermTo;
	private Integer searchAfterPrice;
	private Integer searchAfterPriceTermFrom;
	private Integer searchAfterPriceTermTo;
	private String searchImageBadgeSimpleOpen;
	private String searchPointPlanYn;
	private String searchHiddenYn;

	private String searchOrderListSp;
	private String searchOrderType;

	private Integer searchChatbotGroupId;

	private String searchCondCol;

	private String searchSecondPrice;
	
	private String searchPlanTag;

}
