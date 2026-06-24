package kr.co.ucomp.web.mbm.service.impl;

import kr.co.ucomp.web.mbm.dto.UserSearchDTO;
import kr.co.ucomp.web.mbm.entity.UserEntity;
import kr.co.ucomp.web.mbm.mapper.UserMapper;
import kr.co.ucomp.web.mbm.service.UserService;
import lombok.AllArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;


	/**
	 * 사용자 리스트 조회
	 * @param param
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> getList(UserSearchDTO param) {
		List<UserEntity> list = mapper.getList(param);
		
		return list;
	}
	
	/**
	 * 사용자 리스트 조회 count
	 */
	@Override
	@Transactional(readOnly = true)
	public long getListCount(UserSearchDTO param) {
		long count = mapper.getListCount(param);
		return count;
	}
	
	/**
	 * 사용자 상태값 업데이트
	 */
    @Override
    public int updateState(UserEntity param) { 
    	return mapper.updateState(param);
    }

    /**
     * 사용자 id 로 사용자 조회
     */
	@Override
	public UserEntity getUserUser(@Param("id") int param) {
		UserEntity result = mapper.getUserUser(param);
		return result;
	}

	/**
	 * 사용자 카카오 id 로 사용자 조회
	 */
	@Override
	public UserEntity getUserByKakaoId(UserSearchDTO param) {
		UserEntity result = mapper.getUserByKakaoId(param);
		return result;
	}

	@Override
	public int updateChannel(UserEntity entity) {
		return mapper.updateChannel(entity);
	}
}
