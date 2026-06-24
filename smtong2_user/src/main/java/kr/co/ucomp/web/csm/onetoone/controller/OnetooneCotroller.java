package kr.co.ucomp.web.csm.onetoone.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.biztalk.KakaoBizTalkUtils;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.csm.onetoone.dto.OnetooneDto;
import kr.co.ucomp.web.csm.onetoone.entity.OnetooneEntity;
import kr.co.ucomp.web.csm.onetoone.service.OnetooneService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 김재희
 * @since 2024.12.20
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/csm/onetoone")
@Slf4j
public class OnetooneCotroller {
	
	@Autowired private OnetooneService onetooneService;
	@Autowired CommCodeMngService comCodeService;
	@Autowired private KakaoBizTalkUtils bizTalkService;

	@PostMapping(value = "list")
	public ResponseEntity<CustomApiResponse<List<OnetooneEntity>>> getOneToOneList(HttpServletRequest request,@RequestBody OnetooneDto param) throws IOException {
		try{
			List<OnetooneEntity> resultList = onetooneService.oneToOneList(param);
			int cnt = resultList.size();
			return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);
		
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getOneToOneList: " + e.getMessage());
		
		}
	
	}
	
	@GetMapping("/detail/{id}")
	public ResponseEntity<CustomApiResponse<OnetooneEntity>> getOneToOne(HttpServletRequest request,@PathVariable("id") long id) throws IOException {
		try{
			OnetooneEntity result = onetooneService.oneToOne(id);
			if (result == null) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND, "1:1 문의가 존재 하지 않습니다.");
			}
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getOneToOne: " + e.getMessage());
		}
	}
	
	
	@PostMapping("/create")
	public ResponseEntity<CustomApiResponse<OnetooneEntity>> insertOneToOne(HttpServletRequest request,@RequestBody OnetooneEntity param) throws IOException {
		try{
			onetooneService.insertOneToOne(param);
			
			
			
			// 관리자 알림 전송 전송
			Map<String, String> headerMap = bizTalkService.makeKakaoBizTalkHeader();

    		CommCodeSearchDto codeparam =  new CommCodeSearchDto();
    		codeparam.setCodeGroup("common_env_code");
    		codeparam.setCode("admin_alam_onetoone");
    		CodeEntity codeItm =  comCodeService.getCode(codeparam);
    		String alamTo = codeItm.getEtc1();
    		String sendMsg = codeItm.getEtc2();
    		String[] alamToList  = alamTo.split("/");
    		for (int i = 0; i < alamToList.length; i++) {
    			String adminPhoneNum = alamToList[i] !=null ? alamToList[i] : "";
    			if(StringUtils.isNotBlank(adminPhoneNum)) {
    				bizTalkService.sendSMSMsg(headerMap, sendMsg, "sms", adminPhoneNum);	
    			}
    		}
    		
			return CustomApiResponse.success(ResponseCode.OK, param);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "insertOneToOne: " + e.getMessage());
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "insertOneToOne: " + e.getMessage());
		}
	}
	
	@PostMapping("/update")
	public ResponseEntity<CustomApiResponse<OnetooneEntity>> updateOneToOne(HttpServletRequest request,@RequestBody OnetooneEntity param) throws IOException {
		try{
			onetooneService.updateOneToOne(param);
			return CustomApiResponse.success(ResponseCode.OK, param);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "updateOneToOne: " + e.getMessage());
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "updateOneToOne: " + e.getMessage());
		}
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<CustomApiResponse<String>> deleteOneToOne(HttpServletRequest request,@PathVariable("id") long id) throws IOException {
		try{
			onetooneService.deleteOneToOne(id);
			return CustomApiResponse.success(ResponseCode.OK, "삭제완료");
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "deleteOneToOne: " + e.getMessage());
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "deleteOneToOne: " + e.getMessage());
		}
	}
	
	
	
	
	
	/**
	 * 2024-12-18 조일근
	 * 1:1 문의
	 * @param model
	 * @param  
	*/
	@GetMapping("/onetoone")
	public String  policy( HttpServletRequest request, Model model) throws IOException {
		
		HttpSession session = request.getSession(false);
		UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
		if(loginInfo == null  || loginInfo.getId() == 0) {
			return "redirect:/users/login";
		}
		
		CommCodeSearchDto searchCode = new CommCodeSearchDto();
		searchCode.setRecordSize(1000);	//page 당 사이즈 기본 10 이기에 일단 많이 넣어본다.
		searchCode.setCodeGroup("onetoone_cate");
		searchCode.setUserYn("Y");
		
		List<CodeEntity> list = null;

		long cnt = comCodeService.getListCodeCount(searchCode);
		if(cnt > 0) {
			list =  comCodeService.getListCode(searchCode);	
		}
		
		OnetooneDto searchParam = new OnetooneDto();
		searchParam.setRequestUser(loginInfo.getId() );
		
		List<OnetooneEntity> resultList = onetooneService.oneToOneList(searchParam);
		
		
		
		model.addAttribute("codeCnt"	, cnt);
		model.addAttribute("codeList"	, list);
		model.addAttribute("onetooneList"	, resultList);
		
		return "pages/csm/onetoone/onetoone";
	}
	 

}
