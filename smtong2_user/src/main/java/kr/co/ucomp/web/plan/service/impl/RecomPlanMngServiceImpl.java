package kr.co.ucomp.web.plan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.plan.dto.RecomPlanMngSearchDto;
import kr.co.ucomp.web.plan.entity.RecomPlanEntity;
import kr.co.ucomp.web.plan.entity.RecomPlanPlanListEntity;
import kr.co.ucomp.web.plan.mapper.RecomPlanMngMapper;
import kr.co.ucomp.web.plan.service.RecomPlanMngService;

import java.util.List;

@Component
@Service
public class RecomPlanMngServiceImpl implements RecomPlanMngService {
	

    @Autowired
    RecomPlanMngMapper mapper;

    @Override
    public List<RecomPlanEntity> infolist(RecomPlanMngSearchDto param) {
        return mapper.infolist(param);
    }

    @Override
    public long infolistCount(RecomPlanMngSearchDto param) {
        return mapper.infolistCount(param);
    }

    @Override
    public RecomPlanEntity infoDetail(int id) {
        return mapper.infoDetail(id);
    }

 
}
