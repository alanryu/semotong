package kr.co.ucomp.web.pmb.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.AdminUserEntity;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.pmb.dto.ErrorReportDto;
import kr.co.ucomp.web.pmb.entity.ErrorReportEntity;
import kr.co.ucomp.web.pmb.service.ErrorReportService;
import kr.co.ucomp.web.svc.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.svc.event.dto.EvtSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtEntity;
import kr.co.ucomp.web.svc.event.entity.EvtPlanEntity;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 김재희
 * @since 2024.12.20
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/pbm/error-report")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class ErrorReportController {

	@Autowired private ErrorReportService errorReportService;
	@Autowired FileService fileService;
	
	
	@GetMapping("/list")
	public String list ( HttpServletRequest request, Model model ) {
		
		log.info("/pbm/error-reportt 리스트 진입");
				
		return "pages/svc/report/list";
	}
	
	@ResponseBody
	@PostMapping(value = "/ajaxList")
	public ResponseEntity<CustomApiResponse<List<ErrorReportEntity>>> getEventPlanList (HttpServletRequest request, @RequestBody ErrorReportDto param) throws Exception 
	{
		Long totCnt  							= null;
		List<ErrorReportEntity> resultList 		= null;
			
		try {
			totCnt  			= errorReportService.countErrorReportList(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= errorReportService.errorReportList(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventPlanList: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown (HttpServletRequest request, @RequestBody ErrorReportDto param) throws Exception 
	{
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		List<ErrorReportEntity> resultList = errorReportService.errorReportList(param);
		
		for (ErrorReportEntity itm  :  resultList) {
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("processSpName"		, itm.getProcessSpName()		);
			data.put("planName"				, itm.getPlanName()				);
			data.put("reportTypeName"		, itm.getReportTypeName()		);
			data.put("username"				, itm.getUsername()				);
			data.put("createDate"			, itm.getCreateDate()			);
			data.put("memo"					, itm.getMemo()					);
			data.put("managerName"			, itm.getManagerName()			);
			data.put("modifiedDate"			, itm.getModifiedDate()			);
			dataList.add(data);			
		}
		
		// 엑셀 헤더 설정
		String[] headers = {"상태", "상품", "내용","신고자","작성일자","내부메모","담당자", "처리일시"};
		
		byte[] excelData = fileService.getExcelData(headers,dataList);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}
	
	
	
	
	
	
	@GetMapping("/goUpdatePage/{id}")
	public String goUpdatepate ( HttpServletRequest request, @PathVariable("id") int id, Model model ) {
		
		log.info("/pbm/error-report/goUpdatepate - id:" + id);
		
		ErrorReportEntity result = errorReportService.errorReport(id);
		
		model.addAttribute("result"			, result);
		
		return "pages/svc/report/edit";
	}
	
	
	@PostMapping( value = "/ajaxUpdate" )
	public ResponseEntity<CustomApiResponse<ErrorReportEntity>> ajaxUpdate (HttpServletRequest request, @RequestBody ErrorReportEntity ent) throws Exception 
	{
		try {
			
			
			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
			
			ent.setModifiedId(loginadminInfo.getId());
			ent.setProcessManager(loginadminInfo.getId());
			
			errorReportService.updateErrorReport(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "Error Report ajaxUpdate: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error Report ajaxUpdate: " + e.getMessage());
		}
	}
	
	
	
	
	
	
	
	

	@PostMapping(value = "/list")
	public ResponseEntity<CustomApiResponse<List<ErrorReportEntity>>> getReviewList(
			HttpServletRequest request,
			@RequestBody ErrorReportDto param
	) throws IOException {

		try{

			List<ErrorReportEntity> resultList = errorReportService.errorReportList(param);

			if (resultList.isEmpty()) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND);
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
	public ResponseEntity<CustomApiResponse<ErrorReportEntity>> getErrorReport(
			HttpServletRequest request,
			@PathVariable("id") long id
	) throws IOException {

		try{

			ErrorReportEntity result = errorReportService.errorReport(id);

			if (result == null) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND);
			}

			return CustomApiResponse.success(ResponseCode.OK, result);

		} catch (Exception e) {

			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping("/create")
	public ResponseEntity<CustomApiResponse<ErrorReportEntity>> insertErrorReport(
			HttpServletRequest request,
			@RequestBody ErrorReportEntity param
	) throws IOException {

		try{
			errorReportService.insertErrorReport(param);
			return CustomApiResponse.success(ResponseCode.CREATED, param);

		} catch (IllegalArgumentException e) {

			return CustomApiResponse.error(ResponseCode.BAD_REQUEST);

		} catch (Exception e) {

			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}


	@PostMapping("/update")
	public ResponseEntity<CustomApiResponse<ErrorReportEntity>> updateErrorReport(
			HttpServletRequest request,
			@RequestBody ErrorReportEntity param
	) throws IOException {

		try{
			errorReportService.updateErrorReport(param);
			return CustomApiResponse.success(ResponseCode.OK, param);

		} catch (IllegalArgumentException e) {

			return CustomApiResponse.error(ResponseCode.BAD_REQUEST);

		} catch (Exception e) {

			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}


	@DeleteMapping("/delete/{id}")
	public ResponseEntity<CustomApiResponse<String>> deleteErrorReport(
			HttpServletRequest request,
			@PathVariable("id") long id
	) throws IOException {

		try{
			errorReportService.deleteErrorReport(id);
			return CustomApiResponse.success(ResponseCode.OK, "삭제 완료");

		} catch (IllegalArgumentException e) {

			return CustomApiResponse.error(ResponseCode.BAD_REQUEST);

		} catch (Exception e) {

			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}

}
