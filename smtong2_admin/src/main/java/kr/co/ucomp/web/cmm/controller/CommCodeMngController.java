package kr.co.ucomp.web.cmm.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.cmm.dto.CodeGroupDto;
import kr.co.ucomp.web.cmm.dto.CommCodeDto;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.entity.CodeGroupEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
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
    
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 코드그룹 신규입력
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/group/create")
    public ResponseEntity<CustomApiResponse<CodeGroupDto>> createCodeGroup( HttpServletResponse response,
    		@RequestBody  CodeGroupDto record
    		) throws IOException {
    	try {
    		 service.createCodeGroup(record);
    		 return CustomApiResponse.success(ResponseCode.CREATED,0, record);
        } catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, " createCodeGroup : " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, " createCodeGroup  " + e.getMessage());
        }
    	
    }
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 코드그룹 업데이트
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/group/update")
    public ResponseEntity<CustomApiResponse<CodeGroupDto>> updateCodeGroup( HttpServletResponse response,
    		@RequestBody  CodeGroupDto record
    		) throws IOException {
    	try {
    		service.updateCodeGroup(record);
    		 return CustomApiResponse.success(ResponseCode.OK,0, record);
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "updateCodeGroup: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "updateCodeGroup: " + e.getMessage());
        }
    	
    }
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 코드그룹 정보를 삭제
     *
     * @param groupCode 
     * @param 결과정보
     */
    @DeleteMapping("/group/delete/{groupCode}")
    public ResponseEntity<CustomApiResponse<String>> delCodeGroup( HttpServletResponse response,
    		@PathVariable("groupCode") String groupCode
    		) throws IOException {
    	
    	try {
    		service.delCodeGroup(groupCode);
    		 return CustomApiResponse.success(ResponseCode.OK,0, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delCodeGroup: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delCodeGroup: " + e.getMessage());
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
    @GetMapping("/code/detail")
    public ResponseEntity<CustomApiResponse<CodeEntity>> getCode( HttpServletResponse response,
    		@RequestParam("groupCode") String groupCode,
    		@RequestParam("code") String code
    		) throws IOException {
    	
    	try {
    		CodeEntity  info =  service.getCode(groupCode,code);
    		 if (info == null) {
                 return CustomApiResponse.error(ResponseCode.NOT_FOUND, "코드가 존재하지 않습니다.");
             }
    		return CustomApiResponse.success(ResponseCode.OK,0, info);
    	} catch (Exception e) {
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getCode: " + e.getMessage());
		}
    	
    }
    
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 공통코드 신규입력
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @PostMapping("/code/create")
    public ResponseEntity<CustomApiResponse<CommCodeDto>> createCode( HttpServletResponse response,
    		@RequestBody  CommCodeDto record
    		) throws IOException {
    	try {
    		 service.createCode(record);
    		 return CustomApiResponse.success(ResponseCode.CREATED,0, record);
        } catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "Error createCode: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "createCode: " + e.getMessage());
        }
    	
    }
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 공통코드 업데이트
     *
     * @param CodeGroupDto 
     * @param CodeGroupDto
     */
    @ResponseBody
    @PostMapping("/code/update")
    public ResponseEntity<CustomApiResponse<CommCodeDto>> updateCode( HttpServletRequest request, HttpServletResponse response,
    		@RequestBody  CommCodeDto record
    		) throws IOException {
    	try {
    		
    		
    		HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
    		record.setModifiedId(loginadminInfo.getId());    		
    		service.updateCode(record);
    		 return CustomApiResponse.success(ResponseCode.OK,0, record);
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "updateCode: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "updateCode: " + e.getMessage());
        }
    	
    }
    
    
    /**
     * 2024-12-10 (화) 조일근
     * - 공통코드 정보를 삭제
     *
     * @param groupCode 
     * @param 결과정보
     */
    @DeleteMapping("/code/delete")
    public ResponseEntity<CustomApiResponse<String>> delCode( HttpServletResponse response,
    		@RequestParam("groupCode") String groupCode,
    		@RequestParam("code") String code
    		) throws IOException {
    	
    	try {
    		service.delCode(groupCode,code);
    		 return CustomApiResponse.success(ResponseCode.OK,0, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delCode: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delCode: " + e.getMessage());
        }
    }
    
    
    
}
