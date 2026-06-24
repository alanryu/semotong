package kr.co.ucomp.web.csm.banner.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.csm.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.csm.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.csm.banner.entity.MainDealMstEntity;

import java.util.List;

@Mapper
public interface DealBannerMapper {

    /* ============================ Main Deal Mst SQL ============================ */
    List<MainDealMstEntity> mainDealMstList(MainDealMstDto param);
    
    MainDealMstEntity mainDealMst(@Param("id") long id);
    
    MainDealMstEntity mainDealMstRec(MainDealMstDto params);
    
    /* ============================================================================== */


    /* ============================ Main Deal Banner SQL ============================ */

    List<MainDealBannerEntity> mainDealBannerList(MainDealBannerDto param);

    MainDealBannerEntity mainDealBanner(@Param("id") long id);

	List<MainDealBannerEntity> mainPageDealBanner(MainDealBannerDto param);

    /* ============================================================================== */

}
