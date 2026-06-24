package kr.co.ucomp.web.plan.service;

import java.util.List;

import kr.co.ucomp.web.plan.dto.PlanReqMngDto;
import kr.co.ucomp.web.plan.entity.PlanReqMngEntity;

public interface PlanReqMngService {
    List<PlanReqMngEntity> getList(PlanReqMngDto param);
    
    long getListCount(PlanReqMngDto param);
    
    PlanReqMngEntity getDetail(Integer id);
    
    long create(PlanReqMngEntity param);
    
    long update(PlanReqMngEntity param);
    
    long delete(Integer id);

	long getListCountDistinct(PlanReqMngDto searchRequest);
} 