package kr.co.ucomp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.ucomp.common.util.CommonUtil;

@Configuration
public class ThymeleafConfig {

    @Bean
    public CommonUtil commonUtils() {
        return new CommonUtil();
    }
}