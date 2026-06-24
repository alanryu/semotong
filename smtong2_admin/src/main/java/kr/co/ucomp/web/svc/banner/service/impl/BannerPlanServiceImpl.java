package kr.co.ucomp.web.svc.banner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.banner.dto.BannerPlanCreateDTO;
import kr.co.ucomp.web.svc.banner.dto.BannerPlanSearchDTO;
import kr.co.ucomp.web.svc.banner.entity.BannerPlanEntity;
import kr.co.ucomp.web.svc.banner.mapper.BannerPlanMapper;
import kr.co.ucomp.web.svc.banner.service.BannerPlanService;

import java.util.List;

@Component
@Service
public class BannerPlanServiceImpl implements BannerPlanService {


    @Autowired
    BannerPlanMapper mapper;

    @Override
    public List<BannerPlanEntity> list(BannerPlanSearchDTO param) {
        return mapper.list(param);
    }

    @Override
    public long listCount(BannerPlanSearchDTO param) {
        return mapper.listCount(param);
    }

    
    @Override
    public long create(BannerPlanEntity param) {
        return mapper.create(param);
    }

    
    @Override
    public long delete(int id) {
        return mapper.delete(id);
    }

	@Override
	public List<BannerPlanEntity> bannerPlanList(BannerPlanSearchDTO dto) {
		return mapper.bannerPlanList(dto);
	}

	@Override
	public BannerPlanEntity bannerPlan(BannerPlanSearchDTO bannerPlanSearchDTO) {
		return mapper.bannerPlan(bannerPlanSearchDTO);
	}

	@Override
	public void update(BannerPlanEntity bannerPlanEntity) {
		mapper.update(bannerPlanEntity);
	}



}
