package kr.co.ucomp.web.point.service;


import java.util.List;

import kr.co.ucomp.web.point.dto.PointCashDTO;
import kr.co.ucomp.web.point.entity.PointCashEntity;

public interface PointCashService {
	
	List<PointCashEntity>		getPointCash(PointCashDTO param);
	
	int							getPointCashCount(PointCashDTO param);
	
	PointCashEntity 			getPointCashById(PointCashDTO param);
	
	int 						insertCash(PointCashEntity param);
	int 						updateCash(PointCashEntity param);
	
}
