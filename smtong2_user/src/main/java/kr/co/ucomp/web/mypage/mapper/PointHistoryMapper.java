package kr.co.ucomp.web.mypage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.mypage.dto.PointHistoryDTO;
import kr.co.ucomp.web.mypage.entity.PointHistoryDetailEntity;
import kr.co.ucomp.web.mypage.entity.PointHistoryEntity;

@Mapper
public interface PointHistoryMapper {

	List<PointHistoryEntity>	getMyPointHistory(PointHistoryDTO param);
	PointHistoryEntity 			getMyPointHistoryById(PointHistoryDTO param);
	
	int update(PointHistoryEntity param);
	
	int insert(PointHistoryEntity param);
	
	
	
	int insertDetail(PointHistoryDetailEntity param);
	
	int insertPointHistory(PointHistoryEntity param);
	
	List<PointHistoryEntity> 	getMyPointHistoryNew(PointHistoryDTO param);
	
}
