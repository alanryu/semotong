package kr.co.ucomp.web.svc.banner.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.svc.banner.entity.BannerEntity;

import java.util.List;

@Mapper
public interface BannerMapper {


    List<BannerEntity> list(BannerSearchDto param);
    
    long listCount(BannerSearchDto param);

    BannerEntity getDetail(@Param("id") long id);

    long create(BannerEntity param);

    long update(BannerEntity param);

    long delete(@Param("id") long id);

	List<BannerEntity> listWithoutLimit(BannerSearchDto dto);

	int getUseCnt(BannerEntity entity);


}
