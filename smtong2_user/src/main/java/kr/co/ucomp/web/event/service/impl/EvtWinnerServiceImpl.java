package kr.co.ucomp.web.event.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.event.dto.EvtWinnerSearchDTO;
import kr.co.ucomp.web.event.entity.EvtWinnerEntity;
import kr.co.ucomp.web.event.mapper.EvtWinnerMapper;
import kr.co.ucomp.web.event.service.EvtWinnerService;

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
}
