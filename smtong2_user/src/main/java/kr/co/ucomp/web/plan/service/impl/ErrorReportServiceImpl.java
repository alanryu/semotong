package kr.co.ucomp.web.plan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.plan.dto.ErrorReportDto;
import kr.co.ucomp.web.plan.entity.ErrorReportEntity;
import kr.co.ucomp.web.plan.mapper.ErrorReportMapper;
import kr.co.ucomp.web.plan.service.ErrorReportService;

import java.util.List;

@Component
@Service
public class ErrorReportServiceImpl implements ErrorReportService {

    @Autowired ErrorReportMapper errorReportMapper;

    @Override
    public List<ErrorReportEntity> errorReportList(ErrorReportDto param) {
        return errorReportMapper.errorReportList(param);
    }

    @Override
    public ErrorReportEntity errorReport(int id) {
        return errorReportMapper.errorReport(id);
    }

    @Override
    public long insertErrorReport(ErrorReportEntity param) {
        return errorReportMapper.insertErrorReport(param);
    }

    @Override
    public long updateErrorReport(ErrorReportEntity param) {
        return errorReportMapper.updateErrorReport(param);
    }

    @Override
    public long deleteErrorReport(int id) {
        return errorReportMapper.deleteErrorReport(id);
    }
}
