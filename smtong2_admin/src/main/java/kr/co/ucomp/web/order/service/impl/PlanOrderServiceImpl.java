package kr.co.ucomp.web.order.service.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.order.service.PlanOrderService;
import kr.co.ucomp.web.order.dto.PlanOrderSearchDto;
import kr.co.ucomp.web.order.entity.OrderStateEntity;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;
import kr.co.ucomp.web.order.mapper.PlanOrderMapper;
@Service("PlanOrderService")
public class PlanOrderServiceImpl implements PlanOrderService {
	
	  @Autowired
	    private PlanOrderMapper mapper;

	    @Override
	    @Transactional(readOnly = true)
	    public List<PlanOrderEntity> getList(PlanOrderSearchDto param) {
	        List<PlanOrderEntity> list  = mapper.getList(param);
	        return list;
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public long getListCount(PlanOrderSearchDto param) {
	        return mapper.getListCount(param);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public PlanOrderEntity getDetail(int id) {
	        return mapper.getDetail(id);
	    }


	    @Override
	    @Transactional
	    public long update(PlanOrderEntity param) {
	        return mapper.update(param);
	    }

	    @Override
	    @Transactional
	    public long delete(int id) {
	        return mapper.delete(id);
	    }

		@Override
		public long createOrderState(OrderStateEntity param) {
			return mapper.createOrderState(param);
		}

	    @Override
	    @Transactional(readOnly = true)
	    public List<OrderStateEntity> getListState(PlanOrderSearchDto param) {
	        List<OrderStateEntity> list  = mapper.getListState(param);
	        return list;
	    }

		@Override
		public List<PlanOrderEntity> getListWithoutLimit(PlanOrderSearchDto param) {
			return mapper.getListWithoutLimit(param);
		}
}
