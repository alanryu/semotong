package kr.co.ucomp.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.binary.Base64;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtil {
	
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	
	public static String getDatetime(String format) {
		String res = "";

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		res = now.format(formatter);
		return res;
		
	}
	
	public static String getBirthDateByHid(String residentNumber) {
        // 주민등록번호의 길이 확인
        if (residentNumber == null || residentNumber.length() < 7) {
            throw new IllegalArgumentException("유효한 주민등록번호를 입력하세요.");
        }

        // 생년월일 추출
        String birthYear = residentNumber.substring(0, 2); // 앞 두 자리 연도
        String birthMonth = residentNumber.substring(2, 4); // 월
        String birthDay = residentNumber.substring(4, 6); // 일
        char genderCode = residentNumber.charAt(6); // 성별 코드

        // 출생 연도 계산
        int year;
        switch (genderCode) {
            case '1': // 1900~1999 남성
            case '2': // 1900~1999 여성
                year = 1900 + Integer.parseInt(birthYear);
                break;
            case '3': // 2000~2099 남성
            case '4': // 2000~2099 여성
                year = 2000 + Integer.parseInt(birthYear);
                break;
            case '5': // 1900~1999 외국인 남성
            case '6': // 1900~1999 외국인 여성
                year = 1900 + Integer.parseInt(birthYear);
                break;
            case '7': // 2000~2099 외국인 남성
            case '8': // 2000~2099 외국인 여성
                year = 2000 + Integer.parseInt(birthYear);
                break;
            default:
                throw new IllegalArgumentException("유효한 성별 코드를 포함한 주민등록번호를 입력하세요.");
        }

        // 8자리 생년월일 생성
        return String.format("%04d%02d%02d", year, Integer.parseInt(birthMonth), Integer.parseInt(birthDay));
    }
	
	
	
	
	public static String generateSecureRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder randomString = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(index));
        }

        return randomString.toString();
    }
	
	
	 // Base64 인코딩 함수
    public static String encodeBase64(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return Base64.encodeBase64String(input.getBytes());
    }

    // Base64 디코딩 함수
    public static String decodeBase64(String encodedInput) {
        if (encodedInput == null || encodedInput.isEmpty()) {
            return "";
        }
        byte[] decodedBytes = Base64.decodeBase64(encodedInput);
        return new String(decodedBytes);
    }


    // URL-safe 인코더
    private static final Base64 base64UrlSafe = new Base64(true);

    // URL-safe Base64 인코딩
    public static String encodeBase64UrlSafe(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String encoded = base64UrlSafe.encodeToString(input.getBytes());
        return encoded.replace("=", "");
    }

    // URL-safe Base64 디코딩
    public static String decodeBase64UrlSafe(String encodedInput) {
        if (encodedInput == null || encodedInput.isEmpty()) {
            return "";
        }
        int paddingNeeded = (4 - (encodedInput.length() % 4)) % 4;
        StringBuilder sb = new StringBuilder(encodedInput);
        for (int i = 0; i < paddingNeeded; i++) {
            sb.append('=');
        }
        byte[] decodedBytes = base64UrlSafe.decode(sb.toString());
        return new String(decodedBytes);
    }
    
	
    
    
    public static String sha256HashForFacebook(String input) {
        input = input.trim().toLowerCase(); // 전처리
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public static String getClientIp(HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");
	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
	        // 다중 프록시일 경우, 첫 번째 IP가 실제 클라이언트 IP
	        return ip.split(",")[0];
	    }
	    return request.getRemoteAddr();
	}
    
	
	public static void main(String[] args) {
        // Define the pattern
        String pattern = "yyyyMMddHHmmss";
        
        System.out.println("Parsed DateTime: " + getDatetime(pattern));
    }
	
	
	
}
