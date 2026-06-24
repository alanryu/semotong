package kr.co.ucomp.web.pmb.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.cmm.entity.ColumnInfoEntity;
import kr.co.ucomp.web.pmb.dto.ScrapingLogSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.entity.ScrapingLogEntity;
import kr.co.ucomp.web.pmb.mapper.ScrapingLogMapper;
import kr.co.ucomp.web.pmb.service.ScrapingLogService;

@Component
@Service
public class ScrapingLogServiceImpl implements ScrapingLogService {
	
	@Autowired
	private ScrapingLogMapper scrapingLogMapper;
	
	

	@Override
	public long getScrapingLogListCnt(ScrapingLogSearchDto param) {
		return scrapingLogMapper.getScrapingLogListCnt(param);
	}
	
	@Override
	public List<ScrapingLogEntity> getScrapingLogList(ScrapingLogSearchDto param) {
		return scrapingLogMapper.getScrapingLogList(param);
	}
	
	@Override
	public PlanEntity getPlanId(ScrapingLogSearchDto param) {
		return scrapingLogMapper.getPlanId(param);
	}
	
	@Override
	public List<PlanEntity> getPlanIds(ScrapingLogSearchDto param) {
		return scrapingLogMapper.getPlanIds(param);
	}
	
	@Override
	public ColumnInfoEntity getPlanListColumnInfo() {
		return scrapingLogMapper.getPlanListColumnInfo();
	}
	
	
	
	
	@Override
	public long insertScrapingLog(ScrapingLogEntity scrapingLog) {
		return scrapingLogMapper.insertScrapingLog(scrapingLog);
	}

	

	
}
