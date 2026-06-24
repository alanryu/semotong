package kr.co.ucomp.web.plan.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PlanReqMngDto extends BaseSearchDto {

    private Long id;
    //private String reqPhoneNum;
    private Long searchId;
    
    private String reqNm;
    private String reqPhonNum;
    
	
}
