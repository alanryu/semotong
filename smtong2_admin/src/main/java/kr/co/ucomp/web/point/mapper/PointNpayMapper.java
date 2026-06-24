package kr.co.ucomp.web.point.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.point.dto.PointNpayDTO;
import kr.co.ucomp.web.point.entity.PointNpayEntity;

@Mapper
public interface PointNpayMapper {

	List<PointNpayEntity>	getPointNpay(PointNpayDTO param);
	int 					getPointNpayCount(PointNpayDTO param);
	PointNpayEntity 		getPointNpayById(PointNpayDTO param);
	int 					getPointNpayByPartnerTxNo(PointNpayDTO param);
	
	
	
	void 		insertNpay(PointNpayEntity param);
	int 						updateNpay(PointNpayEntity param);
	
}
