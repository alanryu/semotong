package kr.co.ucomp.web.mbm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.AdminUserSearchDto;
import kr.co.ucomp.web.mbm.entity.AdminUserEntity;

@Mapper
public interface AdminUserMapper {
	List<AdminUserEntity> getList(AdminUserSearchDto param);

	Long getListCount(AdminUserSearchDto param);
	
	AdminUserEntity getDetail(@Param("id") int param);

	AdminUserDto getDetailById(@Param("userId") String userId);
	
	long create(AdminUserEntity param);

	long update(AdminUserEntity param);
	
	long updatePwd(AdminUserEntity param);

	long del(@Param("id") int param);
	
}
