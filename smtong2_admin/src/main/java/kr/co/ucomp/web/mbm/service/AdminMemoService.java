package kr.co.ucomp.web.mbm.service;


import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.mbm.dto.AdminMemoDto;
import kr.co.ucomp.web.mbm.entity.AdminMemoEntity;

import java.util.List;

public interface AdminMemoService {

    List<AdminMemoEntity> adminMemoList(AdminMemoDto param);

    AdminMemoEntity adminMemo(@Param("id") long id);

    long insertAdminMemo(AdminMemoEntity param);

    long updateAdminMemo(AdminMemoEntity param);

    long deleteAdminMemo(@Param("id") long id);

}
