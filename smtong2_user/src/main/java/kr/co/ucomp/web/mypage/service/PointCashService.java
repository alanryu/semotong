package kr.co.ucomp.web.mypage.service;


import java.util.List;

import kr.co.ucomp.web.mypage.dto.PointCashDTO;
import kr.co.ucomp.web.mypage.entity.PointCashEntity;

public interface PointCashService {
	
	List<PointCashEntity>		getMyPointCash(PointCashDTO param);
	
	PointCashEntity 			getMyPointCashById(PointCashDTO param);
	
	int 						insertCash(PointCashEntity param);
	
}
