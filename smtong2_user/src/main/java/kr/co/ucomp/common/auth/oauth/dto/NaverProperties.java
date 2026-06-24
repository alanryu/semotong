package kr.co.ucomp.common.auth.oauth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "naver")
@Getter
@Setter
public class NaverProperties {
	private Auth 		auth;
	private Token 		token;
	private Profile		profile;
	private Client 		client;
	
	@Getter
	@Setter
	public static class Auth {
		private String uri;
	}
	
	@Getter
	@Setter
	public static class Token {
		private String uri;
	}
	
	@Getter
	@Setter
	public static class Profile {
		private String uri;
	}
	
	@Getter
	@Setter
	public static class Client {
		private String id;
		private String secret;
		private String redirect;
	}

}
