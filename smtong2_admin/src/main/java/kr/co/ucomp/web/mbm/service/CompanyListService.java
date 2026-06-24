package kr.co.ucomp.web.mbm.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;

import java.util.List;


public interface CompanyListService {
	
	List<CompanyListEntity> getListCompanyList(CompanyListSearchDto param);
	
	long getListCompanyListCount(CompanyListSearchDto param);
	
	CompanyListEntity getCompanyList(@Param("id") int param);
	
	long create(CompanyListEntity param);
	
	long update(CompanyListEntity param);
	
	long delCompanyList(@Param("id") int param);

	List<CompanyListEntity> getListCompanyListWithoutLimit(CompanyListSearchDto param);
	
	
	
	
}
