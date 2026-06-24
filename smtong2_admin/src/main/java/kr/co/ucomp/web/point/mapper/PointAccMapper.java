package kr.co.ucomp.web.point.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.point.dto.PointAccDTO;
import kr.co.ucomp.web.point.entity.PointAccEntity;

@Mapper
public interface PointAccMapper {

	PointAccEntity getMyPoint(PointAccDTO param);
	
	List<PointAccEntity> getPointUserList(PointAccDTO param);
	
	int update(PointAccEntity param);
	
}
