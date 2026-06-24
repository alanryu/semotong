package kr.co.ucomp.web.mbm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.mbm.dto.AdminMemoDto;
import kr.co.ucomp.web.mbm.entity.AdminMemoEntity;
import kr.co.ucomp.web.mbm.mapper.AdminMemoMapper;
import kr.co.ucomp.web.mbm.service.AdminMemoService;

import java.util.List;

@Component
@Service
public class AdminMemoServiceImpl implements AdminMemoService {

    @Autowired AdminMemoMapper adminMemoMapper;

    @Override
    public List<AdminMemoEntity> adminMemoList(AdminMemoDto param) {
        return adminMemoMapper.adminMemoList(param);
    }

    @Override
    public AdminMemoEntity adminMemo(long id) {
        return adminMemoMapper.adminMemo(id);
    }

    @Override
    public long insertAdminMemo(AdminMemoEntity param) {
        return adminMemoMapper.insertAdminMemo(param);
    }

    @Override
    public long updateAdminMemo(AdminMemoEntity param) {
        return adminMemoMapper.updateAdminMemo(param);
    }

    @Override
    public long deleteAdminMemo(long id) {
        return adminMemoMapper.deleteAdminMemo(id);
    }
}
