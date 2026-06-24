package kr.co.ucomp.web.csm.banner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.csm.banner.dto.BannerPlanCreateDTO;
import kr.co.ucomp.web.csm.banner.dto.BannerPlanSearchDTO;
import kr.co.ucomp.web.csm.banner.entity.BannerPlanEntity;
import kr.co.ucomp.web.csm.banner.mapper.BannerPlanMapper;
import kr.co.ucomp.web.csm.banner.service.BannerPlanService;

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


}
