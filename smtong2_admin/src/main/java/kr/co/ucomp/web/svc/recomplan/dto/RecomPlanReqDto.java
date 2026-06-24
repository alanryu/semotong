package kr.co.ucomp.web.svc.recomplan.dto;


import java.util.List;
import kr.co.ucomp.common.global.base.BaseSearchDto;
import kr.co.ucomp.web.svc.recomplan.entity.RecomPlanEntity;
import kr.co.ucomp.web.svc.recomplan.entity.RecomPlanPlanListEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecomPlanReqDto extends BaseSearchDto {

	private RecomPlanEntity record			;
	private List<RecomPlanPlanListEntity> planList;
	
}
