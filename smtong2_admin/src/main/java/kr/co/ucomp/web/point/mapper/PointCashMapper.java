package kr.co.ucomp.web.point.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.point.dto.PointCashDTO;
import kr.co.ucomp.web.point.entity.PointCashEntity;

@Mapper
public interface PointCashMapper {

	List<PointCashEntity>	getPointCash(PointCashDTO param);
	int						getPointCashCount(PointCashDTO param);
	PointCashEntity 		getPointCashById(PointCashDTO param);
	
	void 		insertCash(PointCashEntity param);
	int 						updateCash(PointCashEntity param);

}
