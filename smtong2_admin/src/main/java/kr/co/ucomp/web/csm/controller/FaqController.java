package kr.co.ucomp.web.csm.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.csm.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.entity.FaqEntity;
import kr.co.ucomp.web.csm.entity.NoticeEntity;
import kr.co.ucomp.web.csm.service.FaqService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cms/faq")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class FaqController {
	
	
	
	@Autowired FaqService service;
	
	@Autowired CommCodeMngService commCodeMngService;
	
    /**
     * 2024-12-10 (화) 조일근
     * - 자주뭄는 질문
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
	@ResponseBody
    @PostMapping("/ajaxlist")
    public ResponseEntity<CustomApiResponse<List<FaqEntity>>> ajaxlist( HttpServletResponse response,
    		@RequestBody FaqSearchDto searchRequest
    		) throws IOException {
    	
    	List<FaqEntity> list = null;
    	
    	try {
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
     * - 자주뭄는 질문 신규 입력
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    @PostMapping("/create")
    public String create(HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("recordForm") FaqEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes
    		) throws IOException {
    	
    	String searchId= "";
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/cms/faq/edit";
		 }

    	try {
    		if(StringUtils.isBlank(record.getDisplayYn())) {
     			record.setDisplayYn("0");	
     		}
    		 service.create(record);
    		 searchId = String.valueOf(record.getId());
    		 redirectAttributes.addFlashAttribute("procMsg", "sucess");
    		 redirectAttributes.addFlashAttribute("procSp", "c");
        } catch (IllegalArgumentException e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        }
    	
    	return "redirect:/cms/faq/edit?searchId="+searchId;
    	
    }
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 자주뭄는 질문 업데이트
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    @PostMapping("/update")
    public String update( HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("recordForm") FaqEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes
    		) throws IOException {
    	String searchId= "";
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/cms/faq/edit";
		 }    	
    	try {
    		if(StringUtils.isBlank(record.getDisplayYn())) {
     			record.setDisplayYn("0");	
     		}
    		service.update(record);
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
    		redirectAttributes.addFlashAttribute("procSp", "u");
    	}  catch (IllegalArgumentException e) {
    		redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        }
    	
    	return "redirect:/cms/faq/edit?searchId="+searchId;
    	
    }
    
    
    /**
     * 2024-12-18 (수) 백신의
     * - 공지사항 목록
     *
     * @param searchRequest               서치 params
     * @param List<NoticeEntity> 공지사항 조회 리스트
     */
	@ResponseBody
    @PostMapping("/ajaxUpdate")
    public ResponseEntity<CustomApiResponse<FaqEntity>> ajaxUpdate(HttpServletRequest request,HttpServletResponse response,
    		@RequestBody FaqEntity record
    		) throws IOException {
    	try {
    		
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setModifiedId(loginadminInfo.getId());
     		
    		service.update(record);
    		return CustomApiResponse.success(ResponseCode.OK, record);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ajaxUpdate: " + e.getMessage());
		}
    }
    
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 자주뭄는 질문 상세 조회
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    @ResponseBody
    @DeleteMapping("/delete")
    public ResponseEntity<CustomApiResponse<String>> delFaq( HttpServletResponse response,
			@RequestParam("id") int delId
    		) throws IOException {
    	
    	try {
    		service.delFaq(delId);
    		 return CustomApiResponse.success(ResponseCode.OK, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delFaq: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delFaq: " + e.getMessage());
        }
    }
    
    
    
    
    /**
 	* 2024-12-18 조일근
 	*
 	* @param CommCodeSearchDto
 	* @param List<CodeGroupEntity> 코드그룹 리스트
 	*/
 	@GetMapping("/list")
 	public String  listview( HttpServletRequest request,HttpServletResponse response,Model model)  {
 		
 		CommCodeSearchDto codeparam = new CommCodeSearchDto();
 		codeparam.setPage(1);
 		codeparam.setRecordSize(999);
 		codeparam.setCodeGroup("faq_cate");
 		List<CodeEntity> codeList = commCodeMngService.getListCode(codeparam);
 		
 		model.addAttribute("codeList", codeList);    
 		
 		return "pages/csm/faq/list";
 	}

    /**
     * 2024-12-10 (화) 조일근
     * - 자주뭄는 질문 상세 조회
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    @GetMapping("/edit")
    public String InquiryGet(HttpServletRequest request, HttpServletResponse response, 
    		@RequestParam(value="searchId" , required = false) String searchId,Model model) throws IOException {
    	FaqEntity  record =   new FaqEntity(); 
    	try {
    		
     		CommCodeSearchDto codeparam = new CommCodeSearchDto();
     		codeparam.setPage(1);
     		codeparam.setRecordSize(999);
     		codeparam.setCodeGroup("faq_cate");
     		List<CodeEntity> codeList = commCodeMngService.getListCode(codeparam);
     		model.addAttribute("codeList", codeList);    
     		
     		
    		if(model.getAttribute("org.springframework.validation.BindingResult.record") !=null) {
    			record = (FaqEntity) model.getAttribute("record");
    		    model.addAttribute("record", record); 
    		    model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
    		} else {		
    			if (StringUtils.isNoneBlank(searchId)) {
    				record =  service.getFaq(Integer.valueOf(searchId));
    	        }
    		}
    		
    		
    	} catch (Exception e) {
		}
    	
    	model.addAttribute("record", record);    
    	
    	return "pages/csm/faq/edit";
    }
}
