package kr.co.ucomp.web.event.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.event.entity.EvtPlanEntity;

@Service
public interface EvtPlanService {

	public List<EvtPlanEntity> evtList(EvtPlanSearchDTO param);
	
	public Long evtCount(EvtPlanSearchDTO param);
	
	public EvtPlanEntity evtById(EvtPlanSearchDTO param);
	
}
