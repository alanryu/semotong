package kr.co.ucomp.web.pmb.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.pmb.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.pmb.entity.InternetPlanEntity;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;

@Service
public interface InternetPlanService {

	public List<InternetPlanEntity> list(InternetPlanSearchDTO param);
	
	public Long count(InternetPlanSearchDTO param);
	
	public InternetPlanEntity getDetail(@Param("id") int id);
	
	public long create(InternetPlanEntity param);
	
	public long update(InternetPlanEntity param);
	
	public long delete(InternetPlanEntity param);
	
	public List<InternetPlanMnoEntity> getInternetPlanMno(@Param("useYn") String useYn,@Param("isNewYn") String isNewYn,@Param("mnoId") String mnoId);

	public List<InternetPlanEntity> listWithOutLimit(InternetPlanSearchDTO param);
	
	public long updataAlamRcvNum(InternetPlanMnoEntity param);
	
}
