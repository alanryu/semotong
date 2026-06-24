package kr.co.ucomp.web.csm.onetoone.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.onetoone.dto.OnetooneDto;
import kr.co.ucomp.web.csm.onetoone.entity.OnetooneEntity;

import java.util.List;

@Mapper
public interface OnetooneMapper {

    List<OnetooneEntity> oneToOneList(OnetooneDto param);

    OnetooneEntity oneToOne(@Param("id") long id);

    long insertOneToOne(OnetooneEntity param);

    long updateOneToOne(OnetooneEntity param);

    long deleteOneToOne(@Param("id") long id);

}
