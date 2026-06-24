package kr.co.ucomp.web.svc.randing.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.randing.dto.InternetRdpMngSearchDto;
import kr.co.ucomp.web.svc.randing.entity.InternetRdpMngEntity;

public interface InternetRdpMngService {

	List<InternetRdpMngEntity> list(InternetRdpMngSearchDto param);
	
	Long count(InternetRdpMngSearchDto param);
	
	InternetRdpMngEntity getDetail(@Param("id") int id);
	
	long create(InternetRdpMngEntity param);
	
	long update(InternetRdpMngEntity param);
	
	long updateEx(InternetRdpMngEntity param);
	
	long delete(Integer id);
	
}
