package kr.co.ucomp.web.csm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.PolicyContentSearchDto;
import kr.co.ucomp.web.csm.entity.PolicyContentEntity;

import java.util.List;

@Mapper
public interface PolicyContentMapper {
  List<PolicyContentEntity> getList(PolicyContentSearchDto param);

  Long getListCount(PolicyContentSearchDto param);

  PolicyContentEntity getDetail(@Param("id") int param);

  long create(PolicyContentEntity param);

  long update(PolicyContentEntity param);

  long delete(@Param("id") int param);


}
