package kr.co.ucomp.web.stl.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.stl.dto.EarningSearchDto;
import kr.co.ucomp.web.stl.entity.EarningEntity;
import kr.co.ucomp.web.stl.entity.EarningListEntity;

public interface EarningService {

	EarningEntity selectEarning(EarningSearchDto param);

	int createEarning(EarningEntity entity);

	void excelUpload(MultipartFile uploadMapData, AdminUserDto loginadminInfo, long earningId);

	long listCount(EarningSearchDto param);

	List<EarningEntity> list(EarningSearchDto param);

	long detailListCount(EarningSearchDto param);

	List<EarningListEntity> detailList(EarningSearchDto param);

	List<EarningListEntity> detailListWithoutLimit(EarningSearchDto param);

	void deleteEarningList(@Param("earningId") long id);

}
