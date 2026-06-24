package kr.co.ucomp.web.svc.event.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EvtSearchDTO extends BaseSearchDto {
	
	private Integer 	searchId;
	
	
	private String		searchKeywordType;
	private String		searchKeyword;
	
	private String		searchPeriodType;
	private String		searchStartDate;
	private String		searchEndDate;
	
	private String		searchCategory;
	private String		searchUseYn;
		
		
	
	
}
