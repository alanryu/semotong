package kr.co.ucomp.web.bizpurio.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.bizpurio.dto.BizPurioMsgResDto;
import kr.co.ucomp.web.bizpurio.entity.BizPurioMsgResEntity;
import kr.co.ucomp.web.bizpurio.mapper.BizPurioIFMapper;
import kr.co.ucomp.web.bizpurio.service.BizPurioIFService;

@Service
public class BizPurioIFSeviceImpl implements BizPurioIFService {

	 @Autowired private BizPurioIFMapper mapper;
	 
	 
	@Override
	public List<BizPurioMsgResEntity> getList(BizPurioMsgResDto param) {
		return mapper.getList(param);
	}


	@Override
	public long getListCount(BizPurioMsgResDto param) {
		return mapper.getListCount(param);
	}

}
