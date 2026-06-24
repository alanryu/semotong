package kr.co.ucomp.web.event.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.event.dto.EvtWinnerSearchDTO;
import kr.co.ucomp.web.event.entity.EvtWinnerEntity;

@Mapper
public interface EvtWinnerMapper {
	
	List<EvtWinnerEntity> evtList(EvtWinnerSearchDTO param);
	
	Long evtCount(EvtWinnerSearchDTO param);
	
	EvtWinnerEntity evtById(EvtWinnerSearchDTO param);
	
}
