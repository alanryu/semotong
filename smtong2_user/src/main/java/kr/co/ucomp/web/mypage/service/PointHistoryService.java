package kr.co.ucomp.web.mypage.service;


import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.web.mypage.dto.PointHistoryDTO;
import kr.co.ucomp.web.mypage.entity.PointHistoryDetailEntity;
import kr.co.ucomp.web.mypage.entity.PointHistoryEntity;

public interface PointHistoryService {
	
	List<PointHistoryEntity> 	getMyPointHistory(PointHistoryDTO param);
	
	PointHistoryEntity 			getMyPointHistoryById(PointHistoryDTO param);
	
	int update(PointHistoryEntity param);
	
	int insert(PointHistoryEntity param);
	
	
	
	int insertDetail(PointHistoryDetailEntity param);
	
	/**
	 * pType - REV: 후기, ACT:개통
	 * @param request
	 * @param pType
	 * @return
	 */
	int insertPointHistory(HttpServletRequest request, String pType);
	
	
	List<PointHistoryEntity> 	getMyPointHistoryNew(PointHistoryDTO param);
	
}
