package kr.co.ucomp.web.csm.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemotongReviewDto extends BaseSearchDto {

	private long 		searchId				;
	private String 		searchReviewType		;		//ORDER, SEMOTONG
	private String 		searchKeyword			;
	private String 		searchPeriodType		;		//CREATE 고정
	private String		searchStartDate			;
	private String		searchEndDate			;
	private String 		searchDisplayYn			;		//
	
	

}
