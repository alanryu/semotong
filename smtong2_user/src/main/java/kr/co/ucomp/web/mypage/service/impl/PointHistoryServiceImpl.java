package kr.co.ucomp.web.mypage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.mypage.dto.PointHistoryDTO;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.mypage.entity.PointHistoryDetailEntity;
import kr.co.ucomp.web.mypage.entity.PointHistoryEntity;
import kr.co.ucomp.web.mypage.mapper.PointHistoryMapper;
import kr.co.ucomp.web.mypage.service.PointHistoryService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

	private final CommCodeMngService commCodeMngService;
	
	private final PointHistoryMapper pointHistoryMapper;

	@Override
	public List<PointHistoryEntity> getMyPointHistory(PointHistoryDTO param) {
		return pointHistoryMapper.getMyPointHistory(param);
	}

	@Override
	public PointHistoryEntity getMyPointHistoryById(PointHistoryDTO param) {
		return pointHistoryMapper.getMyPointHistoryById(param);
	}

	@Override
	public int update(PointHistoryEntity param) {
		return pointHistoryMapper.update(param);
	}

	@Override
	public int insert(PointHistoryEntity param) {
		return pointHistoryMapper.insert(param);
	}

	@Override
	public int insertDetail(PointHistoryDetailEntity param) {
		return pointHistoryMapper.insertDetail(param);
	}

	/**
	 * pType - REV: 후기, ACT:개통
	 */
	@Override
	public int insertPointHistory(HttpServletRequest request, String pType) {
		
		PointHistoryEntity param = new PointHistoryEntity();
		HttpSession session = request.getSession(false);
		UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
		
		CommCodeSearchDto searchDto = new CommCodeSearchDto();
		searchDto.setCodeGroup("point_value");
		List<CodeEntity> codeList = commCodeMngService.getListCode(searchDto);
		
		int valAmount = 0;
		for(CodeEntity itm : codeList) {
			if(itm.getCode().equals(pType)) {
				valAmount = Integer.parseInt( itm.getEtc1() );
			}
		}
		
		param.setPointId(			(int) session.getAttribute("pointId")	);
		param.setDrCr(				"CR"		);
		param.setCrPointType(		pType		);
		param.setAmount(			valAmount	);
		param.setCreateId(			(int) loginInfo.getId()		);
		//return pointHistoryMapper.insertPointHistory(param);		//xml 내 있으면 않넣기 해뒀는데, 두번째 개통에도 후기를 달 수 있다. 그 때는 포인트 지급되어야 한다...
		return pointHistoryMapper.insert(param);	 
		//
	}
	
	@Override
	public List<PointHistoryEntity> getMyPointHistoryNew(PointHistoryDTO param) {
		return pointHistoryMapper.getMyPointHistoryNew(param);
	}



}
