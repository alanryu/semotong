package kr.co.ucomp.web.pmb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanDataEntity;
import kr.co.ucomp.web.pmb.entity.PlanEntity;

import java.util.List;

@Mapper
public interface PlanMapper {
    List<PlanEntity> getList(SearchPlanDto param);
    
    long getListCount(SearchPlanDto param);
    
    PlanEntity getDetail(int id);
    
    PlanEntity getDetailByUUid(@Param("uuid") String searchUUid);
    
    long update(PlanUpdateDto param);
    
    long delete(int id);
    
    
    List<PlanEntity> getMapListExcel(SearchPlanDto param);    
    
    
    long getListCountByPlanCode(PlanUpdateDto param);
    
    
    List<PlanEntity> getAllListByPlanIds(SearchPlanDto param);

	List<PlanEntity> getListWithoutLimit(SearchPlanDto param);

	List<PlanEntity> getDataQosGroupBy(SearchPlanDto dto);
	
	
	List<PlanEntity> getOrderList(SearchPlanDto dto);
	
	
	long updataByUuid(PlanUpdateDto param);
	
	long planMapUploadOrderInit(PlanUpdateDto param);
	
	// planData 테이블 관리
	PlanDataEntity getDetailOrg(@Param("uuid") String searchUUid);
	long createOrg(PlanDataEntity param);
	long updateOrg(PlanDataEntity param);
	
	
	List<PlanEntity> getChatbotPlanList(SearchPlanDto param);
	List<String> selectPlanSearchCond(SearchPlanDto searchPlanDto);
	 
	
	long updataPopulerOrderInit();
	
	long updataPopulerOrder(PlanUpdateDto param);
	
	long updataPlanTag(PlanUpdateDto param);
	
	
	
} 