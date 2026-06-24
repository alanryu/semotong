package kr.co.ucomp.common.encrypt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DaouEncrypt {
	
	static Charset charSet  = StandardCharsets.UTF_8 ;
	
	// - application.yml 에 등록해 쓸지 말지 나중에 결정
	//@Value("${daou.key.enc-key}"			) String daouEncKey;		<<<--- 이거 해도 null 나옴, 아래처럼, setter 처리해야 먹힘. @Component 는 꼭 붙히고
	//@Value("${daou.key.iv-key}"			) String daouIvKey;
	public static String secretKey;
	public static String iv;
	
	@Value("${daou.key.enc-key}")
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	@Value("${daou.key.iv-key}")
	public void setIv(String iv) {
		this.iv = iv;
	}
	
	public static String encrypt(String plaintext) {
		String encStr = null;
		try {
			SecretKeySpec 		secretKeySpec 		= new SecretKeySpec(secretKey.getBytes(), "AES");
			IvParameterSpec 	ivParameterSpec 	= new IvParameterSpec(iv.getBytes());
			
			Cipher cipher;
		
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			
			byte[] outputBytes	= cipher.doFinal(plaintext.getBytes());
			
			encStr = new String(Base64.getEncoder ().encode(outputBytes), charSet );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encStr;
	}
	
	public static String decrypt(String encryptedText) {
		String plainStr = null;
		try {
			SecretKeySpec 		secretKeySpec 		= new SecretKeySpec(secretKey.getBytes(), "AES");
			IvParameterSpec 	ivParameterSpec 	= new IvParameterSpec(iv.getBytes());
			
			Cipher cipher;
		
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			
			plainStr = new String(decryptedBytes, charSet );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plainStr;
	}
		
		
}
