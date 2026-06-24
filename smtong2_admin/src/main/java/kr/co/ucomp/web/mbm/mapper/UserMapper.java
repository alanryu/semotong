package kr.co.ucomp.web.mbm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.mbm.dto.UserSearchDTO;
import kr.co.ucomp.web.mbm.entity.UserEntity;

import java.util.List;

@Mapper
public interface UserMapper {

	List<UserEntity> getList(UserSearchDTO param);
	
	long getListCount(UserSearchDTO param);

	UserEntity getUserUser(@Param("id") int param);

    UserEntity getUserByKakaoId(UserSearchDTO param);

    int updateState(UserEntity param);

	int updateChannel(UserEntity entity);
}
