package kr.co.ucomp.web.csm.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolicyContentSearchDto extends BaseSearchDto {
	private String searchStatus;
	private String searchRegStartDt;
	private String searchRegEndDt;
	private String searchPolSp;

}
