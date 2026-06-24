package kr.co.ucomp.web.svc.event.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtPlanEntity;

@Service
public interface EvtPlanService {

	public List<EvtPlanEntity> evtList(EvtPlanSearchDTO param);
	
	public Long evtCount(EvtPlanSearchDTO param);
	
	public EvtPlanEntity evtById(EvtPlanSearchDTO param);
	
	public Integer create(EvtPlanEntity param);
	
	public Integer update(EvtPlanEntity param);
	
	public Integer delete(EvtPlanEntity param);
	
	
	
	
	List<EvtPlanEntity> evtPlanList(EvtPlanSearchDTO param);
	
	
}
