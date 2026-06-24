package kr.co.ucomp.web.csm.notice.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;
import kr.co.ucomp.web.csm.banner.service.BannerService;
import kr.co.ucomp.web.csm.notice.dto.NoticeSearchDto;
import kr.co.ucomp.web.csm.notice.entity.NoticeEntity;
import kr.co.ucomp.web.csm.notice.service.NoticeService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cms/notice") 
@Slf4j
public class NoticeController {
	
	@Autowired NoticeService service;
	
	@Autowired 
	private BannerService bannerService;
	
    /**
     * 2024-12-18 (수) 백신의
     * - 공지사항 목록
     *
     * @param searchRequest               서치 params
     * @param List<NoticeEntity> 공지사항 조회 리스트
     */
	@ResponseBody
    @PostMapping("/list")
    public ResponseEntity<CustomApiResponse<List<NoticeEntity>>> noticeList(HttpServletResponse response,
    		@RequestBody NoticeSearchDto searchRequest
    		) throws IOException {
    	
    	List<NoticeEntity> list = null;
    	
    	try {
    		long cnt = service.getListNoticeCount(searchRequest);
    		list = service.getListNotice(searchRequest);
    		
    		return CustomApiResponse.success(ResponseCode.OK,cnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "noticeList: " + e.getMessage());
		}
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 공지사항 상세 조회
     *
     * @param searchRequest               서치 params
     * @param NoticeEntity 공지사항 상세 정보
     */
    @GetMapping("/detail/{id}")
    public String getNotice(HttpServletResponse response,
    		@PathVariable("id") long searchId, Model model
    		) throws IOException {
    	
    	NoticeEntity info = service.getNotice(searchId);
    	
    	model.addAttribute("info", info);
    	
    	return "pages/csm/notice/noticeDetail";
    }
    
    
    /**
 	  * 2024-12-18 조일근
 	  * 알뜰 폰이란
 	  * @param model
 	  * @param 
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
 	  */
 	 @SuppressWarnings("unchecked")
	@GetMapping("/noticeList")
 	 public String  noticeList( HttpServletResponse response,Model model) throws Exception  {
 		 
 		ObjectMapper mapper = new ObjectMapper();
 		 
 		/* 결합 배너 (searchType : 05) */
    	BannerSearchDto bannerSearchDto = new BannerSearchDto();
    	bannerSearchDto.setSearchUseYn("Y");
    	bannerSearchDto.setSearchBannerType("05");
    	bannerSearchDto.setIsDispStatusDsp(1);
    	List<BannerEntity> bannerList = bannerService.list(bannerSearchDto);
    	
    	if ( bannerList.size() > 0 ) {
    		
    		for ( BannerEntity temp : bannerList ) {
    			if(StringUtils.isNoneBlank(temp.getImagePc())) {
    				Map<String, Object> map = mapper.readValue(temp.getImagePc(), Map.class);
        			temp.setImagePc(map.get("fileUrl").toString());	
    			}
    			if(StringUtils.isNoneBlank(temp.getImageMo())) {    			
    				Map<String, Object> map  = mapper.readValue(temp.getImageMo(), Map.class);
	    			temp.setImageMo(map.get("fileUrl").toString());
    			}
    		}
    	}
    	
    	model.addAttribute("bannerList", bannerList);
 		 
 	 	return "pages/csm/notice/noticeList";
 	 }
}
