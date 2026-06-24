package kr.co.ucomp.web.plan.service;

import kr.co.ucomp.web.plan.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.plan.entity.SalesPlanEntity;

public interface PlanSalesService {

	SalesPlanEntity getPlanSales(PlanSalesSearchDto param);

}
