package kr.co.ucomp.web.pmb.service;

import java.util.List;

import kr.co.ucomp.web.pmb.entity.ScrapingPlanEntity;

public interface ScrapingPlanService {
	List<ScrapingPlanEntity> getScrapingPlanList();
	long getScrapingPlanListCnt();
}
