package kr.co.ucomp.web.svc.banner.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.banner.dto.BannerPlanCreateDTO;
import kr.co.ucomp.web.svc.banner.dto.BannerPlanSearchDTO;
import kr.co.ucomp.web.svc.banner.entity.BannerPlanEntity;

import java.util.List;

public interface BannerPlanService {


    List<BannerPlanEntity> list(BannerPlanSearchDTO param);
    
    long listCount(BannerPlanSearchDTO param);

    long create(BannerPlanEntity param);

    long delete(@Param("id") int id);

	List<BannerPlanEntity> bannerPlanList(BannerPlanSearchDTO dto);

	BannerPlanEntity bannerPlan(BannerPlanSearchDTO bannerPlanSearchDTO);

	void update(BannerPlanEntity bannerPlanEntity);



}
