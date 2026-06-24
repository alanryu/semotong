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
import kr.co.ucomp.web.csm.dto.NoticeSearchDto;
import kr.co.ucomp.web.csm.entity.FaqEntity;
import kr.co.ucomp.web.csm.entity.NoticeEntity;
import kr.co.ucomp.web.csm.service.NoticeService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cms/notice") 
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class NoticeController {
	
	@Autowired NoticeService service;
	

	
    /**
     * 2024-12-18 (수) 백신의
     * - 공지사항 목록
     *
     * @param searchRequest               서치 params
     * @param List<NoticeEntity> 공지사항 조회 리스트
     */
	@ResponseBody
    @PostMapping("/ajaxlist")
    public ResponseEntity<CustomApiResponse<List<NoticeEntity>>> ajaxlist(HttpServletResponse response,
    		@RequestBody NoticeSearchDto searchRequest
    		) throws IOException {
    	
    	List<NoticeEntity> list = null;
    	
    	try {
    		long count = service.getListNoticeCount(searchRequest);
    		if(count >0 ) {
    			list = service.getListNotice(searchRequest);	
    		}
    		return CustomApiResponse.success(ResponseCode.OK,count, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "noticeList: " + e.getMessage());
		}
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
    public ResponseEntity<CustomApiResponse<NoticeEntity>> ajaxUpdate(HttpServletRequest request,HttpServletResponse response,
    		@RequestBody NoticeEntity record
    		) throws IOException {
    	try {
    		
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setModifiedId(loginadminInfo.getId());
     		
    		service.update(record);
    		return CustomApiResponse.success(ResponseCode.OK, record);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "noticeList: " + e.getMessage());
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
		
		return "pages/csm/notice/list";
	}
	
	  /**
     * 2024-12-18 (수) 백신의
     * - 공지사항 상세 조회
     *
     * @param searchRequest               서치 params
     * @param NoticeEntity 공지사항 상세 정보
     */
    @GetMapping("/edit")
    public String getNotice(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="searchId" , required = false) String searchId,Model model) throws IOException {
    	NoticeEntity record = new NoticeEntity();
    	try {
    		
    		if(model.getAttribute("org.springframework.validation.BindingResult.record") !=null) {
    			record = (NoticeEntity) model.getAttribute("record");
    		    model.addAttribute("record", record); 
    		    model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
    		} else {		
    			if(StringUtils.isNotBlank(searchId)) {
        			record = service.getNotice(Integer.valueOf(searchId));
        		}
    		}    		
    		

    	} catch (Exception e) {
    		
		}
		model.addAttribute("record", record);    	
    	return "pages/csm/notice/edit";
    }
	
  
    
    /**
     * 
     * - 공지사항 신규 입력
     *
     * @param record 공지사항 정보
     */
    @PostMapping("/create")
    public String createNotice(HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("recordForm") NoticeEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes
    		) throws IOException {
    	String searchId= "";
    	searchId = String.valueOf(record.getId());
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/cms/notice/edit?searchId="+searchId;
		 }
	   	 
    	try {
    		
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setCreateId(loginadminInfo.getId());
     		record.setModifiedId(loginadminInfo.getId());
    		
     		if(StringUtils.isBlank(record.getTopYn())) {
     			record.setTopYn("0");	
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
    	
    	return "redirect:/cms/notice/edit?searchId="+searchId;
    }
    
    /**
     * 
     * - 공지사항 업데이트
     *
     * @param record 공지사항 정보
     */
    @PostMapping("/update")
    public String updateNotice(HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("recordForm") NoticeEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes
    		) throws IOException {
    	
    	 String searchId= "";
    	 searchId= String.valueOf(record.getId());
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/cms/notice/edit?searchId="+searchId;
		 }
	   	 
    	try {
    		
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setModifiedId(loginadminInfo.getId());
     		
     		if(StringUtils.isBlank(record.getDisplayYn())) {
     			record.setDisplayYn("0");	
     		}
     		if(StringUtils.isBlank(record.getTopYn())) {
     			record.setTopYn("0");	
     		}
     		
    		
    		
    		service.update(record);
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
    		redirectAttributes.addFlashAttribute("procSp", "u");
    	} catch (IllegalArgumentException e) {
    		redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        }
    	
    	return "redirect:/cms/notice/edit?searchId="+searchId;
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 공지사항 삭제
     *
     * @param delId 삭제할 공지사항 ID
     */
    @DeleteMapping("/delete")
    public ResponseEntity<CustomApiResponse<String>> deleteNotice(HttpServletResponse response,
    		@RequestParam("id") int delId
    		) throws IOException {
    	
    	try {
    		service.delNotice(delId);
    		return CustomApiResponse.success(ResponseCode.OK, "delete success");
    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "deleteNotice: " + e.getMessage());
        } catch (Exception e) {
        	e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "deleteNotice: " + e.getMessage());
        }
    }
}
