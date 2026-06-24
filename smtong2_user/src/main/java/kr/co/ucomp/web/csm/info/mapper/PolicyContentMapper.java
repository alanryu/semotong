package kr.co.ucomp.web.csm.info.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.info.dto.PolicyContentSearchDto;
import kr.co.ucomp.web.csm.info.entity.PolicyContentEntity;

import java.util.List;

@Mapper
public interface PolicyContentMapper {
  List<PolicyContentEntity> getList(PolicyContentSearchDto param);

  Long getListCount(PolicyContentSearchDto param);

  PolicyContentEntity getPolicyContent(@Param("polSp") String polSp,@Param("polId") Integer polId);


}
