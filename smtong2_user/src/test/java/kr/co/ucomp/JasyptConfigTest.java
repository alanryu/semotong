package kr.co.ucomp;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptConfigTest {

	/*
	 *  1. application 내 평문으로 세팅 후 실행
	 *  2. 프린트 된 암호문을 ENC()와 함께 붙혀 넣어 설정 완료 한다.
	 */
    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor jasyptStringEncryptor;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Test
    public void test() {

        // Redis host 암호화
        String encryptRedisHost = jasyptStringEncryptor.encrypt("172.17.0.1");
        System.out.println("encryptRedisHost = " + encryptRedisHost);




    }
}