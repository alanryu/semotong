package kr.co.ucomp.web.csm.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.dto.PolicyContentSearchDto;
import kr.co.ucomp.web.csm.entity.PolicyContentEntity;
import kr.co.ucomp.web.csm.mapper.PolicyContentMapper;
import kr.co.ucomp.web.csm.service.PolicyContentService;

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
	
	
	/**
	 * 리스트 count
	 * @param : PolicyContentEntity
	 * @return 결과(생성 갯수)
	 */
	@Override
	@Transactional
	public long getListCount (PolicyContentSearchDto param) {
		
		return mapper.getListCount(param);
		
	}
	
	
	/**
	 * 정책 내용 단건 조회
	 * @param : id(조회 id)
	 * @return PolicyContentEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public PolicyContentEntity getDetail(@Param("id") int param) {
		return mapper.getDetail(param);
	}
	
	/**
	 * 정책 내용 저장
	 * @param : PolicyContentEntity
	 * @return 결과(생성 갯수)
	 */
	@Override
	@Transactional
	public long create(PolicyContentEntity param) {
		
		return mapper.create(param);
		
	}
	
	/**
	 * 정책 내용 수정
	 * @param : PolicyContentEntity
	 * @return 결과(수정 갯수)
	 */
	@Override
	@Transactional
		public long update(PolicyContentEntity param) {
		
		return mapper.update(param);
		
	}
	
	/**
	 * 정책 내용 삭제
	 * @param : id(삭제 id)
	 * @return 결과(삭제 갯수)
	 */
	@Override
	@Transactional
	public long delete(@Param("id") int param) {
		
		return mapper.delete(param);
	}
	
}
