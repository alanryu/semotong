package kr.co.ucomp.web.mbm.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminUserDto implements UserDetails {
		private static final long serialVersionUID = -5849590619413998459L;
	    private int id;
		private String adminId;
		private String password;
		private String adminUserName;
		private String authType;
		private String comGroup;
		private String phoneNumber;
		private String email;
		private String allAuthYn;
		private String adminMemAuthYn;
		private String userMngYn;
		private String companyMngYn;
		private String prodMngYn;
		private String reqMngYn;
		private String settleMngYn;
		private String serviceMngYn;
		private String salesMngYn;
		private String companyCode;
		private String manageId;
		private String disable;
		private LocalDateTime modifiedDate;
		private String simpleOpenYn;
		private String pointMngYn;
		private String kakaoMsgMngYn;
		
	   
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			
			List<GrantedAuthority> auth = new ArrayList<>();
			
			if ( StringUtils.equals("Y", getAllAuthYn()) ) {
				auth.add(new SimpleGrantedAuthority("ALL"));
			}
			
			if ( StringUtils.equals("Y", getAdminMemAuthYn()) ) {
				auth.add(new SimpleGrantedAuthority("ADMIN_MEM"));
			}
			
			if ( StringUtils.equals("Y", getCompanyMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("COMPANY_MNG"));
			}
			
			if ( StringUtils.equals("Y", getUserMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("USER_MNG"));
			}
			
			if ( StringUtils.equals("Y", getReqMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("REQ_MNG"));
			}
			
			if ( StringUtils.equals("Y", getSettleMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("SETTLE_MNG"));
			}
			
			if ( StringUtils.equals("Y", getServiceMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("SERVICE_MNG"));
			}
			
			if ( StringUtils.equals("Y", getSalesMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("SALES_MNG"));
			}
			
			if ( StringUtils.equals("Y", getPointMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("POINT_MNG"));
			}
			
			if ( StringUtils.equals("Y", getProdMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("PROD_MNG"));
			}
			
			if ( StringUtils.equals("Y", getKakaoMsgMngYn()) ) {
				auth.add(new SimpleGrantedAuthority("MESSAGE_MNG"));
			}
			
		    return auth;
		}

	   @Override
	   public String getPassword() { return this.password; }

	   @Override
	   public String getUsername() { return this.adminId; }

	   @Override
	   public boolean isAccountNonExpired() { return true; }

	   @Override
	   public boolean isAccountNonLocked() { return true; }

	   @Override
	   public boolean isCredentialsNonExpired() { return true; }

	   @Override
	   public boolean isEnabled() { return true; }


}
