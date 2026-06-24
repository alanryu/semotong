package kr.co.ucomp.web.internet.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternetPlanSearchDTO extends BaseSearchDto {
	
	private Integer 	searchId;
	private Integer 	searchInternetMno	;	//통신사 - 인터넷 통신사 코드
	
	private String		searchProdName		;
	private Integer		searchChannelCount	;
	
	private String		searchInternetSpeed	;
	
	private String		searchSiteSp	; //인터넷 유입 사이트 구분(01: 세모통, 02:오늘의통신) 
	
	

}
