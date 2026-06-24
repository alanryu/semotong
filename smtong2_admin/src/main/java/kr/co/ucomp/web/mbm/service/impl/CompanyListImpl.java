package kr.co.ucomp.web.mbm.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.mapper.CompanyListMapper;
import kr.co.ucomp.web.mbm.service.CompanyListService;

import java.util.List;

@Service("CompanyListService")
public class CompanyListImpl implements CompanyListService {
	@Autowired
	CompanyListMapper mapper;
	
	/**
	 * 정책 내용 목록 조회
	 * @param : PolicyContentSearchDto
	 * @return List<PolicyContentEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CompanyListEntity> getListCompanyList(CompanyListSearchDto param) {
		List<CompanyListEntity> list = mapper.getListCompanyList(param);
		
		return list;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public long getListCompanyListCount(CompanyListSearchDto param) {
		long count = mapper.getListCompanyListCount(param);
		return count;
	}
	
	
	/**
	 * 정책 내용 단건 조회
	 * @param : id(조회 id)
	 * @return PolicyContentEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public CompanyListEntity getCompanyList(@Param("id") int param) {
		return mapper.getCompanyList(param);
	}
	
	/**
	 * 정책 내용 저장
	 * @param : PolicyContentEntity
	 * @return 결과(생성 갯수)
	 */
	@Override
	@Transactional
	public long create(CompanyListEntity param) {
		
		return mapper.create(param);
		
	}
	
	/**
	 * 정책 내용 수정
	 * @param : PolicyContentEntity
	 * @return 결과(수정 갯수)
	 */
	@Override
	@Transactional
		public long update(CompanyListEntity param) {
		
		return mapper.update(param);
		
	}
	
	/**
	 * 정책 내용 삭제
	 * @param : id(삭제 id)
	 * @return 결과(삭제 갯수)
	 */
	@Override
	@Transactional
	public long delCompanyList(@Param("id") int param) {
		
		return mapper.delCompanyList(param);
	}


	@Override
	public List<CompanyListEntity> getListCompanyListWithoutLimit(CompanyListSearchDto param) {
		return mapper.getListCompanyListWithoutLimit(param);
	}
	
}
