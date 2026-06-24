package kr.co.ucomp.web.pmb.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.pmb.entity.InternetPlanEntity;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;

@Mapper
public interface InternetPlanMapper {
	
	List<InternetPlanEntity> list(InternetPlanSearchDTO param);
	
	Long count(InternetPlanSearchDTO param);
	
	InternetPlanEntity getDetail(@Param("id") int id);
	
	long create(InternetPlanEntity param);
	
	long update(InternetPlanEntity param);
	
	long delete(InternetPlanEntity param);
	
	List<InternetPlanMnoEntity> getInternetPlanMno(@Param("useYn") String useYn,@Param("isNewYn") String isNewYn,@Param("mnoId") String mnoId);

	List<InternetPlanEntity> listWithOutLimit(InternetPlanSearchDTO param);
	
	long updataAlamRcvNum(InternetPlanMnoEntity param);
}
