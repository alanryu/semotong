package kr.co.ucomp.web.point.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.point.dto.PointHistoryDTO;
import kr.co.ucomp.web.point.dto.PointHistoryDetailDTO;
import kr.co.ucomp.web.point.entity.PointHisDetCalcEntity;
import kr.co.ucomp.web.point.entity.PointHistoryDetailEntity;
import kr.co.ucomp.web.point.entity.PointHistoryEntity;

@Mapper
public interface PointHistoryMapper {

	List<PointHistoryEntity>	getPointHistory(PointHistoryDTO param);
	int							getPointHistoryCount(PointHistoryDTO param);
	PointHistoryEntity 			getPointHistoryById(PointHistoryDTO param);
	
	int update(PointHistoryEntity param);
	
	void insert(PointHistoryEntity param);
	
	
	
	
	
	int insertDetail(PointHistoryDetailEntity param);
	
	// DetailEntity가 아니다.  PointHisDetCalcEntity 계산을 위한 별도 Entity 사용
	List<PointHisDetCalcEntity>	getPointHistoryDetail(PointHistoryDetailDTO param);
	
	
	
	PointHistoryEntity 			getPointTotInfo(PointHistoryDTO param);
	
	List<PointHistoryEntity> 	getPointHistoryNew(PointHistoryDTO param);
				

}
