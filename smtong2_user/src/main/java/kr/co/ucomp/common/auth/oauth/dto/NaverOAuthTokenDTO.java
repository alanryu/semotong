package kr.co.ucomp.common.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


/**
 * Naver 토큰 받기 부분
 *
 * https://nid.naver.com/oauth2.0/token의 POST 요청으로
 * 응답하는 결과 DTO
  */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverOAuthTokenDTO {
	
	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	@JsonProperty("token_type")
	private String tokenType;
	
	@JsonProperty("expires_in")
	private String expiresIn;

}
/*
{
  "access_token": "AAAANrrD1gf6VdJPp6M7mwCg4mCcMT2hDj1EbRezSiJiG9vZkVnsXgEwxMcUmei4oeA5z96SbTNPeAgfvKCx6BHao5M",
  "refresh_token": "O5foipurgYgTIC0NCFUTqJyrBWCzk7n0q1BoipisqOFcz9WgwkT2c307X2TDAwNUQvXrDsuLEV8nMPosQyfXNT7fMeoV3CUp73V1isMepPIu33J7vZjBKLBLfVo2mskvCDu0",
  "token_type": "bearer",
  "expires_in": "3600"
}
*/