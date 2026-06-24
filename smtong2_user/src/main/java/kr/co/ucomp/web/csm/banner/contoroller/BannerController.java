package kr.co.ucomp.web.csm.banner.contoroller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.csm.banner.dto.BannerPlanCreateDTO;
import kr.co.ucomp.web.csm.banner.dto.BannerPlanSearchDTO;
import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;
import kr.co.ucomp.web.csm.banner.entity.BannerPlanEntity;
import kr.co.ucomp.web.csm.banner.service.BannerPlanService;
import kr.co.ucomp.web.csm.banner.service.BannerService;
import kr.co.ucomp.web.plan.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.plan.dto.PlanFreebieSearchDto;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanBenefitMappingEntity;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanFreebieMappingEntity;
import kr.co.ucomp.web.plan.service.PlanBenefitService;
import kr.co.ucomp.web.plan.service.PlanFreebieService;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author 조일근
 * @since 2024.12.20
 * @version v1.0
 */

@Controller
@RequestMapping(value = "/svc/banner")
@Slf4j
public class BannerController {

    @Autowired private BannerService service;
    @Autowired private BannerPlanService bannerPlanService;
    @Autowired private FileService fileService;    
    @Autowired private PlanService planService;
    @Autowired private PlanBenefitService benefitService;
    @Autowired private PlanFreebieService freebieservice;
    
    
    
    /* =============================  배너관리 ================================== */
    @PostMapping(value = "list")
    public ResponseEntity<CustomApiResponse<List<BannerEntity>>> list(
            HttpServletRequest request,
            @RequestBody BannerSearchDto param
    ) throws IOException {

        try{
        	
        	if(param.getIsDispStatusAll() !=null && param.getIsDispStatusAll() == 1) {
        		param.setIsDispStatusBef(0);
        		param.setIsDispStatusDsp(0);
        		param.setIsDispStatusEnd(0);
        	} else {
        		param.setIsDispStatusAll(0);
        	}
            long resultcnt = service.listCount(param);
            List<BannerEntity> resultList = new ArrayList<BannerEntity>();
            if(resultcnt > 0 ) {
            	resultList = service.list(param);
            }
            
            int cnt = resultList.size();

            return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }

    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<BannerEntity>> getMainDealMst(
            HttpServletRequest request,
            @PathVariable("id") int id
    ) throws IOException {

        try{

            BannerEntity result = service.getDetail(id);

            if (result == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND,"배너 상세 값이 없습니다.");
            }

            if(result.getBannPlanCnt() >0) {
            	List<PlanEntity> planList = new ArrayList<PlanEntity>();
            	
            	SearchPlanDto planparam = new SearchPlanDto();
            	planparam.setSearchplanIds(result.getBannPlanIds());
            	planparam.setSearchDispYn(1);
            	planList = planService.getAllListByPlanIds(planparam);
            	
            	for(PlanEntity itm : planList) {
	    			// 요금제 혜{택 정보 추가
	    			PlanBenefitSearchDto benefitParam = new PlanBenefitSearchDto();
	    			benefitParam.setDisplayYn("Y");
	    			benefitParam.setPlanId(itm.getId());
	    			List<PlanBenefitMappingEntity> benList =  benefitService.maplistAll(benefitParam);
	    			itm.setBenefitList(benList);
	    			if(benList !=null) {
	    				itm.setPlanBenefitCnt(benList.size());	
	    			}
	    			// 요금제 사은품정보 추가
	    			
	    			PlanFreebieSearchDto freebieParam = new PlanFreebieSearchDto();
	    			freebieParam.setDisplayYn("Y");
	    			freebieParam.setPlanId(itm.getId());
	    			List<PlanFreebieMappingEntity> freebieList = freebieservice.maplistAll(freebieParam);
	    			itm.setFreebieList(freebieList);
	    			if(freebieList !=null) {
	    				itm.setPlanFreebieCnt(freebieList.size());
	    			}
	    		}
            	result.setBannerPlanList(planList);
            	
            }
            return CustomApiResponse.success(ResponseCode.OK, result);

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, " "+ e.getMessage());

        }

    }



    /* ============================== ======================================== */

    
	@PostMapping(value = "/plan/list")
	public ResponseEntity<CustomApiResponse<List<BannerPlanEntity>>> getbannerPlanList (HttpServletRequest request, @RequestBody BannerPlanSearchDTO param) throws Exception 
	{
		Long totCnt  					= null;
		List<BannerPlanEntity> resultList 	= null;
		try {
			totCnt  			= bannerPlanService.listCount(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= bannerPlanService.list(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getbannerPlanList: " + e.getMessage());
		}
	}
	
	
	
}
