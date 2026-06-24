package kr.co.ucomp.web.csm.banner.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.banner.dto.BannerPlanCreateDTO;
import kr.co.ucomp.web.csm.banner.dto.BannerPlanSearchDTO;
import kr.co.ucomp.web.csm.banner.entity.BannerPlanEntity;

import java.util.List;

public interface BannerPlanService {


    List<BannerPlanEntity> list(BannerPlanSearchDTO param);
    
    long listCount(BannerPlanSearchDTO param);



}
