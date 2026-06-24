package kr.co.ucomp.web.point.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointHistoryDetailDTO extends BaseSearchDto {

	private int 			searchHistoryId;
	private int 			searchUserId;
	private int 			searchPointId;
	
	private int 			searchNpayId;
	private int 			searchCashId;
	
	
}
