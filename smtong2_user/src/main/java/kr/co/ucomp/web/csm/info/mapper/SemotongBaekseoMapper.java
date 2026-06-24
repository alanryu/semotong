package kr.co.ucomp.web.csm.info.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.info.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.info.entity.SemotongBaekseoEntity;
import kr.co.ucomp.web.csm.review.entity.SemotongReviewEntity;

import java.util.List;

@Mapper
public interface SemotongBaekseoMapper {

	List<SemotongBaekseoEntity> getListBaekseo(SemotongBaekseoDto param);
	
	long getListBaekseoCount(SemotongBaekseoDto param);
	
	SemotongBaekseoEntity getBaekseo(@Param("id") int id);
	
	
	SemotongBaekseoEntity getMvnoInfo();

 }
