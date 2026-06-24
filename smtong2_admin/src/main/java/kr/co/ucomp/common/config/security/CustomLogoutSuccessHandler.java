package kr.co.ucomp.common.config.security;

import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.security.core.Authentication;


@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("[CustomLogoutSuccessHandler] :: 로그아웃");
        HttpSession session = request.getSession();
        session.removeAttribute("loginUser");
		session.removeAttribute("loginUserNm");
		
        response.sendRedirect("/login");
    }
}