package kr.co.ucomp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication(scanBasePackages = "kr.co.ucomp")
//@EnableAutoConfiguration
@EnableEncryptableProperties
@EnableRedisHttpSession
public class SmtongFrontApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmtongFrontApplication.class, args);
	}

}
