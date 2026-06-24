package kr.co.ucomp.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import kr.co.ucomp.common.config.security.CustomAuthenticationFailHandler;
import kr.co.ucomp.common.config.security.CustomAuthenticationSuccessHandler;
import kr.co.ucomp.common.config.security.CustomLogoutSuccessHandler;
import kr.co.ucomp.common.config.security.CustomUserDetailsService;
import kr.co.ucomp.common.config.security.OtpVerificationFilter;
import lombok.RequiredArgsConstructor;




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
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	
	@Autowired
	private CustomAuthenticationFailHandler customAuthenticationFailHandler;
	
	@Autowired
	private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    private static final String[] AUTH_WHITELIST = {
            "/login/**",
            "/sendAuthKey/**",
            "/logincert/**",
            "/upload/**",
            "/auth/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/robots.txt",// robots.txt 추가
            "/kakao/**"//카카오웹훅 추가
    };
    
    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        	http.httpBasic(AbstractHttpConfigurer::disable)
			        .csrf(AbstractHttpConfigurer::disable)
			        .userDetailsService(customUserDetailsService);
			        
	        	// 접근권한
	        	http.authorizeHttpRequests(auth -> auth
            	    .requestMatchers(AUTH_WHITELIST).permitAll()
                	.anyRequest().authenticated()
	             );
	        	
	        	
	        	// form login
	        	http.formLogin(formLogin -> formLogin
                    .loginPage("/login")
					.permitAll()
                    .loginProcessingUrl("/loginProc")
                    .successForwardUrl("/mbm/adminuser/list")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(customAuthenticationSuccessHandler)
                    .failureHandler(customAuthenticationFailHandler)
	            );
	        	
	        	http.addFilterAfter(new OtpVerificationFilter(), UsernamePasswordAuthenticationFilter.class);
	        	
			    
	        	// log out
	        	http.logout((logout) -> logout
	        				.logoutUrl("/logout")
							.logoutSuccessUrl("/login")
							.invalidateHttpSession(true)
							.deleteCookies("JSESSIONID")
							.logoutSuccessHandler(customLogoutSuccessHandler)
					 );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
