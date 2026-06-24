package kr.co.ucomp.web.svc.banner.service;


import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.svc.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.svc.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.svc.banner.entity.MainDealMstEntity;

import java.util.List;

public interface DealBannerService {

    /* ============================ Main Deal Mst SQL ============================ */

	long listCount(MainDealMstDto param);
	
    List<MainDealMstEntity> mainDealMstList(MainDealMstDto param);

    MainDealMstEntity mainDealMst(@Param("id") long id);

    long insertMainDealMst(MainDealMstEntity param);

    long updateMainDealMst(MainDealMstEntity param);

    long deleteMainDealMst(@Param("id") long id);

    /* ============================================================================== */



    /* ============================ Main Deal Banner SQL ============================ */

    List<MainDealBannerEntity> mainDealBannerList(MainDealBannerDto param);

    MainDealBannerEntity mainDealBanner(@Param("id") long id);

    long insertMainDealBanner(MainDealBannerEntity param);

    long updateMainDealBanner(MainDealBannerEntity param);

    long deleteMainDealBanner(@Param("id") long id);

	List<MainDealBannerEntity> mainPageDealBanner(MainDealBannerDto dto);

	void deleteMainDealBannerMstId(@Param("id")long id);

    /* ============================================================================== */

}
