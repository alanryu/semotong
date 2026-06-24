package kr.co.ucomp.web.svc.randing.service.impl;

import lombok.AllArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.randing.dto.InternetRdpMngSearchDto;
import kr.co.ucomp.web.svc.randing.entity.InternetRdpMngEntity;
import kr.co.ucomp.web.svc.randing.mapper.InternetRdpMngMapper;
import kr.co.ucomp.web.svc.randing.service.InternetRdpMngService;

import java.util.List;

@Service
@AllArgsConstructor
public class InternetRdpMngServiceImpl implements InternetRdpMngService {
    private InternetRdpMngMapper mapper;

    @Override
    public List<InternetRdpMngEntity> list(InternetRdpMngSearchDto searchDto) {
        return mapper.list(searchDto);
    }
    
    
    @Override
    public Long count(InternetRdpMngSearchDto InternetRdpMngSearchDto) {
        return mapper.count(InternetRdpMngSearchDto);
    }


	@Override
	public InternetRdpMngEntity getDetail(@Param("id") int id) {
		return mapper.getDetail(id);
	}

	@Override
	public long create(InternetRdpMngEntity param) {
		return mapper.create(param);
	}
	
	
	@Override
	public long update(InternetRdpMngEntity param) {
		return mapper.update(param);
	}
	
	@Override
	public long updateEx(InternetRdpMngEntity param) {
		return mapper.updateEx(param);
	}
	
	
	@Override
	public long delete(Integer param) {
		return mapper.delete(param);
	}

}
