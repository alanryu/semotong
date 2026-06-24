package kr.co.ucomp.common.encrypt;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESEncryptUtilGOGO {

	  private static final String CIPHER_INSTANCE_TYPE = "AES/GCM/NoPadding";
	  private static final String SECRET_KEY_ALGORITHM = "AES";
	  private static final String DEFAULT_ENCODING = "UTF-8";
	  private static final String secretKey = "S2e%M0o2T!o8ng";
	  private static final int TAG_LENGTH = 128; // 128, 120, 112, 104, 96
	  public static byte[] ivBytes = new byte[16]; 

	  public static String encrypt(String message) throws
	    NoSuchAlgorithmException, NoSuchPaddingException,
	    UnsupportedEncodingException, InvalidKeyException,
	    InvalidAlgorithmParameterException,
	    IllegalBlockSizeException, BadPaddingException
	  {
	    try {
	    	String aeakey = Base64.getEncoder().encodeToString(secretKey.getBytes(DEFAULT_ENCODING));
	      Cipher ciper = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
	      byte[] keyBytes = string2SizedBytes(aeakey, 32);
	      
	      SecretKeySpec keySpec = new SecretKeySpec(keyBytes,SECRET_KEY_ALGORITHM);
	      GCMParameterSpec ivSpec = new GCMParameterSpec(TAG_LENGTH,ivBytes);
	  
	      ciper.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
	      
	      byte[] cipherText = ciper.doFinal(message.getBytes(DEFAULT_ENCODING));
	      
	      return Base64.getEncoder().encodeToString(cipherText);
	    } catch (NoSuchAlgorithmException
	        | NoSuchPaddingException
	        | UnsupportedEncodingException
	        | InvalidKeyException
	        | InvalidAlgorithmParameterException
	        | IllegalBlockSizeException
	        | BadPaddingException
	        e) {
	      throw e;
	    }
	  }

	  public static String decrypt(String input) throws
	    NoSuchAlgorithmException, NoSuchPaddingException,
	    UnsupportedEncodingException, InvalidKeyException,
	    InvalidAlgorithmParameterException,
	    IllegalBlockSizeException, BadPaddingException
	  {
	    try {
	      String aeakey = Base64.getEncoder().encodeToString(secretKey.getBytes(DEFAULT_ENCODING));
	      byte[] data  = Base64.getDecoder().decode(input);
	      
	      Cipher ciper = Cipher.getInstance(CIPHER_INSTANCE_TYPE);
	      byte[] keyBytes = string2SizedBytes(aeakey, 32);
	      
	      SecretKeySpec keySpec = new SecretKeySpec(keyBytes,SECRET_KEY_ALGORITHM);
	      GCMParameterSpec ivSpec = new GCMParameterSpec(TAG_LENGTH,ivBytes);
	  
	      ciper.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
	      
	      return new String(ciper.doFinal(data));
	    } catch (NoSuchAlgorithmException
	        | NoSuchPaddingException
	        | UnsupportedEncodingException
	        | InvalidKeyException
	        | InvalidAlgorithmParameterException
	        | IllegalBlockSizeException
	        | BadPaddingException
	        e) {
	      throw e;
	    }
	  }

	  private static byte[] string2SizedBytes(String in, int size) throws UnsupportedEncodingException {
	    byte[] bytesIn = in.getBytes(DEFAULT_ENCODING);
	    byte[] out = new byte[size];
	    int maxLen = bytesIn.length > size ? size : bytesIn.length;
	    System.arraycopy(bytesIn, 0, out, 0, maxLen);
	    return out;
	  }
	  
    
}