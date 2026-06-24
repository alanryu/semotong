package kr.co.ucomp.web.pmb.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ucomp.web.pmb.dto.InternetReqMngSearchDto;
import kr.co.ucomp.web.pmb.entity.InternetReqMngEntity;

public interface InternetReqMngService {

    List<InternetReqMngEntity> getList(InternetReqMngSearchDto searchDto);

    long getListCount(InternetReqMngSearchDto internetReqMngSearchDto);

    InternetReqMngEntity getDetail(@Param("id") int id);

    long update(InternetReqMngEntity param);

    long updateState(InternetReqMngEntity param);

    List<InternetReqMngEntity> getListWithOutLimit(InternetReqMngSearchDto param);

    Map<String, Object> reqExcelUpload(MultipartFile file, String isNew, String outboundCenter) throws IOException;
}
