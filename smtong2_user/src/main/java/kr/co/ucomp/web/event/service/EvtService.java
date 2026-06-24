package kr.co.ucomp.web.event.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.event.dto.EvtSearchDTO;
import kr.co.ucomp.web.event.entity.EvtEntity;

@Service
public interface EvtService {

	public List<EvtEntity> evtList(EvtSearchDTO param);
	
	public Long evtCount(EvtSearchDTO param);
	
	public EvtEntity evtById(EvtSearchDTO param);

	public long getTimeDealEventId();
	
}
