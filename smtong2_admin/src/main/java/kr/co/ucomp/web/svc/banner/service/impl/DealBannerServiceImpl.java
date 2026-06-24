package kr.co.ucomp.web.svc.banner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.svc.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.svc.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.svc.banner.entity.MainDealMstEntity;
import kr.co.ucomp.web.svc.banner.mapper.DealBannerMapper;
import kr.co.ucomp.web.svc.banner.service.DealBannerService;

import java.util.List;

@Component
@Service
public class DealBannerServiceImpl implements DealBannerService {

    /* ============================ Main Deal Mst SQL ============================ */

    @Autowired
    DealBannerMapper dealBannerMapper;
    
    @Override
	public long listCount(MainDealMstDto param) {
		return dealBannerMapper.listCount(param);
	}

    @Override
    public List<MainDealMstEntity> mainDealMstList(MainDealMstDto param) {
        return dealBannerMapper.mainDealMstList(param);
    }

    @Override
    public MainDealMstEntity mainDealMst(long id) {
        return dealBannerMapper.mainDealMst(id);
    }

    @Override
    public long insertMainDealMst(MainDealMstEntity param) {
        return dealBannerMapper.insertMainDealMst(param);
    }

    @Override
    public long updateMainDealMst(MainDealMstEntity param) {
        return dealBannerMapper.updateMainDealMst(param);
    }

    @Override
    public long deleteMainDealMst(long id) {
        return dealBannerMapper.deleteMainDealMst(id);
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
    public long insertMainDealBanner(MainDealBannerEntity param) {
        return dealBannerMapper.insertMainDealBanner(param);
    }

    @Override
    public long updateMainDealBanner(MainDealBannerEntity param) {
        return dealBannerMapper.updateMainDealBanner(param);
    }

    @Override
    public long deleteMainDealBanner(long id) {
        return dealBannerMapper.deleteMainDealBanner(id);
    }

	@Override
	public List<MainDealBannerEntity> mainPageDealBanner(MainDealBannerDto dto) {
		return dealBannerMapper.mainPageDealBanner(dto);
	}

	@Override
	public void deleteMainDealBannerMstId(long id) {
		dealBannerMapper.deleteMainDealBannerMstId(id);
	}

    /* ============================================================================== */
}
