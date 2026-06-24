package kr.co.ucomp.web.plan.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.plan.dto.PlanReqMngDto;
import kr.co.ucomp.web.plan.entity.PlanReqMngEntity;

import java.util.List;

@Mapper
public interface PlanReqMngMapper {
	
    List<PlanReqMngEntity> getList(PlanReqMngDto param);
    
    long getListCount(PlanReqMngDto param);
    
    PlanReqMngEntity getDetail(Integer id);
    
    long create(PlanReqMngEntity param);
    
    long update(PlanReqMngEntity param);
    
    long delete(Integer id);

	long getListCountDistinct(PlanReqMngDto searchRequest);
} 