package kr.co.ucomp.web.csm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.csm.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.entity.SemotongBaekseoEntity;
import kr.co.ucomp.web.csm.entity.SemotongReviewEntity;
import kr.co.ucomp.web.csm.service.SemotongReviewService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.pmb.dto.ErrorReportDto;
import kr.co.ucomp.web.pmb.entity.ErrorReportEntity;
import kr.co.ucomp.web.pmb.entity.PlanReqMngEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 김재희
 * @since 2024.12.18
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/csm/review")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class SemotongReviewController {

	@Autowired private SemotongReviewService reviewService;
	@Autowired FileService fileService;
	
	
	@GetMapping("/list")
	public String list ( HttpServletRequest request, Model model ) {
		
		log.info("후기 관리 리스트 진입");
				
		return "pages/svc/review/list";
	}
	
	

	@PostMapping("/ajaxList")
	public ResponseEntity<CustomApiResponse<List<SemotongReviewEntity>>> getReviewList(HttpServletResponse response, @RequestBody SemotongReviewDto dto) throws IOException  {
	
		Long totCnt  							= null;
		List<SemotongReviewEntity> resultList 	= null;
		try{
			totCnt  			= reviewService.countReviewList(dto);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= reviewService.reviewList(dto);
				for(SemotongReviewEntity itm : resultList) {
					if(itm.getContent().length() > 15) {
						itm.setContentShort( itm.getContent().substring(0, 10) + "..." );
					}else {
						itm.setContentShort( itm.getContent());
					}
				}
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventPlanList: " + e.getMessage());
		}
	}
	
	
	@PostMapping( value = "/ajaxDisplayYn" )
	public ResponseEntity<CustomApiResponse<SemotongReviewEntity>> displayYn (HttpServletRequest request, @RequestBody SemotongReviewEntity ent) throws Exception 
	{
		try {
			reviewService.updateDisplayYn(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "review updateDisplayYn: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "review updateDisplayYn: " + e.getMessage());
		}
	}
	
	@ResponseBody
	@PostMapping("/ajaxDetail")
	public ResponseEntity<CustomApiResponse<SemotongReviewEntity>> ajaxDetail(@RequestBody SemotongReviewDto dto) throws IOException  {
		try{
			SemotongReviewEntity result = reviewService.review(dto);
			if(result == null) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND);
			}
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ajaxDetail: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown (HttpServletRequest request, @RequestBody SemotongReviewDto dto) throws Exception 
	{
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		List<SemotongReviewEntity> resultList = reviewService.reviewList(dto);
		
		for (SemotongReviewEntity itm  :  resultList) {
			
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("reviewType"			, itm.getReviewType()		);
			data.put("planName"				, itm.getPlanName()				);
			data.put("reportTypeName"		, itm.getContent()		);
			data.put("displayYn"			, itm.getDisplayYn()				);
			data.put("score"				, itm.getScore()			);
			data.put("username"				, itm.getUsername()					);
			data.put("createDate"			, itm.getCreateDate()			);
			dataList.add(data);
		}
		
		// 엑셀 헤더 설정
		String[] headers = {"구분", "상품", "내용", "게시여부", "별점", "회원","등록일자"};
		
		byte[] excelData = fileService.getExcelData(headers,dataList);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 수정 페이지 이동  -  
	 */
	@GetMapping("/goUpdatePage/{id}")
	public String goUpdatepate ( HttpServletRequest request, @PathVariable("id") int id, Model model ) {
		
		log.info("review goUpdatepate - id:" + id);
		SemotongReviewDto param = new SemotongReviewDto();
		param.setSearchId(id);
		SemotongReviewEntity result = reviewService.review(param);
		model.addAttribute("result", result);
		return "pages/svc/review/edit";
	}
	
	
	
	
	
	@PostMapping( value = "/ajaxUpdate" )
	public ResponseEntity<CustomApiResponse<SemotongReviewEntity>> ajaxUpdate (HttpServletRequest request, @RequestBody SemotongReviewEntity ent) throws Exception 
	{
		try {
			
			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
			
			ent.setModifiedId(loginadminInfo.getId());
			
			reviewService.updateReview(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "review ajaxUpdate: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "review ajaxUpdate: " + e.getMessage());
		}
	}
	
	
	
	
	
	


}
