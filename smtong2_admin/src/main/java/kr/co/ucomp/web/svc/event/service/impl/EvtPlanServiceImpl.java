package kr.co.ucomp.web.svc.event.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.svc.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtPlanEntity;
import kr.co.ucomp.web.svc.event.mapper.EvtPlanMapper;
import kr.co.ucomp.web.svc.event.service.EvtPlanService;

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

	@Override
	public Integer create(EvtPlanEntity param) {
		return evtPlanMapper.create(param);
	}

	@Override
	public Integer update(EvtPlanEntity param) {
		return evtPlanMapper.update(param);
	}

	@Override
	public Integer delete(EvtPlanEntity param) {
		return evtPlanMapper.delete(param);
	}

	
	
	/**
	 * 이벤트 상세 내 이벤트와 연결된 요금제들
	 */
	@Override
	public List<EvtPlanEntity> evtPlanList(EvtPlanSearchDTO param) {
		return evtPlanMapper.evtPlanList(param);
	}
	
}
