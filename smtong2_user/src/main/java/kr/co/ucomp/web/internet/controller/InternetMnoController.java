package kr.co.ucomp.web.internet.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.internet.entity.InternetMnoEntity;
import kr.co.ucomp.web.internet.service.InternetMnoService;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping( value = "/internetMno" )
@Slf4j
public class InternetMnoController {
	
	@Autowired
	private InternetMnoService 			internetMnoService;
	
	
	@ResponseBody
	@PostMapping(value = "/getMnoList")
	public ResponseEntity<CustomApiResponse<List<InternetMnoEntity>>> getMnoList(HttpServletRequest request) throws IOException {
		Long totCnt  							= null;
		List<InternetMnoEntity> resultList 	= null;
		try {
			totCnt  			= internetMnoService.count();
			if(totCnt != null && totCnt > 0) { 
				resultList 		= internetMnoService.list();
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan list: " + e.getMessage());
		}
	}
	
	
	
	@ResponseBody
	@PostMapping(value = "/getMnoListNew")
	public ResponseEntity<CustomApiResponse<List<InternetMnoEntity>>> getMnoListNew(HttpServletRequest request) throws IOException {
		Long totCnt  							= null;
		List<InternetMnoEntity> resultList 	= null;
		try {
			totCnt  			= internetMnoService.countNew();
			if(totCnt != null && totCnt > 0) { 
				resultList 		= internetMnoService.listNew();
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan list: " + e.getMessage());
		}
	}	

}
