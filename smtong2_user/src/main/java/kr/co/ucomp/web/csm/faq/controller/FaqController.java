package kr.co.ucomp.web.csm.faq.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.csm.faq.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.faq.entity.FaqEntity;
import kr.co.ucomp.web.csm.faq.service.FaqService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cms/faq")
@Slf4j
public class FaqController {
	
	
	
	@Autowired FaqService service;
	@Autowired
	private CommCodeMngService commCodeMngService;
	
    /**
     * 2024-12-10 (화) 조일근
     * - 자주뭄는 질문
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
	@ResponseBody
    @PostMapping("/list")
    public ResponseEntity<CustomApiResponse<List<FaqEntity>>> InquiryList( HttpServletResponse response,
    		@RequestBody FaqSearchDto searchRequest
    		) throws IOException {
    	
    	List<FaqEntity> list = null;
    	
    	try {
    		searchRequest.setNotCategory("cate07");
    		long cnt = service.getListFaqCount(searchRequest);
    		if(cnt > 0) {
    			list =  service.getListFaq(searchRequest);	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK, cnt , list);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "InquiryList :  " + e.getMessage());
		}
    		
    }
    
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 자주뭄는 질문 상세 조회
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<FaqEntity>> InquiryGet( HttpServletResponse response,
    		@PathVariable("id") int searchId
    		) throws IOException {
    	
    	try {
    		FaqEntity  info =  service.getFaq(searchId);
    		 if (info == null) {
                 return CustomApiResponse.error(ResponseCode.NOT_FOUND, "FAQ 가 존재 하지 않습니다.");
             }
    		return CustomApiResponse.success(ResponseCode.OK, info);
    	} catch (Exception e) {
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "FAQ  InquiryGet: " + e.getMessage());
		}
    	
    }
    
    
	   /**
	  * 2024-12-18 조일근
	  *
	  * @param model
	  * @param 
	  */
	 @GetMapping("/faqlist")
	 public String  faqlist( HttpServletResponse response,Model model)  {
		 /* 카테고리 코드 get */
		CommCodeSearchDto searchDto = new CommCodeSearchDto();
		searchDto.setCodeGroup("faq_cate");
		searchDto.setNotCode("cate07");
		searchDto.setUserYn("Y");
		List<CodeEntity> cateList = commCodeMngService.getListCode(searchDto);
		
		model.addAttribute("cateList", cateList);

	 	return "pages/csm/faq/faqlist";
	 }
    
}
