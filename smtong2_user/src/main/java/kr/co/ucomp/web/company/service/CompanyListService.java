package kr.co.ucomp.web.company.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.company.dto.CompanyListSearchDto;
import kr.co.ucomp.web.company.entity.CompanyListEntity;

import java.util.List;


public interface CompanyListService {
	
	List<CompanyListEntity> getListCompanyList(CompanyListSearchDto param);
	
	CompanyListEntity getCompany(@Param("id") int param);
	
	List<CompanyListEntity> getReviewCompanyList(@Param("userId") long param);
	
	
}
