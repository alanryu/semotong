package kr.co.ucomp.web.plan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.plan.dto.RecomPlanMngSearchDto;
import kr.co.ucomp.web.plan.entity.RecomPlanEntity;
import kr.co.ucomp.web.plan.entity.RecomPlanPlanListEntity;

@Mapper
public interface RecomPlanMngMapper {
	
	  List<RecomPlanEntity> infolist(RecomPlanMngSearchDto param);

	  long infolistCount(RecomPlanMngSearchDto param);

	  RecomPlanEntity infoDetail(@Param("id") int id);

	
}
