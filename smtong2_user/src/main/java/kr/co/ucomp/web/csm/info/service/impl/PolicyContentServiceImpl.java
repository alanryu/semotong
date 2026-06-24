package kr.co.ucomp.web.csm.info.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.info.dto.PolicyContentSearchDto;
import kr.co.ucomp.web.csm.info.entity.PolicyContentEntity;
import kr.co.ucomp.web.csm.info.mapper.PolicyContentMapper;
import kr.co.ucomp.web.csm.info.service.PolicyContentService;

import java.util.List;

@Service("PolicyContentService")
public class PolicyContentServiceImpl implements PolicyContentService {
	@Autowired
	PolicyContentMapper mapper;
	
	/**
	 * 정책 내용 목록 조회
	 * @param : PolicyContentSearchDto
	 * @return List<PolicyContentEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PolicyContentEntity> getList(PolicyContentSearchDto param) {
		
		return mapper.getList(param);
	}
	


	@Override
	public Long getListCount(PolicyContentSearchDto param) {
		// TODO Auto-generated method stub
		return mapper.getListCount(param);
	}
	
	/**
	 * 정책 내용 단건 조회
	 * @param : id(조회 id)
	 * @return PolicyContentEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public PolicyContentEntity getPolicyContent(@Param("polSp") String polSp,@Param("polId") Integer polId) {
		return mapper.getPolicyContent(polSp,polId);
	}

	
	
}
