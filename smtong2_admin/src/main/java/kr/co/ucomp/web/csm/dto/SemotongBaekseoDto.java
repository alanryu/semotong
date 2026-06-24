package kr.co.ucomp.web.csm.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemotongBaekseoDto extends BaseSearchDto {

	private long 				id;
	private String		searchKeywordType;
	private String		searchKeyword;
	
	private String		searchPeriodType;
	private String		searchStartDate;
	private String		searchEndDate;
	
	
	

}
