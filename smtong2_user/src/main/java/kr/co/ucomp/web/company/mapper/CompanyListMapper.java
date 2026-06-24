package kr.co.ucomp.web.company.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.company.dto.CompanyListSearchDto;
import kr.co.ucomp.web.company.entity.CompanyListEntity;

import java.util.List;

@Mapper
public interface CompanyListMapper {
  List<CompanyListEntity> getListCompanyList(CompanyListSearchDto param);

  Long getListCompanyListCount(CompanyListSearchDto param);

  CompanyListEntity getCompany(@Param("id") int param);

  List<CompanyListEntity> getReviewCompanyList(@Param("userId") long param);
  
}
