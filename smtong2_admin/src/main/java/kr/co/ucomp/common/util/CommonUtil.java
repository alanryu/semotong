package kr.co.ucomp.common.util;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;


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
	

	
	/**
     * 문자열 날짜를 yyyy-MM-dd HH:mm 형식으로 변환
     *
     * @param dateTimeString 변환할 날짜 문자열 (예: "20250303150947")
     * @return 변환된 날짜 문자열 (예: "2025-03-03 15:09")
     */
    public static String formatDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.length() != 14) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyyMMddHHmmss");
        }

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, inputFormatter);
        return dateTime.format(outputFormatter);
    }
    
    
    
    /**
     * 카드 번호를 형식에 맞게 변환하는 함수
     * @param cardNumber 카드 번호 문자열 (숫자만 포함된 문자열)
     * @return 형식화된 카드 번호 (예: 1234-5678-9012-3456 또는 1234-567890-12345)
     */
    public static String formatCardNumber(String cardNumber) {
        // 숫자 외의 문자는 제거
        String cleaned = cardNumber.replaceAll("\\D", "");

        // 카드 번호가 15자리 또는 16자리인지 확인
        if (cleaned.length() == 16) {
            // 16자리 카드 번호 형식 (1234-5678-9012-3456)
            return cleaned.replaceAll("(.{4})", "$1-").substring(0, 19);
        } else if (cleaned.length() == 15) {
            // 15자리 카드 번호 형식 (1234-567890-12345)
            return cleaned.replaceAll("(.{4})(.{6})(.{5})", "$1-$2-$3");
        } else {
        	return "";
        }
    }
    
    
/**
 * 운전면허 번호 포매팅    
 * @param licenseNumber
 * @return
 */
    public static String formatLicenseNumber(String param) {
        // 문자열 길이 확인
        if (param.length() != 12) {
        	return param;
        }

        // 포맷팅
        return param.replaceAll("^(\\d{2})(\\d{2})(\\d{6})(\\d{2})$", "$1-$2-$3-$4");
    }
    
    
    
    /**
     * 8자리 년월일 포매팅
     * @param licenseNumber
     * @return
     */
    public static String formatDate(String param) {
        // 문자열 길이 확인
        if (param.length() != 8) {
        	return param;
        }

        // 포맷팅
        return param.replaceAll("^(\\d{4})(\\d{2})(\\d{2})$", "$1.$2.$3");
    }
    
    /**
     * 6자리 년월 포매팅
     * @param licenseNumber
     * @return
     */
    public static String formatYM(String param) {
        // 문자열 길이 확인
        if (param.length() != 6) {
        	return param;
        }

        // 포맷팅
        return param.replaceAll("^(\\d{4})(\\d{2})$", "$1.$2");
    }
    
    /**
     * 휴대폰 번호 포매팅
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.matches("^\\d{11}$")) {
            // 11자리: 3-4-4 형식
            return phoneNumber.replaceAll("^(\\d{3})(\\d{4})(\\d{4})$", "$1-$2-$3");
        } else if (phoneNumber.matches("^\\d{10}$")) {
            // 10자리: 3-3-4 형식
            return phoneNumber.replaceAll("^(\\d{3})(\\d{3})(\\d{4})$", "$1-$2-$3");
        } else {
        	return phoneNumber;
        }
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


//	public static void main(String[] args) {
//        String encoded = getDatetime("yyyyMMddHHmmssSSS");
//        System.out.println("Encoded: " + encoded);
//        
//    }
	
	
	
	/**
     * apache commons 이용, String 반환
     * @return
     */
    public static String generateAuthNo() {
        return RandomStringUtils.randomNumeric(6);
    }
    
    public static String getUnixTime() throws ParseException {
    	String result = "";
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = sdf.parse(timestamp);
        long unixTime = date.getTime() / 1000;
        result = String.valueOf(unixTime);
        return result;
    }
    
    
    
	public static String readExcelCell(Cell cell) {
        String cellValue = ""; // 셀 데이터를 문자열로 저장

        if (cell != null) {
            // 셀의 데이터 유형 확인
            switch (cell.getCellType()) {
                case STRING:
                    // 셀이 문자열인 경우
                    cellValue = cell.getStringCellValue();
                    break;

                case NUMERIC:
                    // 셀이 숫자인 경우
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 날짜 형식인 경우
                        cellValue = cell.getDateCellValue().toString();
                    } else {
                        // 숫자 형식인 경우
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;

                case BOOLEAN:
                    // 셀이 Boolean인 경우
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;

                case FORMULA:
                    // 셀이 수식인 경우
                    try {
                        cellValue = cell.getStringCellValue();
                    } catch (IllegalStateException e) {
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;

                case BLANK:
                    // 셀이 비어 있는 경우
                    cellValue = "";
                    break;

                default:
                    // 기타 데이터 유형 처리
                    cellValue = "";
            }
        }

        return cellValue;
    }
   	
    
}
