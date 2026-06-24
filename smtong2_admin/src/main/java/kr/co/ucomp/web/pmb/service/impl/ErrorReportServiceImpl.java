package kr.co.ucomp.web.pmb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.pmb.dto.ErrorReportDto;
import kr.co.ucomp.web.pmb.entity.ErrorReportEntity;
import kr.co.ucomp.web.pmb.mapper.ErrorReportMapper;
import kr.co.ucomp.web.pmb.service.ErrorReportService;

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
	public long countErrorReportList(ErrorReportDto param) {
    	return errorReportMapper.countErrorReportList(param);
	}

    @Override
    public ErrorReportEntity errorReport(long id) {
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
    public long deleteErrorReport(long id) {
        return errorReportMapper.deleteErrorReport(id);
    }
	
}
