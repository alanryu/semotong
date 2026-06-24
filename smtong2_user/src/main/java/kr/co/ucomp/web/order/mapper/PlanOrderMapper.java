package kr.co.ucomp.web.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.order.dto.PlanOrderSearchDto;
import kr.co.ucomp.web.order.entity.OrderStateEntity;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;

@Mapper
public interface PlanOrderMapper {

	List<PlanOrderEntity> getList(PlanOrderSearchDto param);
    
    long getListCount(PlanOrderSearchDto param);
    
    PlanOrderEntity getDetail(int id);
    
    long create(PlanOrderEntity param);
    
    long update(PlanOrderEntity param);
    
    long delete(int id);
    
    long createOrderState(OrderStateEntity param);
    
    List<OrderStateEntity> getListState(PlanOrderSearchDto param);
    
}
