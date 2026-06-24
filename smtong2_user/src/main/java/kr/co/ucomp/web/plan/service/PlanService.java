package kr.co.ucomp.web.plan.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;

public interface PlanService {
    List<PlanEntity> getList(SearchPlanDto param);
    
    long getListCount(SearchPlanDto param);
    
    PlanEntity getDetail(int id);
    
    List<PlanEntity> getAllListByPlanIds(SearchPlanDto param);

	long getBannerPlanListCount(SearchPlanDto param);

	List<PlanEntity> getBannerPlanList(SearchPlanDto param);
	
	List<PlanEntity> getChatbotPlanList(SearchPlanDto param);
	
	long getRecomPlanListCount(SearchPlanDto param);
	
	List<PlanEntity> getRecomPlanList(SearchPlanDto param);
	
	List<PlanEntity> getPopulerPlanList();
} 