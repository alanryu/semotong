package kr.co.ucomp.web.pmb.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanCateMngDto extends BaseSearchDto {
	private String searchCateGroupSp;
	private String searchCateCode;
}
