package kr.co.ucomp.common.auth.oauth.service;


import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.ucomp.common.auth.oauth.dto.KakaoOAuthTokenDTO;
import kr.co.ucomp.common.auth.oauth.dto.KakaoOAuthUserDTO;
import kr.co.ucomp.common.auth.oauth.dto.NaverOAuthTokenDTO;
import kr.co.ucomp.web.mypage.dto.UserDTO;

@Service
public interface OAuthService {

	public KakaoOAuthTokenDTO getAccessToken(String code, String kakaoRedirectUri);

	public Map<String, Object> getKakaoUser(String accessToken);

	public UserDTO createUserByKakaoUser(KakaoOAuthUserDTO kakaoOAuthUserDTO, Map<String,Object> map);

	public Map<String, Object> updateKakaoTermsCol(String accessToken);

	public boolean disconnectKakaoUser(String kakaoId);
	
	public boolean sanchoDisconnectKakaoUser(String kakaoId);
	
	
	//02.14 sancho
	public NaverOAuthTokenDTO getNaverAccessToken(String code);
	
	public Map<String, Object> getNaverUser(String accessToken);
}
