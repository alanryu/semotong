package kr.co.ucomp.web.csm.info.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.info.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.info.entity.SemotongBaekseoEntity;

import java.util.List;

public interface SemotongBaekseoSevice {

	List<SemotongBaekseoEntity> getListBaekseo(SemotongBaekseoDto param);
	
	long getListBaekseoCount(SemotongBaekseoDto param);
	
	SemotongBaekseoEntity getBaekseo(@Param("id") int id);

	SemotongBaekseoEntity getMvnoInfo();
}
