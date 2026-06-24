package kr.co.ucomp.web.svc.event.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.svc.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtPlanEntity;

@Mapper
public interface EvtPlanMapper {
	
	List<EvtPlanEntity> evtList(EvtPlanSearchDTO param);
	
	Long evtCount(EvtPlanSearchDTO param);
	
	EvtPlanEntity evtById(EvtPlanSearchDTO param);
	
	Integer create(EvtPlanEntity param);
	
	Integer update(EvtPlanEntity param);
	
	Integer delete(EvtPlanEntity param);
	
	
	
	List<EvtPlanEntity> evtPlanList(EvtPlanSearchDTO param);
	
	
}
