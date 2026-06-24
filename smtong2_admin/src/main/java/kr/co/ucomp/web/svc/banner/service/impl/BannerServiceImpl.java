package kr.co.ucomp.web.svc.banner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.svc.banner.entity.BannerEntity;
import kr.co.ucomp.web.svc.banner.mapper.BannerMapper;
import kr.co.ucomp.web.svc.banner.service.BannerService;

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
    public BannerEntity getDetail(long id) {
        return mapper.getDetail(id);
    }

    @Override
    public long create(BannerEntity param) {
        return mapper.create(param);
    }

    @Override
    public long update(BannerEntity param) {
        return mapper.update(param);
    }

    @Override
    public long delete(long id) {
        return mapper.delete(id);
    }

	@Override
	public List<BannerEntity> listWithoutLimit(BannerSearchDto dto) {
		return mapper.listWithoutLimit(dto);
	}

	@Override
	public int getUseCnt(BannerEntity entity) {
		return mapper.getUseCnt(entity);
	}



}
