package kr.co.ucomp.web.stl.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.stl.dto.SettlementMngSearchDto;
import kr.co.ucomp.web.stl.dto.SettlementMngUploadDto;
import kr.co.ucomp.web.stl.entity.SettlementMSTEntity;
import kr.co.ucomp.web.stl.entity.SettlementMngEntity;

import java.util.List;

@Mapper
public interface SettlementMngMapper {
  List<SettlementMngEntity> getListSettlement(SettlementMngSearchDto param);
  
  List<SettlementMSTEntity> getListSettlementMst(SettlementMngSearchDto param);

  Long getListSettlementCount(SettlementMngSearchDto param);

  SettlementMngEntity getSettlement(@Param("id") int param);

  long create(SettlementMngEntity param);

  long update(SettlementMngEntity param);

  long delSettlement(@Param("id") int param);
  
  long deleteByYearMonthAndCompany(SettlementMngUploadDto param);

}
