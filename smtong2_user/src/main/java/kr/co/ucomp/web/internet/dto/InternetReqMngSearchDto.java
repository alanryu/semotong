package kr.co.ucomp.web.internet.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class InternetReqMngSearchDto extends BaseSearchDto {


    private long userId;
    
	
	private Integer 	inputPlanId		;
	private Integer 	inputUserId		;
	private String		inputUserName	;
	private String		inputUserPhoneNumber	;
	
}
