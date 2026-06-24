package kr.co.ucomp.web.internet.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.internet.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.internet.entity.InternetPlanEntity;
import kr.co.ucomp.web.internet.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.internet.entity.InternetRdpMngEntity;

@Service
public interface InternetPlanService {

	
	public List<InternetPlanEntity> listPlanName(InternetPlanSearchDTO param);
	
	public List<InternetPlanEntity> list(InternetPlanSearchDTO param);
	
	public Long count(InternetPlanSearchDTO param);
	
	public InternetPlanEntity byId(InternetPlanSearchDTO param);
	
	public InternetPlanMnoEntity getInternetMno(@Param("id") int param);
	
	InternetRdpMngEntity getRDPDetail(@Param("id") int id);
	
}
