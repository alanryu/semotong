package kr.co.ucomp.web.plan.dto;


import java.util.List;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class searchPlanZzimDto extends BaseSearchDto {

	private Long userMngId;    
	private int planListId;
	private List<String> searchplanIdList;

}
