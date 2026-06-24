package kr.co.ucomp.web.plan.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.mapper.PlanMyPageMapper;
import kr.co.ucomp.web.plan.service.PlanMyPageService;

@Component
@Service
public class PlanMyPageServiceImpl implements PlanMyPageService {

	@Autowired PlanMyPageMapper mapper;

	@Override
	public List<PlanEntity> getMyPlanList(SearchPlanDto param) {
		return mapper.getMyPlanList(param);
	}
	
	@Override
	public PlanEntity getMyPlan(SearchPlanDto param) {
		return mapper.getMyPlan(param);
	}

}
