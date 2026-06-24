package kr.co.ucomp.web.pmb.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.pmb.entity.InternetPlanEntity;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;

@Mapper
public interface InternetPlanNewMapper {

	long updateMno(InternetPlanMnoEntity param);
	
	List<InternetPlanEntity> list(InternetPlanSearchDTO param);
	
	long update(InternetPlanEntity param);
	
	long updataAlamRcvNum(InternetPlanMnoEntity param);
	
	
}
