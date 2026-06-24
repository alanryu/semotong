package kr.co.ucomp.web.pmb.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.cmm.entity.ColumnInfoEntity;
import kr.co.ucomp.web.pmb.dto.ScrapingLogSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.entity.ScrapingLogEntity;

@Mapper
public interface ScrapingLogMapper {
	
	
	List<ScrapingLogEntity> getScrapingLogList(ScrapingLogSearchDto param);
	
	long getScrapingLogListCnt(ScrapingLogSearchDto param);
	
	PlanEntity getPlanId(ScrapingLogSearchDto param);
	
	List<PlanEntity> getPlanIds(ScrapingLogSearchDto param);
	
	ColumnInfoEntity getPlanListColumnInfo();
	
	
	
	
	long insertScrapingLog(ScrapingLogEntity scrapingLog);
	
}
