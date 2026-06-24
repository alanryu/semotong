package kr.co.ucomp.web.plan.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.ApiExampleCommon;
import kr.co.ucomp.common.response.ApiExampleHttp;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.plan.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.plan.entity.PlanBenefitEntity;
import kr.co.ucomp.web.plan.entity.PlanBenefitMappingEntity;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.service.PlanBenefitService;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;

/**
*
* @author 조일근
* @since 2024.12.25
* @version v1.0
*/
@Controller
@RequestMapping(value = "/pbm/planbenefit")
@Slf4j
public class PlanBenefitContoroller {
	
	
	@Autowired PlanBenefitService service;
	@Autowired PlanService planService;	
	
	/* =================================================== 상품 혜택 정보 관리 =================================== */
	
    /**
     * 2024-12-25 조일근
     * - 상품 혜택 정보 리스트 조회
     *
     * @param PlanBenefitSearchDto
     * @param List<PlanBenefitEntity> 요금제 혜택 리스트
     */
    @PostMapping("/info/list")
    public ResponseEntity<CustomApiResponse<List<PlanBenefitEntity>>> getInfolist( HttpServletResponse response,
    		@RequestBody  PlanBenefitSearchDto searchRequest
    		) throws IOException {
    	
    	List<PlanBenefitEntity> list = null;
    	
    	try {
    		long cnt = service.infolistCount(searchRequest);
    		if(cnt > 0) {
    			list =  service.infolist(searchRequest);	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK,cnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    		
    }
    
    
    /**
     * 2024-12-25 (화) 조일근
     * - 상품 혜택 정보 상세 조회
     *
     * @param groupCode 
     * @param PlanBenefitEntity
     */
    @GetMapping("/info/detail/{id}")
    public ResponseEntity<CustomApiResponse<PlanBenefitEntity>> getInfoDetail( HttpServletResponse response,
    		@PathVariable("id") int id
    		) throws IOException {
    	
    	try {
    		
    		PlanBenefitEntity  info =  service.infoDetail(id);
    		 if (info == null) {
                 return CustomApiResponse.error(ResponseCode.NOT_FOUND,"상품 혜택정보가 존재하지 않습니다.");
             }
    		return CustomApiResponse.success(ResponseCode.OK,0, info);
    	} catch (Exception e) {
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    	
    }
    
    
    /**
     * 2024-12-25 (화) 조일근
     * - 상품 혜택 정보 신규입력
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/info/create")
    public ResponseEntity<CustomApiResponse<PlanBenefitEntity>> createInfo( HttpServletResponse response,
    		@RequestBody  PlanBenefitEntity record
    		) throws IOException {
    	try {
    		 service.createInfo(record);
    		 return CustomApiResponse.success(ResponseCode.CREATED,0, record);
        } catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    	
    }
    
    
    /**
     * 2024-12-25 (화) 조일근
     * - 상품 혜택 정보 업데이트
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/info/update")
    public ResponseEntity<CustomApiResponse<PlanBenefitEntity>> updateInfo( HttpServletResponse response,
    		@RequestBody  PlanBenefitEntity record
    		) throws IOException {
    	try {
    		
    		
    		int benefitid = record.getId() ;
    		PlanBenefitEntity  benefit =  service.infoDetail(benefitid);
    		if(benefit == null) {
    			// 혜택 정보 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"상품혜택 정보가 존재하지 않습니다.");
    		}
    		
    		service.updateInfo(record);
    		 return CustomApiResponse.success(ResponseCode.OK,0, record);
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    	
    }
    
    
    /**
     * 2024-12-25 (화) 조일근
     * - 상품 혜택 정보 삭제
     *
     * @param groupCode 
     * @param 결과정보
     */
    @DeleteMapping("/info/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> deleteInfo( HttpServletResponse response,
    		@PathVariable("id") int id
    		) throws IOException {
    	
    	try {
    		
    		PlanBenefitEntity  benefit =  service.infoDetail(id);
    		if(benefit == null) {
    			// 혜택 정보 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR);
    		}
    		
    		service.deleteInfo(id);
    		 return CustomApiResponse.success(ResponseCode.OK,0, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    
    
    
    
    
    
	/* =================================================== 요금제 혜택 매핑 정보 관리 =================================== */
	
    /**
     * 2024-12-25 조일근
     * - 요금제 혜택 매핑 정보 리스트 조회
     *
     * @param PlanBenefitSearchDto
     * @param List<PlanBenefitMappingEntity> 요금제 혜택 매핑 리스트
     */
    @PostMapping("/map/list")
    public ResponseEntity<CustomApiResponse<List<PlanBenefitMappingEntity>>> getMaplist( HttpServletResponse response,
    		@RequestBody  PlanBenefitSearchDto searchRequest
    		) throws IOException {
    	
    	List<PlanBenefitMappingEntity> list = null;
    	
    	try {
    		long cnt = service.maplistCount(searchRequest);
    		if(cnt > 0) {
    			list =  service.maplist(searchRequest);	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK,cnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getMaplist : " + e.getMessage());
		}
    		
    }
    
    
    /**
     * 2024-12-25 (화) 조일근
     * - 요금제 혜택 매핑 정보 상세 조회
     *
     * @param groupCode 
     * @param PlanBenefitEntity
     */
    @GetMapping("/map/detail/{id}")
    public ResponseEntity<CustomApiResponse<PlanBenefitMappingEntity>> getMapDetail( HttpServletResponse response,
    		@PathVariable("id") int id
    		) throws IOException {
    	
    	try {
    		PlanBenefitMappingEntity  info =  service.mapDetail(id);
    		 if (info == null) {
                 return CustomApiResponse.error(ResponseCode.NOT_FOUND,"요금제 혜택 매핑 정보가 존재하지 않습니다.");
             }
    		return CustomApiResponse.success(ResponseCode.OK,0, info);
    	} catch (Exception e) {
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getMapDetail : " + e.getMessage());
		}
    	
    }
    
    
    /**
     * 2024-12-25 (화) 조일근
     * - 요금제 혜택 매핑 정보 신규입력
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/map/create")
    public ResponseEntity<CustomApiResponse<PlanBenefitMappingEntity>> createmap( HttpServletResponse response,
    		@RequestBody  PlanBenefitMappingEntity record
    		) throws IOException {
    	try {
    		
    		PlanEntity plan = planService.getDetail(record.getPlanListId() );
    		PlanBenefitEntity  benefit =  service.infoDetail(record.getPlanBenefitsId());
    		
    		if(plan == null) {
    			// 요금제 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"요금제 정보가 존재하지 않습니다.");
    		}
    		
    		if(benefit == null) {
    			// 혜택정보 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"혜택 정보가 존재하지 않습니다.");
    		}    		
    		
    		
    		 service.createmap(record);
    		 return CustomApiResponse.success(ResponseCode.CREATED,0, record);
        } catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    	
    }
    
    
    /**
     * 2024-12-25 (화) 조일근
     * - 요금제 혜택 매핑 정보 업데이트
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/map/update")
    public ResponseEntity<CustomApiResponse<PlanBenefitMappingEntity>> updatemap( HttpServletResponse response,
    		@RequestBody  PlanBenefitMappingEntity record
    		) throws IOException {
    	try {
    		
    		
    		PlanBenefitMappingEntity  info =  service.mapDetail(record.getId());
    		PlanEntity plan = planService.getDetail(record.getPlanListId() );
    		PlanBenefitEntity  benefit =  service.infoDetail(record.getPlanBenefitsId());
    		
    		
    		if(info == null) {
    			// 업데이트 id 오류
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"요금제 혜택 매핑 정보가 존재하지 않습니다.");
    		}
    		
    		if(plan == null) {
    			// 요금제 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"요금제 정보가 존재하지 않습니다.");
    		}
    		
    		if(benefit == null) {
    			// 혜택정보 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"혜택 정보가 존재하지 않습니다.");
    		}
    		
    		service.updatemap(record);
    		 return CustomApiResponse.success(ResponseCode.OK,0, record);
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    	
    }
    
    
    /**
     * 2024-12-25 (화) 조일근
     * - 요금제 혜택 매핑 정보 삭제
     *
     * @param groupCode 
     * @param 결과정보
     */
    @DeleteMapping("/map/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> deletemap( HttpServletResponse response,
    		@PathVariable("id") int id
    		) throws IOException {
    	
    	try {
    		PlanBenefitMappingEntity  info =  service.mapDetail(id);
    		if(info == null) {
    			// 삭제 id 오류
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR,"요금제 혜택 매핑 정보가 존재하지 않습니다.");
    		}
    		service.deletemap(id);
    		 return CustomApiResponse.success(ResponseCode.OK,0, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }    
    
	

}
