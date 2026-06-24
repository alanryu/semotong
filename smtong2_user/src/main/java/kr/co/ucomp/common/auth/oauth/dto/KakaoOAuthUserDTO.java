package kr.co.ucomp.common.auth.oauth.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoOAuthUserDTO {
    private Long id;

    @JsonProperty("synched_at")
    private String synchedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @Setter
    public static class KakaoAccount {
        private String name;

        @JsonProperty("is_email_valid")
        private boolean isEmailValid;

        @JsonProperty("is_email_verified")
        private boolean isEmailVerified;

        private String email;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("age_range")
        private String ageRange;

        private String birthyear;

        private String birthday;
    }
}
