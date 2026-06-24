package kr.co.ucomp.web.cmm.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.cmm.dto.CodeGroupDto;
import kr.co.ucomp.web.cmm.dto.CommCodeDto;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.entity.CodeGroupEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cmm/cmmcode")
@Slf4j
public class CommCodeMngController {
	
	
	
	@Autowired CommCodeMngService service;
	
	
	/* =================================================== 공통코드 그룹관리 =================================== */
	
    /**
     * 2024-12-18 조일근
     * - 코드그룹 리스트 조회
     *
     * @param CommCodeSearchDto
     * @param List<CodeGroupEntity> 코드그룹 리스트
     */
	@ResponseBody
    @PostMapping("/group/list")
    public ResponseEntity<CustomApiResponse<List<CodeGroupEntity>>> getListCodeGroup( HttpServletResponse response,
    		@RequestBody  CommCodeSearchDto searchRequest
    		) throws IOException {
    	
    	List<CodeGroupEntity> list = null;
    	
    	try {
    		long cnt = service.getListCodeGroupCount(searchRequest);
    		if(cnt > 0) {
    			list =  service.getListCodeGroup(searchRequest);	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK,cnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getListCodeGroup : " + e.getMessage());
		}
    		
    }
    
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 코드그룹 상세 조회
     *
     * @param groupCode 
     * @param CodeGroupEntity
     */
	@ResponseBody
    @GetMapping("/group/detail/{groupCode}")
    public ResponseEntity<CustomApiResponse<CodeGroupEntity>> getCodeGroup( HttpServletResponse response,
    		@PathVariable("groupCode") String groupCode
    		) throws IOException {
    	
    	try {
    		CodeGroupEntity  info =  service.getCodeGroup(groupCode);
    		 if (info == null) {
                 return CustomApiResponse.error(ResponseCode.NOT_FOUND, "그룹코드가 존재 하지 않습니다.");
             }
    		return CustomApiResponse.success(ResponseCode.OK,0, info);
    	} catch (Exception e) {
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getListCodeGroup : " + e.getMessage());
		}
    	
    }
    
    
    
    
    
    /* =================================================== 공통코드 관리 =================================== */
    
    
    
    
    /**
     * 2024-12-18 조일근
     * - 공통코드 리스트 조회
     *
     * @param CommCodeSearchDto
     * @param List<CodeGroupEntity> 코드그룹 리스트
     */
	@ResponseBody
    @PostMapping("/code/list")
    public ResponseEntity<CustomApiResponse<List<CodeEntity>>> getListCode( HttpServletResponse response,
    		@RequestBody  CommCodeSearchDto searchRequest
    		) throws IOException {
    	
    	List<CodeEntity> list = null;
    	
    	try {
    		long cnt = service.getListCodeCount(searchRequest);
    		if(cnt > 0) {
    			list =  service.getListCode(searchRequest);	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK,cnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getListCode: " + e.getMessage());
		}
    		
    }
    
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 공통코드 상세 조회
     *
     * @param groupCode 
     * @param CodeGroupEntity
     */
	@ResponseBody
    @GetMapping("/code/detail")
    public ResponseEntity<CustomApiResponse<CodeEntity>> getCode( HttpServletResponse response,
    		CommCodeSearchDto searchRequest
    		) throws IOException {
    	
    	try {
    		CodeEntity  info =  service.getCode(searchRequest);
    		 if (info == null) {
                 return CustomApiResponse.error(ResponseCode.NOT_FOUND, "코드가 존재하지 않습니다.");
             }
    		return CustomApiResponse.success(ResponseCode.OK,0, info);
    	} catch (Exception e) {
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getCode: " + e.getMessage());
		}
    	
    }
    
    
}
