package kr.co.ucomp.web.bizpurio.controller;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.bizpurio.dto.BizPurioMsgResDto;
import kr.co.ucomp.web.bizpurio.entity.BizPurioMsgResEntity;
import kr.co.ucomp.web.bizpurio.service.BizPurioIFService;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeGroupEntity;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/bizpurio")
@PreAuthorize("hasAnyAuthority('ALL', 'MESSAGE_MNG')")
@Slf4j
public class BizPurioIFController {
	
	@Autowired BizPurioIFService service;
	
	
	
    /**
     * 2024-12-18 조일근
     * - 알람 발송내역 조회
     *
     * @param CommCodeSearchDto
     * @param List<CodeGroupEntity> 알람 발송내역
     */
    @PostMapping("/ajaxList")
    public ResponseEntity<CustomApiResponse<List<BizPurioMsgResEntity>>> getList( HttpServletResponse response,
    		@RequestBody  BizPurioMsgResDto searchRequest
    		) throws IOException {
    	
    	List<BizPurioMsgResEntity> list = null;
    	
    	try {
    		long cnt = service.getListCount(searchRequest);
    		if(cnt > 0) {
    			list =  service.getList(searchRequest);	
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK,cnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
			// TODO: handle exception
    		 return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getListCodeGroup : " + e.getMessage());
		}
    		
    }
    
    
    
	   /**
	* 2024-12-18 조일근
	*
	* @param CommCodeSearchDto
	* @param List<CodeGroupEntity> 코드그룹 리스트
	*/
	@GetMapping("/list")
	public String  listview( HttpServletResponse response,Model model)  {
		
		return "pages/bizpurio/list";
	}

    
    
   
    
    
}
