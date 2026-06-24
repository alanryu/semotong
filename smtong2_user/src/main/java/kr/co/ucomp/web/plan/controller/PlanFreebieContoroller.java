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
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.plan.dto.PlanFreebieSearchDto;
import kr.co.ucomp.web.plan.entity.PlanBenefitEntity;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanFreebieEntity;
import kr.co.ucomp.web.plan.entity.PlanFreebieMappingEntity;
import kr.co.ucomp.web.plan.service.PlanFreebieService;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;

/**
*
* @author 조일근
* @since 2024.12.26
* @version v1.0
*/
@Controller
@RequestMapping(value = "/pbm/planfreebie")
@Slf4j
public class PlanFreebieContoroller {
	
	
	@Autowired PlanFreebieService service;
	@Autowired PlanService planService;
	
	
	/* =================================================== 상품 사은품 정보 관리 =================================== */
	
    /**
     * 2024-12-26 조일근
     * - 상품 사은품 정보 리스트 조회
     *
     * @param PlanFreebieSearchDto
     * @param List<PlanFreebieEntity> 요금제 사은품 리스트
     */
    @PostMapping("/info/list")
    public ResponseEntity<CustomApiResponse<List<PlanFreebieEntity>>> getInfolist( HttpServletResponse response,
    		@RequestBody  PlanFreebieSearchDto searchRequest
    		) throws IOException {
    	
    	List<PlanFreebieEntity> list = null;
    	
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
     * 2024-12-26  조일근
     * - 상품 사은품 정보 상세 조회
     *
     * @param groupCode 
     * @param PlanFreebieEntity
     */
    @GetMapping("/info/detail/{id}")
    public ResponseEntity<CustomApiResponse<PlanFreebieEntity>> getInfoDetail( HttpServletResponse response,
    		@PathVariable("id") int id
    		) throws IOException {
    	
    	try {
    		PlanFreebieEntity  info =  service.infoDetail(id);
    		 if (info == null) {
                 return CustomApiResponse.error(ResponseCode.NOT_FOUND);
             }
    		return CustomApiResponse.success(ResponseCode.OK,0, info);
    	} catch (Exception e) {
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    	
    }
    
    
    /**
     * 2024-12-26  조일근
     * - 상품 사은품 정보 신규입력
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/info/create")
    public ResponseEntity<CustomApiResponse<PlanFreebieEntity>> createInfo( HttpServletResponse response,
    		@RequestBody  PlanFreebieEntity record
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
     * 2024-12-26  조일근
     * - 상품 사은품 정보 업데이트
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/info/update")
    public ResponseEntity<CustomApiResponse<PlanFreebieEntity>> updateInfo( HttpServletResponse response,
    		@RequestBody  PlanFreebieEntity record
    		) throws IOException {
    	try {
    		
    		int freebieid = record.getId();
    		PlanFreebieEntity  freebie =  service.infoDetail(freebieid);
    		if(freebie == null) {
    			// 업데이트 id 오류
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR);
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
     * 2024-12-26  조일근
     * - 상품 사은품 정보 삭제
     *
     * @param groupCode 
     * @param 결과정보
     */
    @DeleteMapping("/info/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> deleteInfo( HttpServletResponse response,
    		@PathVariable("id") int id
    		) throws IOException {
    	
    	try {
    		
    		PlanFreebieEntity  freebie =  service.infoDetail(id);
    		if(freebie == null) {
    			// 삭제 id 오류
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
    
    
    
    
    
    
    
    
	/* =================================================== 요금제 사은품 매핑 정보 관리 =================================== */
	
    /**
     * 2024-12-26 조일근
     * - 요금제 사은품 매핑 정보 리스트 조회
     *
     * @param PlanFreebieSearchDto
     * @param List<PlanFreebieMappingEntity> 요금제 사은품 매핑 리스트
     */
    @PostMapping("/map/list")
    public ResponseEntity<CustomApiResponse<List<PlanFreebieMappingEntity>>> getMaplist( HttpServletResponse response,
    		@RequestBody  PlanFreebieSearchDto searchRequest
    		) throws IOException {
    	
    	List<PlanFreebieMappingEntity> list = null;
    	
    	try {
    		long cnt = service.maplistCount(searchRequest);
    		if(cnt > 0) {
    			list =  service.maplist(searchRequest);	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK,cnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    		
    }
    
    
    /**
     * 2024-12-26  조일근
     * - 요금제 사은품 매핑 정보 상세 조회
     *
     * @param groupCode 
     * @param PlanFreebieEntity
     */
    @GetMapping("/map/detail/{id}")
    public ResponseEntity<CustomApiResponse<PlanFreebieMappingEntity>> getMapDetail( HttpServletResponse response,
    		@PathVariable("id") int id
    		) throws IOException {
    	
    	try {
    		PlanFreebieMappingEntity  info =  service.mapDetail(id);
    		 if (info == null) {
                 return CustomApiResponse.error(ResponseCode.NOT_FOUND);
             }
    		return CustomApiResponse.success(ResponseCode.OK,0, info);
    	} catch (Exception e) {
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    	
    }
    
    
    /**
     * 2024-12-26  조일근
     * - 요금제 사은품 매핑 정보 신규입력
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/map/create")
    public ResponseEntity<CustomApiResponse<PlanFreebieMappingEntity>> createmap( HttpServletResponse response,
    		@RequestBody  PlanFreebieMappingEntity record
    		) throws IOException {
    	try {
    		
    		int rateid = record.getPlanListId() ;
    		int freebieid = record.getPlanfreebieId() ;
    		PlanEntity plan = planService.getDetail(rateid);
    		PlanFreebieEntity  freebie =  service.infoDetail(freebieid);
    		
    		if(plan == null) {
    			// 요금제 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR);
    		}
    		
    		if(freebie == null) {
    			// 사은품 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR);
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
     * 2024-12-26  조일근
     * - 요금제 사은품 매핑 정보 업데이트
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/map/update")
    public ResponseEntity<CustomApiResponse<PlanFreebieMappingEntity>> updatemap( HttpServletResponse response,
    		@RequestBody  PlanFreebieMappingEntity record
    		) throws IOException {
    	try {
    		
    		int updateId = record.getId();
    		int rateid = record.getPlanListId() ;
    		int freebieid = record.getPlanfreebieId() ;
    		
    		PlanFreebieEntity  info =  service.infoDetail(updateId);
    		PlanEntity plan = planService.getDetail(rateid);
    		PlanFreebieEntity  freebie =  service.infoDetail(freebieid);
    		
    		
    		if(info == null) {
    			//업데이트 id 오류 
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR);
    		}
    		
    		if(plan == null) {
    			// 요금제 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR);
    		}
    		
    		if(freebie == null) {
    			// 사은품 정보 없음
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR);
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
     * 2024-12-26  조일근
     * - 요금제 사은품 매핑 정보 삭제
     *
     * @param groupCode 
     * @param 결과정보
     */
    @DeleteMapping("/map/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> deletemap( HttpServletResponse response,
    		@PathVariable("id") int id
    		) throws IOException {
    	
    	try {
    		
    		PlanFreebieEntity  info =  service.infoDetail(id);
    		if(info == null) {
    			//업데이트 id 오류 
    			return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR);
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
