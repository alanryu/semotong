package kr.co.ucomp.web.mbm.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.mbm.dto.AdminMemoDto;
import kr.co.ucomp.web.mbm.entity.AdminMemoEntity;

import java.util.List;

@Mapper
public interface AdminMemoMapper {

    List<AdminMemoEntity> adminMemoList(AdminMemoDto param);

    AdminMemoEntity adminMemo(@Param("id") long id);

    long insertAdminMemo(AdminMemoEntity param);

    long updateAdminMemo(AdminMemoEntity param);

    long deleteAdminMemo(@Param("id") long id);

}
