package kr.co.ucomp.web.svc.randing.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.randing.dto.InternetRdpMngSearchDto;
import kr.co.ucomp.web.svc.randing.entity.InternetRdpMngEntity;

@Mapper
public interface InternetRdpMngMapper {
	
	List<InternetRdpMngEntity> list(InternetRdpMngSearchDto param);
	
	Long count(InternetRdpMngSearchDto param);
	
	InternetRdpMngEntity getDetail(@Param("id") int id);
	
	long create(InternetRdpMngEntity param);
	
	long update(InternetRdpMngEntity param);
	
	long updateEx(InternetRdpMngEntity param);
	
	long delete(Integer id);
	
}
