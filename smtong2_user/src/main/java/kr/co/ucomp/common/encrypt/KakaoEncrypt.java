package kr.co.ucomp.common.encrypt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class KakaoEncrypt {
	 static Charset charSet  = StandardCharsets.UTF_8 ;
	 
	    public static String encryptAESCTRWithCustomInfo(String inMsg,String base64Key,String base64IV) {
	    	
	        byte[] keyData = Base64.getDecoder ().decode(base64Key.getBytes(charSet ));
	        byte[] kakaoIV = Base64.getDecoder ().decode(base64IV.getBytes(charSet ));
	        
	        
	        SecretKey secureKey = new SecretKeySpec(keyData, "AES");
	        if (inMsg == null || inMsg.length() == 0) {
	            return null;
	        }
	        Cipher cipher;
	        String encStr = null;
	        try {
	            cipher = Cipher.getInstance ("AES/CTR/NoPadding");
	            cipher.init(Cipher.ENCRYPT_MODE , secureKey, new IvParameterSpec(kakaoIV));
	            byte[] inputBytes1 = inMsg.getBytes(charSet );
	            byte[] outputBytes1 = cipher.doFinal(inputBytes1);
	            encStr = new String(Base64.getEncoder ().encode(outputBytes1), charSet );
	        } catch (Exception e) {
	        }
	        return encStr;
	    }
	    
	    public static String decryptAESCTRWithCustomInfo(String encMsg,String base64Key,String base64IV) {
       	    byte[] keyData = Base64.getDecoder ().decode(base64Key.getBytes(charSet ));
       	    byte[] kakaoIV = Base64.getDecoder ().decode(base64IV.getBytes(charSet ));
	        SecretKey secureKey = new SecretKeySpec(keyData, "AES");
	        if (encMsg == null || encMsg.length() == 0) {
	            return null;
	        }
	        Cipher cipher;
	        String plainStr = null;
	        try {
	            cipher = Cipher.getInstance ("AES/CTR/NoPadding");
	            cipher.init(Cipher.ENCRYPT_MODE , secureKey, new IvParameterSpec(kakaoIV));
	            byte[] decodedBytes = Base64.getDecoder ().decode(encMsg);
	            byte[] decrypted = cipher.doFinal(decodedBytes);
	            plainStr = new String(decrypted, charSet );
	        } catch (Exception e) {
	        }
	        return plainStr;
	    }
//	    
//	    
//	    public static void main(String[] args) {
//	        // Define the pattern
//	        String pattern = "yyyyMMddHHmmss";
//	        // SecretKey: YspoQoTjtl8leg0wxS5W1wlZxzPQUvOMhmIlViI6zvo=
//	        // iv: Xfsb+wiO8SBLtht9wG/9OQ==
//	        String endMsg ="TrhgX/SiRq80roUnew3X+FObZlGQ/FILeybPSVoY05r7Fgq77GLgCeZLJtz47Wqdf1Rbgtr4ViZMrfLX6JIIC5K8o/nwog3b1jTrV6iw1e858qzAj+a9pQ==";
//	        System.out.println("Parsed DateTime: " + decryptAESCTRWithCustomInfo(endMsg,"YspoQoTjtl8leg0wxS5W1wlZxzPQUvOMhmIlViI6zvo=","Xfsb+wiO8SBLtht9wG/9OQ=="));
//	    }
	    
}
