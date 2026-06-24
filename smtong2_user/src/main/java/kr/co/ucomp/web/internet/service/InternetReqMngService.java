package kr.co.ucomp.web.internet.service;

import java.util.List;

import kr.co.ucomp.web.internet.dto.InternetReqMngSearchDto;
import kr.co.ucomp.web.internet.entity.InternetReqMngEntity;

public interface InternetReqMngService {

    long insertInternetReqMng(InternetReqMngEntity internetReqMngEntity);

    long getInternetReqMngCount(InternetReqMngSearchDto internetReqMngSearchDto);

    List<InternetReqMngEntity> getInternetReqMngList(InternetReqMngSearchDto searchDto);

    long insertInternetReqNewMng(InternetReqMngEntity internetReqMngEntity);

    String getLastOutboundCenter();

}
