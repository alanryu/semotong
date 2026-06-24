package kr.co.ucomp.web.plan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.plan.dto.PlanReqMngDto;
import kr.co.ucomp.web.plan.entity.PlanReqMngEntity;
import kr.co.ucomp.web.plan.mapper.PlanReqMngMapper;
import kr.co.ucomp.web.plan.service.PlanReqMngService;

import java.util.List;

@Service("PlanReqMngService")
public class PlanReqMngServiceImpl implements PlanReqMngService {

    @Autowired
    private PlanReqMngMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PlanReqMngEntity> getList(PlanReqMngDto param) {
        List<PlanReqMngEntity> list = null;
        long count = mapper.getListCount(param);
        if (count > 0) {
            list = mapper.getList(param);
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public long getListCount(PlanReqMngDto param) {
        return mapper.getListCount(param);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanReqMngEntity getDetail(Integer id) {
        return mapper.getDetail(id);
    }

    @Override
    @Transactional
    public long create(PlanReqMngEntity param) {
        return mapper.create(param);
    }

    @Override
    @Transactional
    public long update(PlanReqMngEntity param) {
        return mapper.update(param);
    }

    @Override
    @Transactional
    public long delete(Integer id) {
        return mapper.delete(id);
    }

	@Override
	public long getListCountDistinct(PlanReqMngDto searchRequest) {
		return mapper.getListCountDistinct(searchRequest);
	}
} 