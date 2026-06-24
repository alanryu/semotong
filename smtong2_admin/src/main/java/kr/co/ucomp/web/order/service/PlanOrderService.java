package kr.co.ucomp.web.order.service;

import java.util.List;


import kr.co.ucomp.web.order.dto.PlanOrderSearchDto;
import kr.co.ucomp.web.order.entity.OrderStateEntity;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;

public interface PlanOrderService {
	
	List<PlanOrderEntity> getList(PlanOrderSearchDto param);
    
    long getListCount(PlanOrderSearchDto param);
    
    PlanOrderEntity getDetail(int id);
    
    long update(PlanOrderEntity param);
    
    long delete(int id);
    
    long createOrderState(OrderStateEntity param);
    
    List<OrderStateEntity> getListState(PlanOrderSearchDto param);

	List<PlanOrderEntity> getListWithoutLimit(PlanOrderSearchDto param);
}
