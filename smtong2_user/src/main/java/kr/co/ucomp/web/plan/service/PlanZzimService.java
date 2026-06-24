package kr.co.ucomp.web.plan.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.plan.dto.searchPlanZzimDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanZzimEntity;

public interface PlanZzimService {
	
	List<PlanEntity> getZzimListPlan(searchPlanZzimDto param);
	
	List<PlanEntity> getZzimNoDataListPlan(searchPlanZzimDto param);
	
	long getZzimNoDataListPlanCount(searchPlanZzimDto param);
	
	

    List<PlanZzimEntity> getlist(searchPlanZzimDto param);
    
    long getCount(searchPlanZzimDto param);

    long create(PlanZzimEntity param);

    long delete(PlanZzimEntity param);
    
    
    long deleteAll(PlanZzimEntity param);

}
