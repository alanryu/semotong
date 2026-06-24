package kr.co.ucomp.web.pmb.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.searchPlanZzimDto;
import kr.co.ucomp.web.pmb.entity.PlanZzimEntity;

import java.util.List;

public interface PlanZzimService {

    List<PlanZzimEntity> getlist(searchPlanZzimDto param);

    long create(PlanZzimEntity param);

    long delete(@Param("userId") int userId,@Param("prodId") int prodId);

}
