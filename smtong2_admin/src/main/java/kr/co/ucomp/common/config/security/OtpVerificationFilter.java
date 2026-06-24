package kr.co.ucomp.common.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;

public class OtpVerificationFilter extends OncePerRequestFilter {


    
    public OtpVerificationFilter() {
    }
    


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

    	HttpSession session = request.getSession();
    	
        // OTP 입력 폼을 제출한 경우에만 필터 적용
        if ("/otpVerify".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
        	
        	String finalSuccessUrl = (String) session.getAttribute("successUrl");
            // OTP 검증 로직
            if (!isValidOtp(request)) {
            	// 로그인 성공 후 최종 페이지로 리다이렉트

                // Ajax 응답으로 실패 메시지 반환
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"99\",\"message\":\"인증번호가 잘못되었습니다.\"}");
                
                
                return;
            }
            
        	session.removeAttribute("LoginCert");


            // OTP 검증 성공 시 최종 인증 성공 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                // 로그인 성공 후 최종 페이지로 리다이렉트
            	session.setAttribute("LoginCertYn", "Y");
            	response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"00\",\"redirectUrl\":\"" + finalSuccessUrl + "\"}");
                
                
                
                return;
            }
        }

        // 나머지 요청은 필터 체인에 넘깁니다.
        chain.doFilter(request, response);
    }


    
    private String getJsonParam(HttpServletRequest request, String paramName) throws IOException {
    	// HttpServletRequest에서 본문을 읽어오기
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // JSON 문자열을 JSON 객체로 파싱
        try {
            // JSONParser로 문자열을 JSONObject로 변환
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(sb.toString()); // JSON 문자열 파싱

            // "certNo" 값을 추출
            return (String) jsonObject.get(paramName); // "certNo"의 값을 반환
        } catch (ParseException e) {
            // JSON 파싱 오류 처리
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    private boolean isValidOtp(HttpServletRequest request) throws IOException {
    	
    	
		HttpSession session = request.getSession();
		boolean res  = true;
		
		String certNo =  getJsonParam(request,"certNo");
		String userId = session.getAttribute("LoginId") !=null ?(String) session.getAttribute("LoginId") : ""; // 인증번호 전송 로그인 아이디
		
		if(StringUtils.isEmpty(userId)) {
			res = false;
		}
		
		
		
		//==================== 인증번호 체크
		String chkCertNo = session.getAttribute("LoginCert") !=null ?(String) session.getAttribute("LoginCert") : ""; // 전송된 인증번호  
		
		
		if(StringUtils.isEmpty(chkCertNo)) {
			res = false;
		}
		
		
		if(StringUtils.isEmpty(certNo)) {
			res = false;
		}
		
		
		if(!chkCertNo.equals(certNo)) {
			res = false;
		}
		
		
		return res;
    	
    }


}