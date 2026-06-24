package kr.co.ucomp.web.internet.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.internet.dto.InternetReqMngSearchDto;
import kr.co.ucomp.web.internet.entity.InternetReqMngEntity;
import kr.co.ucomp.web.internet.mapper.InternetReqMngMapper;
import kr.co.ucomp.web.internet.service.InternetReqMngService;

import java.util.List;

@Service
@AllArgsConstructor
public class InternetReqMngServiceImpl implements InternetReqMngService {
    private InternetReqMngMapper internetReqMngMapper;

    @Override
    public long insertInternetReqMng(InternetReqMngEntity internetReqMngEntity) {
        return internetReqMngMapper.insertInternetReqMng(internetReqMngEntity);
    }

    @Override
    public long getInternetReqMngCount(InternetReqMngSearchDto internetReqMngSearchDto) {
        return internetReqMngMapper.getInternetReqMngCount(internetReqMngSearchDto);
    }

    @Override
    public List<InternetReqMngEntity> getInternetReqMngList(InternetReqMngSearchDto searchDto) {
        return internetReqMngMapper.getInternetReqMngList(searchDto);
    }

    @Override
    public long insertInternetReqNewMng(InternetReqMngEntity internetReqMngEntity) {
        return internetReqMngMapper.insertInternetReqNewMng(internetReqMngEntity);
    }

    @Override
    public String getLastOutboundCenter() {
        return internetReqMngMapper.getLastOutboundCenter();
    }

}
