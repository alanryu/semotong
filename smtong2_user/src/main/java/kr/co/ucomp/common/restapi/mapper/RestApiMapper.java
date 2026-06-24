package kr.co.ucomp.common.restapi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.common.restapi.entity.RestApiLogEntity;
import kr.co.ucomp.common.restapi.entity.RestApiTokenMngEntity;

@Mapper
public interface RestApiMapper {
	
	long createLog(RestApiLogEntity param);
	
	long createToken(RestApiTokenMngEntity param);
	
	long deleteToken(@Param("tokenCode") String tokenCode);
	
	RestApiTokenMngEntity getToken(@Param("tokenCode") String tokenCode);
}
