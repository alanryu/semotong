package kr.co.ucomp.common.auth.oauth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kakao")
@Getter
@Setter
public class KakaoProperties {
    private Client client;
    private Token token;
    private UserInfo userInfo;
    private TermsInfo termsInfo;
    private UserDrop userDrop;

    @Getter
    @Setter
    public static class Client {
        private String id;
        private String akey;
    }

    @Getter
    @Setter
    public static class Token {
        private String uri;
    }

    @Getter
    @Setter
    public static class UserInfo {
        private String uri;
    }

    @Getter
    @Setter
    public static class TermsInfo {
        private String uri;
    }

    @Getter
    @Setter
    public static class UserDrop {
        private String uri;
    }

}
