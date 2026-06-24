package kr.co.ucomp.web.plan.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.plan.service.PlanService;
import kr.co.ucomp.web.plan.service.RecomPlanMngService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.plan.dto.RecomPlanMngSearchDto;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.RecomPlanEntity;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 조일근
 * @since 2025.04.25
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/svc/recomPlan")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class RecomPlanMngController {


  @Autowired
  private RecomPlanMngService service;

  @Autowired PlanService planService;


  @ResponseBody
	@PostMapping(value = "/getPlanlist")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getReviewList(
			HttpServletRequest request,
			@RequestBody SearchPlanDto param
	) throws IOException {

		try{
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
			long resulCnt = 0;
			
			resulCnt = planService.getRecomPlanListCount(param);
		   	 
			if(resulCnt>0) {
				resultList = planService.getRecomPlanList(param);
			}	

			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}
  
  


}
