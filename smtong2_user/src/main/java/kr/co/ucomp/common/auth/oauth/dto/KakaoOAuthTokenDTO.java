package kr.co.ucomp.common.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


/**
 * Kakao Sync 의 토큰받기 부분
 *
 * https://kauth.kakao.com/oauth/token의 POST 요청으로
 * 응답하는 결과 DTO
 *
 * @author 이정민
 * @since 2024.12.19
 * @version v1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoOAuthTokenDTO {

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("kakao_id")
    private String kakaoId;

}
