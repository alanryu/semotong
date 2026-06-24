package kr.co.ucomp.web.pmb.service;

import java.util.List;

import kr.co.ucomp.web.pmb.dto.PlanReqSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanReqMngEntity;

public interface PlanReqMngService {
    List<PlanReqMngEntity> getList(PlanReqSearchDto param);
    
    long getListCount(PlanReqSearchDto param);
    
    PlanReqMngEntity getDetail(Integer id);
    
    long update(PlanReqMngEntity param);
    
    long delete(Integer id);

	List<PlanReqMngEntity> getListWithOutLimit(PlanReqSearchDto param);
} 