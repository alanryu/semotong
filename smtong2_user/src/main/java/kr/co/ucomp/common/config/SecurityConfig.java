package kr.co.ucomp.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * 스프링 시큐리티 설정 클래스
 *
 * @author 이정민
 * @since 2024-12-11
 * @version v1.1
 *
 * 2024.12.16(월)
 * 개발환경을 위한 모든 인증 절차 PASS
 *
 * 2024.12.20(금)
 * Next.js와 api 통신 중,
 * CORS 문제 발생으로 인한
 * cors configuration 생성
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private static final String[] AUTH_WHITELIST = {
            "/upload/**",
            "/auth/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/inc/**"
    };

    
    private final CustomAccessDeniedHandler accessDeniedHandler;
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler) // 커스텀 AccessDeniedHandler 등록
                 )
                .formLogin(AbstractHttpConfigurer::disable)
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/users/login") // 커스텀 로그인 페이지
//                        .permitAll()
//                    )
                .logout(logout -> logout.permitAll())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
                 .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
