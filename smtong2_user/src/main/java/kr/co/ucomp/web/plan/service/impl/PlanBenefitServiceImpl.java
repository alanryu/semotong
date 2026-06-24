package kr.co.ucomp.web.plan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.plan.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.plan.entity.PlanBenefitEntity;
import kr.co.ucomp.web.plan.entity.PlanBenefitMappingEntity;
import kr.co.ucomp.web.plan.mapper.PlanBenefitMapper;
import kr.co.ucomp.web.plan.service.PlanBenefitService;

import java.util.List;

@Component
@Service
public class PlanBenefitServiceImpl implements PlanBenefitService {

    @Autowired PlanBenefitMapper mapper;

    // ========================== 베네핏 정보 관리 ================================
    
    @Override
    public List<PlanBenefitEntity> infolist(PlanBenefitSearchDto param) {
        return mapper.infolist(param);
    }    
    
    @Override
    public long infolistCount(PlanBenefitSearchDto param) {
        return mapper.infolistCount(param);
    }    

    @Override
    public PlanBenefitEntity infoDetail(int id) {
        return mapper.infoDetail(id);
    }

    @Override
    public long createInfo(PlanBenefitEntity param) {
        return mapper.createInfo(param);
    }

    @Override
    public long updateInfo(PlanBenefitEntity param) {
        return mapper.updateInfo(param);
    }

    @Override
    public long deleteInfo(int id) {
        return mapper.deleteInfo(id);
    }
    
    
    
    
    // ========================== 베네핏 요금제 매핑 정보 관리 ================================
    
    @Override
    public List<PlanBenefitMappingEntity> maplist(PlanBenefitSearchDto param) {
        return mapper.maplist(param);
    }    
    
    @Override
    public long maplistCount(PlanBenefitSearchDto param) {
        return mapper.maplistCount(param);
    }    

    @Override
    public PlanBenefitMappingEntity mapDetail(int id) {
        return mapper.mapDetail(id);
    }

    @Override
    public long createmap(PlanBenefitMappingEntity param) {
        return mapper.createmap(param);
    }

    @Override
    public long updatemap(PlanBenefitMappingEntity param) {
        return mapper.updatemap(param);
    }

    @Override
    public long deletemap(int id) {
        return mapper.deletemap(id);
    }
    
    
    @Override
    public List<PlanBenefitMappingEntity> maplistAll(PlanBenefitSearchDto param) {
        return mapper.maplistAll(param);
    }    
    
}
