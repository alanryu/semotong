package kr.co.ucomp.web.pmb.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.pmb.entity.SalesPlanEntity;
import kr.co.ucomp.web.pmb.entity.SalesPlanListEntity;

public interface PlanSalesMngService {

	SalesPlanEntity getDetail(long id);

	long getCount(PlanSalesSearchDto param);

	int create(SalesPlanEntity entity);

	List<SalesPlanEntity> list(PlanSalesSearchDto param);

	void createByPlanList(SalesPlanListEntity salesPlanListEntity);

	List<SalesPlanListEntity> salePlanList(SalesPlanListEntity salesParam);

	void planDelete(@Param("id") long id);

	int update(SalesPlanEntity entity);

	List<SalesPlanEntity> listWithoutLimit(PlanSalesSearchDto param);

}
