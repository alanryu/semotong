package kr.co.ucomp;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
    	 System.out.println("url = " + url);
    	 System.out.println("username = " + username);
    	 System.out.println("password = " + password);
        String encryptPassword = jasyptStringEncryptor.encrypt("0634fee303128b107ece06fd5dfd1062");
        String encryptUrl = jasyptStringEncryptor.encrypt(url);
        String encryptUsername = jasyptStringEncryptor.encrypt(username);

        System.out.println("encryptPassword = " + encryptPassword);
        System.out.println("encryptUrl = " + encryptUrl);
        System.out.println("encryptUsername = " + encryptUsername);
    }

    @Test
    public void decryptTest() {
        // application-dev.yml 복호화
        System.out.println("=== Datasource ===");
        String decryptUrl = jasyptStringEncryptor.decrypt("bsQMi+yDb5wbsXoKnAqtMVmC0sVMvzSXinoCObqjaSX/9zDhVgyJvmyZOvJrUws2huMczNPMolVXrgU8RMcZ82jQf7EOHKHRFclynx9Qk6nF6B8oday5eFqrwJiK0xiD");
        String decryptUsername = jasyptStringEncryptor.decrypt("N166+NtYWKpAYp7nJyVwu416S+j32HmknVrD+yYTiYg=");
        String decryptPassword = jasyptStringEncryptor.decrypt("zPkWIgwNDVm0bkzwlDwMXdMUuMeIQ4ew5KxxKZP5kq0=");

        System.out.println("decryptUrl = " + decryptUrl);
        System.out.println("decryptUsername = " + decryptUsername);
        System.out.println("decryptPassword = " + decryptPassword);

        System.out.println("=== Redis ===");
        String decryptRedisHost = jasyptStringEncryptor.decrypt("Yn3WecrcpOc9GGzzZJMEalYM9xUY3PObgLRZxE60gdg=");
        String decryptRedisPassword = jasyptStringEncryptor.decrypt("ovlknXi1mGvsRkohs7d/dS6mpqRf3Ot428FpGqGfamA=");

        System.out.println("decryptRedisHost = " + decryptRedisHost);
        System.out.println("decryptRedisPassword = " + decryptRedisPassword);
    }
}