package kr.co.ucomp.web.internet.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.internet.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.internet.entity.InternetPlanEntity;
import kr.co.ucomp.web.internet.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.internet.entity.InternetRdpMngEntity;

@Mapper
public interface InternetPlanMapper {
	
	List<InternetPlanEntity> listPlanName(InternetPlanSearchDTO param);
	
	List<InternetPlanEntity> list(InternetPlanSearchDTO param);
	
	Long count(InternetPlanSearchDTO param);
	
	InternetPlanEntity byId(InternetPlanSearchDTO param);
	
	
	InternetPlanMnoEntity getInternetMno(@Param("id") int param);
	
	InternetRdpMngEntity getRDPDetail(@Param("id") int id);
	
}
