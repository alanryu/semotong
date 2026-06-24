package kr.co.ucomp.web.csm.onetoone.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.onetoone.dto.OnetooneDto;
import kr.co.ucomp.web.csm.onetoone.entity.OnetooneEntity;
import kr.co.ucomp.web.csm.onetoone.mapper.OnetooneMapper;
import kr.co.ucomp.web.csm.onetoone.service.OnetooneService;

import java.util.List;

@Component
@Service
public class OnetooneServiceImpl implements OnetooneService {

    @Autowired OnetooneMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<OnetooneEntity> oneToOneList(OnetooneDto param) {
        return mapper.oneToOneList(param);
    }

    @Override
    @Transactional(readOnly = true)
    public OnetooneEntity oneToOne(long id) {
        return mapper.oneToOne(id);
    }

    @Override
    public long insertOneToOne(OnetooneEntity param) {
        return mapper.insertOneToOne(param);
    }

    @Override
    public long updateOneToOne(OnetooneEntity param) {
        return mapper.updateOneToOne(param);
    }

    @Override
    public long deleteOneToOne(long id) {
        return mapper.deleteOneToOne(id);
    }

}
