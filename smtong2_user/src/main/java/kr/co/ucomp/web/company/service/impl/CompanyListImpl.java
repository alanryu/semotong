package kr.co.ucomp.web.company.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.company.dto.CompanyListSearchDto;
import kr.co.ucomp.web.company.entity.CompanyListEntity;
import kr.co.ucomp.web.company.mapper.CompanyListMapper;
import kr.co.ucomp.web.company.service.CompanyListService;

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
		List<CompanyListEntity> list = null;
		long count = mapper.getListCompanyListCount(param);
		if(count > 0) list = mapper.getListCompanyList(param);
		
		return list;
	}
	
	/**
	 * 정책 내용 단건 조회
	 * @param : id(조회 id)
	 * @return PolicyContentEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public CompanyListEntity getCompany(@Param("id") int param) {
		return mapper.getCompany(param);
	}
	
	
	/**
	 * 특정 사용자의 통신사별 후기 count 
	 * @param : PolicyContentSearchDto
	 * @return List<PolicyContentEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CompanyListEntity> getReviewCompanyList(@Param("userId") long param) {
		return mapper.getReviewCompanyList(param);
	}
	
}
