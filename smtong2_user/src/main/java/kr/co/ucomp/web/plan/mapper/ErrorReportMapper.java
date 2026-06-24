package kr.co.ucomp.web.plan.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.plan.dto.ErrorReportDto;
import kr.co.ucomp.web.plan.entity.ErrorReportEntity;

import java.util.List;

@Mapper
public interface ErrorReportMapper {

    List<ErrorReportEntity> errorReportList(ErrorReportDto param);

    ErrorReportEntity errorReport(@Param("id") int id);

    long insertErrorReport(ErrorReportEntity param);

    long updateErrorReport(ErrorReportEntity param);

    long deleteErrorReport(@Param("id") int id);

}
