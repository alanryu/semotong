package kr.co.ucomp.web.bizpurio.service;

import java.util.List;

import kr.co.ucomp.web.bizpurio.dto.BizPurioMsgResDto;
import kr.co.ucomp.web.bizpurio.entity.BizPurioMsgResEntity;

public interface BizPurioIFService {
	
	List<BizPurioMsgResEntity> getList(BizPurioMsgResDto param);
	long getListCount(BizPurioMsgResDto param);
}
