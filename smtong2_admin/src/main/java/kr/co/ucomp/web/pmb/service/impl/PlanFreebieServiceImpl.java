package kr.co.ucomp.web.pmb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.pmb.dto.PlanFreebieSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanFreebieEntity;
import kr.co.ucomp.web.pmb.entity.PlanFreebieMappingEntity;
import kr.co.ucomp.web.pmb.mapper.PlanFreebieMapper;
import kr.co.ucomp.web.pmb.service.PlanFreebieService;

import java.util.List;

@Component
@Service
public class PlanFreebieServiceImpl implements PlanFreebieService {

    @Autowired PlanFreebieMapper mapper;

    // ========================== 사은품 정보 관리 ================================
    
    @Override
    public List<PlanFreebieEntity> infolist(PlanFreebieSearchDto param) {
        return mapper.infolist(param);
    }    
    
    @Override
    public long infolistCount(PlanFreebieSearchDto param) {
        return mapper.infolistCount(param);
    }    

    @Override
    public PlanFreebieEntity infoDetail(int id) {
        return mapper.infoDetail(id);
    }

    @Override
    public long createInfo(PlanFreebieEntity param) {
        return mapper.createInfo(param);
    }

    @Override
    public long updateInfo(PlanFreebieEntity param) {
        return mapper.updateInfo(param);
    }

    @Override
    public long deleteInfo(int id) {
        return mapper.deleteInfo(id);
    }
    
    
    
    
    // ========================== 사은품 요금제 매핑 정보 관리 ================================
    
    @Override
    public List<PlanFreebieMappingEntity> maplist(PlanFreebieSearchDto param) {
        return mapper.maplist(param);
    }    
    
    @Override
    public long maplistCount(PlanFreebieSearchDto param) {
        return mapper.maplistCount(param);
    }    

    @Override
    public PlanFreebieMappingEntity mapDetail(int id) {
        return mapper.mapDetail(id);
    }

    @Override
    public long createmap(PlanFreebieMappingEntity param) {
        return mapper.createmap(param);
    }

    @Override
    public long updatemap(PlanFreebieMappingEntity param) {
        return mapper.updatemap(param);
    }

    @Override
    public long updatemapOrder(PlanFreebieMappingEntity param) {
        return mapper.updatemapOrder(param);
    }
    
    @Override
    public long deletemap(int id) {
        return mapper.deletemap(id);
    }
    
    
    @Override
    public List<PlanFreebieMappingEntity> maplistAll(PlanFreebieSearchDto param) {
        return mapper.maplistAll(param);
    }    
}
