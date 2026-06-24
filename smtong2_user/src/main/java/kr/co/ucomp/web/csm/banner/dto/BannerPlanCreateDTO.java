package kr.co.ucomp.web.csm.banner.dto;



import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BannerPlanCreateDTO extends BaseSearchDto {
	private Integer		Id;
	private Integer		bannerId;
	private Integer		planId;
	
	
}
