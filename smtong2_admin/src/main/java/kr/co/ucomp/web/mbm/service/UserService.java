package kr.co.ucomp.web.mbm.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.mbm.dto.UserSearchDTO;
import kr.co.ucomp.web.mbm.entity.UserEntity;

public interface UserService {


	List<UserEntity> getList(UserSearchDTO param);
	
	long getListCount(UserSearchDTO param);

	UserEntity getUserUser(@Param("id") int param);

    UserEntity getUserByKakaoId(UserSearchDTO param);

    int updateState(UserEntity param);

	int updateChannel(UserEntity entity);
	
	
}
