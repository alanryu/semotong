package kr.co.ucomp.web.pmb.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.pmb.entity.ScrapingPlanEntity;
import kr.co.ucomp.web.pmb.mapper.ScrapingPlanMapper;
import kr.co.ucomp.web.pmb.service.ScrapingPlanService;

@Component
@Service
public class ScrapingPlanServiceImpl implements ScrapingPlanService {
	
	@Autowired
	private ScrapingPlanMapper scrapingPlanMapper;

	@Override
	public List<ScrapingPlanEntity> getScrapingPlanList() {
		return scrapingPlanMapper.getScrapingPlanList();
	}

	@Override
	public long getScrapingPlanListCnt() {
		return 0;
	}
}
