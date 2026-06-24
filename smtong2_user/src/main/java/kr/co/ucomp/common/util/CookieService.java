package kr.co.ucomp.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieService {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 쿠키 저장
	 * @param response
	 * @param cookieNm
	 * @param cookieval
	 * @throws JsonProcessingException 
	 */
	public void createCookie(HttpServletResponse response,String cookieNm, String cookieVal) throws JsonProcessingException {

		
		Cookie cookie = new Cookie(cookieNm,cookieVal);
		cookie.setPath("/");
		cookie.setMaxAge(30*60);
		cookie.setSecure(true);
		response.addCookie(cookie);
	}

	/**
	 * 쿠키 삭제
	 * @param response
	 * @param cookieNm
	 */
	public void deleteCookie(HttpServletResponse response,String cookieNm) {
        Cookie cookie = new Cookie(cookieNm, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
	
	
	/**
	 * 쿠키 가져오기
	 * @param request
	 * @param cookieNm
	 * @return
	 * @throws IOException
	 */
	public String getCookie(HttpServletRequest request,String cookieNm) throws IOException {
        // 모든 쿠키 가져오기
        Cookie[] cookies = request.getCookies();
        String cookieVal = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieNm.equals(cookie.getName())) {
                	cookieVal = cookie.getValue();
                }
            }
        }
        
        return cookieVal;
    }
	
	
	/**
	 * 쿠키값 중복 체크
	 * @param cookieVal
	 * @param chkVal
	 * @return
	 * @throws IOException
	 */
	public String chkDupCookieVal(String cookieVal, String chkVal) throws IOException {
		String [] itmList = cookieVal.split("/");
		String exPlanId = "N";
		for (String string : itmList) {
			if(string.equals(chkVal)) {
				exPlanId = "Y";
			}
		}
		if("N".equals(exPlanId)) {
			cookieVal = cookieVal + "/" +chkVal;	
		} else {
			cookieVal = cookieVal;
		}
		
        return cookieVal;
    }
	
	
}
