package kr.co.ucomp.web.svc.randing.controller;

import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.svc.randing.dto.InternetRdpMngSearchDto;
import kr.co.ucomp.web.svc.randing.entity.InternetRdpMngEntity;
import kr.co.ucomp.web.svc.randing.service.InternetRdpMngService;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  조일근
 * @since   2025.05.29
 * @version v1.0
 */
@Controller
@AllArgsConstructor
@RequestMapping("/svc/internetrdp")
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class InternetRdpMngController {
	
	@Autowired InternetRdpMngService service;
    @Autowired FileService fileService;
    @Autowired CommCodeMngService codeService;

    @ResponseBody
    @PostMapping("/ajaxList")
    public ResponseEntity<CustomApiResponse<List<InternetRdpMngEntity>>> ajaxList (
            @RequestBody InternetRdpMngSearchDto param
    ) {
        try{
        	List<InternetRdpMngEntity> list = new ArrayList<InternetRdpMngEntity>(); 
        	long count = service.count(param);
        	if(count > 0 ) {
        		list = service.list(param);		
        	}
        
        
        return CustomApiResponse.success(ResponseCode.OK, count, list);
        } catch (Exception e){
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, ResponseCode.BAD_REQUEST.getMessage());
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
    public ResponseEntity<CustomApiResponse<InternetRdpMngEntity>> ajaxUpdate(HttpServletRequest request,HttpServletResponse response,
    		@RequestBody InternetRdpMngEntity record
    		) throws IOException {
    	try {
    		
    		HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setModifiedId(loginadminInfo.getId());
     		
    		service.updateEx(record);
    		return CustomApiResponse.success(ResponseCode.OK, record);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ajaxUpdate: " + e.getMessage());
		}
    }
    
    
    /**
     * 
     * 인터넷 랜딩 페이지  업데이트
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/insUpdProc")
    public String updateInfo(HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute ("record") InternetRdpMngEntity record,
			BindingResult bindingResult,Model model, RedirectAttributes redirectAttributes) 
	throws IOException {
    	int reqid = record.getId() ;
    	try {
    		
    		HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
    		
    		String procSp = "C";
    		String fileuploadResTop = ""; 
			String fileuploadResFoot = "" ; 
			String uploadFileTop = ""; 
			String uploadFileFoot = "" ;
			ObjectMapper mapper = new ObjectMapper();
    		try {
				if (!record.getTopFile().isEmpty() || record.getTopFile() !=null) {
					if(StringUtils.isNoneBlank(record.getTopFile().getOriginalFilename()) ) {
						fileuploadResTop = fileService.FileUpload("rdpbanner", record.getTopFile());
						
						Map<String, Object> map = mapper.readValue(fileuploadResTop, Map.class);
				        if (map.get("fileUrl") != null) {
				        	uploadFileTop = (String) map.get("fileUrl");
				        	record.setTopBanner(uploadFileTop);
				        }   
					}
					
				}
				
				if (!record.getFootFile().isEmpty() || record.getFootFile() !=null) {
					if(StringUtils.isNoneBlank(record.getFootFile().getOriginalFilename()) ) {
						fileuploadResFoot = fileService.FileUpload("rdpbanner", record.getFootFile());
						
						Map<String, Object> map = mapper.readValue(fileuploadResFoot, Map.class);
				        if (map.get("fileUrl") != null) {
				        	uploadFileFoot = (String) map.get("fileUrl");
				        	record.setFootBanner(uploadFileFoot);
				        }
				        
					}
				}
					
			} catch (Exception e) {
				// TODO: handle exception
			}
    		
    		String rdUrl = "/internet/landing/";
    		if(reqid == 0) {
    			record.setCreateId(loginadminInfo.getId());
    			service.create(record);
    			
    			reqid = record.getId() ;
    			
    			InternetRdpMngEntity record2 = new InternetRdpMngEntity();
    			record2.setModifiedId(loginadminInfo.getId());
    			record2.setId(reqid);
    			record2.setRdUrl(rdUrl + reqid);
    			service.updateEx(record2);
    			
    		} else {
    			InternetRdpMngEntity  info =  service.getDetail(reqid);
        		if(info == null) {
        			//  정보 없음
        			return "redirect:/svc/internetrdp/edit?searchId="+reqid;
        		}
        		record.setModifiedId(loginadminInfo.getId());
        		service.update(record);
        		procSp = "U";
    		}
    		
    		redirectAttributes.addFlashAttribute("procSp", procSp);
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
			
			return "redirect:/svc/internetrdp/edit?searchId=" + record.getId();
			
		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
			return "redirect:/svc/internetrdp/edit?searchId="+reqid;

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
			return "redirect:/svc/internetrdp/edit?searchId="+reqid;

		}
    	
    }
    
    
    
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown (HttpServletRequest request, @RequestBody InternetRdpMngSearchDto param) throws Exception 
	{
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		List<InternetRdpMngEntity> resultList		= service.list(param);
		
		for (InternetRdpMngEntity itm  :  resultList) {
			
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("id"				, itm.getId());
			data.put("title"			, itm.getTitle());
			data.put("rdUrl"			, itm.getRdUrl());
			data.put("useYn"			, itm.getUseYn());
			data.put("footBarText"		, itm.getFootBarText());
			data.put("btnCta"			, itm.getBtnCta());
			data.put("createDate"		, itm.getCreateDate()			);
			dataList.add(data);			
		}
		
		// 엑셀 헤더 설정
		String[] headers = {"번호", "제목", "랜딩url", "사용여부", "하단텓스트", "버튼CTA", "등록일시"};
		
		byte[] excelData = fileService.getExcelData(headers,dataList);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}
	

	/**
	 * 인터넷 신청 리스트 화면
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String  listview( HttpServletResponse response, Model model)  {
		
		InternetRdpMngEntity  record = new  InternetRdpMngEntity();
		record.setId(0);
		model.addAttribute("record", record);
		
		
		return "pages/svc/internetRdp/list";
	}
    
    
	
	/**
	 * 인터넷 신청 리스트 화면
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/edit")
	public String  listview(@RequestParam(value="searchId", defaultValue = "") String searchId, HttpServletResponse response,Model model)  {
		
	
		InternetRdpMngEntity  record =  service.getDetail(Integer.valueOf(searchId));
		if(record == null) {
			record = new InternetRdpMngEntity();
			record.setId(0);
			record.setUseYn("Y");
		}
		model.addAttribute("record", record);
		
		return "pages/svc/internetRdp/edit";
	}	

	
	/**
	 * 인터넷 신청 리스트 화면
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/reqList")
	public String  reqList( HttpServletResponse response, Model model,
			@RequestParam(value="searchId", defaultValue = "") String searchId)  {
		
		InternetRdpMngEntity  record = new  InternetRdpMngEntity();
		record.setId(0);
		model.addAttribute("record", record);
		model.addAttribute("searchId", searchId);
		
		
		
		return "pages/svc/internetRdp/reqList";
	}	
	
	
	
	
}
