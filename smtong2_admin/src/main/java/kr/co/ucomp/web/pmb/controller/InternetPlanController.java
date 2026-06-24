package kr.co.ucomp.web.pmb.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.csm.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.entity.SemotongReviewEntity;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.pmb.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.pmb.entity.InternetPlanEntity;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.pmb.service.InternetPlanService;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping( value = "/pmb/internetplan" )
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'PROD_MNG')")
public class InternetPlanController {
	
	@Autowired private InternetPlanService service;
	@Autowired FileService fileService;
	
	/**
	 * Internet Plan
	 *  - 전체 only
	 * @param request
	 * @param 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/ajaxList")
	public ResponseEntity<CustomApiResponse<List<InternetPlanEntity>>> ajaxList (HttpServletRequest request, @RequestBody InternetPlanSearchDTO param) throws Exception 
	{
		Long totCnt  						= null;
		List<InternetPlanEntity> resultList 	= null;
		
		try {
			totCnt  			= service.count(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= service.list(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan list: " + e.getMessage());
		}
	}

	
	
	
	
	/**
	 * Internet Plan 상세를 생성한다.
	 * @param request
	 * @param ent
	 * @return
	 * @throws Exception
	 */
	@PostMapping( value = "/create" )
	public String create (HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute("recordForm") InternetPlanEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException 
	{
		String searchId= "";
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/pmb/internetplan/edit/"+record.getSiteSp();
		 }
	   	 
		try {
			
			HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setCreateId(loginadminInfo.getId());
     		record.setModifiedId(loginadminInfo.getId());
     		
			service.create(record);
			searchId = String.valueOf(record.getId());
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
    		
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		}
		
		return "redirect:/pmb/internetplan/edit/"+record.getSiteSp()+"?searchId="+searchId;
	}
	
	/**
	 * Internet Plan 상세를 수정한다.
	 * @param request
	 * @param ent
	 * @return
	 * @throws Exception
	 */
	@PostMapping( value = "/update" )
	public String update (HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute("recordForm") InternetPlanEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException 
	{
		String searchId= "";
		searchId = String.valueOf(record.getId());
	   	 if (bindingResult.hasErrors()) {
		        // 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
		        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
		        redirectAttributes.addFlashAttribute("record", record);
		        return "redirect:/pmb/internetplan/edit/"+record.getSiteSp()+"?searchId="+searchId;
		 }
		try {
			
			HttpSession session = request.getSession();
     		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
     		record.setCreateId(loginadminInfo.getId());
     		record.setModifiedId(loginadminInfo.getId());
     		
			service.update(record);
			
    		redirectAttributes.addFlashAttribute("procMsg", "sucess");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		}
		
		return "redirect:/pmb/internetplan/edit/"+record.getSiteSp()+"?searchId="+searchId;
	}
	
	/**
	 * Event 상세를 삭제한다.
	 * @param request
	 * @param ent
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping( value = "/delete" )
	public ResponseEntity<CustomApiResponse<InternetPlanEntity>> delete (HttpServletRequest request,
	@RequestBody InternetPlanEntity ent) throws IOException
	{
		try {
			service.delete(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "Internet Plan delete: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Internet Plan delete: " + e.getMessage());
		}
	}
	
	
	
	
	

	
	/**
	 * 인터넷 요금제 리스트 화면
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/list/{siteSp}")
	public String  listview( HttpServletResponse response,Model model, @PathVariable("siteSp") String siteSp)  {
		
		List<InternetPlanMnoEntity> compnyList = service.getInternetPlanMno("Y","N","");
		model.addAttribute("compnyList", compnyList);
		model.addAttribute("siteSp", siteSp);
		
		return "pages/pmb/internet/list";
	}
    
	
	/**
	 * Internet Plan 상세 내역을 조회 한다.
	 * @param request
	 * @param param /detail
	 * @return
	 * @throws Exception
	 */
	@GetMapping( value = "/edit/{siteSp}" )
	public String edit(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value="searchId" , required = false)String searchId,
			@PathVariable("siteSp") String siteSp,
			Model model) throws Exception 
	{
		InternetPlanEntity record =new InternetPlanEntity();
    	try {
    		List<InternetPlanMnoEntity> compnyList = service.getInternetPlanMno("Y","N","");
    		model.addAttribute("compnyList", compnyList);
    		
    		if(model.getAttribute("org.springframework.validation.BindingResult.record") !=null) {
    			record = (InternetPlanEntity) model.getAttribute("record");
    		    model.addAttribute("record", record); 
    		    model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
    		} else {		
    			if(StringUtils.isNotBlank(searchId)) {
        			record = service.getDetail(Integer.valueOf(searchId));	
        		}
    		}    		
  
    	} catch (Exception e) {
    		
		}
		model.addAttribute("record", record);    	
		model.addAttribute("siteSp", siteSp);
    	return "pages/pmb/internet/edit";
	}
	
	
	
	
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown (HttpServletRequest request, @RequestBody InternetPlanSearchDTO param) throws Exception 
	{
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		List<InternetPlanEntity> resultList		= service.listWithOutLimit(param);
		
		for (InternetPlanEntity itm  :  resultList) {
			
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("useYn"				, itm.getUseYn()					);
			data.put("internetMno"			, itm.getInternetMno()				);
			data.put("prodName"				, itm.getProdName()					);
			data.put("channelCount"			, itm.getChannelCount()				);
			data.put("internetSpeed"			, itm.getInternetSpeed()			);
			data.put("normalPrice"				, itm.getNormalPrice()				);
			data.put("combinationPrice"			, itm.getCombinationPrice()			);
			data.put("combinationFreeblePrice"	, itm.getCombinationFreeblePrice()	);
			data.put("modifiedDate"				, itm.getModifiedDate()				);
			dataList.add(data);			
		}
		
		// 엑셀 헤더 설정
		String[] headers = {"상태", "통신사", "상품명", "TV", "인터넷", "미결합금액", "결합금액", "사은품", "최근수정일"};
		
		byte[] excelData = fileService.getExcelData(headers,dataList);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

}
