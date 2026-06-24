package kr.co.ucomp;

import org.apache.ibatis.session.SqlSessionFactory;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.sql.Connection;

@SpringBootTest
public class SmtongFrontApplicationTests {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private Environment environment;

    @Autowired
    private StringEncryptor jasyptStringEncryptor;

    @Test
    public void connection_test() {
        // 활성화된 프로파일 확인
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("Active profiles: " + String.join(", ", activeProfiles));

        // 시스템 환경 변수에서 ENCRYPTION_KEY 확인
        String encryptionKey = System.getenv("ENCRYPTION_KEY");
        if (encryptionKey == null) {
            throw new IllegalStateException("ENCRYPTION_KEY 환경 변수가 설정되어 있지 않습니다.");
        }
        System.out.println("Using ENCRYPTION_KEY: " + encryptionKey);

        // 데이터베이스 설정 복호화
        String encryptedUrl = environment.getProperty("spring.datasource.url");
        String encryptedUsername = environment.getProperty("spring.datasource.username");
        String encryptedPassword = environment.getProperty("spring.datasource.password");

        String decryptedUrl = decryptValue(encryptedUrl);
        String decryptedUsername = decryptValue(encryptedUsername);
        String decryptedPassword = decryptValue(encryptedPassword);

        System.out.println("Decrypted Database URL: " + decryptedUrl);
        System.out.println("Decrypted Database Username: " + decryptedUsername);
        System.out.println("Decrypted Database Password: " + decryptedPassword);
        
        
        
        // 데이터베이스 설정 복호화
        String encryptedRedisUrl = environment.getProperty("spring.data.redis.host");
        String encryptedRedisPassword = environment.getProperty("spring.data.redis.password");

        String decryptedRedisUrl = decryptValue(encryptedRedisUrl);
        String decryptedRedisPassword = decryptValue(encryptedRedisPassword);

        System.out.println("Decrypted decryptedRedisUrl URL: " + decryptedRedisUrl);
        System.out.println("Decrypted decryptedRedisPassword Username: " + decryptedRedisPassword);
        

        // 프로파일이 'prod'인 경우 데이터베이스 연결 테스트 생략
        if (isProdProfile(activeProfiles)) {
            System.out.println("'prod' 프로파일에서는 데이터베이스 연결 테스트를 건너뜁니다.");
            return;
        }

        // 데이터베이스 연결 테스트
        try (Connection con = sqlSessionFactory.openSession().getConnection()) {
            System.out.println("Connection successful.");
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 주어진 값이 ENC() 형식의 암호화된 값인지 확인하고 복호화
     *
     * @param encryptedValue 암호화된 값 (ENC() 형식)
     * @return 복호화된 값 또는 원본 값
     */
    private String decryptValue(String encryptedValue) {
        if (encryptedValue != null && encryptedValue.startsWith("ENC(") && encryptedValue.endsWith(")")) {
            try {
                // ENC() 제거 후 복호화
                return jasyptStringEncryptor.decrypt(encryptedValue.substring(4, encryptedValue.length() - 1));
            } catch (Exception e) {
                System.err.println("Failed to decrypt value: " + encryptedValue);
                e.printStackTrace();
                return null;
            }
        }
        return encryptedValue; // 암호화되지 않은 값은 그대로 반환
    }

    /**
     * 활성화된 프로파일이 'prod'인지 확인
     *
     * @param activeProfiles 활성화된 프로파일 배열
     * @return 'prod' 프로파일이 활성화된 경우 true
     */
    private boolean isProdProfile(String[] activeProfiles) {
        for (String profile : activeProfiles) {
            if ("prod".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
}
