package kr.co.ucomp.web.plan.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.config.LoginRequired;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.company.dto.CompanyListSearchDto;
import kr.co.ucomp.web.company.entity.CompanyListEntity;
import kr.co.ucomp.web.company.service.CompanyListService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.plan.dto.PlanReqMngDto;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanReqMngEntity;
import kr.co.ucomp.web.plan.service.PlanReqMngService;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pmb/plan-request")
@Slf4j
public class PlanReqMngController {

	@Autowired PlanReqMngService 		service			;
	@Autowired PlanService 				planService		;
	@Autowired CompanyListService companyListService;
	
	
	
	/**
	 * MyPage 에서 보는 [신청 요금제] 목록
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@PostMapping(value = "/getAppliedPlanList")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getAppliedPlanList( HttpServletRequest request, @RequestBody SearchPlanDto param) throws IOException {
		
		HttpSession 		session 	= request.getSession(false);
		UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
		
		try{
			
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
			
			// [신청 요금제] count 용.
			PlanReqMngDto searchRequest = new PlanReqMngDto();
			searchRequest.setSearchId(loginInfo.getId());
			
			
			List<String>	prodList = new ArrayList<String>();
			long 			resulCnt = service.getListCount(searchRequest);
			if(resulCnt != 0) {
				List<PlanReqMngEntity> reqlist = service.getList(searchRequest);
				for(PlanReqMngEntity itm : reqlist) {
					prodList.add(itm.getReqProd() + "");
				}
				param.setSearchplanIdList(prodList);
				resultList = planService.getAllListByPlanIds(param);
			}
			
			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	


    @ResponseBody
    @PostMapping("/list")
    public ResponseEntity<CustomApiResponse<List<PlanReqMngEntity>>> getList(
            HttpServletResponse response,
            @RequestBody PlanReqMngDto searchRequest) throws IOException {
        try {
            long cnt = service.getListCount(searchRequest);
            List<PlanReqMngEntity> list = null;
            if (cnt > 0) {
                list = service.getList(searchRequest);
            }
            return CustomApiResponse.success(ResponseCode.OK, cnt, list);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<PlanReqMngEntity>> getDetail(
            @PathVariable("id") Integer id) {
        try {
        	PlanReqMngEntity detail = service.getDetail(id);
            if (detail == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND);
            }
            return CustomApiResponse.success(ResponseCode.OK, detail);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<PlanReqMngEntity>> create(HttpServletRequest request,HttpServletResponse response,
            @RequestBody PlanReqMngEntity param) {
        try {
        	HttpSession session = request.getSession();
        	if(session.getAttribute("userInfo") == null) {
        		return CustomApiResponse.error(ResponseCode.FORBIDDEN,"요금제 신청을 위해 로그인을 해주세요.");
        	}
        	
        	UserDTO userrInfo = (UserDTO) session.getAttribute("userInfo");
        	String userPhonNum = (String)userrInfo.getPhoneNumber();
        	String userNm = (String)userrInfo.getUsername();
        	long userMngId = (long)userrInfo.getId();
        	
        	System.out.println(userrInfo);
        	//{birthDay=19770126, memberStat=ACTIVE, ageGroup=40대, emailAgreeYn=true, polAgreeYn=true, activeYn=true, piAgreeYn=true, phoneNumber=+82 10-2674-0126, kakaoUserId=kakao_3869259505, joinDate=2025-01-08T07:31:27, modifiedDate=2025-01-08T16:31:37, id=7, email=joynet3@hanmail.net, smsAgreeYn=true, username=조일근, createDate=2025-01-08T16:31:37}
        	param.setModifiedId((int) userMngId);
        	param.setCreateId((int) userMngId);
        	param.setReqNm(userNm);
        	param.setReqPhonNum(userPhonNum);
            service.create(param);
            return CustomApiResponse.success(ResponseCode.CREATED, param);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/update")
    public ResponseEntity<CustomApiResponse<PlanReqMngEntity>> update(
            @RequestBody PlanReqMngEntity request) {
        try {
            service.update(request);
            return CustomApiResponse.success(ResponseCode.OK, request);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> delete(
            @PathVariable("id") Integer id) {
        try {
            service.delete(id);
            return CustomApiResponse.success(ResponseCode.OK, "삭제 완료");
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
    
     @LoginRequired
	 @GetMapping("/planReq")
	 public String  planList( HttpServletRequest request ,  HttpServletResponse response,Model model)  {
		 
		 String reqPlanid = request.getParameter("reqPlanid");
		 String type = request.getParameter("type");
		 String recomSalesId = request.getParameter("recomSalesId") == null ? "N" : request.getParameter("recomSalesId") ;
		 
		 if(StringUtils.isNotBlank(reqPlanid)) {
			 PlanEntity result = new PlanEntity();
			 result = planService.getDetail(Integer.parseInt(reqPlanid));
			 
			 CompanyListEntity compInfo = companyListService.getCompany(result.getHost());
			 model.addAttribute("compInfo", compInfo);
			 model.addAttribute("reqPlanid", reqPlanid);
			 model.addAttribute("result", result);
			 model.addAttribute("type", type);
			 model.addAttribute("recomSalesId", recomSalesId);
		 }
		 
		 return "pages/plan/planReq";
	 }
}