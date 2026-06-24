package kr.co.ucomp.web.pmb.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanDataEntity;
import kr.co.ucomp.web.pmb.entity.PlanEntity;

public interface PlanService {
    List<PlanEntity> getList(SearchPlanDto param);
    
    long getListCount(SearchPlanDto param);
    
    PlanEntity getDetail(int id);
    
    PlanEntity getDetailByUUid(@Param("uuid") String searchUUid);
    
    long update(PlanUpdateDto param);
    
    
    long updataPlanCode(PlanUpdateDto param);
    
    long getListCountByPlanCode(PlanUpdateDto param);
    
    
    long delete(int id);
    
    
    // excel 매핑 데이터 조회
    List<PlanEntity> getMapListExcel(SearchPlanDto param);
    
    Map<String,Object> planMapUpload(MultipartFile file) throws IOException;
    
    List<PlanEntity> getAllListByPlanIds(SearchPlanDto param);

	List<PlanEntity> getListWithoutLimit(SearchPlanDto param);

	List<PlanEntity> getDataQosGroupBy(SearchPlanDto dto);
	
	List<PlanEntity> getOrderList(SearchPlanDto dto);
	
	
	Map<String,Object> planTagUpload(MultipartFile file) throws IOException;
	
	
	Map<String,Object> planMapUploadOrder(String searchOrderListSp,MultipartFile file) throws IOException;
	
	
	// planData 테이블 관리
	PlanDataEntity getDetailOrg(String searchUUid);
	long createOrg(PlanDataEntity param);
	long updateOrg(PlanDataEntity param);
	
	List<PlanEntity> getChatbotPlanList(SearchPlanDto param);
	List<String> selectPlanSearchCond(SearchPlanDto searchPlanDto);
	
	long updataPopulerOrderInit();
	
	long updataPopulerOrder(PlanUpdateDto param);
	
	long updataPlanTag(PlanUpdateDto param);
	
} 