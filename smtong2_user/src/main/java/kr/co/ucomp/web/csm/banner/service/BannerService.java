package kr.co.ucomp.web.csm.banner.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;

import java.util.List;

public interface BannerService {


    List<BannerEntity> list(BannerSearchDto param);
    
    long listCount(BannerSearchDto param);

    BannerEntity getDetail(@Param("id") int id);


}
