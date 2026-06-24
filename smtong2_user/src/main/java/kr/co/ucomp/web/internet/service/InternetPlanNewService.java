package kr.co.ucomp.web.internet.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.internet.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.internet.entity.InternetPlanEntity;
import kr.co.ucomp.web.internet.entity.InternetPlanMnoEntity;

@Service
public interface InternetPlanNewService {

	
	public List<InternetPlanEntity> list(InternetPlanSearchDTO param);
	
	public Long count(InternetPlanSearchDTO param);
	
	public InternetPlanEntity getDetail(InternetPlanSearchDTO param);
	
	public InternetPlanMnoEntity getInternetMno(@Param("id") int param);
	
	
}
