package kr.co.ucomp.web.pmb.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.pmb.dto.searchPlanZzimDto;
import kr.co.ucomp.web.pmb.entity.PlanZzimEntity;
import kr.co.ucomp.web.pmb.mapper.PlanZzimMapper;
import kr.co.ucomp.web.pmb.service.PlanZzimService;

import java.util.List;

@Component
@Service
public class PlanZzimServiceImpl implements PlanZzimService {

    @Autowired PlanZzimMapper mapper;

    @Override
    public List<PlanZzimEntity> getlist(searchPlanZzimDto param) {
        return mapper.getlist(param);
    }


    @Override
    public long create(PlanZzimEntity param) {
        return mapper.create(param);
    }


    @Override
    public long delete(@Param("userId") int userId,@Param("prodId") int prodId) {
        return mapper.delete(userId,prodId);
    }
}
