package kr.co.ucomp.web.order.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanOrderSearchDto extends BaseSearchDto {
	private String companyId;
	private Integer orderSeq;
	private String orderId;
	private Integer orderUserId;	
	private String searchOrderState;
	private String searchPointPlanYn;
	private String searchCompanyList;
	
	private String searchMno;
	private String searchRecomSp;
	private String searchEntrType;
	
	private Integer searchBizPlanMngId;
	
	private String searchModelDivList;		//요금제 통신규격. 가능한 값: - 01: 2G - 02: 3G - 03: LTE - 04: 5G
}
