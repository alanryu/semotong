package kr.co.ucomp.web.stl.service;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ucomp.web.stl.dto.SettlementMngSearchDto;
import kr.co.ucomp.web.stl.dto.SettlementMngUploadDto;
import kr.co.ucomp.web.stl.entity.SettlementMSTEntity;
import kr.co.ucomp.web.stl.entity.SettlementMngEntity;

import java.util.List;
import java.util.Map;


public interface SettlementMngService {
	
	List<SettlementMSTEntity> getListSettlementMst(SettlementMngSearchDto param);
	
	List<SettlementMngEntity> getListSettlement(SettlementMngSearchDto param);
	
	SettlementMngEntity getSettlement(@Param("id") int param);
	
	long create(SettlementMngEntity param);
	
	long update(SettlementMngEntity param);
	
	long delSettlement(@Param("id") int param);

	Map<String,Object> uploadSettlementData(MultipartFile file, SettlementMngUploadDto param) throws Exception;
	
	void deleteByYearMonthAndCompany(SettlementMngUploadDto param);

	
	
	
}
