package kr.co.ucomp.web.pmb.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.pmb.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.pmb.entity.InternetPlanEntity;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.pmb.mapper.InternetPlanNewMapper;
import kr.co.ucomp.web.pmb.service.InternetPlanNewService;

@Component
public class InternetServiceNewImpl implements InternetPlanNewService {
	
	@Autowired
	private InternetPlanNewMapper mapper;
	
	@Override
	public long updateMno(InternetPlanMnoEntity param) {
		return mapper.updateMno(param);
	}
		
	
	@Override
	public List<InternetPlanEntity> list(InternetPlanSearchDTO param) {
		List<InternetPlanEntity> rtn = mapper.list(param);
		return rtn;
	}
	

	@Override
	public long update(InternetPlanEntity param) {
		return mapper.update(param);
	}


	
	@Override
	public long updataAlamRcvNum(InternetPlanMnoEntity param) {
		return mapper.updataAlamRcvNum(param);
	}
	

}
