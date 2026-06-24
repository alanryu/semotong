package kr.co.ucomp.web.csm.banner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;
import kr.co.ucomp.web.csm.banner.mapper.BannerMapper;
import kr.co.ucomp.web.csm.banner.service.BannerService;

import java.util.List;

@Component
@Service
public class BannerServiceImpl implements BannerService {


    @Autowired
    BannerMapper mapper;

    @Override
    public List<BannerEntity> list(BannerSearchDto param) {
        return mapper.list(param);
    }

    @Override
    public long listCount(BannerSearchDto param) {
        return mapper.listCount(param);
    }

    
    @Override
    public BannerEntity getDetail(int id) {
        return mapper.getDetail(id);
    }


}
