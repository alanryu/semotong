package kr.co.ucomp.common.auth.oauth.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverOAuthUserDTO {
	
	private String resultcode;
	
	private String message;

	@JsonProperty("response")
	private Response response;

	@Getter
	@Setter
	public static class Response {
		private String id;
		private String nickname;
		private String name;
		private String email;
		private String gender;
		private String age;
		private String birthday;
		private String profile_image;
		private String birthyear;
		private String mobile;
	}
}
/*
{
  "resultcode": "00",
  "message": "success",
  "response": {
	"id": "P_VRcMphOGnVKRfG0bcMLvwkCidc5tB_MnpvPU0-7sQ",
	"nickname": "\uc724\uc131\ud638",
	"profile_image": "https:\/\/ssl.pstatic.net\/static\/pwe\/address\/img_profile.png",
	"age": "30-39",
	"gender": "M",
	"email": "dune93@naver.com",
	//"name": "\uc724\uc131\ud638",
	//"birthday": "07-31",
	//"birthyear": "1993"
  },
  
	resultcode				String	Y	API 호출 결과 코드
	message					String	Y	호출 결과 메시지
	response/id				String	Y	동일인 식별 정보 네이버 아이디마다 고유하게 발급되는 유니크한 일련번호 값 (API 호출 결과로 네이버 아이디값은 제공하지 않으며, 대신 'id'라는 애플리케이션당 유니크한 일련번호값을 이용해서 자체적으로 회원정보를 구성하셔야 합니다.)
	response/nickname		String	Y	사용자 별명 (별명이 설정되어 있지 않으면 id*** 형태로 리턴됩니다.)
	response/name			String	Y	사용자 이름
	response/email			String	Y	사용자 메일 주소 기본적으로 네이버 내정보에 등록되어 있는 '기본 이메일' 즉 네이버ID@naver.com 값이나, 사용자가 다른 외부메일로 변경했을 경우는 변경된 이메일 주소로 됩니다.
	response/gender			String	Y	성별 - F: 여성 - M: 남성 - U: 확인불가
	response/age			String	Y	사용자 연령대
	response/birthday		String	Y	사용자 생일(MM-DD 형식)
	response/profile_image	String	Y	사용자 프로필 사진 URL
	response/birthyear		String	Y	출생연도
	response/mobile			String	Y	휴대전화번호
*/  

