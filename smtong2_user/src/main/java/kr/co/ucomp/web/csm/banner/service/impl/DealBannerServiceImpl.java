package kr.co.ucomp.web.csm.banner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.csm.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.csm.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.csm.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.csm.banner.entity.MainDealMstEntity;
import kr.co.ucomp.web.csm.banner.mapper.DealBannerMapper;
import kr.co.ucomp.web.csm.banner.service.DealBannerService;

import java.util.List;

@Component
@Service
public class DealBannerServiceImpl implements DealBannerService {

    /* ============================ Main Deal Mst SQL ============================ */

    @Autowired
    DealBannerMapper dealBannerMapper;

    @Override
    public List<MainDealMstEntity> mainDealMstList(MainDealMstDto param) {
        return dealBannerMapper.mainDealMstList(param);
    }

    @Override
    public MainDealMstEntity mainDealMst(long id) {
        return dealBannerMapper.mainDealMst(id);
    }
    
    @Override
	public MainDealMstEntity mainDealMstRec(MainDealMstDto params) {
    	return dealBannerMapper.mainDealMstRec(params);
	}

    /* ============================================================================== */



    /* ============================ Main Deal Banner SQL ============================ */

    @Override
    public List<MainDealBannerEntity> mainDealBannerList(MainDealBannerDto param) {
        return dealBannerMapper.mainDealBannerList(param);
    }

    @Override
    public MainDealBannerEntity mainDealBanner(long id) {
        return dealBannerMapper.mainDealBanner(id);
    }

	@Override
	public List<MainDealBannerEntity> mainPageDealBanner(MainDealBannerDto param) {
		return dealBannerMapper.mainPageDealBanner(param);
	}

    /* ============================================================================== */
}
