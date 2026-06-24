package kr.co.ucomp.web.point.service;


import java.util.List;

import kr.co.ucomp.web.point.dto.PointAccDTO;
import kr.co.ucomp.web.point.entity.PointAccEntity;

public interface PointAccService {
	
	PointAccEntity getMyPoint(PointAccDTO param);
	
	List<PointAccEntity> getPointUserList(PointAccDTO param);
	
	int update(PointAccEntity param);
}
