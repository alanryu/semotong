package kr.co.ucomp.web.svc.event.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.event.dto.EvtSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtEntity;

@Service
public interface EvtService {

	public List<EvtEntity> evtList(EvtSearchDTO param);
	
	public Long evtCount(EvtSearchDTO param);
	
	public EvtEntity evtById(EvtSearchDTO param);
	
	public Integer create(EvtEntity param);
	
	public Integer update(EvtEntity param);
	
	public Integer delete(EvtEntity param);
	
	public Integer updateUseYn(EvtEntity param);
	
	
}
