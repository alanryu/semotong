package kr.co.ucomp.common.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;


public class SecurityContextUtil {

    public static void setAuthentication(String username, String password, String role) {
        // 1. UserDetails 생성
        UserDetails userDetails = User.builder()
                .username(username)
                .password(password) // 일반적으로 암호화된 비밀번호를 저장
                .roles(role) // "ROLE_" prefix가 자동으로 추가됨
                .build();

        // 2. Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, 
                null, 
                userDetails.getAuthorities() // 권한 정보
        );

        // 3. SecurityContext에 인증 정보 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }
}