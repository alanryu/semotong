package kr.co.ucomp.web.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanOrderSearchDto {
	private String companyId;
	private Integer orderSeq;
	private String orderId;
	private Integer orderUserId;	
	private String searchStDttm;
	private String searchEdDttm;
	private String searchType;
	private String searchOrderState;
}
