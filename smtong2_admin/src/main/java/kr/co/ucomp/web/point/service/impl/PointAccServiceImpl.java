package kr.co.ucomp.web.point.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.point.dto.PointAccDTO;
import kr.co.ucomp.web.point.entity.PointAccEntity;
import kr.co.ucomp.web.point.mapper.PointAccMapper;
import kr.co.ucomp.web.point.service.PointAccService;

@Component
@Service
public class PointAccServiceImpl implements PointAccService {

	@Autowired PointAccMapper pointAccMapper;

	@Override
	public PointAccEntity getMyPoint(PointAccDTO param) {
		return pointAccMapper.getMyPoint(param);
	}

	@Override
	public List<PointAccEntity> getPointUserList(PointAccDTO param) {
		return pointAccMapper.getPointUserList(param);
	}

	@Override
	public int update(PointAccEntity param) {
		return pointAccMapper.update(param);
	}



}
