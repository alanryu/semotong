package kr.co.ucomp.web.csm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.entity.SemotongBaekseoEntity;

@Mapper
public interface SemotongBaekseoMapper {

    List<SemotongBaekseoEntity> getListBaekseo(SemotongBaekseoDto param);
    
    Long getListBaekseoCount(SemotongBaekseoDto param);

    SemotongBaekseoEntity getBaekseo(SemotongBaekseoDto param);

    long insertBaekseo (SemotongBaekseoEntity param);

    long updateBaekseo (SemotongBaekseoEntity param);

    long deleteBaekseo (SemotongBaekseoEntity param);
    
    long updateDisplaySp(SemotongBaekseoEntity param);
}
