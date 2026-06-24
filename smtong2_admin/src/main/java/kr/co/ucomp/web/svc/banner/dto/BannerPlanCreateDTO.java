package kr.co.ucomp.web.svc.banner.dto;



import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerPlanCreateDTO extends BaseSearchDto {
	private Integer		Id;
	private Integer		bannerId;
	private Integer		planId;
	
	
}
