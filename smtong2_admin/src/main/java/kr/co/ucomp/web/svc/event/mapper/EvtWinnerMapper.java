package kr.co.ucomp.web.svc.event.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.svc.event.dto.EvtWinnerSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtWinnerEntity;

@Mapper
public interface EvtWinnerMapper {
	
	List<EvtWinnerEntity> evtList(EvtWinnerSearchDTO param);
	
	Long evtCount(EvtWinnerSearchDTO param);
	
	EvtWinnerEntity evtById(EvtWinnerSearchDTO param);
	
	Integer create(EvtWinnerEntity param);
	
	Integer update(EvtWinnerEntity param);
	
	Integer delete(EvtWinnerEntity param);
	
}
