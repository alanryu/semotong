package kr.co.ucomp.web.event.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.event.entity.EvtEntity;
import kr.co.ucomp.web.event.entity.EvtPlanEntity;
import kr.co.ucomp.web.event.mapper.EvtPlanMapper;
import kr.co.ucomp.web.event.service.EvtPlanService;

@Component
public class EvtPlanServiceImpl implements EvtPlanService{
	
	@Autowired
	private EvtPlanMapper evtPlanMapper;

	@Override
	public List<EvtPlanEntity> evtList(EvtPlanSearchDTO param) {
		return evtPlanMapper.evtList(param);
	}

	@Override
	public Long evtCount(EvtPlanSearchDTO param) {
		return evtPlanMapper.evtCount(param);
	}

	@Override
	public EvtPlanEntity evtById(EvtPlanSearchDTO param) {
		return  evtPlanMapper.evtById(param);
	}
	
}
