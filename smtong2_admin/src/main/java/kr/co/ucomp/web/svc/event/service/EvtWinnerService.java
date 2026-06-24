package kr.co.ucomp.web.svc.event.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.event.dto.EvtWinnerSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtWinnerEntity;

@Service
public interface EvtWinnerService {

	public List<EvtWinnerEntity> evtList(EvtWinnerSearchDTO param);
	
	public Long evtCount(EvtWinnerSearchDTO param);
	
	public EvtWinnerEntity evtById(EvtWinnerSearchDTO param);
	
	public Integer create(EvtWinnerEntity param);
	
	public Integer update(EvtWinnerEntity param);
	
	public Integer delete(EvtWinnerEntity param);
	
	
}
