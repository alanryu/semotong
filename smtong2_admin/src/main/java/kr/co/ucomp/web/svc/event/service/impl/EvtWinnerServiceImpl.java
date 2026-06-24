package kr.co.ucomp.web.svc.event.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.svc.event.dto.EvtWinnerSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtWinnerEntity;
import kr.co.ucomp.web.svc.event.mapper.EvtWinnerMapper;
import kr.co.ucomp.web.svc.event.service.EvtWinnerService;

@Component
public class EvtWinnerServiceImpl implements EvtWinnerService{
	
	@Autowired
	private EvtWinnerMapper evtWinnerMapper;
	
	
	@Override
	public List<EvtWinnerEntity> evtList(EvtWinnerSearchDTO param) {
		return evtWinnerMapper.evtList(param);
	}
	
	@Override
	public Long evtCount(EvtWinnerSearchDTO param) {
		return evtWinnerMapper.evtCount(param);
	}
	
	@Override
	public EvtWinnerEntity evtById(EvtWinnerSearchDTO param) {
		EvtWinnerEntity rtn = evtWinnerMapper.evtById(param);
		return rtn;
	}

	@Override
	public Integer create(EvtWinnerEntity param) {
		return evtWinnerMapper.create(param);
	}

	@Override
	public Integer update(EvtWinnerEntity param) {
		return evtWinnerMapper.update(param);
	}

	@Override
	public Integer delete(EvtWinnerEntity param) {
		return evtWinnerMapper.delete(param);
	}

	
	
}
