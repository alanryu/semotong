package kr.co.ucomp.web.csm.banner.dto;



import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class BannerSearchDto  extends BaseSearchDto {
	
	private Integer isDispStatusAll;
	private Integer isDispStatusBef;
	private Integer isDispStatusDsp;
	private Integer isDispStatusEnd;
	private String searchRegDtStdt;
	private String searchRegDtEddt;
	private String searchStartDt;
	private String searchEndDt;
	private String searchBannerType;
	private String searchUseYn;

	
}
