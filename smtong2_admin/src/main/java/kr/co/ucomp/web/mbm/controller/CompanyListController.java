package kr.co.ucomp.web.mbm.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/mbm/company") 
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'COMPANY_MNG')")
public class CompanyListController {
	
	@Autowired CompanyListService service;
	@Autowired FileService fileService;
	
    /**
     * 2024-12-19 (목) 백신의
     * - 사업자 목록
     *
     * @param searchRequest               서치 params
     * @param List<CompanyListEntity> 사업자 조회 리스트
     */
	@ResponseBody
    @PostMapping("/ajaxList")
    public ResponseEntity<CustomApiResponse<List<CompanyListEntity>>> ajaxList(HttpServletResponse response,
    		@RequestBody CompanyListSearchDto searchRequest
    		) throws IOException {
    	
    	List<CompanyListEntity> list = null;
    	
    	try {
    		long totCnt = service.getListCompanyListCount(searchRequest);
    		if(totCnt > 0) {
    			list = service.getListCompanyList(searchRequest);
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK,totCnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    }
    
	
	@ResponseBody
    @PostMapping("/exceldown")
	public ResponseEntity<byte[]> downPlanMap(@RequestBody CompanyListSearchDto param) throws Exception {
    	
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>(); 
    	List<CompanyListEntity> list =  service.getListCompanyList(param);
    	for (CompanyListEntity itm  :  list) {
        	Map<String, Object> data = new LinkedHashMap<String, Object>();
        	data.put("id", itm.getId());
        	data.put("name", itm.getName());
        	data.put("companyNm", itm.getCompanyNm());
        	data.put("companyCode", itm.getCompanyCode());
        	data.put("companyMno", itm.getCompanyMno());
        	data.put("createDate", itm.getCreateDate());
        	dataList.add(data);    		
    	}

    	
    	// 엑셀 헤더 설정
    	String[] headers = {"번호", "사업자명", "회사","사업자코드","통시사구분","등록일"};
    	
        byte[] excelData = fileService.getExcelData(headers,dataList);

        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=userList.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 사업자 신규 입력
     *
     * @param record 사업자 정보
     */
    @PostMapping("/create")
    public String createCompany(HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("recordForm") CompanyListEntity record,@RequestParam("uploadFile") MultipartFile uploadFile, 
    		BindingResult bindingResult, RedirectAttributes redirectAttributes
    		) throws IOException {
    	String searchId= "";
    	 if (bindingResult.hasErrors()) {
    	        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
    	        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
    	        redirectAttributes.addFlashAttribute("record", record);
    	        return "redirect:/mbm/company/edit";
    	 }
    	
    	try {
    		HttpSession session = request.getSession();
	 		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
	 		record.setCreateId(loginadminInfo.getId());
			record.setModifiedId(loginadminInfo.getId());

			 if (uploadFile != null && !uploadFile.isEmpty()) {
//				 String imagePc = fileService.FileUpload("company", uploadFile);
				 String imageMoJson = fileService.FileUpload("company", uploadFile);
				 ObjectMapper mapper = new ObjectMapper();
				 Map<String, Object> map = mapper.readValue(imageMoJson, Map.class);
				 String imageSrc = (String)map.get("fileUrl");
				log.info("PC 이미지 업로드 결과 : {}", imageSrc);
				record.setLogoImg(imageSrc);
		    }
		 	
    		service.create(record);
    		searchId = String.valueOf(record.getId());
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
    		
        } catch (IllegalArgumentException e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
        }
    	
    	return "redirect:/mbm/company/edit?searchId="+searchId+"&type=create";
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 사업자 업데이트
     *
     * @param record 사업자 정보
     */
    @PostMapping("/update")
    public String updateCompany( HttpServletRequest request, HttpServletResponse response,@RequestParam("uploadFile") MultipartFile uploadFile,
    		@Valid @ModelAttribute("recordForm") CompanyListEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes
    		) throws IOException {
    	String searchId = String.valueOf(record.getId());
    	try {
    		
    		
    		 if (bindingResult.hasErrors()) {
     	        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
     	        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
     	        redirectAttributes.addFlashAttribute("record", record);
     	        return "redirect:/mbm/company/edit?searchId="+searchId;
    		 }
    		 
    		 
    		 
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setModifiedId(loginadminInfo.getId());
     		
			 if (uploadFile != null && !uploadFile.isEmpty()) {
				String imageMoJson = fileService.FileUpload("company", uploadFile);
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = mapper.readValue(imageMoJson, Map.class);
				String imageSrc = (String)map.get("fileUrl");
				record.setLogoImg(imageSrc);
		    }     		
    		service.update(record);
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
    		
    	} catch (IllegalArgumentException e) {
    		redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
        }
    	
    	
    	
    	return "redirect:/mbm/company/edit?searchId="+searchId+"&type=update";
    	
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 사업자 삭제
     *
     * @param delId 삭제할 사업자 ID
     */
    @DeleteMapping("/delete")
    public ResponseEntity<CustomApiResponse<String>> deleteCompany(HttpServletResponse response,
			@RequestParam("id") Integer delId
    		) throws IOException {
    	
    	try {
    		service.delCompanyList(delId);
    		return CustomApiResponse.success(ResponseCode.OK, "delete success");
    	} catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete Company: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete Company: " + e.getMessage());
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
		public String  companyList( HttpServletRequest request, HttpServletResponse response,Model model)  {
			
			return "pages/mbm/company/list";
		}
		
	   /**
	    * 
	    * @param request
	    * @param response
	    * @param model
	    * @return
	    */
		@GetMapping("/edit")
		public String  companyEdit(@RequestParam(value="searchId" , required = false) String searchId, HttpServletRequest request, HttpServletResponse response,Model model)  {
			
			CompanyListEntity record = new CompanyListEntity();
			
			if(model.getAttribute("org.springframework.validation.BindingResult.record") !=null) {
				record = (CompanyListEntity) model.getAttribute("record");
			    model.addAttribute("record", record); 
			    model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
			} else {
				try {
					if(StringUtils.isNoneEmpty(searchId)) {
						record = service.getCompanyList(Integer.parseInt(searchId));
					}
					
		    		
		    	} catch (Exception e) {
		    		
				}
				 model.addAttribute("record", record);
			}
	    	
			 
			return "pages/mbm/company/edit";
		}

}
