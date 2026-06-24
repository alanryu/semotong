package kr.co.ucomp.web.csm.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
import kr.co.ucomp.web.csm.dto.PolicyContentSearchDto;
import kr.co.ucomp.web.csm.entity.FaqEntity;
import kr.co.ucomp.web.csm.entity.PolicyContentEntity;
import kr.co.ucomp.web.csm.service.PolicyContentService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cms/policy-content") 
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class PolicyContentController {
	@Autowired PolicyContentService service;
	
    /**
     * 2024-12-18 (수) 백신의
     * - 약관 내용 목록
     *
     * @param searchRequest               서치 params
     * @param List<PolicyContentEntity> 약관 내용 조회 리스트
     */
	@ResponseBody
    @PostMapping("/ajaxlist")
    public ResponseEntity<CustomApiResponse<List<PolicyContentEntity>>> ajaxlist(HttpServletResponse response,
    		@RequestBody PolicyContentSearchDto searchRequest
    		) throws IOException {
    	
    	List<PolicyContentEntity> list = new ArrayList<PolicyContentEntity>();
    	
    	try {
    		long totCnt = service.getListCount(searchRequest);
    		if(totCnt > 0) list = service.getList(searchRequest);
    		return CustomApiResponse.success(ResponseCode.OK,totCnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 약관 신규 입력
     *
     * @param record 공지사항 정보
     */
    @PostMapping("/create")
	public String create(HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("recordForm") PolicyContentEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes
    		) throws IOException {    	
    	
    	String searchId = "";
    	String polSp = "";
    	polSp = record.getContentCategory();
    	
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/cms/policy-content/edit/"+polSp;
		 }
	   	 
    	try {
    		
    		
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setCreateId(loginadminInfo.getId());
     		record.setModifiedId(loginadminInfo.getId());
     		
    		service.create(record);
    		searchId = String.valueOf(record.getId());
    		
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
   		 	redirectAttributes.addFlashAttribute("procSp", "c");
        } catch (IllegalArgumentException e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        }
    	
    	
    	return "redirect:/cms/policy-content/edit/"+polSp+"?searchId="+searchId;
    	
    }
    
  
    
    
    /**
     * 2024-12-18 (수) 백신의
     * - 이용약관 노출여부 업데이트
     *
     * @param searchRequest               서치 params
     * @param List<NoticeEntity> 이용약관 노출여부 업데이트
     */
	@ResponseBody
    @PostMapping("/ajaxUpdate")
    public ResponseEntity<CustomApiResponse<PolicyContentEntity>> ajaxUpdate(HttpServletRequest request,HttpServletResponse response,
    		@RequestBody PolicyContentEntity record
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
     * 2024-12-18 (수) 업데이트
     *
     * @param record 공지사항 정보
     */
    @PostMapping("/update")
	public String update(HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("recordForm") PolicyContentEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes
    		) throws IOException {    	
    	
    	String searchId = "";
    	String polSp = "";
    	polSp = record.getContentCategory();
    	searchId = String.valueOf(record.getId());
    	
    	
    	
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/cms/policy-content/edit/"+polSp;
		 }
	   	 
    	try {
    		
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setModifiedId(loginadminInfo.getId());
    		service.update(record);
    		
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
   		 	redirectAttributes.addFlashAttribute("procSp", "u");
        } catch (IllegalArgumentException e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        }
    	
    	return "redirect:/cms/policy-content/edit/"+polSp+"?searchId="+searchId;
    	
    }
    
    
    
    
    /**
     * 2024-12-18 (수) 백신의
     * - 약관 삭제
     *
     * @param delId 삭제할 공지사항 ID
     */
    @ResponseBody
    @DeleteMapping("/deletie")
    public ResponseEntity<CustomApiResponse<String>> deletePolicyContent(HttpServletResponse response,
			@RequestParam("id") int delId
    		) throws IOException {
    	
    	try {
    		service.delete(delId);
    		return CustomApiResponse.success(ResponseCode.OK, "delete success");
    	} catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "deletePolicyContent: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "deletePolicyContent: " + e.getMessage());
        }
    }
    
    
    
    
    /**
     * 이용약관 리스트
     * @param request
     * @param response
     * @param model
     * @return
     */
 	@GetMapping("/list/{polSp}")
 	public String  listview( HttpServletRequest request,HttpServletResponse response,Model model
 			,@PathVariable("polSp") String polSp)  {
 		
 		
 		model.addAttribute("polSp", polSp);
 		return "pages/csm/policy/list";
 	}
 	
    

    /**
     * 2024-12-18 (수) 백신의
     * - 약관 상세 조회
     *
     * @param searchRequest               서치 params
     * @param NoticeEntity 약관 상세 정보
     */
    @GetMapping("/edit/{polSp}") 
    public String edit(HttpServletRequest request, HttpServletResponse response
    		,@PathVariable("polSp") String polSp
    		,@RequestParam(value="searchId" , required = false) String searchId,Model model) throws IOException {
    	
    	try {
    		
    		PolicyContentEntity record = new PolicyContentEntity();
    		if(model.getAttribute("org.springframework.validation.BindingResult.record") !=null) {
    			record = (PolicyContentEntity) model.getAttribute("record");
    		    model.addAttribute("record", record); 
    		    model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
    		} else {		
    			if (StringUtils.isNoneBlank(searchId)) {
    	    		record = service.getDetail(Integer.valueOf(searchId));
    	        }
    		}
    		if(record.getId() == null) {
    			record.setDispYn("1");
    		}
    		model.addAttribute("record", record);
    		model.addAttribute("polSp", polSp);
    		
    	} catch (Exception e) {
    		
		}
    	
    	return "pages/csm/policy/edit";
    }
    
    
    
}
