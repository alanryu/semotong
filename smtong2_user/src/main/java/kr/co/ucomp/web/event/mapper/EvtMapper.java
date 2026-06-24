package kr.co.ucomp.web.event.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.csm.faq.entity.FaqEntity;
import kr.co.ucomp.web.event.dto.EvtSearchDTO;
import kr.co.ucomp.web.event.entity.EvtEntity;

@Mapper
public interface EvtMapper {
	
	List<EvtEntity> evtList(EvtSearchDTO param);
	
	Long evtCount(EvtSearchDTO param);
	
	EvtEntity evtById(EvtSearchDTO param);

	long getTimeDealEventId();
	
}
