package kr.co.ucomp.web.plan.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.plan.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.plan.entity.PlanBenefitEntity;
import kr.co.ucomp.web.plan.entity.PlanBenefitMappingEntity;


public interface PlanBenefitService {
     // ========================== 베네핏 정보 관리 ================================
    List<PlanBenefitEntity> infolist(PlanBenefitSearchDto param);

    long infolistCount(PlanBenefitSearchDto param);
    

    PlanBenefitEntity infoDetail(@Param("id") int id);

    long createInfo(PlanBenefitEntity param);

    long updateInfo(PlanBenefitEntity param);

    long deleteInfo(@Param("id") int id);
    
    // ========================== 베네핏 요금제 매핑 정보 관리 ================================
    List<PlanBenefitMappingEntity> maplist(PlanBenefitSearchDto param);

    long maplistCount(PlanBenefitSearchDto param);

    PlanBenefitMappingEntity mapDetail(@Param("id") int id);

    long createmap(PlanBenefitMappingEntity param);

    long updatemap(PlanBenefitMappingEntity param);

    long deletemap(@Param("id") int id);   
    
    List<PlanBenefitMappingEntity> maplistAll(PlanBenefitSearchDto param);
    
}
