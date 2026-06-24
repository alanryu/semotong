package kr.co.ucomp.web.internet.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.internet.dto.InternetReqMngSearchDto;
import kr.co.ucomp.web.internet.entity.InternetReqMngEntity;

import java.util.List;

@Mapper
public interface InternetReqMngMapper {
    long insertInternetReqMng(InternetReqMngEntity internetReqMngEntity);

    long getInternetReqMngCount(InternetReqMngSearchDto internetReqMngSearchDto);

    List<InternetReqMngEntity> getInternetReqMngList(InternetReqMngSearchDto internetReqMngSearchDto);

    long insertInternetReqNewMng(InternetReqMngEntity internetReqMngEntity);

    String getLastOutboundCenter();

}
