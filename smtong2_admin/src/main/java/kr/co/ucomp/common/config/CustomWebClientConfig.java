package kr.co.ucomp.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 외부 API URI 호출을 위한
 * Web Client 설정파일
 *
 * @author 이정민
 * @since 2024.12.19
 * @version v1.0
 */

@Configuration
public class CustomWebClientConfig  {

    @Bean
    public WebClient kakaoWebClient(WebClient.Builder builder) {
        return builder
                .defaultHeaders(headers -> {
                    headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    headers.add("Accept", "application/json");
                })
                .build();
    }

}
