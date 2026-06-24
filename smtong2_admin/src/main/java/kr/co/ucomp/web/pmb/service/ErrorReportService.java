package kr.co.ucomp.web.pmb.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.ErrorReportDto;
import kr.co.ucomp.web.pmb.entity.ErrorReportEntity;

import java.util.List;

public interface ErrorReportService {

    List<ErrorReportEntity> errorReportList(ErrorReportDto param);
    
    long countErrorReportList(ErrorReportDto param);

    ErrorReportEntity errorReport(@Param("id") long id);

    long insertErrorReport(ErrorReportEntity param);

    long updateErrorReport(ErrorReportEntity param);

    long deleteErrorReport(@Param("id") long id);

}
