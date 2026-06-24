package kr.co.ucomp.web.plan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.plan.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.plan.entity.SalesPlanEntity;
import kr.co.ucomp.web.plan.mapper.PlanSalesMapper;
import kr.co.ucomp.web.plan.service.PlanSalesService;

@Component
@Service
public class PlanSalesServiceImpl implements PlanSalesService {

	@Autowired
	private PlanSalesMapper planSalesMapper;
	
	@Override
	public SalesPlanEntity getPlanSales(PlanSalesSearchDto param) {
		return planSalesMapper.getPlanSales(param);
	}

}
