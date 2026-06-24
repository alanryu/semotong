package kr.co.ucomp.web.bizpurio.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.bizpurio.dto.BizPurioMsgResDto;
import kr.co.ucomp.web.bizpurio.entity.BizPurioMsgResEntity;




@Mapper
public interface BizPurioIFMapper {
	List<BizPurioMsgResEntity> getList(BizPurioMsgResDto param);
	long getListCount(BizPurioMsgResDto param);
}
