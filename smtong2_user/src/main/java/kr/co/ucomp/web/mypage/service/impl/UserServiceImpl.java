package kr.co.ucomp.web.mypage.service.impl;

import kr.co.ucomp.common.auth.oauth.service.OAuthService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.mypage.mapper.UserMapper;
import kr.co.ucomp.web.mypage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;


    @Override
    @Transactional
    public boolean createUser(UserDTO user) {
        return userMapper.insertUser(user) == 1;
    }
    
    @Override
    @Transactional
    public boolean updateUser(UserDTO user) {
        return userMapper.updateUser(user) == 1;
    }

    @Override
    public UserDTO getUserById(long id) {
        return userMapper.getUserById(id);
    }

    @Override
    public UserDTO findInactiveUserByEmail(String email, String active) {
        return userMapper.findUserEntityByEmailAndDisable(email, active);
    }

    @Override
    public UserDTO getUserByKakaoId(String kakaoId) {
        return userMapper.getUserByKakaoId(kakaoId);
    }

    @Override
    public int deleteUserToggle(UserDTO user) {
		//if ( user.getMemberStat().equals("ACTIVE") ){			//왜 이렇게 만들었는지 짐작이 않감. 뭔가 토글식으로 자동으로 켰다/껐다를 구상 한듯 그러나 현재구조와 맞지안아 밑에 reJoin 별도 만듬
    	if ( user.getMemberStat().equals("DROP") ){		
			int rtn = userMapper.deleteUserToggle(user);
			return rtn;
		}else if (user.getMemberStat().equals("STOP")){			//뭐지 이건?
		    return -1;
		} else {
		    return -2;
		}
	}

	@Override
	public int reJoinUserToggle(UserDTO user) {
		return userMapper.deleteUserToggle(user);
	}

	@Override
	public int updateChannel(UserDTO userDto) {
		return userMapper.updateChannel(userDto);
	}

}
