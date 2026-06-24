package kr.co.ucomp.web.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.mypage.dto.PointDTO;
import kr.co.ucomp.web.mypage.entity.PointEntity;

@Mapper
public interface PointMapper {

	PointEntity getMyPoint(PointDTO param);

}
