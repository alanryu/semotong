package kr.co.ucomp.web.event.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.event.entity.EvtPlanEntity;

@Mapper
public interface EvtPlanMapper {
	
	List<EvtPlanEntity> evtList(EvtPlanSearchDTO param);
	
	Long evtCount(EvtPlanSearchDTO param);
	
	EvtPlanEntity evtById(EvtPlanSearchDTO param);
	
}
