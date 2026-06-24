package kr.co.ucomp.web.stl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.stl.dto.EarningSearchDto;
import kr.co.ucomp.web.stl.entity.EarningEntity;
import kr.co.ucomp.web.stl.entity.EarningListEntity;

@Mapper
public interface EarningMapper {

	EarningEntity selectEarning(EarningSearchDto param);

	int createEarning(EarningEntity entity);

	void createEarningList(EarningListEntity item);

	long listCount(EarningSearchDto param);

	List<EarningEntity> list(EarningSearchDto param);

	long detailListCount(EarningSearchDto param);

	List<EarningListEntity> detailList(EarningSearchDto param);

	List<EarningListEntity> detailListWithoutLimit(EarningSearchDto param);

	void deleteEarningList(@Param("earningId") long id);
}
