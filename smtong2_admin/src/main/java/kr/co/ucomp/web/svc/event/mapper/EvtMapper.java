package kr.co.ucomp.web.svc.event.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.svc.event.dto.EvtSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtEntity;

@Mapper
public interface EvtMapper {
	
	List<EvtEntity> evtList(EvtSearchDTO param);
	
	Long evtCount(EvtSearchDTO param);
	
	EvtEntity evtById(EvtSearchDTO param);
	
	Integer create(EvtEntity param);
	
	Integer update(EvtEntity param);
	
	Integer delete(EvtEntity param);
	
	Integer updateUseYn(EvtEntity param);
	
}
