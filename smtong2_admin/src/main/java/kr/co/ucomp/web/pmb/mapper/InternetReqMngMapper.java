package kr.co.ucomp.web.pmb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.InternetReqMngSearchDto;
import kr.co.ucomp.web.pmb.entity.InternetReqMngEntity;

import java.util.List;

@Mapper
public interface InternetReqMngMapper {
	
	List<InternetReqMngEntity> getList(InternetReqMngSearchDto internetReqMngSearchDto);
	
    long getListCount(InternetReqMngSearchDto internetReqMngSearchDto);
    
    InternetReqMngEntity getDetail(@Param("id") int id);
    
    long update(InternetReqMngEntity param);
    
    long updateState(InternetReqMngEntity param);

	List<InternetReqMngEntity> getListWithOutLimit(InternetReqMngSearchDto param);
	
	long create(InternetReqMngEntity param);
	
}
