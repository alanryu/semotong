package kr.co.ucomp.common.auth.oauth.controller;

import java.util.Enumeration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.auth.oauth.dto.NaverOAuthTokenDTO;
import kr.co.ucomp.common.auth.oauth.service.OAuthService;
import kr.co.ucomp.common.config.LoginRequired;
import kr.co.ucomp.common.encrypt.DaouEncrypt;
import kr.co.ucomp.web.mypage.service.UserService;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class NidOAuthController {
	private final OAuthService oauthService;
	private final UserService userService;
	
	/*
	naver:
	  auth:
	    uri: https://nid.naver.com/oauth2.0/authorize
	  token:
	    uri: https://nid.naver.com/oauth2.0/token
	  profile:
	    uri: https://openapi.naver.com/v1/nid/me
	  client:
	    id: 9argipQqSTYgmK3gI3pB
	    secret: SUK6kQb6xZ
    
	 */
	
	@Autowired PasswordEncoder passwordEncoder; // DI	

	@GetMapping("/naver/callback")
	public String kakaoCallback(@RequestParam("code") String code,HttpSession session,HttpServletRequest request,HttpServletResponse response, RedirectAttributes redirectAttributes, Model model ){
		
		try {
			String nId = (String) session.getAttribute("naverId");
			if(nId == null || nId.equals("")) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				//System.out.println("code:" + code);
				
				// 1. 액세스 토큰 획득
				NaverOAuthTokenDTO tokenDTO = oauthService.getNaverAccessToken(code);
				
				//System.out.println("tokenDTO:" + tokenDTO);
				
				// 2. Naver 사용자 정보 획득
				Map<String, Object> naverUser = oauthService.getNaverUser(tokenDTO.getAccessToken());
				
				String resNaverId = (String)naverUser.get("id");
				
				// --------------
				session.setAttribute("naverId", resNaverId);
				// --------------
				
				System.out.println("-=session=-");
				Enumeration<String> attributeNames = session.getAttributeNames();
				while (attributeNames.hasMoreElements()) {
					String attributeName = attributeNames.nextElement();
					Object attributeValue = session.getAttribute(attributeName);
					System.out.println("attributeName:" + attributeName + "//" + "value:" + attributeValue);
				}
				System.out.println("-=session=-");
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Model model = new ;
		model.addAttribute("callback"	, "Y");
		
		//return "redirect:/";
		//return "redirect:/users/login";
		return "pages/mypage/myscash";

	}

}
