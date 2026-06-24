package kr.co.ucomp.web.csm.banner.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.banner.dto.BannerPlanCreateDTO;
import kr.co.ucomp.web.csm.banner.dto.BannerPlanSearchDTO;
import kr.co.ucomp.web.csm.banner.entity.BannerPlanEntity;

import java.util.List;

@Mapper
public interface BannerPlanMapper {


    List<BannerPlanEntity> list(BannerPlanSearchDTO param);
    
    long listCount(BannerPlanSearchDTO param);

}
