package kr.co.ucomp.web.point.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.point.dto.PointNpayDTO;
import kr.co.ucomp.web.point.entity.PointNpayEntity;
import kr.co.ucomp.web.point.mapper.PointNpayMapper;
import kr.co.ucomp.web.point.service.PointNpayService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PointNpayServiceImpl implements PointNpayService {

	private final PointNpayMapper pointNpayMapper;

	@Override
	public List<PointNpayEntity> getPointNpay(PointNpayDTO param) {
		return pointNpayMapper.getPointNpay(param);
	}

	@Override
	public int getPointNpayCount(PointNpayDTO param) {
		return pointNpayMapper.getPointNpayCount(param);
	}
	
	@Override
	public PointNpayEntity getPointNpayById(PointNpayDTO param) {
		return pointNpayMapper.getPointNpayById(param);
	}
	
	@Override
	public int getPointNpayByPartnerTxNo(PointNpayDTO param) {
		return pointNpayMapper.getPointNpayByPartnerTxNo(param);
	}
	
	
	

	@Override
	public int insertNpay(PointNpayEntity param) {
		pointNpayMapper.insertNpay(param);
		return param.getId();
	}

	@Override
	public int updateNpay(PointNpayEntity param) {
		return pointNpayMapper.updateNpay(param);
	}


}
