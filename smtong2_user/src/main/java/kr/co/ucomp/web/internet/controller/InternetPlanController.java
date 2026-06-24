package kr.co.ucomp.web.internet.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.meta.MetaInfoService;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.internet.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.internet.entity.InternetPlanEntity;
import kr.co.ucomp.web.internet.service.InternetPlanNewService;
import kr.co.ucomp.web.internet.service.InternetPlanService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import kr.co.ucomp.web.internet.entity.InternetRdpMngEntity;


@Controller
@RequestMapping( value = "/internet" )
@Slf4j
public class InternetPlanController {
	
	@Autowired
	private InternetPlanService 			internetPlanService;
	
	@Autowired
	private InternetPlanNewService 			internetPlanNewService;
	
	
	
	@GetMapping("/internetReq")
	public String internetReq( HttpServletRequest request, Model model) throws IOException {
		
		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		
		Map<String, Object> userInfoMap = new HashMap<>();
		userInfoMap.put("id"			, "");
		userInfoMap.put("username"		, "");
		userInfoMap.put("phoneNumber"	, "");
		
		HttpSession session = request.getSession(false);
		if(session != null) {
			UserDTO sessionUserInfo = (UserDTO)session.getAttribute("userInfo");
			if(sessionUserInfo != null) {
					
				userInfoMap.clear();
				userInfoMap.put("id"			, sessionUserInfo.getId()			);
				userInfoMap.put("username"		, sessionUserInfo.getUsername()		);
				userInfoMap.put("phoneNumber"	, sessionUserInfo.getPhoneNumber()	);
			}
		}
		model.addAttribute("userInfoMap", userInfoMap);
		
		return "pages/internet/internetReq";
	}
	
	/**
	 * 오늘에 통신용 	
	 * @param request
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/internetReq/{siteSp}")
	public String internetReqToday( HttpServletRequest request, Model model, @PathVariable("siteSp") String siteSp) throws IOException {
		
		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능

		Map<String, Object> userInfoMap = new HashMap<>();
		userInfoMap.put("id"			, "");
		userInfoMap.put("username"		, "");
		userInfoMap.put("phoneNumber"	, "");
		
		HttpSession session = request.getSession(false);
		if(session != null) {
			UserDTO sessionUserInfo = (UserDTO)session.getAttribute("userInfo");
			if(sessionUserInfo != null) {
					
				userInfoMap.clear();
				userInfoMap.put("id"			, sessionUserInfo.getId()			);
				userInfoMap.put("username"		, sessionUserInfo.getUsername()		);
				userInfoMap.put("phoneNumber"	, sessionUserInfo.getPhoneNumber()	);
			}
		}
		model.addAttribute("userInfoMap", userInfoMap);
		
		model.addAttribute("siteSp", siteSp);
		
		return "pages/internet/internetReqOther";
	}
	
	
	
	
	@GetMapping("/landing/{rdMng}")
	public String internetReqRD( HttpServletRequest request, Model model, @PathVariable("rdMng") String rdMng) throws IOException {
		
		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		
		Map<String, Object> userInfoMap = new HashMap<>();
		userInfoMap.put("id"			, "");
		userInfoMap.put("username"		, "");
		userInfoMap.put("phoneNumber"	, "");
		
		HttpSession session = request.getSession(false);
		if(session != null) {
			UserDTO sessionUserInfo = (UserDTO)session.getAttribute("userInfo");
			if(sessionUserInfo != null) {
					
				userInfoMap.clear();
				userInfoMap.put("id"			, sessionUserInfo.getId()			);
				userInfoMap.put("username"		, sessionUserInfo.getUsername()		);
				userInfoMap.put("phoneNumber"	, sessionUserInfo.getPhoneNumber()	);
			}
		}
		model.addAttribute("userInfoMap", userInfoMap);
		
		
		InternetRdpMngEntity record = internetPlanService.getRDPDetail(Integer.parseInt(rdMng));
		
		model.addAttribute("record", record);
		
		return "pages/internet/internetReqRD";
	}
	
	/**
	 * 상품명만 Distinct 된 data 조회
	 *  현재 화면 구조가 이름 먼저 선택 후 속도 선택 이기에 이와 같은 구조로 한다.
	 */
	@ResponseBody
	@PostMapping(value = "/getInternetPlanList")
	public ResponseEntity<CustomApiResponse<List<InternetPlanEntity>>> getInternetPlanList(HttpServletRequest request ,@RequestBody InternetPlanSearchDTO param) throws IOException {
		List<InternetPlanEntity> resultList 	= null;
		try {
			resultList 		= internetPlanService.listPlanName(param);
			return CustomApiResponse.success(ResponseCode.OK, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan list: " + e.getMessage());
		}
	}
	
	/**
	 * 상품명 + 채널수 <- 로 선택 된, speed 들을 조회해 여기서 최종 상품 선택을 마친다. result 에는 모든 정보가 있어야, 다음 단계를 수행 할 수 있다.
	 */
	@ResponseBody
	@PostMapping(value = "/getInternetSpeedList")
	public ResponseEntity<CustomApiResponse<List<InternetPlanEntity>>> getInternetSpeedList(HttpServletRequest request ,@RequestBody InternetPlanSearchDTO param) throws IOException {
		List<InternetPlanEntity> resultList 	= null;
		try {
			resultList 		= internetPlanService.list(param);
			return CustomApiResponse.success(ResponseCode.OK, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan list: " + e.getMessage());
		}
	}
	
	
	
	/**
	 * Internet Plan
	 *  - 전체 only
	 * @param request
	 * @param 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/list")
	public ResponseEntity<CustomApiResponse<List<InternetPlanEntity>>> list (HttpServletRequest request, @RequestBody InternetPlanSearchDTO param) throws Exception 
	{
		Long totCnt  						= null;
		List<InternetPlanEntity> resultList 	= null;
		
		try {
			totCnt  			= internetPlanService.count(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= internetPlanService.list(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan list: " + e.getMessage());
		}
	}

	/**
	 * Internet Plan 상세 내역을 조회 한다.
	 * @param request
	 * @param param /detail
	 * @return
	 * @throws Exception
	 */
	@PostMapping( value = "/detail" )
	public ResponseEntity<CustomApiResponse<InternetPlanEntity>> detail (HttpServletRequest request, @RequestBody InternetPlanSearchDTO param) throws Exception 
	{
		int searchid = (param.getSearchId() == null) ? 0 : param.getSearchId();
		if(searchid == 0) return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		try {
			InternetPlanEntity result = internetPlanService.byId(param);
			if (result == null) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND);
			}
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan  detail: " + e.getMessage());
		}
	}
	
	
	
	//============================ 인터넷 신청 신규 버전
	
	
	/**
	 * Internet Plan
	 *  - 전체 only
	 * @param request
	 * @param 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/list_new")
	public ResponseEntity<CustomApiResponse<List<InternetPlanEntity>>> list_new (HttpServletRequest request, @RequestBody InternetPlanSearchDTO param) throws Exception 
	{
		Long totCnt  						= null;
		List<InternetPlanEntity> resultList 	= null;
		
		try {
			totCnt  			= internetPlanNewService.count(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= internetPlanNewService.list(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "internet plan list: " + e.getMessage());
		}
	}
	
	
	

}
