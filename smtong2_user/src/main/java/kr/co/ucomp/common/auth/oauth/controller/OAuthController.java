package kr.co.ucomp.common.auth.oauth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.auth.oauth.dto.KakaoOAuthTokenDTO;
import kr.co.ucomp.common.auth.oauth.service.OAuthService;
import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.util.SecurityContextUtil;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.mypage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oauthService;
    private final UserService userService;
    @Value("${kakao.client.redirect-uri}")
	String kakaoRedirectUri;
    
    @Autowired PasswordEncoder passwordEncoder; // DI
    
    @Autowired
    private RestTempletUtil restTempletUtil;

    @SuppressWarnings("unchecked")
	@GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code,HttpSession session,HttpServletRequest request,HttpServletResponse response, RedirectAttributes redirectAttributes ){
    	
        try {
            // 1. 액세스 토큰 획득
            KakaoOAuthTokenDTO tokenDTO = oauthService.getAccessToken(code, kakaoRedirectUri);
            
            // 2. 카카오 사용자 정보 획득
            Map<String, Object> kakaoUser = oauthService.getKakaoUser(tokenDTO.getAccessToken());
            
            // 카카오 로그인 시 이름이나 전화번호 없는 경우 처리
            String getKakaoUserInfoErrYn = (String) kakaoUser.get("errYn");
            if("Y".equals(getKakaoUserInfoErrYn)) {
            	String chkphoneNumber = (String) kakaoUser.get("chkphoneNumber");
            	String chkuserName = (String) kakaoUser.get("chkuserName");
            	if(StringUtils.isBlank(chkuserName)) {
            		redirectAttributes.addFlashAttribute("loginErrCode", "91");
                	redirectAttributes.addFlashAttribute("loginErr", "사용자 명이 없습니다.");
            	} else if(StringUtils.isBlank(chkphoneNumber)) {
            		redirectAttributes.addFlashAttribute("loginErrCode", "92");
                	redirectAttributes.addFlashAttribute("loginErr", "사용자 전화번호가 없습니다.");
            	}
            	
           	return "redirect:/users/login"; 
            }
            

            // 3. 응답 데이터 구성
            Map<String, Object> responseData = new HashMap<>();

            responseData.put("kakao", kakaoUser);

            // 4. 사용자 정보 조회
            Object kakaoId = kakaoUser.get("kakao_id");

            if (kakaoId != null) {
                UserDTO userInfo = userService.getUserByKakaoId(String.valueOf(kakaoId));
                
                if("ACTIVE".equals(userInfo.getMemberStat())) {
                	responseData.put("user", userInfo);
                    session.setAttribute("userInfo",userInfo);
                    session.setAttribute("kakaoUser",kakaoUser);
                    session.setAttribute("isLoginYn","Y");
                    String encodedPwd = passwordEncoder.encode(userInfo.getEmail()); //암호화 하는부분
                    SecurityContextUtil.setAuthentication(String.valueOf(kakaoId), encodedPwd, "USER");
                    
                    /* 플러스 채널 추가/차단여부 확인 */
                    String url = "https://kapi.kakao.com/v2/api/talk/channels";
                    Map<String,String> headerMap = new HashMap<String, String>();
                    headerMap.put("Authorization", "Bearer " + tokenDTO.getAccessToken());
                    
                    Map<String,String> bodyMap = new HashMap<String, String>();
					bodyMap.put("channel_ids", "_EwxoVn"); 
					bodyMap.put("channel_id_type","channel_public_id");
                    
                    Map<String, Object> result = restTempletUtil.sendRestApi("CHANNEL_CONFIRM", url, "POST", bodyMap, headerMap);
                    
                    /* 로그인 사용자가 채널추가되어있는 경우 업데이트 한다 */
                    if ( result.get("resultBody") != null ) {
                    	
                    	Map<String, Object> resultBody =  (Map<String, Object>) result.get("resultBody");
                    	List<Map<String, Object>> channels = (List<Map<String, Object>>) resultBody.get("channels");
                    	
                    	if ( channels.size() > 0 ) {
                    		String relation = MapUtils.getString(channels.get(0), "relation");
                    		UserDTO userDto = new UserDTO();
                    		userDto.setKakaoUserId(kakaoUser.get("kakao_id").toString());
                    		if ( StringUtils.equals("ADDED", relation) ) {
                        		userDto.setChannelYn(1);
                        	} else {
                        		userDto.setChannelYn(0);
                        	}
                    		int updRes = userService.updateChannel(userDto);
                    	}
                    }
                    
                } else {
                	redirectAttributes.addFlashAttribute("loginErrCode", "99");
                	redirectAttributes.addFlashAttribute("loginErr", "중지된 회원 입니다.");
                	return "redirect:/users/login"; 
                }
                
            } 
            
            
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> session prevPage" + session.getAttribute("prevPage") );
            
            if(session.getAttribute("prevPage") !=null) {
            	String prevuri = (String) session.getAttribute("prevPage");
            	if(StringUtils.isNoneBlank(prevuri) && !prevuri.contains("/users/login")) {
            		session.removeAttribute("prevPage");
            		return "redirect:"+prevuri; 
            		
            		
            	}
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "redirect:/";
    }

}
