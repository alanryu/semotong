package kr.co.ucomp.web.mypage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.mypage.dto.PointNpayDTO;
import kr.co.ucomp.web.mypage.entity.PointNpayEntity;
import kr.co.ucomp.web.mypage.mapper.PointNpayMapper;
import kr.co.ucomp.web.mypage.service.PointNpayService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PointNpayServiceImpl implements PointNpayService {

	private final PointNpayMapper pointNpayMapper;

	@Override
	public List<PointNpayEntity> getMyPointNpay(PointNpayDTO param) {
		return pointNpayMapper.getMyPointNpay(param);
	}

	@Override
	public PointNpayEntity getMyPointNpayById(PointNpayDTO param) {
		return pointNpayMapper.getMyPointNpayById(param);
	}

	@Override
	public int insertNpay(PointNpayEntity param) {
		pointNpayMapper.insertNpay(param);
		return param.getId();
	}



}
