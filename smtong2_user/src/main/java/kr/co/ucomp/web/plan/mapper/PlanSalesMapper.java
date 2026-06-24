package kr.co.ucomp.web.plan.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.plan.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.plan.entity.SalesPlanEntity;

@Mapper
public interface PlanSalesMapper {

	SalesPlanEntity getPlanSales(PlanSalesSearchDto param);

}
