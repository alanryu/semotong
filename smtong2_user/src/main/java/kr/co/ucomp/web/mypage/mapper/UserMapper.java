package kr.co.ucomp.web.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.mypage.dto.UserDTO;

import java.util.Map;

@Mapper
public interface UserMapper {
    int insertUser(UserDTO user);
    
    int updateUser(UserDTO user);

    UserDTO getUserById(long id);

    UserDTO findUserEntityByEmailAndDisable(String email, String activeYn);

    UserDTO getUserByKakaoId(String kakaoUserId);

    //int deleteUserToggle(String memberStat, long id);	// 이거 않됨... ㅜ.ㅜ
    int deleteUserToggle(UserDTO param);

	int updateChannel(UserDTO userDto);

}
