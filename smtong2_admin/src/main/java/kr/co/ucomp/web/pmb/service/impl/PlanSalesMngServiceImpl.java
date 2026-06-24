package kr.co.ucomp.web.pmb.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.pmb.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.pmb.entity.SalesPlanEntity;
import kr.co.ucomp.web.pmb.entity.SalesPlanListEntity;
import kr.co.ucomp.web.pmb.mapper.PlanSalesMngMapper;
import kr.co.ucomp.web.pmb.service.PlanSalesMngService;

@Component
@Service
public class PlanSalesMngServiceImpl implements PlanSalesMngService {

	@Autowired
	private PlanSalesMngMapper planSalesMngMapper;
	
	@Override
	public SalesPlanEntity getDetail(long id) {
		return planSalesMngMapper.getDetail(id);
	}

	@Override
	public long getCount(PlanSalesSearchDto param) {
		return planSalesMngMapper.getCount(param);
	}

	@Override
	public int create(SalesPlanEntity entity) {
		return planSalesMngMapper.create(entity);
	}

	@Override
	public List<SalesPlanEntity> list(PlanSalesSearchDto param) {
		return planSalesMngMapper.list(param);
	}

	@Override
	public void createByPlanList(SalesPlanListEntity salesPlanListEntity) {
		planSalesMngMapper.createByPlanList(salesPlanListEntity);
	}

	@Override
	public List<SalesPlanListEntity> salePlanList(SalesPlanListEntity salesParam) {
		return planSalesMngMapper.salePlanList(salesParam);
	}

	@Override
	public void planDelete(@Param("id") long id) {
		planSalesMngMapper.planDelete(id);
	}

	@Override
	public int update(SalesPlanEntity entity) {
		return planSalesMngMapper.update(entity);
	}

	@Override
	public List<SalesPlanEntity> listWithoutLimit(PlanSalesSearchDto param) {
		return planSalesMngMapper.listWithoutLimit(param);
	}

}
