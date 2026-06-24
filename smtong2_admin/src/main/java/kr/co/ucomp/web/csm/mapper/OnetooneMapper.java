package kr.co.ucomp.web.csm.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.OnetooneDto;
import kr.co.ucomp.web.csm.entity.OnetooneEntity;

import java.util.List;

@Mapper
public interface OnetooneMapper {

    List<OnetooneEntity> getList(OnetooneDto param);
    
    long getListCount(OnetooneDto param);
    
    OnetooneEntity getDetail(@Param("id") int id);

    long update(OnetooneEntity param);

    long delete(@Param("id") int id);

}
