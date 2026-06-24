package kr.co.ucomp.web.csm.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.entity.SemotongBaekseoEntity;

import java.util.List;

public interface SemotongBaekseoSevice {

    List<SemotongBaekseoEntity> getListBaekseo(SemotongBaekseoDto param);
    
    Long getListBaekseoCount(SemotongBaekseoDto param);

    SemotongBaekseoEntity getBaekseo(SemotongBaekseoDto param);

    long insertBaekseo (SemotongBaekseoEntity param);

    long updateBaekseo (SemotongBaekseoEntity param);

    long deleteBaekseo (SemotongBaekseoEntity param);
    
    long updateDisplaySp(SemotongBaekseoEntity param);

}
