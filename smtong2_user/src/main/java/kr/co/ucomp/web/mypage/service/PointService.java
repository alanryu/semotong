package kr.co.ucomp.web.mypage.service;


import kr.co.ucomp.web.mypage.dto.PointDTO;
import kr.co.ucomp.web.mypage.entity.PointEntity;

public interface PointService {
	
	PointEntity getMyPoint(PointDTO param);
	
}
