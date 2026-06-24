package kr.co.ucomp.web.svc.banner.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerPlanSearchDTO extends BaseSearchDto {
	
	private Integer 	searchId;
	private Integer		searchBannerId;
	private Integer		searchPlanId;
	
	
}
