package kr.co.ucomp.web.plan.service;

import java.util.List;

import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;

public interface PlanMyPageService {
	
	List<PlanEntity> getMyPlanList(SearchPlanDto param);
	
	PlanEntity getMyPlan(SearchPlanDto param);

}
