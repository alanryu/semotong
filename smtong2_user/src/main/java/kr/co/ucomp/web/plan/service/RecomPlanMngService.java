package kr.co.ucomp.web.plan.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.plan.dto.RecomPlanMngSearchDto;
import kr.co.ucomp.web.plan.entity.RecomPlanEntity;
import kr.co.ucomp.web.plan.entity.RecomPlanPlanListEntity;

public interface RecomPlanMngService {

	List<RecomPlanEntity> infolist(RecomPlanMngSearchDto param);

    long infolistCount(RecomPlanMngSearchDto param);

    RecomPlanEntity infoDetail(@Param("id") int id);




}
