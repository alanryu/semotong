package kr.co.ucomp.web.pmb.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.pmb.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.pmb.entity.InternetPlanEntity;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;

@Service
public interface InternetPlanNewService {

	public List<InternetPlanEntity> list(InternetPlanSearchDTO param);
	
	public long update(InternetPlanEntity param);
	
	public long updataAlamRcvNum(InternetPlanMnoEntity param);
	
	long updateMno(InternetPlanMnoEntity param);
	
}
