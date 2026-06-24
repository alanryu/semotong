package kr.co.ucomp.web.plan.controller;

import java.io.IOException;
import java.util.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.meta.MetaInfoService;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.CookieService;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.company.dto.CompanyListSearchDto;
import kr.co.ucomp.web.company.entity.CompanyListEntity;
import kr.co.ucomp.web.company.service.CompanyListService;
import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;
import kr.co.ucomp.web.csm.banner.service.BannerService;
import kr.co.ucomp.web.csm.faq.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.faq.entity.FaqEntity;
import kr.co.ucomp.web.csm.faq.service.FaqService;
import kr.co.ucomp.web.csm.review.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.review.entity.SemotongReviewEntity;
import kr.co.ucomp.web.csm.review.service.SemotongReviewService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.dto.searchPlanZzimDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.RecomPlanEntity;
import kr.co.ucomp.web.plan.service.PlanBenefitService;
import kr.co.ucomp.web.plan.service.PlanFreebieService;
import kr.co.ucomp.web.plan.service.PlanService;
import kr.co.ucomp.web.plan.service.PlanZzimService;
import kr.co.ucomp.web.plan.service.RecomPlanMngService;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 조일근
 * @since 2024.12.25
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/pbm/plan")
@Slf4j
public class PlanContoroller {

	@Autowired PlanService planService;
	@Autowired FileService fileService;
	@Autowired PlanBenefitService benefitService;
	@Autowired PlanFreebieService freebieservice;
	@Autowired CookieService cookieService;	
	@Autowired BannerService bannerService;;
	@Autowired CompanyListService companyListService;
	@Autowired PlanZzimService planZzimService;
	
	@Autowired private RecomPlanMngService recomPlanMngService;
	
	@Autowired CommCodeMngService comCodeService;
	
	@Autowired 
	private FaqService faqService;
	
	@Autowired 
	private SemotongReviewService reviewService;

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
					idx = idx > 2 ? 3 : idx;
						
					List<String> cookieList = planIdsR.subList(0, idx);			// 3개까지 자른다
					
					param.setSearchplanIdList(cookieList);
					param.setSearchOrderType("cookie");
					param.setSearchCookieIds(String.join(",", cookieList));
					resultList = planService.getAllListByPlanIds(param);
					if(resultList !=null) {
						resulCnt = resultList.size();
					}	
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
	 * 검색결과 총 갯수
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@PostMapping(value = "/getPlanlistCount")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getPlanlistCount(
			HttpServletRequest request,
			@RequestBody SearchPlanDto param
	) throws IOException {

		try{
			long resulCnt = planService.getListCount(param);
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();

			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}

	//==================================== 화면 컨트롤러
	
	   /**
	  * 2024-12-18 조일근
	  * 요금제 리스트
	  * @param CommCodeSearchDto
	  * 
	  */
	 @GetMapping("/planList/{type}")
	 public String  planList( HttpServletRequest request ,  HttpServletResponse response,Model model, @PathVariable("type") String type)  {
		 
		 MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		 
		 /* 검색 필터용 통신사 get */
		 CompanyListSearchDto searchDto = new CompanyListSearchDto();
		 searchDto.setUseYn(1);
		 searchDto.setRecordSize(100);
		 searchDto.setType(type);
		 List<CompanyListEntity> list = companyListService.getListCompanyList(searchDto);
		 
		 model.addAttribute("list", list);
		// 인기요금제 추가
		List<PlanEntity> popPlanList = new ArrayList<PlanEntity>();
			
		popPlanList = planService.getPopulerPlanList();
		model.addAttribute("popPlanList", popPlanList);
		
		
		CommCodeSearchDto searchCode = new CommCodeSearchDto();
		searchCode.setRecordSize(100);	
		searchCode.setCodeGroup("plan_tag_code_group");
		searchCode.setUserYn("Y");
		if("mvno".equals(type)) {
			searchCode.setEtc1("1");	
		} else {
			searchCode.setEtc1("2");
		}
		System.out.println(type);	
		
		List<CodeEntity> tagCodelist = comCodeService.getListCode(searchCode);
		model.addAttribute("tagCodelist", tagCodelist);

		// 오늘이 속한 주의 월요일 날짜 구하기
		LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        Date mondayDate = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
		model.addAttribute("mondayDate", mondayDate);
		 return "pages/plan/planList";
	 }
	 
	   /**
	  * 2024-12-18 조일근
	  * 요금제 상세
	  * @param CommCodeSearchDto
	  * 
	  */
	 @SuppressWarnings("unchecked")
	@GetMapping("/planDetail/{type}")
	 public String  planLDetail( HttpServletRequest request , HttpServletResponse response,Model model, @PathVariable("type") String type)  {
		 
		 MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		 
		 ObjectMapper mapper = new ObjectMapper();
		 
		 String planid = request.getParameter("planid");
		 
		 if(StringUtils.isNotBlank(planid)) {
			PlanEntity result = new PlanEntity();  
			result = planService.getDetail(Integer.parseInt(planid));
			if (result != null) {
				 
				// 결합배너
				BannerSearchDto param = new BannerSearchDto(); 
				param.setIsDispStatusDsp(1);
				param.setSearchBannerType("05");				
				List<BannerEntity> bannerList = bannerService.list(param);
				if (bannerList != null && !bannerList.isEmpty()) {
					model.addAttribute("bannerListCount", bannerList.size());
					model.addAttribute("bannerList", bannerList);	
				} else {
					model.addAttribute("bannerListCount", 0);
				}
			}
			
			try {
				
				HttpSession 		session		= request.getSession(false);
				
				if(session != null) {
					UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
					if(loginInfo!=null && StringUtils.isNoneBlank( loginInfo.getKakaoUserId())) {
						searchPlanZzimDto zzimparam = new searchPlanZzimDto();
						zzimparam.setUserMngId(loginInfo.getId());
						zzimparam.setPlanListId(Integer.parseInt(planid));
						long cnt = planZzimService.getCount(zzimparam);
						if(cnt > 0 ) {
							result.setPlanLoginUserZzimCnt((int)cnt);
						} else {
							result.setPlanZzimCnt(0);
						}
					}
				}
				
				model.addAttribute("result", result);
				model.addAttribute("planid", planid);
			
			 
			 //try {
				cookieService.deleteCookie(response,"lastViewPlan");
				String lastViewPlan =  cookieService.getCookie(request, "lastViewPlan");
				
				String cookieVal = "";
				if(StringUtils.isNotBlank(lastViewPlan)) {
					
					cookieVal = cookieService.chkDupCookieVal(lastViewPlan,planid);
					
				} else {
					cookieVal = planid;
				}
				
				cookieService.createCookie(response, "lastViewPlan", cookieVal);
				
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
				
				/* application/ld+json */
				JSONObject json = new JSONObject();
				json.put("@context", "https://schema.org");
				json.put("@type", "Product");
				json.put("productID", result.getId());
				json.put("name", result.getPlanName());

				// 설명(description) 생성
				StringBuilder description = new StringBuilder();

				// 기본 데이터 제공량
				if (result.getSupDataVal() == 10238976) {
				    description.append("무제한");
				} else {
				    description.append("월 ").append(result.getSupDataVal() / 1024).append("GB");
				}

				// 매일 추가 데이터 제공량
				if (result.getDailyData() > 0) {
				    description.append(" + 매일 ").append(result.getDailyData() / 1024).append("GB");
				}

				// QOS (속도 제한)
				int supQos = result.getSupQos();
				if (supQos > 0) {
				    if (supQos >= 1024) {
				        description.append(" + ").append(supQos / 1024).append("Mbps");
				    } else {
				        description.append(" + ").append(supQos).append("Kbps");
				    }
				}

				json.put("description", description.toString());

				// URL 생성
				String url = request.getScheme() + "://" + request.getServerName();
				if (request.getServerPort() != 80) {
				    url += ":" + request.getServerPort();
				}
				url += "/pbm/plan/planDetail/" + type + "?planid=" + result.getId();
				json.put("url", url);

				// Offer 정보
				JSONObject offers = new JSONObject();
				offers.put("@type", "Offer");
				offers.put("url", url);
				offers.put("price", result.getSalePrice());
				offers.put("priceCurrency", "KRW");
				offers.put("itemCondition", "https://schema.org/NewCondition");
				offers.put("availability", "https://schema.org/InStock");

				json.put("offers", offers);

				// 리뷰 집계 정보
				SemotongReviewDto param = new SemotongReviewDto();
				param.setPlanId(String.valueOf(result.getId()));
				SemotongReviewEntity reviewAg = reviewService.reviewAggregate(param);

				JSONObject aggregateRating = new JSONObject();
				aggregateRating.put("@type", "AggregateRating");
				aggregateRating.put("ratingValue", reviewAg.getAvgScore());
				aggregateRating.put("ratingCount", reviewAg.getCnt());
				aggregateRating.put("reviewCount", reviewAg.getCnt());
				aggregateRating.put("bestRating", reviewAg.getMaxScore());
				aggregateRating.put("worstRating", reviewAg.getMinScore());

				json.put("aggregateRating", aggregateRating);

				// 브랜드 정보
				JSONObject brand = new JSONObject();
				brand.put("@type", "Brand");
				brand.put("name", result.getHostNm());

				json.put("brand", brand);

				// 추가 속성 추가
				JSONArray additionalProperty = new JSONArray();

				// 통화 제공량
				JSONObject callObj = new JSONObject();
				callObj.put("@type", "PropertyValue");
				callObj.put("name", "통화 제공량");
				callObj.put("value", result.getSupCallVal() == 9999 ? "무제한" : result.getSupCallVal() + "분");
				additionalProperty.add(callObj);

				// 데이터 제공량
				JSONObject dataObj = new JSONObject();
				dataObj.put("@type", "PropertyValue");
				dataObj.put("name", "데이터 제공량");
				dataObj.put("value", (result.getSupDataVal() / 1024) + "GB");
				additionalProperty.add(dataObj);

				// 문자 제공량
				JSONObject smsObj = new JSONObject();
				smsObj.put("@type", "PropertyValue");
				smsObj.put("name", "문자 제공량");
				smsObj.put("value", result.getSupSmsVal() == 9999 ? "무제한" : result.getSupSmsVal() + "건");
				additionalProperty.add(smsObj);

				// 망 종류
				JSONObject planObj = new JSONObject();
				planObj.put("@type", "PropertyValue");
				planObj.put("name", "망 종류");
				planObj.put("value", result.getMno() + " " + result.getPlanType());
				additionalProperty.add(planObj);

				// 할인 기간
				int promotionPeriodVal = Integer.parseInt(result.getPromotionPeriodVal());
				if (promotionPeriodVal > 0) {
				    JSONObject promoObj = new JSONObject();
				    promoObj.put("@type", "PropertyValue");
				    promoObj.put("name", "할인 기간");
				    promoObj.put("value", result.getPromotionPeriod());
				    additionalProperty.add(promoObj);
				}

				// QOS 정보 추가
				if (supQos > 0) {
				    JSONObject qosObj = new JSONObject();
				    qosObj.put("@type", "PropertyValue");
				    qosObj.put("name", "QOS");

				    String qosValue = (supQos >= 1024) 
				        ? "초과 시 " + (supQos / 1024) + "Mbps 속도 제한"
				        : "초과 시 " + supQos + "Kbps 속도 제한";

				    qosObj.put("value", qosValue);
				    additionalProperty.add(qosObj);
				}

				json.put("additionalProperty", additionalProperty);
				 
				model.addAttribute("jsonData", json.toJSONString().replaceAll("\\\\", ""));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
	 	return "pages/plan/planDetail";
	 }
	 
	 
	
	   /**
	  * 2024-12-18 조일근
	  * 요금제 배너 상세
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @GetMapping("/bannerPlan")
	 public String  bannerPlan(  HttpServletRequest request , HttpServletResponse response,Model model)  {
		 String bannerId = request.getParameter("bannerId");
		 model.addAttribute("bannerId", bannerId);
		 
		 
		/* 요금제 배너 (searchType : 02) */
		BannerSearchDto bannerSearchDto = new BannerSearchDto();
		bannerSearchDto.setSearchBannerType("02");
		bannerSearchDto.setIsDispStatusDsp(1);
		BannerEntity bannerInfo = bannerService.getDetail(Integer.parseInt(bannerId));
		model.addAttribute("bannerInfo", bannerInfo);

		/* 중단 배너 (searchType : 03) */
		bannerSearchDto = new BannerSearchDto();
		bannerSearchDto.setSearchBannerType("03");
		bannerSearchDto.setIsDispStatusDsp(1);
		List<BannerEntity> middleBannerList = bannerService.list(bannerSearchDto);
		
		model.addAttribute("middleBannerList", middleBannerList);
		
			
	 	return "pages/plan/bannerPlan";
	 }

	 
	   /**
	  * 2024-12-18 조일근
	  * 주간 요금제 리스트
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @GetMapping("/weeklyPlan")
	 public String  weeklyPlan(  HttpServletRequest request , HttpServletResponse response,Model model)  {
	 	
	 	return "pages/plan/weeklyPlan";
	 }

	 

	 
	   /**
	  * 2024-12-18 조일근
	  * 추천 요금제
	  * @param CommCodeSearchDto
	  * @param 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	  */
	 @SuppressWarnings("unchecked")
	@GetMapping("/recomPlan")
	 public String  reconmPlan(  HttpServletRequest request , HttpServletResponse response,Model model) throws Exception  {
	 	
		 ObjectMapper mapper = new ObjectMapper();
		 
		 String recomType = request.getParameter("type");
		 model.addAttribute("recomType", recomType);
		 
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
		
		/* 추천요금제 자주묻는질문 */
		FaqSearchDto faqSearchDto = new FaqSearchDto();
		faqSearchDto.setCategoryId("cate07");
		faqSearchDto.setDisplayYn("1");
		List<FaqEntity> faqList = faqService.getListFaq(faqSearchDto);
		
		model.addAttribute("faqList", faqList);
		 
	 	return "pages/plan/recomPlan";
	 }
	 
	 
	 
	   /**
	  * 2024-12-18 조일근
	  * 추천 요금제
	  * @param CommCodeSearchDto
	  * @param 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	  */
	 @SuppressWarnings("unchecked")
	@GetMapping("/recomPlanList")
	 public String  recomPlanList(  HttpServletRequest request , HttpServletResponse response,Model model) throws Exception  {
	 	
		 ObjectMapper mapper = new ObjectMapper();
		 String mngId = request.getParameter("mngId");
		 long planCnt = 0;
		 
		 
		 if(StringUtils.isBlank(mngId)) {
			 mngId = "-1";
		 } else {
			 
			 SearchPlanDto param =  new SearchPlanDto();
			 param.setRecomMngId(Integer.parseInt(mngId));
			 planCnt = planService.getRecomPlanListCount(param);
			 
			 RecomPlanEntity recomPlanDet =  recomPlanMngService.infoDetail(Integer.parseInt(mngId));
			 model.addAttribute("recomPlanDet", recomPlanDet);
		 }
		 
		 model.addAttribute("planCnt", planCnt);
		 model.addAttribute("mngId", mngId);
		 
		 

		 
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
		
		/* 추천요금제 자주묻는질문 */
		FaqSearchDto faqSearchDto = new FaqSearchDto();
		faqSearchDto.setCategoryId("cate07");
		faqSearchDto.setDisplayYn("1");
		List<FaqEntity> faqList = faqService.getListFaq(faqSearchDto);
		model.addAttribute("faqList", faqList);
		 
	 	return "pages/plan/recomPlan2";
	 }	 
	 
}
