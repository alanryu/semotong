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
        
        registry.addResourceHandler("/images/**", "/css/**","/js/**","/publish/**","/inc/**","/fonts/**", "/files/**")
        		.addResourceLocations("classpath:/static/images/",
                  "classpath:/static/css/",
                  "classpath:/static/js/",
                  "classpath:/static/inc/",
                  "classpath:/static/fonts/",
                  "classpath:/static/publish/",
                  "classpath:/static/files/");
        
        // robots.txt 파일 추가
        registry.addResourceHandler("/robots.txt")
                .addResourceLocations("classpath:/static/");
        
        // favicon.ico 추가
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");
    }
}
