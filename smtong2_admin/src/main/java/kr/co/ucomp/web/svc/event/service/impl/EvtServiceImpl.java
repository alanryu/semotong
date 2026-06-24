package kr.co.ucomp.web.svc.event.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.svc.event.dto.EvtSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtEntity;
import kr.co.ucomp.web.svc.event.mapper.EvtMapper;
import kr.co.ucomp.web.svc.event.service.EvtService;

@Component
public class EvtServiceImpl implements EvtService{
	
	@Autowired
	private EvtMapper evtMapper;
	
	
	@Override
	public List<EvtEntity> evtList(EvtSearchDTO param) {
		List<EvtEntity> rtn = evtMapper.evtList(param);
		return rtn;
	}
	
	@Override
	public Long evtCount(EvtSearchDTO param) {
		Long rtn = evtMapper.evtCount(param);
		return rtn;
	}
	
	@Override
	public EvtEntity evtById(EvtSearchDTO param) {
		EvtEntity rtn = evtMapper.evtById(param);
		return rtn;
	}

	@Override
	public Integer create(EvtEntity param) {
		return evtMapper.create(param);
	}

	@Override
	public Integer update(EvtEntity param) {
		return evtMapper.update(param);
	}

	@Override
	public Integer delete(EvtEntity param) {
		return evtMapper.delete(param);
	}

	@Override
	public Integer updateUseYn(EvtEntity param) {
		return evtMapper.updateUseYn(param);
	}

	
	
}
