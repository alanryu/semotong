package kr.co.ucomp.web.mypage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.mypage.dto.PointNpayDTO;
import kr.co.ucomp.web.mypage.entity.PointNpayEntity;

@Mapper
public interface PointNpayMapper {

	List<PointNpayEntity>	getMyPointNpay(PointNpayDTO param);
	PointNpayEntity 		getMyPointNpayById(PointNpayDTO param);
	
	void 		insertNpay(PointNpayEntity param);

}
