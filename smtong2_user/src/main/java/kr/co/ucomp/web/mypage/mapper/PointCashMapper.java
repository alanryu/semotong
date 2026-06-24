package kr.co.ucomp.web.mypage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.mypage.dto.PointCashDTO;
import kr.co.ucomp.web.mypage.entity.PointCashEntity;
import kr.co.ucomp.web.mypage.entity.PointNpayEntity;

@Mapper
public interface PointCashMapper {

	List<PointCashEntity>	getMyPointCash(PointCashDTO param);
	PointCashEntity 		getMyPointCashById(PointCashDTO param);
	
	void 		insertCash(PointCashEntity param);

}
