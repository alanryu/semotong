package kr.co.ucomp.web.plan.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.ApiExampleCommon;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.plan.dto.ErrorReportDto;
import kr.co.ucomp.web.plan.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.plan.dto.PlanReqMngDto;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.dto.searchPlanZzimDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanReqMngEntity;
import kr.co.ucomp.web.plan.entity.PlanZzimEntity;
import kr.co.ucomp.web.plan.service.PlanService;
import kr.co.ucomp.web.plan.service.PlanZzimService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/pmb/plan_zzim")
@Slf4j
public class PlanZzimController {

	@Autowired private PlanZzimService service;
	@Autowired PlanService planService;
	
	
	
	/**
	 * MyPage 에서 보는 [찜한 요금제] 목록
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@PostMapping(value = "/getZzimPlanList")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getAppliedPlanList( HttpServletRequest request, @RequestBody searchPlanZzimDto param) throws IOException {
		
		HttpSession 		session 	= request.getSession(false);
		UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
		
		try{
//			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
//			
//			// [찜한 요금제]
//			// [찜한 요금제] 2023  2580  2162   1559 1563
//			searchPlanZzimDto zzimparam = new searchPlanZzimDto();
//			zzimparam.setUserMngId(loginInfo.getId());
//			List<PlanZzimEntity> zzimList = service.getlist(zzimparam);
//			//getZzimListPlan
//			
//			List<String>	prodList = new ArrayList<String>();
//			long 			resulCnt = zzimList.size();
//			if(resulCnt != 0) {
//				for(PlanZzimEntity itm : zzimList) {
//					prodList.add(itm.getProdId() + "");
//				}
//				param.setSearchplanIdList(prodList);
//				param.setSearchUserId((int) loginInfo.getId());					////searchUserId zzim count
//				resultList = planService.getAllListByPlanIds(param);
//			}
			
			//searchPlanZzimDto zzimparam = new searchPlanZzimDto();
			//zzimparam.setUserMngId(loginInfo.getId());
			
			
			param.setUserMngId(loginInfo.getId());
			List<PlanEntity> resultList = service.getZzimListPlan(param);
			
			long 			resulCnt = 0;
			resulCnt = service.getCount(param);
			
			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}
	
	
    @PostMapping(value = "/list")
    public ResponseEntity<CustomApiResponse<List<PlanZzimEntity>>> getListZzim(
            HttpServletRequest request,
            @RequestBody searchPlanZzimDto param
    ) throws IOException {

        try{
            List<PlanZzimEntity> resultList = service.getlist(param);

            int cnt = resultList.size();

            return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"getListZzim : " + e.getMessage());

        }

    }
    
    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<PlanZzimEntity>> createZzim(
            HttpServletRequest request,
            @RequestBody PlanZzimEntity param
    ) throws IOException {
    	HttpSession 		session 	= request.getSession(false);
		UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
		if(loginInfo == null  || loginInfo.getId() == 0) {
			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"로그인이 필요 합니다.");
		}
		
        try{
        	param.setUserMngId((int) loginInfo.getId());
        	service.create(param);
            return CustomApiResponse.success(ResponseCode.CREATED, param);

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST,"createZzim : " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"createZzim : " + e.getMessage());

        }

    }
    
    @ResponseBody
    @PostMapping("/delete")
    public ResponseEntity<CustomApiResponse<String>> deleteZzim(
            HttpServletRequest request,
            @RequestBody PlanZzimEntity param
    ) throws IOException {

    	HttpSession 		session 	= request.getSession(false);
		UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
		if(loginInfo == null  || loginInfo.getId() == 0) {
			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"로그인이 필요 합니다.");
		}
		
        try{
        	param.setUserMngId((int) loginInfo.getId());
        	service.delete(param);
            return CustomApiResponse.success(ResponseCode.OK, "해제 완료");

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST,"deleteZzim : " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"deleteZzim : " + e.getMessage());

        }

    }
    
    
    @PostMapping("/planList")
    public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getZzimPlan(
            HttpServletRequest request,
            @RequestParam("userId") Long userId
    ) throws IOException {

    	
        try{

        	List<PlanEntity> resultList = new ArrayList<PlanEntity>();
        	int cnt= 0;
        	
        	searchPlanZzimDto param = new searchPlanZzimDto();
        	param.setUserMngId(userId);
        	// 요금제 찜 리스트에서 조회 사용자에 찜이 된 요금제 관리번호 리스트를 조회
        	List<PlanZzimEntity> resulZzimtList = service.getlist(param);
        	
        	// 찜한 요그메가 있는 경우 요금제 리스트 정보를 조회
        	if(resulZzimtList !=null && resulZzimtList.size()>0) {
        		List<String> searchids = new ArrayList<String>();
        		SearchPlanDto planListparam = new SearchPlanDto(); 
        		
        		for(PlanZzimEntity itm : resulZzimtList) {
        			searchids.add(String.valueOf(itm.getProdId()));
            	}
        		
        		planListparam.setSearchplanIdList(searchids);
            	resultList = planService.getAllListByPlanIds(planListparam);
            	if(resultList !=null) cnt = resultList.size();
            	
        	}
        	
        	return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST,"getZzimPlan : " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"getZzimPlan : " + e.getMessage());

        }

    }

	
}
