package kr.co.ucomp.common.config.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.service.AdminUserService;

@Component
public class CustomAuthenticationFailHandler implements AuthenticationFailureHandler { 
	
	@Autowired private AdminUserService adminUserService;
	//@Autowired PasswordEncoder pwEncoder;
	
	@Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		
		String errorMsg = "";
		PasswordEncoder pwdencoder = new BCryptPasswordEncoder();
		System.out.println(request.getParameter("username"));
		String userId = (String) request.getParameter("username");
		String pwd = (String) request.getParameter("password");
		String engPwd = pwdencoder.encode(pwd);
		
		AdminUserDto adminInfo = adminUserService.getDetailById(userId);
		
		if(adminInfo == null) {
			errorMsg = "91";
		} else {
			
			System.out.println(engPwd + ":::::::::::::" + adminInfo.getPassword());
			if("N".equals(adminInfo.getDisable())) {
				errorMsg = "92";	
			} else {
				if ( exception instanceof BadCredentialsException ) {
					errorMsg = "99";
					System.out.println(exception.getMessage());
				}		
			}
		}
		
		
		response.sendRedirect("/login?errorMsg=" + errorMsg);
    }	 
}
