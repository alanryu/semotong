package kr.co.ucomp.web.pmb.service;

import java.util.List;

import kr.co.ucomp.web.cmm.entity.ColumnInfoEntity;
import kr.co.ucomp.web.pmb.dto.ScrapingLogSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.entity.ScrapingLogEntity;

public interface ScrapingLogService {
	
	

	long getScrapingLogListCnt(ScrapingLogSearchDto param);

	List<ScrapingLogEntity> getScrapingLogList(ScrapingLogSearchDto param);
	
	PlanEntity getPlanId(ScrapingLogSearchDto param);
	
	List<PlanEntity> getPlanIds(ScrapingLogSearchDto param);
	
	ColumnInfoEntity getPlanListColumnInfo();
	
	
	
	long insertScrapingLog(ScrapingLogEntity scrapingLog);
}
