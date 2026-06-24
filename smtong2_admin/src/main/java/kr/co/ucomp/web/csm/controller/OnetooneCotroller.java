package kr.co.ucomp.web.csm.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.csm.dto.OnetooneDto;
import kr.co.ucomp.web.csm.entity.OnetooneEntity;
import kr.co.ucomp.web.csm.service.OnetooneService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 김재희
 * @since 2024.12.20
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/csm/onetoone")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class OnetooneCotroller {

    @Autowired private OnetooneService onetooneService;
    @Autowired CommCodeMngService commCodeMngService;

    @ResponseBody
    @PostMapping("/ajaxlist")
    public ResponseEntity<CustomApiResponse<List<OnetooneEntity>>> ajaxlist(
            HttpServletRequest request,
            @RequestBody OnetooneDto param
    ) throws IOException {
    	List<OnetooneEntity> resultList = new ArrayList<OnetooneEntity>(); 
        try{

        	long count = onetooneService.getListCount(param);
        	if(count > 0) {
        		resultList = onetooneService.getList(param);	
        	}

            return CustomApiResponse.success(ResponseCode.OK, count, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getOneToOneList: " + e.getMessage());

        }

    }

    @ResponseBody
    @DeleteMapping("/delete")
    public ResponseEntity<CustomApiResponse<String>> deleteOneToOne(
            HttpServletRequest request,
            @RequestParam("id") int delId
    ) throws IOException {

        try{

            onetooneService.delete(delId);
            return CustomApiResponse.success(ResponseCode.OK, "삭제완료");

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "deleteOneToOne: " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "deleteOneToOne: " + e.getMessage());

        }

    }



    @PostMapping("/update")
    public String updateOneToOne(HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("recordForm") OnetooneEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes
    ) throws IOException {

    	String searchId= "";
    	searchId= String.valueOf(record.getId());
    	
    	if (bindingResult.hasErrors()) {
	        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
	        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
	        redirectAttributes.addFlashAttribute("record", record);
	        return "redirect:/csm/onetoone/edit?searchId="+searchId;
    	}
    	
        try{
        	HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setModifiedId(loginadminInfo.getId());
     		record.setResponseUser(loginadminInfo.getId());
            onetooneService.update(record);
            
            redirectAttributes.addFlashAttribute("procMsg", "sucess");

        } catch (IllegalArgumentException e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        }
        
        return "redirect:/csm/onetoone/edit?searchId="+searchId;

    }
    
    
    /**
     * 
     * @param request
     * @param response
     * @param record
     * @return
     * @throws IOException
     */
	@ResponseBody
    @PostMapping("/ajaxUpdate")
    public ResponseEntity<CustomApiResponse<OnetooneEntity>> ajaxUpdate(HttpServletRequest request,HttpServletResponse response,
    		@RequestBody OnetooneEntity record
    		) throws IOException {
    	try {
    		
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setModifiedId(loginadminInfo.getId());
     		
     		onetooneService.update(record);
    		return CustomApiResponse.success(ResponseCode.OK, record);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "noticeList: " + e.getMessage());
		}
    }
    
    
    
    
   /**
    * 
    * @param request
    * @param response
    * @param model
    * @return
    */
 	@GetMapping("/list")
 	public String  listview( HttpServletRequest request,HttpServletResponse response,Model model)  {
 		
 		CommCodeSearchDto codeparam = new CommCodeSearchDto();
 		codeparam.setPage(1);
 		codeparam.setRecordSize(999);
 		codeparam.setCodeGroup("onetoone_cate");
 		List<CodeEntity> codeList = commCodeMngService.getListCode(codeparam);
 		
 		model.addAttribute("codeList", codeList);    
 		
 		
		codeparam =  new CommCodeSearchDto();
		codeparam.setCodeGroup("common_env_code");
		codeparam.setCode("admin_alam_onetoone");
		CodeEntity alamCode =  commCodeMngService.getCodeInfo(codeparam);
		model.addAttribute("alamCode", alamCode);   
		
 		
 		return "pages/csm/onetoone/list";
 	}
 	
 	
 	

    @GetMapping("/edit")
    public String editView(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="searchId" , required = false) String searchId,Model model) throws IOException {
    	OnetooneEntity record = new OnetooneEntity();
        try{

        	CommCodeSearchDto codeparam = new CommCodeSearchDto();
     		codeparam.setPage(1);
     		codeparam.setRecordSize(999);
     		codeparam.setCodeGroup("onetoone_cate");
     		List<CodeEntity> codeList = commCodeMngService.getListCode(codeparam);
     		model.addAttribute("codeList", codeList);
    		if(model.getAttribute("org.springframework.validation.BindingResult.record") !=null) {
    			record = (OnetooneEntity) model.getAttribute("record");
    		    model.addAttribute("record", record); 
    		    model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
    		} else {		
    			if(StringUtils.isNotBlank(searchId)) {
        			record = onetooneService.getDetail(Integer.valueOf(searchId));
        		}
    			model.addAttribute("record", record); 
    		}    	
    		
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "pages/csm/onetoone/edit";
    }
}
