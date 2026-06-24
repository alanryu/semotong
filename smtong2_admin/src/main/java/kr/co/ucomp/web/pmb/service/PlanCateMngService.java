package kr.co.ucomp.web.pmb.service;


import kr.co.ucomp.web.pmb.dto.PlanCateMngDto;
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.entity.PlanCateMngEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface PlanCateMngService {

    List<PlanCateMngEntity> getTaglist(PlanCateMngDto param);
    
    List<PlanCateMngEntity> getCatelist(PlanCateMngDto param);
    
    long getCateListCount(PlanCateMngDto param);    
    
    long create(PlanCateMngEntity param);
    
    long update(PlanCateMngEntity param);

    long delete(PlanCateMngEntity param);
    
    long updatePlanTag(PlanUpdateDto param);
    
    String maxTagCode();
    
    String maxCateCode();
    
    long deleteTagCateMap(@Param("cateCode") String cateCode);
    
    long insertTagCateMap(@Param("cateCode") String cateCode,@Param("tagCode") String tagCode);

}
