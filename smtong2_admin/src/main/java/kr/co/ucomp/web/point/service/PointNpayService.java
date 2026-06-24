package kr.co.ucomp.web.point.service;


import java.util.List;

import kr.co.ucomp.web.point.dto.PointNpayDTO;
import kr.co.ucomp.web.point.entity.PointNpayEntity;

public interface PointNpayService {
	
	List<PointNpayEntity>		getPointNpay(PointNpayDTO param);
	
	int							getPointNpayCount(PointNpayDTO param);
	
	PointNpayEntity 			getPointNpayById(PointNpayDTO param);
	
	int 						getPointNpayByPartnerTxNo(PointNpayDTO param);
	
	
	
	int 						insertNpay(PointNpayEntity param);
	int 						updateNpay(PointNpayEntity param);
	
}
