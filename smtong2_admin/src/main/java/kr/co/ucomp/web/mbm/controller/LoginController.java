package kr.co.ucomp.web.mbm.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.biztalk.KakaoBizTalkUtils;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.service.AdminUserService;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

@Controller
public class LoginController {
	
	@Autowired AdminUserService service;
	@Autowired PasswordEncoder pwEncoder;
	
    @Autowired
    private Environment environment; // 활성 프로파일 확인용
    @Autowired private KakaoBizTalkUtils bizTalkService;
	
	
    /**
     * 로그인
     * @param request
     * @param model
     * @return
     * @throws IOException 
     */
    @GetMapping( value = "/login")
    public String login( HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
    	
    	HttpSession session = request.getSession();
    	
    	
		if(session.getAttribute("loginUser") !=null) {
			
			String LoginCertYn =  session.getAttribute("LoginCertYn") ==null ? "N" : (String)session.getAttribute("LoginCertYn");
			
			if("Y".equals(LoginCertYn)) {
				String successUrl = session.getAttribute("loginUser") == null ? "/mbm/adminuser/list" :   (String) session.getAttribute("successUrl");
				response.sendRedirect(successUrl);
			} else {
				
				response.sendRedirect("/logincert");
			}
		}
    	
        // 활성 프로파일 가져오기
        String[] activeProfiles = environment.getActiveProfiles();
        String activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        
        // Thymeleaf로 전달
        model.addAttribute("activeProfile", activeProfile);
		
    	
    	return "/pages/mbm/login";
    }
    
	
	
    /**
     * 로그인
     * @param request
     * @param model
     * @return
     * @throws IOException 
     */
    @GetMapping( value = "/")
    public void index( HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
    	
    	response.sendRedirect("/login");
    }
    
    
    
    /**
     * 로그인 인증코드
     * @param request
     * @param model
     * @return
     * @throws IOException 
     */
    @GetMapping( value = "/logincert")
    public String logincert( HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
    	HttpSession session = request.getSession();
    	
    	session.removeAttribute("LoginCertYn");
    	
    	return "/pages/mbm/logincert";
    }
        
    
    

	@ResponseBody
    @PostMapping("/sendAuthKey")
    public ResponseEntity<CustomApiResponse<Map<String,String>>> sendAuthKey( HttpServletRequest request, HttpServletResponse response
    		) throws IOException {
    	
		Map<String,String> res = new HashMap<String,String>();
		AdminUserDto info = new AdminUserDto();
		HttpSession session = request.getSession();
    	session.removeAttribute("LoginCert");


    	try {
    		
    		String loginId = session.getAttribute("LoginId") !=null ?(String) session.getAttribute("LoginId") : ""; // 인증번호 전송 로그인 아이디

    		if(StringUtils.isBlank(loginId)) {
    			res.put("errCode", "99");
    			res.put("errMsg", "로그인 정보가 없습니다.");
    			return CustomApiResponse.success(ResponseCode.OK, res);
    		}
    		info =  service.getDetailById(loginId);
    		
    		if(info ==null) {
    			res.put("errCode", "99");
    			res.put("errMsg", "로그인 정보가 없습니다.");
    			return CustomApiResponse.success(ResponseCode.OK, res);
    		}

    		String adminPhoneNum = info.getPhoneNumber();
    		String certNo = CommonUtil.generateAuthNo();
    		
    		
    		// 인증번호 전송
    		String token = bizTalkService.getKakaoBizTalkToken();

    		CommCodeSearchDto param =  new CommCodeSearchDto();
    		param.setCodeGroup("biz_template");
    		param.setCode("adminCertMsg");
    		
    		Map<String, String> variable = new HashMap<String, String>();  
    		variable.put("cetNo", certNo);
    		
    		String sendMsg =bizTalkService.sendBizMessage(param,variable);		
    		bizTalkService.sendSMSMsg(token, sendMsg, "sms", adminPhoneNum);
    		
    		
    		
    		System.out.println("certNo >>>>>>>>>>>>>" + certNo);
    		session.setAttribute("LoginCert", certNo);
    		
    		return CustomApiResponse.success(ResponseCode.OK, res);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    		
    }

}
