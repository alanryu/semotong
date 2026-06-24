package kr.co.ucomp.web.plan.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kr.co.ucomp.common.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.plan.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.SalesPlanEntity;
import kr.co.ucomp.web.plan.service.PlanSalesService;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/b2b")
@Slf4j
public class PlanSalesController {
	
	@Autowired
	private PlanSalesService planSalesService;
	
	@Autowired 
	private PlanService planService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	/**
	 * 영업상품화면 진입
	 * @param request
	 * @param response
	 * @param model
	 * @param comUserId
	 * @param idx
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/{encUrl}")
	public String salesPlanList( HttpServletRequest request ,  HttpServletResponse response, Model model, @PathVariable("encUrl") String encUrl) {

		String returnPage = "pages/plan/sales";
		String decUrl = CommonUtil.decodeBase64UrlSafe(encUrl);
		String[] parts = decUrl.split("/");
		String adminUserId = parts[0];
		long idx = 0;
		if (parts.length > 1) {
			try {
				idx = Long.parseLong(parts[1]);
			} catch (NumberFormatException e) {
				System.out.println("숫자 변환 실패: " + parts[1]);
			}
		}

		try {

			PlanSalesSearchDto param = new PlanSalesSearchDto();
			param.setAdminUserId(adminUserId);
			param.setIdx(idx);
			param.setUseYn("1");

			SalesPlanEntity entity = planSalesService.getPlanSales(param);

			if ( entity != null ) {

				if ( !StringUtils.isEmpty(entity.getImagePc()) ) {
					Map<String, Object> map = MAPPER.readValue(entity.getImagePc(), Map.class);
					entity.setImagePc(map.get("fileUrl").toString());
				}
			} else {
				returnPage = "redirect:/";
			}

			model.addAttribute("entity", entity);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return returnPage;
	}
	
	/**
	 * 영업상품 리스트
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@PostMapping(value = "/getPlanlist")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getReviewList( HttpServletRequest request, @RequestBody SearchPlanDto param ) throws IOException {

		try{
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
			long resulCnt = 0;
			
			HttpSession 		session 	= request.getSession(false);
			UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
			if(loginInfo!=null && StringUtils.isNoneBlank( loginInfo.getKakaoUserId())) {
				param.setSearchUserId((int) loginInfo.getId());
			}
			
			resulCnt = planService.getListCount(param);
	   	 
			if(resulCnt>0) {
				resultList = planService.getList(param);
			}	

			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}
	
	/**
	 * 영업용 요금제 상세
	 * @param request
	 * @param response
	 * @param model
	 * @param adminUserId
	 * @return
	 */
	@GetMapping("/{encUrl}/planDetail")
	public String planDetail( HttpServletRequest request ,  HttpServletResponse response, Model model, @PathVariable("encUrl") String encUrl
			, @RequestParam("planid") int planId
			, @RequestParam("recomSalesId") Integer recomSalesId) {

		PlanEntity result = new PlanEntity();
		result = planService.getDetail(planId);

		model.addAttribute("result", result);
		model.addAttribute("planid", planId);
		model.addAttribute("recomSalesId",recomSalesId);


		return "pages/plan/salesDetail";
	}
}
