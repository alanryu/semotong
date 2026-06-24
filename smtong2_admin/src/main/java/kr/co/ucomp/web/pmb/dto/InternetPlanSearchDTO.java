package kr.co.ucomp.web.pmb.dto;



import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternetPlanSearchDTO extends BaseSearchDto {
	
	private String		searchUseYn			;	//상태 - 사용여부 String 1byte
	private String  	searchInternetMno	;	//통신사 - 인터넷 통신사 코드
	private String		searchInternetSpeed	;	//인터넷 - 인터넷종류
	private Integer 	searchId;
	
	private String		searchSiteSp			;	//인터넷 유입 사이트 구분(01: 세모통, 02:오늘의통신) 
	
}
