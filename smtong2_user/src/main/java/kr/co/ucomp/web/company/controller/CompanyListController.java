package kr.co.ucomp.web.company.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.condition.ParamsRequestCondition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.company.dto.CompanyListSearchDto;
import kr.co.ucomp.web.company.entity.CompanyListEntity;
import kr.co.ucomp.web.company.service.CompanyListService;
import kr.co.ucomp.web.csm.review.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.review.entity.SemotongReviewEntity;
import kr.co.ucomp.web.csm.review.service.SemotongReviewService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/mbm/company") 
@Slf4j
public class CompanyListController {
	
	@Autowired CompanyListService service;
	@Autowired  private SemotongReviewService reviewService;
	
    /**
     * 2024-12-19 (목) 백신의
     * - 사업자 목록
     *
     * @param searchRequest               서치 params
     * @param List<CompanyListEntity> 사업자 조회 리스트
     */
	@ResponseBody
    @PostMapping("/list")
    public ResponseEntity<CustomApiResponse<List<CompanyListEntity>>> companyList(HttpServletResponse response,
    		@RequestBody  CompanyListSearchDto searchRequest
    		) throws IOException {
    	
    	List<CompanyListEntity> list = null;
    	
    	try {
    		list = service.getListCompanyList(searchRequest);
    		return CustomApiResponse.success(ResponseCode.OK, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    }
    
    /**
     * 2024-12-19 (목) 백신의
     * - 사업자 상세 조회
     *
     * @param searchRequest               서치 params
     * @param CompanyListEntity 사업자 상세 정보
     */
	@ResponseBody
    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<CompanyListEntity>> getCompany(HttpServletResponse response,
    		@PathVariable("id") int searchId
    		) throws IOException {
    	
    	try {
    		CompanyListEntity info = service.getCompany(searchId);
    		if (info == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND, "요청 파라메터에 대한 사업자 정보가 존재 하지 않습니다.");
            }
    		return CustomApiResponse.success(ResponseCode.OK, info);
    	} catch (Exception e) {
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getCompany: " + e.getMessage());
		}
    }
	
	
	
	
	
	
	/**
     * 2025-03-11
     * - 사업자 목록(리뷰 화면용)
     *
     * @param searchRequest               서치 params
     * @param List<CompanyListEntity> 사업자 조회 리스트
     */
	@ResponseBody
    @PostMapping("/reviewComplist")
    public ResponseEntity<CustomApiResponse<List<CompanyListEntity>>> reviewComplist(HttpServletRequest request, HttpServletResponse response
    		) throws IOException {
    	
    	List<CompanyListEntity> list = null;
    	
    	try {
    		
    		HttpSession 		session 	= request.getSession(false);
    		UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
    		
    		list = service.getReviewCompanyList(loginInfo.getId());
    		return CustomApiResponse.success(ResponseCode.OK, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    }
    
    //==================================== 화면 컨트롤러
    
	   /**
	  * 2024-12-18 조일근
	  *
	  * @param model
	  * @param 파트너사 화면 
	  */
	 @GetMapping("/companeyList")
	 public String  companeyList(HttpServletResponse response,Model model)  {
	 
	 	
	 	return "pages/company/companeyList";
	 }
	 
	 
	 /**
	  * 2024-12-18 조일근
	  *
	  * @param model
	  * @param 파트너사 화면 
	  */
	 @GetMapping("/companeyInfo")
	 public String  companeyInfo( HttpServletRequest request, HttpServletResponse response, Model model)  {
	 	
			
			String searchId = request.getParameter("searchId");
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+searchId);
			if(StringUtils.isNoneBlank(searchId)) {
				CompanyListEntity info = service.getCompany(Integer.parseInt(searchId));
				 model.addAttribute("companyInfo", info);
			}
		 	
			
		// 리뷰 집계 정보
		SemotongReviewDto param = new SemotongReviewDto();
		param.setCompanyId(searchId);
		param.setReviewType("SEMOTONG");
		SemotongReviewEntity reviewAg = reviewService.reviewAggregate(param);

		model.addAttribute("reviewAg", reviewAg);
		
		
		model.addAttribute("searchId", request.getParameter("searchId"));	
		 model.addAttribute("companyNm", request.getParameter("companyNm"));
	 	return "pages/company/companeyInfo";
	 }
}
