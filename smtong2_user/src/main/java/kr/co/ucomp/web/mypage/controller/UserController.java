package kr.co.ucomp.web.mypage.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.auth.oauth.service.OAuthService;
import kr.co.ucomp.common.config.LoginRequired;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.CookieService;
import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.csm.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;
import kr.co.ucomp.web.csm.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.csm.banner.entity.MainDealMstEntity;
import kr.co.ucomp.web.csm.banner.service.BannerService;
import kr.co.ucomp.web.csm.banner.service.DealBannerService;
import kr.co.ucomp.web.csm.review.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.review.service.SemotongReviewService;
import kr.co.ucomp.web.mypage.dto.PointDTO;
import kr.co.ucomp.web.mypage.dto.PointHistoryDTO;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.mypage.entity.PointEntity;
import kr.co.ucomp.web.mypage.entity.PointHistoryEntity;
import kr.co.ucomp.web.mypage.service.PointHistoryService;
import kr.co.ucomp.web.mypage.service.PointService;
import kr.co.ucomp.web.mypage.service.UserService;
import kr.co.ucomp.web.plan.dto.PlanReqMngDto;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.dto.searchPlanZzimDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanZzimEntity;
import kr.co.ucomp.web.plan.service.PlanMyPageService;
import kr.co.ucomp.web.plan.service.PlanReqMngService;
import kr.co.ucomp.web.plan.service.PlanService;
import kr.co.ucomp.web.plan.service.PlanZzimService;
/**
 *
 * @author 이정민
 * @since 2024.12.11
 * @version v1.0
 */
@Controller
@RequestMapping("/users")
public class UserController {

	
	@Autowired private UserService userService;
	@Autowired private OAuthService oAuthService;

	@Value("${kakao.client.id}"				) String kakaoClientId;
	@Value("${kakao.client.redirect-uri}"	) String kakaoRedirectUri;
	
	@Autowired CookieService 			cookieService;
	@Autowired PlanService 				planService;
	@Autowired PlanZzimService 			zzimService;
	@Autowired PlanReqMngService 		reqMngService;
	@Autowired SemotongReviewService	reviewService;
	@Autowired PlanMyPageService		planMyPageService;
	
	@Autowired DealBannerService 		dealBannerService;	//추천딜
	
	@Autowired 
	private BannerService bannerService;
	
	@Autowired PointService				pointService;
	@Autowired PointHistoryService		pointHistoryService;
	
	
	/**
	 * 마이페이지
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@LoginRequired
	@GetMapping("/mypage")
	public String  mypage( HttpServletRequest request, Model model) throws IOException  {
		//session.removeAttribute("isLoginYn");
		HttpSession session = request.getSession(false);
		//Map<String, Object> login = (Map<String, Object>)session.getAttribute("isLoginYn");
		UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
		
		//System.out.println("isLoginYn:" + session.getAttribute("isLoginYn"));
		//System.out.println(loginInfo);
		
		//마이페이지 중단, [최근 본 요금제] , [찜한 요금제] , [신청 요금제] count 용.
		
		// [최근 본 요금제]
		long nearCnt 	= 0;
		long zzimCnt 	= 0;
		long applyCnt 	= 0;
		
		String lastViewPlan =  cookieService.getCookie(request, "lastViewPlan");
		if(StringUtils.isNoneBlank(lastViewPlan)) {
			String[] planIds = lastViewPlan.split("/");
			List<String> planIdsR = Arrays.asList(planIds).reversed();	// 배열 뒤집기
			int idx = planIdsR.size();
			idx = idx > 2 ? 3 : idx;
			//List<String> cookieList = planIdsR.subList(0, idx);			// 3개까지 자른다
			
			SearchPlanDto param = new SearchPlanDto();
			//param.setSearchplanIdList(cookieList);
			
			param.setSearchplanIdList(planIdsR);							//01.26 3개 까지 자르는거 뺌
			
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
			resultList = planService.getAllListByPlanIds(param);
			if(resultList !=null) {
				nearCnt = resultList.size();
			}
		}
		
		// [찜한 요금제] 2023  2580  2162   1559 1563
		searchPlanZzimDto zzimparam = new searchPlanZzimDto();
		zzimparam.setUserMngId(loginInfo.getId());
		//List<PlanZzimEntity> resultList = zzimService.getlist(zzimparam);
		zzimCnt = zzimService.getCount(zzimparam);
		
		// [신청 요금제] count 용.
		PlanReqMngDto searchRequest = new PlanReqMngDto();
		searchRequest.setSearchId(loginInfo.getId());
		applyCnt = reqMngService.getListCountDistinct(searchRequest);
		
		model.addAttribute("nearCnt"	, nearCnt);
		model.addAttribute("zzimCnt"	, zzimCnt);
		model.addAttribute("applyCnt"	, applyCnt);
		
		
		ObjectMapper mapper = new ObjectMapper();
		 
 		// 결합 배너 (searchType : 05) //
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
		
		// [Point]
		//loginInfo.getId()
		PointDTO pointParam = new PointDTO();
		pointParam.setSearchUserId(loginInfo.getId());
		PointEntity pointEnt = pointService.getMyPoint(pointParam);
		model.addAttribute("pointEnt", pointEnt);
		
		// - [내캐쉬] 보기 pointHistoryService
		PointHistoryDTO pointHParam = new PointHistoryDTO();
		pointHParam.setSearchUserId(loginInfo.getId());
		//List<PointHistoryEntity> pointHEnt = pointHistoryService.getMyPointHistory(pointHParam);
		List<PointHistoryEntity> pointHEnt = pointHistoryService.getMyPointHistoryNew(pointHParam);
		model.addAttribute("pointHEnt", pointHEnt);
		
		// --------------
		session.setAttribute("pointId", pointEnt.getId());
		session.setAttribute("balance", pointEnt.getBalance());
		// --------------
		
		return "pages/mypage/mypage";
	}

	/*
	 * 내가 쓰는 요금제 최신 1건
	 */
	@ResponseBody
	@PostMapping(value = "/getMyPlan")
	public ResponseEntity<CustomApiResponse<PlanEntity>> getMyPlan(HttpServletRequest request) throws IOException {
		HttpSession session = request.getSession(false);
		UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
		long id = loginInfo.getId();
		try{
			// 사용중인 최신 1건 요금제 찾기 searchPlanZzimDto zzimparam = new searchPlanZzimDto();
			SearchPlanDto searchMyPlan = new SearchPlanDto();
			searchMyPlan.setSearchUserId((int) id);
			PlanEntity myPlan = planMyPageService.getMyPlan(searchMyPlan);
			long resulCnt = 0;
			if(myPlan != null) resulCnt = 1;
			return CustomApiResponse.success(ResponseCode.OK, resulCnt, myPlan);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 내 계정
	 */
	@LoginRequired
	@GetMapping("/myinfo")
	public String  myinfo( HttpServletRequest request, Model model) throws IOException  {
		
		// 여기서 후기, 요금제 조회 한다.
		HttpSession 		session 	= request.getSession(false);
		UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
		
		SemotongReviewDto param = new SemotongReviewDto();
		
		param.setCreateId(loginInfo.getId());
		param.setDisplayYn("Y");

		//  return 들
		//1.review count 조회
		int reviewCnt = reviewService.reviewListCount(param);

		
		model.addAttribute("reviewCnt"		, reviewCnt		);
		
		return "pages/mypage/myinfo";
	}
	
	/**
	 * 최근 본 요금제
	 */
	@GetMapping("/nearview")
	public String  nrearview( HttpServletResponse response,Model model)  {
		return "pages/mypage/nearview";
	}
	
	@GetMapping("/delNearview")
	public String  delNearview( HttpServletRequest request, HttpServletResponse response, Model model)  {
		//Cookie 삭제
		cookieService.deleteCookie(response,"lastViewPlan");
		return "pages/mypage/nearview";
	}
	
	
	
	//요금제 페이지에서 최근 본 요금제 3개 까지 자른다. 그래서 이곳에 따로 만들어 둠.
	@ResponseBody
	@PostMapping(value = "/getPlanlist")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getReviewList(
			HttpServletRequest request,
			@RequestBody SearchPlanDto param
	) throws IOException {

		try{
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
			long resulCnt = 0;
			
			HttpSession 		session 	= request.getSession(false);
			UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
			if(loginInfo!=null && StringUtils.isNoneBlank( loginInfo.getKakaoUserId())) {
				param.setSearchUserId((int) loginInfo.getId());
			}
			
			if("2".equals(param.getSearchType())) {
				String lastViewPlan =  cookieService.getCookie(request, "lastViewPlan");
				if(StringUtils.isNoneBlank(lastViewPlan)) {
					String[] planIds = lastViewPlan.split("/");
					
					List<String> planIdsR = Arrays.asList(planIds).reversed();	// 배열 뒤집기
					
					int idx = planIdsR.size();
					//idx = idx > 2 ? 3 : idx;
					
					//5개씩 끊기를 원한다. - 개발 다 끝나고 기획서에 추가하는 구나.... 죈장....
					int page = param.getPage();
					int divInt = 5;
					
					int sIdx = (page-1) * divInt;
					int eIdx = 0;
					int moc = idx/divInt;
					int mod = idx%divInt;
					
					if(moc == 0) {
						eIdx = idx;
					}else {
						
						if(page <= moc) {
							eIdx = page * divInt;
						}else {
							eIdx = sIdx + mod;
						}
					}
						
					List<String> cookieList = planIdsR.subList(sIdx, eIdx);			// 3개까지 자른다
					param.setSearchplanIdList(cookieList);
					//param.setSearchplanIdList(planIdsR);						//01.26 3개 자르지 않고, 전량 조회 따로 만듬.
					
					resultList = planService.getAllListByPlanIds(param);
					//if(resultList !=null) {
						//resulCnt = resultList.size();
					//}
					resulCnt = idx;
				}
			} else {
				resulCnt = planService.getListCount(param);
				if(resulCnt>0) {
					resultList = planService.getList(param);
				}	
			}
			

			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	
	
	
	/**
	 * 찜한 요금제
	 */
	@SuppressWarnings("null")
	@GetMapping("/jjimview")
	public String  jjimview( HttpServletResponse response,Model model)  {
		
		return "pages/mypage/jjimview";
	}
	
	/**
	 * 찜 삭제
	 */
	@GetMapping("/delZzim")
	public String  delZzim( HttpServletRequest request, HttpServletResponse response,Model model)  {
		
		HttpSession 		session 	= request.getSession(false);
		UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
		
		PlanZzimEntity param = new PlanZzimEntity();
		param.setUserMngId((int) loginInfo.getId());
		zzimService.deleteAll(param);
		return "pages/mypage/jjimview";
	}
	
	//요금제 페이지에서 최근 본 요금제 3개 까지 자른다. 그래서 이곳에 따로 만들어 둠.
	@ResponseBody
	@PostMapping(value = "/getZzimNoDataPlanlist")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getZzimNoDataPlanlist( HttpServletRequest request, @RequestBody searchPlanZzimDto zzimparam) throws IOException {

		try{
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
			long resulCnt = 0;
			
			
			/*** main 추천딜 가져오는 로직 ***/
			/* 세모통 얼리버드 추천딜 시간 */
			MainDealMstDto params = new MainDealMstDto();
			params.setType("07");
			MainDealMstEntity mainDeal = dealBannerService.mainDealMstRec(params);
			
			/* 추천딜 배너(요금제)*/
			MainDealBannerDto param = new MainDealBannerDto();
			param.setMain_deal(mainDeal.getId());
			
			List<MainDealBannerEntity> mainDealList = dealBannerService.mainPageDealBanner(param);
			//model.addAttribute("mainDealList", mainDealList);
			/*** main 추천딜 가져오는 로직 ***/
			
			// --- 메인 추천딜과 요금제 카드 형태가 다르다 일반 요금제 가져오는 방식으로 조회 한다.
			List<String> idlist = new ArrayList<>();
			for(MainDealBannerEntity itm : mainDealList) {
				idlist.add(itm.getPlan_id() + "");
			}
			zzimparam.setSearchplanIdList(idlist);
			resulCnt = zzimService.getZzimNoDataListPlanCount(zzimparam);
			resultList = zzimService.getZzimNoDataListPlan(zzimparam);
			
			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}
		
	
	
	
	/**
	 * 신청한 요금제
	 */
	@GetMapping("/appliedview")
	public String  appliedview( HttpServletResponse response,Model model)  {
		return "pages/mypage/appliedview";
	}
	
	
	
	@GetMapping("/login")
	public String  login( HttpServletRequest request,HttpServletResponse response,Model model) throws Exception  {
		
		ObjectMapper mapper = new ObjectMapper();
		
		/* 결합 배너 (searchType : 05) */
    	BannerSearchDto bannerSearchDto = new BannerSearchDto();
    	bannerSearchDto.setSearchUseYn("Y");
    	bannerSearchDto.setSearchBannerType("05");
    	bannerSearchDto.setIsDispStatusDsp(1);
    	List<BannerEntity> bannerList = bannerService.list(bannerSearchDto);
    	String uri = request.getHeader("Referer");
    	
    	
    	if(StringUtils.isNoneBlank(uri) && !uri.contains("/users/login")) {
    		 request.getSession().setAttribute("prevPage", uri);
    	}
    	
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
		
		return "pages/mypage/login";
	}
	
	
	
	@GetMapping("/logout")
	public String  loginout( HttpServletRequest request, HttpServletResponse response,Model model)  {
		
		HttpSession session = request.getSession(false);
		session.removeAttribute("userInfo");
		session.removeAttribute("kakaoUser");
		session.removeAttribute("isLoginYn");
		
		return "pages/mypage/login";
	}
	
	
	@GetMapping("/kakaologin")
	public ResponseEntity<Object> exRedirect5() throws URISyntaxException {
		
		String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kakaoClientId+"&redirect_uri="+kakaoRedirectUri + "&prompt=select_account";
		URI redirectUri = new URI(kakaoLoginUrl);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(redirectUri);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}
	
	
	@PostMapping
	public ResponseEntity<CustomApiResponse<String>> createUser(
			@RequestBody UserDTO user) {
		try {
			if (userService.createUser(user)) {
				return CustomApiResponse.success(ResponseCode.CREATED, "Created User");
			} else {
				return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR, "Error processing kakao callback: ");
			}

		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "Error processing kakao callback: " + e.getMessage());
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
	}

	@GetMapping("/{kakaoUserId}")
	public ResponseEntity<CustomApiResponse<UserDTO>> getUserById(
			@PathVariable("kakaoUserId") String kakaoUserId) {
				UserDTO user = userService.getUserByKakaoId(kakaoUserId);
				return CustomApiResponse.success(ResponseCode.OK, user);
	}


	@GetMapping("/isactive")
	public ResponseEntity<CustomApiResponse<UserDTO>> findInactiveUserByEmail(
			@RequestParam String email,
			@RequestParam String active) {
		try {
			UserDTO user = userService.findInactiveUserByEmail(email, active);
			return CustomApiResponse.success(ResponseCode.OK, user);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.NOT_FOUND, "Error processing kakao callback: " + e.getMessage());
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
	}



	
	@PostMapping("/delete")
	public ResponseEntity<CustomApiResponse<String>> deleteUserToggle(HttpServletRequest request) {
		
		HttpSession session = request.getSession(false);
		UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
		long id = loginInfo.getId();
		
		UserDTO user = userService.getUserById(id);
		
		if (oAuthService.disconnectKakaoUser(user.getKakaoUserId())) {
			
			//여기서 명시적으로 DROP 시킨다.
			user.setMemberStat("DROP");
			user.setActiveYn("0");
			
			if(userService.deleteUserToggle(user) == 1) {
				
				session.removeAttribute("userInfo");
				session.removeAttribute("kakaoUser");
				session.removeAttribute("isLoginYn");
				
				return CustomApiResponse.success(ResponseCode.ACCEPTED, "Deleted User");
			} else if (userService.deleteUserToggle(user) == 0) {
				return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "문제있쓰요");
			} else {
				return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "유저 MEMBER_STATUS에 문제있쓰요");
			}
		} else {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "카카오가 문제있쓰요");
		}
		
	}

	
	
	/*
	 * 내가 쓰는 요금제 최신 1건
	 */
	@ResponseBody
	@PostMapping(value = "/setCurruntpage")
	public ResponseEntity<CustomApiResponse<Map<String,String>>> setCurruntpage(HttpServletRequest request) throws IOException {
		HttpSession session = request.getSession(false);
		try{
			Map<String,String> res = new HashMap<String,String>();
			String uri = request.getHeader("Referer");
			session.setAttribute("prevPage", uri);
			return CustomApiResponse.success(ResponseCode.OK,  res);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}
}
