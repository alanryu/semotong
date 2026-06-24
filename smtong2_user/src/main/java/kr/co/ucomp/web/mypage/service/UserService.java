package kr.co.ucomp.web.mypage.service;


import kr.co.ucomp.web.mypage.dto.UserDTO;

public interface UserService {
	boolean createUser(UserDTO user);
	boolean updateUser(UserDTO user);
	UserDTO getUserById(long id);
	UserDTO findInactiveUserByEmail(String email, String active);
	UserDTO getUserByKakaoId(String kakaoId);
	int deleteUserToggle(UserDTO user);
	int reJoinUserToggle(UserDTO user);
	int updateChannel(UserDTO userDto);
}
