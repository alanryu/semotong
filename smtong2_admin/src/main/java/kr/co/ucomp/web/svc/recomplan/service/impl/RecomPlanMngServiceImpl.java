package kr.co.ucomp.web.svc.recomplan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.recomplan.dto.RecomPlanMngSearchDto;
import kr.co.ucomp.web.svc.recomplan.entity.RecomPlanEntity;
import kr.co.ucomp.web.svc.recomplan.entity.RecomPlanPlanListEntity;
import kr.co.ucomp.web.svc.recomplan.mapper.RecomPlanMngMapper;
import kr.co.ucomp.web.svc.recomplan.service.RecomPlanMngService;

import java.util.List;

@Component
@Service
public class RecomPlanMngServiceImpl implements RecomPlanMngService {
	

    @Autowired
    RecomPlanMngMapper mapper;

 // ========================== 추천요금제 정보 관리 ================================

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

    @Override
    public long createInfo(RecomPlanEntity param) {
        return mapper.createInfo(param);
    }

    @Override
    public long updateInfo(RecomPlanEntity param) {
        return mapper.updateInfo(param);
    }

    @Override
    public long deleteInfo(int id) {
        return mapper.deleteInfo(id);
    }

 // ========================== 추천요금제 요금제 리스트 정보 관리 ================================

    @Override
    public List<RecomPlanPlanListEntity> maplist(RecomPlanMngSearchDto param) {
        return mapper.maplist(param);
    }

    @Override
    public long maplistCount(RecomPlanMngSearchDto param) {
        return mapper.maplistCount(param);
    }

    @Override
    public RecomPlanPlanListEntity mapDetail(int id) {
        return mapper.mapDetail(id);
    }


    @Override
    public long createmap(RecomPlanPlanListEntity param) {
        return mapper.createmap(param);
    }

    @Override
    public long updatemap(RecomPlanPlanListEntity param) {
        return mapper.updatemap(param);
    }

    @Override
    public long updatemapOrder(RecomPlanPlanListEntity param) {
        return mapper.updatemapOrder(param);
    }

    @Override
    public long deletemap(int id) {
        return mapper.deletemap(id);
    }
    
    @Override
    public long deletemapListByMngId(int id) {
        return mapper.deletemapListByMngId(id);
    }


}
