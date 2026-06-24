package kr.co.ucomp.web.pmb.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.pmb.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.pmb.entity.InternetPlanEntity;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.pmb.mapper.InternetPlanMapper;
import kr.co.ucomp.web.pmb.service.InternetPlanService;

@Component
public class InternetServiceImpl implements InternetPlanService{
	
	@Autowired
	private InternetPlanMapper internetPlanMapper;
	
	
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
	public InternetPlanEntity getDetail(@Param("id") int id) {
		InternetPlanEntity rtn = internetPlanMapper.getDetail(id);
		return rtn;
	}

	@Override
	public long create(InternetPlanEntity param) {
		return internetPlanMapper.create(param);
	}

	@Override
	public long update(InternetPlanEntity param) {
		return internetPlanMapper.update(param);
	}

	@Override
	public long delete(InternetPlanEntity param) {
		return internetPlanMapper.delete(param);
	}
	
	@Override
	public List<InternetPlanMnoEntity> getInternetPlanMno(String useYn,@Param("isNewYn") String isNewYn,@Param("mnoId") String mnoId) {
		List<InternetPlanMnoEntity> rtn = internetPlanMapper.getInternetPlanMno(useYn,isNewYn,mnoId);
		return rtn;
	}

	@Override
	public List<InternetPlanEntity> listWithOutLimit(InternetPlanSearchDTO param) {
		return internetPlanMapper.listWithOutLimit(param);
	}	
	
	
	@Override
	public long updataAlamRcvNum(InternetPlanMnoEntity param) {
		return internetPlanMapper.updataAlamRcvNum(param);
	}
	
}
