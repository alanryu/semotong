package kr.co.ucomp.web.point.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.point.dto.PointHistoryDTO;
import kr.co.ucomp.web.point.dto.PointHistoryDetailDTO;
import kr.co.ucomp.web.point.entity.PointHisDetCalcEntity;
import kr.co.ucomp.web.point.entity.PointHistoryDetailEntity;
import kr.co.ucomp.web.point.entity.PointHistoryEntity;
import kr.co.ucomp.web.point.mapper.PointHistoryMapper;
import kr.co.ucomp.web.point.service.PointHistoryService;

@Component
@Service
public class PointHistoryServiceImpl implements PointHistoryService {

	@Autowired PointHistoryMapper pointHistoryMapper;

	@Override
	public List<PointHistoryEntity> getPointHistory(PointHistoryDTO param) {
		return pointHistoryMapper.getPointHistory(param);
	}

	@Override
	public int getPointHistoryCount(PointHistoryDTO param) {
		return pointHistoryMapper.getPointHistoryCount(param);
	}
	
	@Override
	public PointHistoryEntity getPointHistoryById(PointHistoryDTO param) {
		return pointHistoryMapper.getPointHistoryById(param);
	}

	@Override
	public int update(PointHistoryEntity param) {
		return pointHistoryMapper.update(param);
	}

	@Override
	public int insert(PointHistoryEntity param) {
		pointHistoryMapper.insert(param);
		return param.getId();
	}

	
	
	
	
	@Override
	public int insertDetail(PointHistoryDetailEntity param) {
		return pointHistoryMapper.insertDetail(param);
	}

	// DetailEntity가 아니다.  PointHisDetCalcEntity 계산을 위한 별도 Entity 사용
	@Override
	public List<PointHisDetCalcEntity> getPointHistoryDetail(PointHistoryDetailDTO param) {
		return pointHistoryMapper.getPointHistoryDetail(param);
	}

	
	
	@Override
	public PointHistoryEntity getPointTotInfo(PointHistoryDTO param) {
		return pointHistoryMapper.getPointTotInfo(param);
	}

	@Override
	public List<PointHistoryEntity> getPointHistoryNew(PointHistoryDTO param) {
		return pointHistoryMapper.getPointHistoryNew(param);
	}
	
	
	


}
