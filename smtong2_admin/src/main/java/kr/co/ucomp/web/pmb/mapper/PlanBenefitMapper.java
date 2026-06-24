package kr.co.ucomp.web.pmb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.pmb.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanBenefitEntity;
import kr.co.ucomp.web.pmb.entity.PlanBenefitMappingEntity;

import java.util.List;

@Mapper
public interface PlanBenefitMapper {

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

  List<PlanBenefitMappingEntity> mapDetailBenefit(@Param("id") int id);

  long createmap(PlanBenefitMappingEntity param);

  long updatemap(PlanBenefitMappingEntity param);

  long updatemapOrder(PlanBenefitMappingEntity param);

  long deletemap(@Param("id") int id);

  long deletemapBenefit(@Param("id") int id);

  List<PlanBenefitMappingEntity> maplistAll(PlanBenefitSearchDto param);

  long deleteBenefit(@Param("id") int id, @Param("modifiedId") int modifiedId);
}
