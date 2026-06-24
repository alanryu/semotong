package kr.co.ucomp.common.config.security;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.service.AdminUserService;



@Component
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private AdminUserService adminUserService;
	
	@Override
	public UserDetails loadUserByUsername(String userId) {
		
		AdminUserDto adminInfo = adminUserService.getDetailById(userId);
		
		Collection<? extends GrantedAuthority> authorities = new ArrayList<>();;
		
		
		if("N".equals(adminInfo.getDisable())) {
			throw new UsernameNotFoundException(userId);
		}
		/* 아이디 없음 */
		if (adminInfo == null ) {
			throw new UsernameNotFoundException(userId);
		} else {
			/* 어드민페이지에 접근할 권한 */
			authorities = adminInfo.getAuthorities();
		}
		
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		
		
		return new User(
				adminInfo.getAdminId(), 
				adminInfo.getPassword(), 
				enabled, 
				accountNonExpired, 
				credentialsNonExpired, 
				accountNonLocked,
				authorities);
		
	}
	

}
