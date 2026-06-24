package kr.co.ucomp.web.mypage.service.impl;

import org.springframework.stereotype.Service;

import kr.co.ucomp.web.mypage.dto.PointDTO;
import kr.co.ucomp.web.mypage.entity.PointEntity;
import kr.co.ucomp.web.mypage.mapper.PointMapper;
import kr.co.ucomp.web.mypage.service.PointService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PointServiceImpl implements PointService {

	private final PointMapper pointMapper;

	@Override
	public PointEntity getMyPoint(PointDTO param) {
		return pointMapper.getMyPoint(param);
	}



}
