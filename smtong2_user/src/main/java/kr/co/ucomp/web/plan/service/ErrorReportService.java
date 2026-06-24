package kr.co.ucomp.web.plan.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.plan.dto.ErrorReportDto;
import kr.co.ucomp.web.plan.entity.ErrorReportEntity;

import java.util.List;

public interface ErrorReportService {

    List<ErrorReportEntity> errorReportList(ErrorReportDto param);

    ErrorReportEntity errorReport(@Param("id") int id);

    long insertErrorReport(ErrorReportEntity param);

    long updateErrorReport(ErrorReportEntity param);

    long deleteErrorReport(@Param("id") int id);

}
