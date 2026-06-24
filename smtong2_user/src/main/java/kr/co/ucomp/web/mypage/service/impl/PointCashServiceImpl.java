package kr.co.ucomp.web.mypage.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.mypage.dto.PointCashDTO;
import kr.co.ucomp.web.mypage.entity.PointCashEntity;
import kr.co.ucomp.web.mypage.mapper.PointCashMapper;
import kr.co.ucomp.web.mypage.service.PointCashService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PointCashServiceImpl implements PointCashService {

	private final PointCashMapper pointCashMapper;

	@Override
	public List<PointCashEntity> getMyPointCash(PointCashDTO param) {
		return pointCashMapper.getMyPointCash(param);
	}

	@Override
	public PointCashEntity getMyPointCashById(PointCashDTO param) {
		return pointCashMapper.getMyPointCashById(param);
	}

	@Override
	public int insertCash(PointCashEntity param) {
		pointCashMapper.insertCash(param);
		return param.getId();
	}



}
