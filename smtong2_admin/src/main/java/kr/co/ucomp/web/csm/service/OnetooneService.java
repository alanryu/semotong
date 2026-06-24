package kr.co.ucomp.web.csm.service;


import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.OnetooneDto;
import kr.co.ucomp.web.csm.entity.OnetooneEntity;

import java.util.List;

public interface OnetooneService {

    List<OnetooneEntity> getList(OnetooneDto param);
    
    long getListCount(OnetooneDto param);    

    OnetooneEntity getDetail(@Param("id") int id);    

    long update(OnetooneEntity param);

    long delete(@Param("id") int id);

}
