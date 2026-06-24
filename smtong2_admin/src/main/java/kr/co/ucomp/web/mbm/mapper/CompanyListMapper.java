package kr.co.ucomp.web.mbm.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;

import java.util.List;

@Mapper
public interface CompanyListMapper {
  List<CompanyListEntity> getListCompanyList(CompanyListSearchDto param);

  long getListCompanyListCount(CompanyListSearchDto param);

  CompanyListEntity getCompanyList(@Param("id") int param);

  long create(CompanyListEntity param);

  long update(CompanyListEntity param);

  long delCompanyList(@Param("id") int param);

  List<CompanyListEntity> getListCompanyListWithoutLimit(CompanyListSearchDto param);


}
