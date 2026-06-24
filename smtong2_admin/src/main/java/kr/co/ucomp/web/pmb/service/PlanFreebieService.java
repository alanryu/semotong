package kr.co.ucomp.web.pmb.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.PlanFreebieSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanFreebieEntity;
import kr.co.ucomp.web.pmb.entity.PlanFreebieMappingEntity;


public interface PlanFreebieService {
     // ========================== 사은품 정보 관리 ================================
    List<PlanFreebieEntity> infolist(PlanFreebieSearchDto param);

    long infolistCount(PlanFreebieSearchDto param);
    

    PlanFreebieEntity infoDetail(@Param("id") int id);

    long createInfo(PlanFreebieEntity param);

    long updateInfo(PlanFreebieEntity param);

    long deleteInfo(@Param("id") int id);
    
    
    
    // ========================== 사은품 요금제 매핑 정보 관리 ================================
    List<PlanFreebieMappingEntity> maplist(PlanFreebieSearchDto param);

    long maplistCount(PlanFreebieSearchDto param);

    PlanFreebieMappingEntity mapDetail(@Param("id") int id);

    long createmap(PlanFreebieMappingEntity param);

    long updatemap(PlanFreebieMappingEntity param);
    
    long updatemapOrder(PlanFreebieMappingEntity param);
    

    long deletemap(@Param("id") int id);   
    
    List<PlanFreebieMappingEntity> maplistAll(PlanFreebieSearchDto param);
    
}
