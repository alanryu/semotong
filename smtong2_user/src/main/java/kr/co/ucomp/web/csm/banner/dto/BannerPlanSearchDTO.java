package kr.co.ucomp.web.csm.banner.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BannerPlanSearchDTO extends BaseSearchDto {
	
	private Integer 	searchId;
	private Integer		searchBannerId;
	private Integer		searchPlanId;
	
	
}
