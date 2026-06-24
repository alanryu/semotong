package kr.co.ucomp.web.mypage.service;


import java.util.List;

import kr.co.ucomp.web.mypage.dto.PointNpayDTO;
import kr.co.ucomp.web.mypage.entity.PointNpayEntity;

public interface PointNpayService {
	
	List<PointNpayEntity>		getMyPointNpay(PointNpayDTO param);
	
	PointNpayEntity 			getMyPointNpayById(PointNpayDTO param);
	
	int 						insertNpay(PointNpayEntity param);
	
}
