package kr.co.ucomp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.OPTIONS.name()
                )
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** URL 경로를 처리할 리소스 핸들러 추가
        registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + uploadDir + "/");
        
        // 정적 리소스 처리 및 캐싱 설정 추가
        registry.addResourceHandler("/images/**", "/css/**", "/js/**", "/publish/**", "/inc/**")
                .addResourceLocations(
                        "classpath:/static/images/",
                        "classpath:/static/css/",
                        "classpath:/static/js/",
                        "classpath:/static/inc/",
                        "classpath:/static/publish/");

        // /fonts/** URL 경로에 대해 캐싱 설정 추가
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/")
                .setCachePeriod(3600 * 24 * 365); // 1년 동안 캐싱
     
        // /favicon.ico 요청을 /images/favicon.ico로 매핑
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");
        
        // /sitemap.xml 및 /robots.txt 정적 리소스 추가
        registry.addResourceHandler("/sitemap.xml", "/robots.txt")
                .addResourceLocations("classpath:/static/");

        // 네이버 인증 HTML 파일 핸들러 추가
        registry.addResourceHandler("/naver2d8719ec9cf27d499243f7721ebe5596.html")
                .addResourceLocations("classpath:/static/");

        // 루트에 있는 모든 기타 정적 파일 처리
        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/",
                        "classpath:/META-INF/resources/",
                        "classpath:/resources/",
                        "classpath:/public/"
                );

    }
}
