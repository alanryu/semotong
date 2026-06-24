package kr.co.ucomp.web.event.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EvtPlanSearchDTO extends BaseSearchDto {
	
	private Integer 	searchId;
	private Integer		searchEventId;
	private Integer		searchPlanId;
	
	
}
