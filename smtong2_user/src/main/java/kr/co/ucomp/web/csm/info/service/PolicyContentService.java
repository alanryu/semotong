package kr.co.ucomp.web.csm.info.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.info.dto.PolicyContentSearchDto;
import kr.co.ucomp.web.csm.info.entity.PolicyContentEntity;

import java.util.List;


public interface PolicyContentService {
	
	  List<PolicyContentEntity> getList(PolicyContentSearchDto param);

	  Long getListCount(PolicyContentSearchDto param);

	  PolicyContentEntity getPolicyContent(@Param("polSp") String polSp,@Param("polId") Integer polId);
	
}
