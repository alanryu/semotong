package kr.co.ucomp.web.svc.banner.service;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.svc.banner.entity.BannerEntity;

import java.util.List;

public interface BannerService {


    List<BannerEntity> list(BannerSearchDto param);
    
    long listCount(BannerSearchDto param);

    BannerEntity getDetail(@Param("id") long id);

    long create(BannerEntity param);

    long update(BannerEntity param);

    long delete(@Param("id") long id);

	List<BannerEntity> listWithoutLimit(BannerSearchDto dto);

	int getUseCnt(BannerEntity entity);

}
