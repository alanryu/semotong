package kr.co.ucomp.web.csm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.dto.OnetooneDto;
import kr.co.ucomp.web.csm.entity.OnetooneEntity;
import kr.co.ucomp.web.csm.mapper.OnetooneMapper;
import kr.co.ucomp.web.csm.service.OnetooneService;

import java.util.List;

@Component
@Service
public class OnetooneServiceImpl implements OnetooneService {

    @Autowired OnetooneMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<OnetooneEntity> getList(OnetooneDto param) {
        return mapper.getList(param);
    }
    
	public long getListCount(OnetooneDto param)  {
		long count = mapper.getListCount(param);
		return  count;
	}
	

    @Override
    @Transactional(readOnly = true)
    public OnetooneEntity getDetail(int id) {
        return mapper.getDetail(id);
    }


    @Override
    public long update(OnetooneEntity param) {
        return mapper.update(param);
    }

    @Override
    public long delete(int id) {
        return mapper.delete(id);
    }

}
