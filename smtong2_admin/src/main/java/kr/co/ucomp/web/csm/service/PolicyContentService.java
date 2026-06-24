package kr.co.ucomp.web.csm.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.PolicyContentSearchDto;
import kr.co.ucomp.web.csm.entity.PolicyContentEntity;

import java.util.List;


public interface PolicyContentService {
	
	List<PolicyContentEntity> getList(PolicyContentSearchDto param);
	
	long getListCount(PolicyContentSearchDto param);
	
	PolicyContentEntity getDetail(@Param("id") int param);
	
	long create(PolicyContentEntity param);
	
	long update(PolicyContentEntity param);
	
	long delete(@Param("id") int param);
	
	
	
	
}
