package kr.co.ucomp.web.csm.banner.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;

import java.util.List;

@Mapper
public interface BannerMapper {


    List<BannerEntity> list(BannerSearchDto param);
    
    long listCount(BannerSearchDto param);

    BannerEntity getDetail(@Param("id") int id);



}
