package kr.co.ucomp.web.internet.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.internet.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.internet.entity.InternetPlanEntity;
import kr.co.ucomp.web.internet.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.internet.mapper.InternetPlanNewMapper;
import kr.co.ucomp.web.internet.service.InternetPlanNewService;

@Component
public class InternetPlanServiceNewImpl implements InternetPlanNewService{
	
	@Autowired
	private InternetPlanNewMapper internetPlanMapper;
	
	
	@Override
	public List<InternetPlanEntity> list(InternetPlanSearchDTO param) {
		List<InternetPlanEntity> rtn = internetPlanMapper.list(param);
		return rtn;
	}
	
	@Override
	public Long count(InternetPlanSearchDTO param) {
		Long rtn = internetPlanMapper.count(param);
		return rtn;
	}
	
	@Override
	public InternetPlanEntity getDetail(InternetPlanSearchDTO param) {
		InternetPlanEntity rtn = internetPlanMapper.getDetail(param);
		return rtn;
	}
	
	
	@Override
	public InternetPlanMnoEntity getInternetMno (@Param("id") int param) {
		InternetPlanMnoEntity rtn = internetPlanMapper.getInternetMno(param);
		return rtn;
	}

	
}
