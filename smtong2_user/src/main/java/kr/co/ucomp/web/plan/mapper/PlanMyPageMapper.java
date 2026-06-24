package kr.co.ucomp.web.plan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;

@Mapper
public interface PlanMyPageMapper {
	
	List<PlanEntity> getMyPlanList(SearchPlanDto param);
	
	PlanEntity getMyPlan(SearchPlanDto param);

}
