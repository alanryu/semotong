package kr.co.ucomp.web.pmb.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.pmb.dto.PlanCateMngDto;
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.entity.PlanCateMngEntity;
import kr.co.ucomp.web.pmb.mapper.PlanCateMngMapper;
import kr.co.ucomp.web.pmb.service.PlanCateMngService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service
public class PlanCateMngServiceImpl implements PlanCateMngService {

    @Autowired PlanCateMngMapper mapper;

    
    @Override
    public List<PlanCateMngEntity> getTaglist(PlanCateMngDto param) {
        return mapper.getTaglist(param);
    }


    @Override
    public List<PlanCateMngEntity> getCatelist(PlanCateMngDto param) {
        return mapper.getCatelist(param);
    }


    @Override
    public long getCateListCount(PlanCateMngDto param) {
        return mapper.getCateListCount(param);
    }
    
    
    @Override
    public long create(PlanCateMngEntity param) {
        return mapper.create(param);
    }
    
    @Override
    public long update(PlanCateMngEntity param) {
        return mapper.update(param);
    }


    @Override
    public long delete(PlanCateMngEntity param){
        return mapper.delete(param);
    }
    
    @Override
    public long updatePlanTag(PlanUpdateDto param) {
        return mapper.updatePlanTag(param);
    }
    
    
    @Override
    public String maxTagCode() {
        return mapper.maxTagCode();
    }
    
    @Override
    public String maxCateCode() {
        return mapper.maxCateCode();
    }
    
    @Override
    public long deleteTagCateMap(@Param("cateCode") String cateCode) {
        return mapper.deleteTagCateMap(cateCode);
    }
    
    @Override
    public long insertTagCateMap(@Param("cateCode") String cateCode,@Param("tagCode") String tagCode) {
        return mapper.insertTagCateMap(cateCode,tagCode);
    }

}
