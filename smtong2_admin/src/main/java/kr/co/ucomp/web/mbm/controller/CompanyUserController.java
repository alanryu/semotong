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
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.AdminUserSearchDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.AdminUserEntity;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.AdminUserService;
import kr.co.ucomp.web.mbm.service.CompanyListService;

/**
*
* @author 조일근
* @since 2024.12.11
* @version v1.0
*/
@Controller
@RequestMapping("/mbm/companyuser")
public class CompanyUserController {
	@Autowired AdminUserService service;
	@Autowired PasswordEncoder pwEncoder;
	@Autowired CompanyListService companyListService;
	@Autowired FileService fileService;
	
	
    /**
     * 2024-12-10 (화) 조일근
     * - admin 사용자 리스트 조회
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
	@ResponseBody
    @PostMapping("/getlist")
    public ResponseEntity<CustomApiResponse<List<AdminUserEntity>>> getList( HttpServletResponse response,
    		@RequestBody AdminUserSearchDto searchRequest
    		) throws IOException {
    	
    	List<AdminUserEntity> list = null;
    	
    	try {
    		long cnt = service.getListCount(searchRequest);
    		if(cnt > 0) {
    			list =  service.getList(searchRequest);	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK, cnt , list);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    		
    }
    
	@ResponseBody
    @PostMapping("/exceldown")
	public ResponseEntity<byte[]> downPlanMap(@RequestBody AdminUserSearchDto param) throws Exception {
    	
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>(); 
    	List<AdminUserEntity> list =  service.getList(param);
    	for (AdminUserEntity itm  :  list) {
        	Map<String, Object> data = new LinkedHashMap<String, Object>();
        	data.put("id", itm.getId());
        	data.put("adminUserName", itm.getAdminUserName());
        	data.put("adminId", itm.getAdminId());
        	data.put("companyNm", itm.getCompanyNm());
        	data.put("businessNm", itm.getBusinessNm());
        	data.put("companyMno", itm.getCompanyMno());
        	data.put("createDate", itm.getCreateDate());
        	dataList.add(data);    		
    	}

    	
    	// 엑셀 헤더 설정
    	String[] headers = {"번호", "이름", "아이디", "사업자명", "회사", "통신사구분", "등록일"};
    	
        byte[] excelData = fileService.getExcelData(headers,dataList);

        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=userList.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
    
    
	@ResponseBody
    @PostMapping("/getUserById")
    public ResponseEntity<CustomApiResponse<AdminUserDto>> getUserById( HttpServletResponse response,
    		@RequestBody AdminUserSearchDto searchRequest
    		) throws IOException {
    	
		AdminUserDto info = new AdminUserDto();
    	
    	try {

    		if(searchRequest !=null && StringUtils.isNoneBlank(searchRequest.getSearchAdminid())) {
    			info =  service.getDetailById(searchRequest.getSearchAdminid());	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK, info);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    		
    }
	
	
    
    /**
     * 2024-12-10 (화) 조일근
     * - admin 사용자 신규 입력
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    
    @PostMapping("/create")
    public String create( HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute("record") AdminUserEntity record , BindingResult bindingResult,Model model, RedirectAttributes redirectAttributes) throws IOException {
    	
    	 if (bindingResult.hasErrors()) {
 	        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
 	        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
 	        redirectAttributes.addFlashAttribute("record", record);
 	        return "redirect:/mbm/companyuser/edit";
    	 }
    	
    	try {
    		
    		HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
    		
    		String encyptPw = pwEncoder.encode(record.getPassword());
    		record.setPassword(encyptPw);
    		record.setCreateId(loginadminInfo.getId());
    		record.setModifiedId(loginadminInfo.getId());
    		 
    		if("Y".equals(record.getAllAuthYn()) ) {
	   			record.setAllAuthYn("");
	   			record.setProdMngYn("Y");
	   			record.setReqMngYn("Y");
	   			record.setSettleMngYn("Y");
	   		}
	   		
    		
    		service.create(record);
    		 redirectAttributes.addFlashAttribute("procMsg", "sucess");
        } catch (IllegalArgumentException e) {
        	redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
        	return "redirect:/mbm/companyuser/edit?id=";
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
        	return "redirect:/mbm/companyuser/edit?id=";
        }
    	return "redirect:/mbm/companyuser/edit?id=" + record.getId() + "&type=create";
    }
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - admin 사용자 업데이트
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    @PostMapping("/update")
    public String update( HttpServletRequest request, HttpServletResponse response,
    		@Valid @ModelAttribute AdminUserEntity record , BindingResult bindingResult,Model model, RedirectAttributes redirectAttributes) throws IOException {
    	
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/mbm/companyuser/edit";
	 	 }
	 	
    	try {
    		
    		HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
    		record.setModifiedId(loginadminInfo.getId());
    		
    		
	   		String encyptPw = pwEncoder.encode(record.getPassword());
	   		record.setPassword(encyptPw);
	   		if("Y".equals(record.getAllAuthYn()) ) {
	   			record.setAllAuthYn("");
	   			record.setProdMngYn("Y");
	   			record.setReqMngYn("Y");
	   			record.setSettleMngYn("Y");
	   		}
	   		
    		service.update(record);
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
        } catch (IllegalArgumentException e) {
        	redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
        	return "redirect:/mbm/companyuser/edit?id=";
        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
        	return "redirect:/mbm/companyuser/edit?id=";
        }
    	
    	return "redirect:/mbm/companyuser/edit?id=" + record.getId() + "&type=update";
    	
    }
    
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - admin 사용자 비밀번호 업데이트
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    @ResponseBody
    @PostMapping("/updatePwd")
    public ResponseEntity<CustomApiResponse<AdminUserEntity>> updatePwd( HttpServletRequest request, HttpServletResponse response,
    		@RequestBody AdminUserEntity record ,Model model) throws IOException {
    	
    	try {
    		
    		HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
    		record.setModifiedId(loginadminInfo.getId());
    		
	   		String encyptPw = pwEncoder.encode(record.getPassword());
	   		//record.setPassword(encyptPw);
   		 
	   		/* adminuser객체 새로 생성 */
	   		AdminUserEntity temp = service.getDetail(record.getId());
	   		temp.setModifiedId(loginadminInfo.getId());
	   		temp.setPassword(encyptPw);
   		 
    		service.update(temp);
    		return CustomApiResponse.success(ResponseCode.OK, record);
    	} catch (Exception e) {
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
        }
    	
    }
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - admin 사용자  상세 조회
     *
     * @param searchRequest               서치 params
     * @param List<ScrapingLogEntity> 로그 조회 리스트
     */
    @ResponseBody
    @DeleteMapping("/delete")
    public ResponseEntity<CustomApiResponse<String>> delete( HttpServletResponse response,
    		@RequestParam("delId") String delId
    		) throws IOException {
    	
    	try {
    		service.del(Integer.parseInt(delId));
    		 return CustomApiResponse.success(ResponseCode.OK, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
        }
    }
    
    
		   /**
	  * 2024-12-18 조일근
	  *
	  * @param CommCodeSearchDto
	  * @param List<CodeGroupEntity> 코드그룹 리스트
	  */
	 @GetMapping("/list")
	 public String  listview( HttpServletResponse response,Model model)  {
	 	
	 	return "pages/mbm/companyuser/list";
	 }
	 
	 
	 /**
	* 2024-12-18 조일근
	*
	* @param CommCodeSearchDto
	* @param List<CodeGroupEntity> 코드그룹 리스트
	*/
	@GetMapping("/edit")
	public String  editview( HttpServletResponse response,Model model
			,@RequestParam(value="id", defaultValue = "") String searchId)  {
		
		AdminUserEntity  record =  new AdminUserEntity();
		
		if(model.getAttribute("org.springframework.validation.BindingResult.record") !=null) {
			record = (AdminUserEntity) model.getAttribute("record");
		    model.addAttribute("record", record); 
		    model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
		} else {		
			if (StringUtils.isNoneBlank(searchId)) {
			 record =  service.getDetail(Integer.parseInt(searchId));
			 
			 if("Y".equals(record.getProdMngYn()) && "Y".equals(record.getReqMngYn()) && "Y".equals(record.getSettleMngYn()) ) {
				 	record.setAllAuthYn("Y");
				 	record.setProdMngYn("");
		   			record.setReqMngYn("");
		   			record.setSettleMngYn("");
			 }
	        }
		}
		
		CompanyListSearchDto searchRequest = new CompanyListSearchDto();
		searchRequest.setSearchUseYn(1);
		List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
		model.addAttribute("compnyList", compnyList);
		 
		model.addAttribute("record", record);
		return "pages/mbm/companyuser/edit";
	}
    
    
}
