package kr.co.ucomp.web.pmb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.searchPlanZzimDto;
import kr.co.ucomp.web.pmb.entity.PlanZzimEntity;

import java.util.List;

@Mapper
public interface PlanZzimMapper {

    List<PlanZzimEntity> getlist(searchPlanZzimDto param);
    
    long create(PlanZzimEntity param);

    long delete(@Param("userId") int userId,@Param("prodId") int prodId);

}
