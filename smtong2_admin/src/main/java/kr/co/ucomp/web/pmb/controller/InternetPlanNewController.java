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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.pmb.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.entity.InternetPlanEntity;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.pmb.service.InternetPlanNewService;
import kr.co.ucomp.web.pmb.service.InternetPlanService;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping( value = "/pmb/internetplanNew" )
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'PROD_MNG')")
public class InternetPlanNewController {
	
	@Autowired private InternetPlanNewService service;
	@Autowired private InternetPlanService orgService;
	@Autowired FileService fileService;
	
	//=============================================== 인터넷 mno 관리 =========================================	
	/**
	 * Internet Plan
	 *  - 전체 only
	 * @param request
	 * @param 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/ajaxListMno")
	public ResponseEntity<CustomApiResponse<List<InternetPlanMnoEntity>>> ajaxList (HttpServletRequest request) throws Exception 
	{
		List<InternetPlanMnoEntity> resultList 	= null;
		try {
			resultList 		= orgService.getInternetPlanMno("","Y","");
			int totCnt = resultList.size();
			
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan list: " + e.getMessage());
		}
	}


	
	@ResponseBody
	@PostMapping(value = "/ajaxUpdateMno")
	public ResponseEntity<CustomApiResponse<InternetPlanMnoEntity>> ajaxUpdate(
			HttpServletRequest request,
			@RequestBody InternetPlanMnoEntity param) throws IOException {

		try {

			HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");    		
    		param.setModifiedId(loginadminInfo.getId());
    		
			service.updateMno(param);
			return CustomApiResponse.success(ResponseCode.OK, param);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}
	}	
	
	
	
	//=============================================== 인터넷 mno별 요금제 관리 =========================================
	/**
	 * 인터넷 요금제 리스트 화면(mno 리스트 화면)
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String  listview( HttpServletResponse response,Model model)  {
		
		
		return "pages/pmb/internetNew/list";
	}
		
	
	/**
	 * Internet 사업자별 인터넷 리스트를 조회 한다.
	 * @param request
	 * @param param /detail
	 * @return
	 * @throws Exception
	 */
	@GetMapping( value = "/edit" )
	public String edit(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value="searchId" , required = false)String searchId,
			Model model) throws Exception 
	{
		
		List<InternetPlanEntity> planList = new ArrayList<InternetPlanEntity>();
		
    	try {
    		List<InternetPlanMnoEntity> compnyList = orgService.getInternetPlanMno("","Y",searchId);
    		model.addAttribute("compny", compnyList.get(0));
    		 
    		
    		if(StringUtils.isNoneBlank(searchId) ) {
    			InternetPlanSearchDTO param = new InternetPlanSearchDTO();
    			param.setSearchInternetMno(searchId);
    			planList = service.list(param);
				
				
			}
    		
    		
    	} catch (Exception e) {
    		
		}
    	
    	model.addAttribute("planList", planList);
    	model.addAttribute("searchId", searchId);
    	
    	return "pages/pmb/internetNew/edit";
	}
	
	
	@ResponseBody
	@PostMapping(value = "/ajaxUpdateMnoPlan")
	public ResponseEntity<CustomApiResponse<InternetPlanEntity>> ajaxUpdateMnoPlan(
			HttpServletRequest request,
			@RequestBody InternetPlanEntity param) throws IOException {

		try {

			HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");    		
    		param.setModifiedId(loginadminInfo.getId());
    		
			service.update(param);
			return CustomApiResponse.success(ResponseCode.OK, param);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}	

}
