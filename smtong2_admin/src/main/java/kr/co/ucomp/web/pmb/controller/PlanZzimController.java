package kr.co.ucomp.web.pmb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.ApiExampleCommon;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.pmb.dto.ErrorReportDto;
import kr.co.ucomp.web.pmb.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.dto.searchPlanZzimDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.entity.PlanZzimEntity;
import kr.co.ucomp.web.pmb.service.PlanService;
import kr.co.ucomp.web.pmb.service.PlanZzimService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/pmb/plan_zzim")
@Slf4j
public class PlanZzimController {

	@Autowired private PlanZzimService service;
	@Autowired PlanService planService;
	
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
    
    
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<PlanZzimEntity>> createZzim(
            HttpServletRequest request,
            @RequestBody PlanZzimEntity param
    ) throws IOException {

        try{
        	
        	service.create(param);
            return CustomApiResponse.success(ResponseCode.CREATED, param);

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST,"createZzim : " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"createZzim : " + e.getMessage());

        }

    }
    
    
    @DeleteMapping("/delete")
    public ResponseEntity<CustomApiResponse<String>> deleteZzim(
            HttpServletRequest request,
            @RequestParam("userId") int userId,@RequestParam("prodId") int prodId
    ) throws IOException {

        try{
        	service.delete(userId,prodId);
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
            @RequestParam("userId") int userId
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
