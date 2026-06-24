package kr.co.ucomp.web.svc.recomplan.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.recomplan.dto.RecomPlanMngSearchDto;
import kr.co.ucomp.web.svc.recomplan.entity.RecomPlanEntity;
import kr.co.ucomp.web.svc.recomplan.entity.RecomPlanPlanListEntity;

public interface RecomPlanMngService {
    // ========================== 추천요금제 정보 관리 ================================
    List<RecomPlanEntity> infolist(RecomPlanMngSearchDto param);

    long infolistCount(RecomPlanMngSearchDto param);

    RecomPlanEntity infoDetail(@Param("id") int id);

    long createInfo(RecomPlanEntity param);

    long updateInfo(RecomPlanEntity param);

    long deleteInfo(@Param("id") int id);

    // ========================== 추천요금제 요금제 리스트 정보 관리 ================================
    List<RecomPlanPlanListEntity> maplist(RecomPlanMngSearchDto param);

    long maplistCount(RecomPlanMngSearchDto param);

    RecomPlanPlanListEntity mapDetail(@Param("id") int id);

    long createmap(RecomPlanPlanListEntity param);

    long updatemap(RecomPlanPlanListEntity param);

    long updatemapOrder(RecomPlanPlanListEntity param);

    long deletemap(@Param("id") int id);
    
    long deletemapListByMngId(@Param("id") int id);
    



}
