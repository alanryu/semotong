package kr.co.ucomp.web.event.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.event.dto.EvtWinnerSearchDTO;
import kr.co.ucomp.web.event.entity.EvtWinnerEntity;

@Service
public interface EvtWinnerService {

	public List<EvtWinnerEntity> evtList(EvtWinnerSearchDTO param);
	
	public Long evtCount(EvtWinnerSearchDTO param);
	
	public EvtWinnerEntity evtById(EvtWinnerSearchDTO param);
	
}
