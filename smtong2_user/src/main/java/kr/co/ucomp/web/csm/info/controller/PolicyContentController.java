package kr.co.ucomp.web.csm.info.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.csm.info.dto.PolicyContentSearchDto;
import kr.co.ucomp.web.csm.info.entity.PolicyContentEntity;
import kr.co.ucomp.web.csm.info.service.PolicyContentService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cms/policy-content") 
@Slf4j
public class PolicyContentController {
	@Autowired PolicyContentService service;
	
    /**
     * 2024-12-18 (수) 백신의
     * - 정책 내용 목록
     *
     * @param searchRequest               서치 params
     * @param List<PolicyContentEntity> 정책 내용 조회 리스트
     */
    @PostMapping("/list")
    public ResponseEntity<CustomApiResponse<List<PolicyContentEntity>>> policyContentList(HttpServletResponse response,
    		@RequestBody PolicyContentSearchDto searchRequest
    		) throws IOException {
    	
    	List<PolicyContentEntity> list = null;
    	
    	try {
    		long totCount = service.getListCount(searchRequest);
    		list = service.getList(searchRequest);
    		
    		return CustomApiResponse.success(ResponseCode.OK,totCount, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    }
    

    
    
    
	   /**
	  * 2024-12-18 조일근
	  * 이용약관
	  * @param model
	  * @param  
	  */
	 @GetMapping("/policy")
	 public String  policy(HttpServletRequest req, HttpServletResponse response,Model model)  {
		 PolicyContentSearchDto searchRequest = new PolicyContentSearchDto();
		 searchRequest.setSearchPolSp("01");
		 List<PolicyContentEntity> list = service.getList(searchRequest);
		 model.addAttribute("polList", list);
		 
		 
		 
		String _polId = req.getParameter("polId");
		PolicyContentEntity info = new PolicyContentEntity();
		if(StringUtils.isBlank(_polId)) {
			info = service.getPolicyContent("01",0);	
		} else {
			info = service.getPolicyContent("01",Integer.parseInt(_polId));
		}
		
		model.addAttribute("record", info);
		
			
	 	return "pages/csm/info/policy";
	 }
	 
	 
	   /**
	  * 2024-12-18 조일근
	  * 개인정보 처리 방침
	  * @param model
	  * @param  
	  */
	 @GetMapping("/persionalInfo")
	 public String  persionalInfo(HttpServletRequest req, HttpServletResponse response,Model model)  {
	 	
		 PolicyContentSearchDto searchRequest = new PolicyContentSearchDto();
		 searchRequest.setSearchPolSp("02");
		 List<PolicyContentEntity> list = service.getList(searchRequest);
		 model.addAttribute("polList", list);
		 
		 
		String _polId = req.getParameter("polId");
		PolicyContentEntity info = new PolicyContentEntity();
		if(StringUtils.isBlank(_polId)) {
			info = service.getPolicyContent("02",0);	
		} else {
			info = service.getPolicyContent("02",Integer.parseInt(_polId));
		}
		
		model.addAttribute("record", info);
		 
	 	return "pages/csm/info/persionalInfo";
	 }
}
