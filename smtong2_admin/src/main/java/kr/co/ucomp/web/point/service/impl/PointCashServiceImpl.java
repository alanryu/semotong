package kr.co.ucomp.web.point.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.point.dto.PointCashDTO;
import kr.co.ucomp.web.point.entity.PointCashEntity;
import kr.co.ucomp.web.point.mapper.PointCashMapper;
import kr.co.ucomp.web.point.service.PointCashService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PointCashServiceImpl implements PointCashService {

	private final PointCashMapper pointCashMapper;

	@Override
	public List<PointCashEntity> getPointCash(PointCashDTO param) {
		return pointCashMapper.getPointCash(param);
	}
	
	@Override
	public int getPointCashCount(PointCashDTO param) {
		return pointCashMapper.getPointCashCount(param);
	}

	@Override
	public PointCashEntity getPointCashById(PointCashDTO param) {
		return pointCashMapper.getPointCashById(param);
	}

	@Override
	public int insertCash(PointCashEntity param) {
		pointCashMapper.insertCash(param);
		return param.getId();
	}

	@Override
	public int updateCash(PointCashEntity param) {
		return pointCashMapper.updateCash(param);
	}
}
