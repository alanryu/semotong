package kr.co.ucomp.web.plan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;

import java.util.List;

@Mapper
public interface PlanMapper {
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