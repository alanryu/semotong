package kr.co.ucomp.common.config;


import java.util.Map;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Aspect
@Component
public class LoginRequiredAspect {

    @Before("@within(kr.co.ucomp.common.config.LoginRequired) || @annotation(kr.co.ucomp.common.config.LoginRequired)")
    public void checkLogin() {
        // 현재 로그인 정보를 가져옴
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		if(request != null) {
			HttpSession session = request.getSession();
			
			Map<String, Object> login = (Map<String, Object>)session.getAttribute("kakaoUser");
			if(login == null) {
				 throw new AccessDeniedException("로그인 해 주세요.");
			}
		}
    }
    
    
    private boolean isAnonymous(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map((authority) -> authority.getAuthority())
            .anyMatch((authority) -> authority.equals("ROLE_ANONYMOUS"));
    }
    
}