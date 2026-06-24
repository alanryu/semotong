package kr.co.ucomp.web.plan.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanSalesSearchDto extends BaseSearchDto {

	private long searchComUserId;
	private String useYn;
	private String adminUserId;
	private long idx;
	
	private Integer salesMngId;
}
