package kr.co.ucomp.web.pmb.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.pmb.dto.PlanReqSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanReqMngEntity;

import java.util.List;

@Mapper
public interface PlanReqMngMapper {
    List<PlanReqMngEntity> getList(PlanReqSearchDto param);
    
    long getListCount(PlanReqSearchDto param);
    
    PlanReqMngEntity getDetail(Integer id);
    
    
    long update(PlanReqMngEntity param);
    
    long delete(Integer id);

	List<PlanReqMngEntity> getListWithOutLimit(PlanReqSearchDto param);
} 