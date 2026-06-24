package kr.co.ucomp.web.order.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.biztalk.KakaoBizTalkUtils;
import kr.co.ucomp.common.config.LoginRequired;
import kr.co.ucomp.common.encrypt.KakaoEncrypt;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.restapi.entity.RestApiTokenMngEntity;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.company.entity.CompanyListEntity;
import kr.co.ucomp.web.company.service.CompanyListService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.order.constant.ApiConstant;
import kr.co.ucomp.web.order.dto.PlanOrderSearchDto;
import kr.co.ucomp.web.order.dto.custCertDto;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;
import kr.co.ucomp.web.order.service.DailySequenceService;
import kr.co.ucomp.web.order.service.PlanOrderService;
import kr.co.ucomp.web.order.service.OrderAPIGOGOService;
import kr.co.ucomp.web.order.service.OrderAPISHAKEService;
import kr.co.ucomp.web.plan.dto.PlanReqMngDto;
import kr.co.ucomp.web.plan.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.entity.PlanReqMngEntity;
import kr.co.ucomp.web.plan.entity.SalesPlanEntity;
import kr.co.ucomp.web.plan.service.PlanSalesService;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;

/**
*
* @author 조일근
* @since 2024.12.25
* @version v1.0
*/
@Controller
@RequestMapping(value = "/order")
@Slf4j
public class PlanOrderController {

		@Autowired PlanOrderService service;
		@Autowired PlanService  planService;
		@Autowired CommCodeMngService codeService;
		@Autowired DailySequenceService sequenceService;
		@Autowired OrderAPIGOGOService orderServiceGOGO;
		@Autowired OrderAPISHAKEService orderServiceSHAKE;
		@Autowired private PlanSalesService planSalesService;
		@Autowired private RestTempletUtil rest;
		@Autowired private KakaoBizTalkUtils kakaoBizTalkUtils;
		@Autowired CompanyListService companyListService;
		
		// 서버 sp
		@Value("${order.serverSp}") String serverSp;
				
				
		// 신분증 진위 여부
		@Value("${coocon.ident.api.base-url}") String identBaseUrl;
		@Value("${coocon.ident.api.client-secret}") String identClientKey;
		@Value("${coocon.ident.api.req-cert-url}") String reqUrlCert;
		
		// 카카오 본인 인증
		@Value("${coocon.personal.api.base-url}") String pcertBaseUrl;
		@Value("${coocon.personal.api.client-id}") String pcertClientId;
		@Value("${coocon.personal.api.client-secret}") String pcertClientKey;
		@Value("${coocon.personal.api.SecretKey}") String pcertENCKey;
		@Value("${coocon.personal.api.iv}") String pcertENCIv;
		@Value("${coocon.personal.api.ret-url}") String pcertRetUrl;
		
		// 계좌 인증
		@Value("${coocon.account.api.base-url}") String accBaseUrl;
		@Value("${coocon.account.api.client-secret}") String accClientKey;
		@Value("${coocon.account.api.client-id}") String accClientId;
		@Value("${coocon.account.api.req-cert-url}") String reqUrlAcc;
		
		
	   /**
	  * 2025-02-20 조일근
	  *  01) 사용자 구분 선택
	  * @param CommCodeSearchDto
	  * @param 
	  */
	@LoginRequired
	@GetMapping("/selCustType")
	 public String  selCustType(  HttpServletRequest request , HttpServletResponse response,Model model,
			 @RequestParam("reqPlanid") String reqPlanid )  {
	 	
		PlanEntity planRecord = new PlanEntity();
		planRecord = planService.getDetail(Integer.parseInt(reqPlanid));
		model.addAttribute("planRecord", planRecord);
		int pointPlanYn = planRecord.getPointPlanYn() == null ? 0:planRecord.getPointPlanYn();   
		
		
		PlanOrderEntity planOrderEntity = new PlanOrderEntity();
		planOrderEntity.setPlanId(planRecord.getId());     // 세모통 요금제 관리 번호
		planOrderEntity.setPlanCd(planRecord.getPlanCode()); // 요금제 코드 (입점사 요금제 코드)
		planOrderEntity.setPointPlanYn(pointPlanYn);         // 포인트 요금제 여부
		planOrderEntity.setCompanyId(planRecord.getHost());  // 입점사 코드
		planOrderEntity.setTelecomCd(planRecord.getMno());  // 통신망
		planOrderEntity.setProdDv("U");						// 가입 상품유형 (- P: 휴대폰 - U: 유심)
		planOrderEntity.setPlanNm(planRecord.getPlanName());
		planOrderEntity.setBasicPrice(planRecord.getNormalPrice());
		planOrderEntity.setEventDiscPrice(planRecord.getSalePrice());
		planOrderEntity.setAfterPrice(planRecord.getAfterPrice());
		planOrderEntity.setPromotionPeriod(planRecord.getPromotionPeriod());
		planOrderEntity.setCustomerType("G");
		planOrderEntity.setRecomSp("N");
		planOrderEntity.setModelDiv(planRecord.getPlanType());
		
		String recomSalesId = request.getParameter("recomSalesId") == null ? "" : request.getParameter("recomSalesId") ;
		
		if(StringUtils.isNotBlank(recomSalesId)) {
			planOrderEntity.setRecomSp("2");
			planOrderEntity.setBizPlanMngId(Integer.parseInt(recomSalesId));
			
			PlanSalesSearchDto param = new PlanSalesSearchDto();
			param.setSalesMngId(Integer.parseInt(recomSalesId));
			SalesPlanEntity entity = planSalesService.getPlanSales(param);
			planOrderEntity.setRecomUserId((int)entity.getComUserId());
			
		}
		
		model.addAttribute("orderReq", planOrderEntity);
		
	 	return "pages/order/selCustType";
	 }
	 
	 
	   /**
	  * 2025-02-20 조일근
	  * 02) 가입방식 선택
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/selEntrType")
	 public String  selEntrType(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/selEntrType";
	 }
	 
	 
	 
	 
	 
	   /**
	  * 2025-02-20 조일근
	  * 03) 약관 가입
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/agreePolicy")
	 public String  agreePolicy(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/agreePolicy";
	 }
	 
	 
	 
	   /**
	  * 2025-02-20 조일근
	  * 04) 가입자 정보 입력
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/entrCustInfo")
	 public String  entrCustInfo(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/entrCustInfo";
	 }
	 
	 
	 
	   /**
	  * 2025-02-20 조일근
	  * 04) 신규 가입자 희망번호 선택
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/hopeNum")
	 public String  hopeNum(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/hopeNum";
	 }
	 
	 
	   /**
	  * 2025-02-20 조일근
	  * 05) 본인인증
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/selPersonCert")
	 public String  perconalCert(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/selPersonCert";
	 }
	 
	 
	   /**
	  * 2025-02-20 조일근
	  * 07-1) 번호이동 현재 사용번호 정보
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/reInputCustInfo")
	 public String  reInputCustInfo(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/reInputCustInfo";
	 }
	 
	 //==================================================== 카카오 인증 ===================================
	 
	   /**
	  * 2025-02-20 조일근
	  * 06) 카카오 인증 
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/kakaoCertConfirm")
	 public String  kakaoCert(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/kakaoCertConfirm";
	 }
	 
	 
	 //==================================================== 카카오 인증 ===================================
	 
	 
	 
	 
	 //==================================================== 번호이동 ===================================
	 
	   /**
	  * 2025-02-20 조일근
	  * 07-1) 번호이동 현재 사용번호 정보
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/prePhoneInfo")
	 public String  prePhoneInfo(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/prePhoneInfo";
	 }
	 
	 //==================================================== 번호이동 ===================================
	 
	 
	   /**
	  * 2025-02-20 조일근
	  * 07-1) 유심 가입 선택
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/selUsimEntr")
	 public String  selUsimEntr(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/selUsimEntr";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 07-2) 유심 있을 경우 유심번호 입력
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/usimNum")
	 public String  usimNum(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/usimNum";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 08) 유심 없을 경우 유심 배송지 입력
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/usimDelivery")
	 public String  usimDelivery(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/usimDelivery";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 08) 유심 종류(일반/NFC)
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/selUsimType")
	 public String  selUsimType(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/selUsimType";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 09) esim 정보 입력
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/esimInfo")
	 public String  esimInfo(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/esimInfo";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 10) 고객 주소 입력
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/custAdress")
	 public String  custAdress(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/custAdress";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 11) 신분증 진위 여부
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/indentifyCert")
	 public String  indentifyCert(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/indentifyCert";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 11) 납부 방법 선택
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/paymentInfo")
	 public String  paymentInfo(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
	 	return "pages/order/paymentInfo";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 12) 청구서 수령 방식 선택
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/selBillMethod")
	 public String  selBillMethod(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
		 
		 
		 
	 	return "pages/order/selBillMethod";
	 }
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 12) 주문 요청 저장
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @PostMapping("/completReqProc")
	 public String  completReqProc(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
		HttpSession session = request.getSession();
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		
		try {
		 	int nextSeq = sequenceService.getNextSequence();
		 	String nextSeqStr = String.format("%05d", nextSeq);
		 	String orderSeq = serverSp + today + nextSeqStr;
		 	
			UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
		 	planOrderEntity.setEntrUserId((int) loginInfo.getId());
		 	planOrderEntity.setCreateId(planOrderEntity.getEntrUserId());
		 	planOrderEntity.setIdentityCi((String)session.getAttribute("CUST_CERT_CI"));
		 	planOrderEntity.setOrderDttm(CommonUtil.getDatetime("yyyyMMddHHmmss"));
		 	planOrderEntity.setOrderSeq(orderSeq);
		 	planOrderEntity.setSettleDiv("D");//후불
		 	planOrderEntity.setOrderState("00"); 
		 	planOrderEntity.setOrderStateDttm(CommonUtil.getDatetime("yyyyMMddHHmmss"));
		 	
			 long res = service.create(planOrderEntity);
			 
			 if (res > 0) {
				session.removeAttribute("txId");
		   		session.removeAttribute("CUST_CERT_SP");
				session.removeAttribute("CUST_CERT_CI");
				session.removeAttribute("CUST_CERT_DI");
				session.removeAttribute("CUST_CERT_NAME");
				session.removeAttribute("CUST_CERT_BIRTHDAY");
		   		
				
				
				Integer orderId = planOrderEntity.getId();
				if(planOrderEntity.getCompanyId() == 19) {
					// 고고 모바일
					orderServiceGOGO.sendOrder(planOrderEntity.getId());
				} else if(planOrderEntity.getCompanyId() == 22) {
					orderServiceSHAKE.sendOrder(planOrderEntity.getId());
				}
				
				
				// 알림톡 전송
				CompanyListEntity info = companyListService.getCompany(planOrderEntity.getCompanyId());
				
				
				/* 개통신청완료 알림 템플릿 찾기 */
				CommCodeSearchDto param = new CommCodeSearchDto();
				param.setCodeGroup("biz_template");
				param.setCode("easy_signup_apply");
				
				/* 받는사람 세팅 */
				Map<String, String> variable = new HashMap<String, String>();
				variable.put("userName", planOrderEntity.getOrderNm());
				variable.put("companyName", info.getName());
				variable.put("planNm", planOrderEntity.getPlanNm());
				String entrTypeNm = "01".equals(planOrderEntity.getEntrType()) ? "신규가입" : "번호이동" ;
				variable.put("entrType", entrTypeNm);
				variable.put("to", planOrderEntity.getOrderPhone());
				
				
				/* 메세지 발송 */
				String result = kakaoBizTalkUtils.sendBizMessage(param, variable);
				
				
				
		        // 저장 성공 시 "completReq"로 리다이렉트
		        return "redirect:/order/completReq?orderId="+orderId;
		    } else {
		        // 저장 실패 시, 에러 메시지를 모델에 추가하고 저장 페이지로 이동
		        model.addAttribute("errorMessage", "주문 요청 저장에 실패했습니다.");
		        model.addAttribute("orderReq", planOrderEntity);
		        return "pages/order/selBillMethod";
		    }			
			
		} catch (Exception e) {
			 model.addAttribute("errorMessage", "주문 요청 저장에 실패했습니다." + e.getMessage());
	        model.addAttribute("orderReq", planOrderEntity);
	        return "pages/order/selBillMethod";
		}
		
		
	 
	 }
	 
	 
	 
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 12) 주문 요청 완료
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @LoginRequired
	 @GetMapping("/completReq")
	 public String  completReq(  HttpServletRequest request , HttpServletResponse response,Model model )  {
	 	
		 String orderId = request.getParameter("orderId");
		 PlanOrderEntity planOrderEntity = new PlanOrderEntity();
		 planOrderEntity = service.getDetail(Integer.parseInt(orderId));
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 
		 Integer reqPlanid = planOrderEntity.getPlanId();
		 
		 PlanEntity planRecord = new PlanEntity();
		 planRecord = planService.getDetail(reqPlanid);
		 
		 model.addAttribute("planInfo", planRecord);
		
			
			
	 	return "pages/order/completReq";
	 }
	 
	 
	 
	 /**
	  * 2025-02-20 조일근
	  * 12) 주문 요청 완료
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @GetMapping("/apiTest")
	 public String  apiTest(  HttpServletRequest request , HttpServletResponse response,Model model )  {
	 	
		 
	 	return "pages/order/apiTest";
	 }
	 
	 
	 //===================================================== REST API 호출 ajax====================================================================================
	 
	 

		
	  /**
    * 2024-12-19 (목) 백신의
    * - 쿠콘 카카오 본인인증 인증 키
    *
    * @param searchRequest               서치 params
    * @param List<CompanyListEntity> 사업자 조회 리스트
    */
	@ResponseBody
   @PostMapping("/gettoken")
   public ResponseEntity<CustomApiResponse<Map<String,Object>>> gettoken( HttpServletRequest request , HttpServletResponse response,
   		@RequestBody  custCertDto searchRequest
   		) throws IOException {
   	
		Map<String,Object> apiresult = new HashMap<String,Object>();
		Map<String,Object> result = new HashMap<String,Object>();
		String resMsg = "";
   	try {
   		
   		HttpSession session = request.getSession();
   		session.removeAttribute("txId");
   		session.removeAttribute("CUST_CERT_SP");
		session.removeAttribute("CUST_CERT_CI");
		session.removeAttribute("CUST_CERT_DI");
		session.removeAttribute("CUST_CERT_NAME");
		session.removeAttribute("CUST_CERT_BIRTHDAY");
   		
   		
   		RestApiTokenMngEntity dbToken = rest.getToken("KAKAO_CERT_TOKEN");
   		if(dbToken !=null) {
   			if(StringUtils.isNoneBlank(dbToken.getTokenVal())) {
   				result.put("resCode", "200");
   		   		result.put("resMsg", "sucess");
   		   		return CustomApiResponse.success(ResponseCode.OK, result);
   			}
   		}
   		
   		
   		String apiUrl = pcertBaseUrl + ApiConstant.COOCON_API_GET_AUTH_TOKEN.URL;
   		apiUrl = apiUrl + "?grant_type=client_credentials&scope=apis&client_id="+pcertClientId+"&client_secret="+pcertClientKey;
   		
   		
   		String method = ApiConstant.COOCON_API_GET_AUTH_TOKEN.METHOD;
   		
   		apiresult = rest.sendRestApi("GET_KAKAO_CERT_TOKEN",apiUrl, method, null, null);
   		String resStatusCode = apiresult.get("resultStatus") !=null ? (String) apiresult.get("resultStatus") : "999";
   		String resultMsg = apiresult.get("resultMsg") !=null ? (String) apiresult.get("resultMsg") : "999";
   		if("200 OK".equals(resStatusCode)) {
   			Map<String, Object> resbody =  new HashMap<String, Object>();
				if(apiresult.get("resultBody") != null) {
					resbody = (Map<String, Object>) apiresult.get("resultBody");
					String tkval = (String) resbody.get("access_token");
					
					rest.deleteToken("KAKAO_CERT_TOKEN");
					RestApiTokenMngEntity resttk = new RestApiTokenMngEntity();
					resttk.setTokenCode("KAKAO_CERT_TOKEN");
					resttk.setTokenName("카카오 본인인증 토큰");
					resttk.setTokenVal(tkval);
					resttk.setExpiredDttm(LocalDateTime.now().plusSeconds(7689600));
					rest.createToken(resttk);
					
				}
				resMsg = "sucess";
   		} else {
   			resMsg = resultMsg;
   		}
   		
   		result.put("resCode", resStatusCode);
   		result.put("resMsg", resMsg);
   		return CustomApiResponse.success(ResponseCode.OK, result);
   	} catch (Exception e) {
   		e.printStackTrace();
   		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
   }		
	
	
	
	
	  /**
	     * 2024-12-19 
	     * - 쿠콘 카카오 본인인증 요청
	     *
	     * @param searchRequest               서치 params
	     * @param List<CompanyListEntity> 사업자 조회 리스트
	     */
		@ResponseBody
	    @PostMapping("/kakaoCert")
	    public ResponseEntity<CustomApiResponse<Map<String,Object>>> kakaoCert( HttpServletRequest request , HttpServletResponse response,
	    		@RequestBody  custCertDto searchRequest
	    		) throws IOException {
	    	
			Map<String,Object> result = new HashMap<String,Object>();
	    	
	    	try {
	    		
	    		RestApiTokenMngEntity dbToken = rest.getToken("KAKAO_CERT_TOKEN");
	    		String tokenVal = "";
	    		if(dbToken ==null) {
	    			result.put("resCode", "999");
	   		   		result.put("resMsg", "tokenKey Not Found");
	    			return CustomApiResponse.success(ResponseCode.OK, result);
	    		} else {
	    			tokenVal = dbToken.getTokenVal();
	    		}
	    		
	    		Map<String,String> headermap = new HashMap<String,String>();
	    		headermap.put("Authorization", "Bearer " + tokenVal);
	    		
	    		String apiUrl = pcertBaseUrl + ApiConstant.COOCON_API_AUTH_KAKAO_K1110.URL;
	    		String method = ApiConstant.COOCON_API_AUTH_KAKAO_K1110.METHOD;
	    		
	    		
	    		Map<String,Object> body = new HashMap<String,Object>();
	    		Map<String,Object> delegateInfo = new HashMap<String,Object>();
	    		String custHid = searchRequest.getCusthid();
	    		String custBirth = CommonUtil.getBirthDateByHid(custHid);
	    		String tranId = CommonUtil.getDatetime("yyyyMMddHHmmssSSS");
	    		String phoneNo = searchRequest.getCustphoneNum();
	    		String name = searchRequest.getCustnm();
	    		
	    		delegateInfo.put("requestType", "전자서명");
	    		delegateInfo.put("requestOrganization", "세모통");
	    		delegateInfo.put("receiverName", KakaoEncrypt.encryptAESCTRWithCustomInfo(name, pcertENCKey, pcertENCIv));
	    		
	    		body.put("isCd",pcertClientId);
	    		body.put("tranId",tranId);
	    		body.put("type","PERSONAL_INFO");
	    		body.put("phoneNo",KakaoEncrypt.encryptAESCTRWithCustomInfo(phoneNo, pcertENCKey, pcertENCIv) );
	    		body.put("name",KakaoEncrypt.encryptAESCTRWithCustomInfo(name, pcertENCKey, pcertENCIv));
	    		body.put("birthday",KakaoEncrypt.encryptAESCTRWithCustomInfo(custBirth, pcertENCKey, pcertENCIv));
	    		body.put("ci","");
	    		body.put("expiresIn",300);
	    		body.put("ci","");
	    		body.put("returnUrl",pcertRetUrl);
	    		body.put("data",CommonUtil.generateSecureRandomString(35));
	    		body.put("delegateInfo",delegateInfo);	    		
	    		body.put("extraMessage","카카오 본인인증을 완료해 주세요.");
	    		
	    		List<String> identifyItems = Arrays.asList("NAME", "BIRTHDAY", "PHONE_NUMBER");
	    		body.put("identifyItems", identifyItems);
	    		
	    		result = rest.sendRestApi("REQ_KAKAO_CERT",apiUrl, method, body, headermap);
	    		String resStatusCode = result.get("resultStatus") !=null ? (String) result.get("resultStatus") : "999";
	    		
	    		if("200 OK".equals(resStatusCode)) {
	    			Map<String, Object> resbody =  new HashMap<String, Object>();
					if(result.get("resultBody") != null) {
						resbody = (Map<String, Object>) result.get("resultBody");
						String txId = resbody.get("txId") == null ? "" : (String) resbody.get("txId");
						if(StringUtils.isNotBlank(txId)) {
							result.put("resultStatus", "200");
						} else {
							result.put("resultStatus", "999");
							return CustomApiResponse.success(ResponseCode.OK, result);
						}
						
					}
	    		}
	    		
	    		
	    		return CustomApiResponse.success(ResponseCode.OK, result);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
			}
	    }
	 


		

		  /**
	     * 2024-12-19 
	     * - 쿠콘 카카오 본인인증 상태 조회
	     *
	     * @param searchRequest               서치 params
	     * @param List<CompanyListEntity> 사업자 조회 리스트
	     */
		@ResponseBody
	    @PostMapping("/kakaoCertStatus")
	    public ResponseEntity<CustomApiResponse<Map<String,Object>>> kakaoCertStatus( HttpServletRequest request , HttpServletResponse response,
	    		@RequestBody  custCertDto searchRequest
	    		) throws IOException {
	    	
			Map<String,Object> result = new HashMap<String,Object>();
	    	
	    	try {
	    		
	    		RestApiTokenMngEntity dbToken = rest.getToken("KAKAO_CERT_TOKEN");
	    		String tokenVal = "";
	    		if(dbToken ==null) {
	    			result.put("resCode", "999");
	   		   		result.put("resMsg", "tokenKey Not Found");
	    			return CustomApiResponse.success(ResponseCode.OK, result);
	    		} else {
	    			tokenVal = dbToken.getTokenVal();
	    		}
	    		
	    		Map<String,String> headermap = new HashMap<String,String>();
	    		headermap.put("Authorization", "Bearer " + tokenVal);
	    		
	    		
	    		String apiUrl = pcertBaseUrl + ApiConstant.COOCON_API_AUTH_KAKAO_STATE.URL;
	    		String method = ApiConstant.COOCON_API_AUTH_KAKAO_STATE.METHOD;
	    		
	    		
	    		Map<String,Object> body = new HashMap<String,Object>();
	    		String tranId = CommonUtil.getDatetime("yyyyMMddHHmmssSSS");
	    		String txId = searchRequest.getTxId();
	    		
	    		body.put("isCd",pcertClientId);
	    		body.put("tranId",tranId);
	    		body.put("txId",txId);

	    		
	    		result = rest.sendRestApi("REQ_KAKAO_CERT_STATUS",apiUrl, method, body, headermap);
	    		String resStatusCode = result.get("resultStatus") !=null ? (String) result.get("resultStatus") : "999";
	    		
	    		if("200 OK".equals(resStatusCode)) {
	    			Map<String, Object> resbody =  new HashMap<String, Object>();
					if(result.get("resultBody") != null) {
						resbody = (Map<String, Object>) result.get("resultBody");
						txId = resbody.get("txId") == null ? "" : (String) resbody.get("txId");
						String status = resbody.get("status") == null ? "" : (String) resbody.get("status");
						if(StringUtils.isNotBlank(txId)) {
							result.put("resultStatus", "200");
						} else {
							result.put("resultStatus", "999");
							return CustomApiResponse.success(ResponseCode.OK, result);
						}
						
					}
	    		}
	    		
	    		
	    		return CustomApiResponse.success(ResponseCode.OK, result);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
			}
	    }
		
		
		
		
		  /**
	     * 2024-12-19 
	     * - 쿠콘 카카오 본인인증 검증
	     *
	     * @param searchRequest               서치 params
	     * @param List<CompanyListEntity> 사업자 조회 리스트
	     */
		@ResponseBody
	    @PostMapping("/kakaoCertVerify")
	    public ResponseEntity<CustomApiResponse<Map<String,Object>>> kakaoCertVerify( HttpServletRequest request , HttpServletResponse response,
	    		@RequestBody  custCertDto searchRequest
	    		) throws IOException {
	    	
			Map<String,Object> resultApi = new HashMap<String,Object>();
			Map<String,Object> result = new HashMap<String,Object>();
	    	
	    	try {
	    		
	    		HttpSession session = request.getSession();
	    		
	    		RestApiTokenMngEntity dbToken = rest.getToken("KAKAO_CERT_TOKEN");
	    		String tokenVal = "";
	    		if(dbToken ==null) {
	    			result.put("resCode", "999");
	   		   		result.put("resMsg", "tokenKey Not Found");
	    			return CustomApiResponse.success(ResponseCode.OK, result);
	    		} else {
	    			tokenVal = dbToken.getTokenVal();
	    		}
	    		
	    		Map<String,String> headermap = new HashMap<String,String>();
	    		headermap.put("Authorization", "Bearer " + tokenVal);
	    		
	    		
	    		String apiUrl = pcertBaseUrl + ApiConstant.COOCON_API_AUTH_KAKAO_VERIFY.URL;
	    		String method = ApiConstant.COOCON_API_AUTH_KAKAO_VERIFY.METHOD;
	    		
	    		
	    		Map<String,Object> body = new HashMap<String,Object>();
	    		String tranId = CommonUtil.getDatetime("yyyyMMddHHmmssSSS");
	    		String txId = searchRequest.getTxId();
	    		
	    		body.put("isCd",pcertClientId);
	    		body.put("tranId",tranId);
	    		body.put("txId",txId);

	    		
	    		resultApi = rest.sendRestApi("REQ_KAKAO_CERT_VERIFY",apiUrl, method, body, headermap);
	    		String resStatusCode = resultApi.get("resultStatus") !=null ? (String) resultApi.get("resultStatus") : "999";
	    		
	    		if("200 OK".equals(resStatusCode)) {
	    			Map<String, Object> resbody =  new HashMap<String, Object>();
					if(resultApi.get("resultBody") != null) {
						resbody = (Map<String, Object>) resultApi.get("resultBody");
						String apiresult = resbody.get("result") == null ? "" : (String) resbody.get("result");
						String userCi = resbody.get("ci") == null ? "" : (String) resbody.get("ci");
						
						userCi = KakaoEncrypt.decryptAESCTRWithCustomInfo(userCi, pcertENCKey, pcertENCIv);


						if(StringUtils.isNotBlank(userCi) && "Y".equals(apiresult)) {							
							session.setAttribute("CUST_CERT_SP", "KAKAO");
							session.setAttribute("CUST_CERT_CI", userCi);
							
							result.put("resultStatus", "200");
						} else {
							result.put("resultStatus", "999");
							return CustomApiResponse.success(ResponseCode.OK, result);
						}
						
					}
	    		}
	    		
	    		
	    		return CustomApiResponse.success(ResponseCode.OK, result);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
			}
	    }
		 
			
		
		  /**
	     * 2024-12-19 
	     * - 신분증 진위여부
	     *
	     * @param searchRequest               서치 params
	     * @param List<CompanyListEntity> 사업자 조회 리스트
	     */
		@ResponseBody
	    @PostMapping("/identifyCert")
	    public ResponseEntity<CustomApiResponse<Map<String,Object>>> identifyCert( HttpServletRequest request , HttpServletResponse response,
	    		@RequestBody  custCertDto searchRequest
	    		) throws IOException {
	    	
			Map<String,Object> resultApi = new HashMap<String,Object>();
			Map<String,Object> result = new HashMap<String,Object>();
	    	
	    	try {
	    		
	    		HttpSession session = request.getSession();
	    		
	    		String apiUrl = identBaseUrl + reqUrlCert;
	    		String method = "POST";
	    		
	    		//REQ_DATA={"API_KEY": "TEST","API_ID": "1320","BANKCD": "111","NAME": "홍길동","REGNO": "19950101","DRIVE_NO": "서울 9922283XXX","SECURE_NO": "","ISSUE_DATE": ""}
	    		
	    		Map<String,Object> body = new HashMap<String,Object>();
	    		String idSp = searchRequest.getIdentifySp();
	    		String REGNO = "";
	    		String DRIVE_NO = "";
	    		String SECURE_NO = "";
	    		String ISSUE_DATE = searchRequest.getIssudt();
	    		if("104".equals(idSp)) {
	    			REGNO = searchRequest.getCusthid();
	    		} else {
	    			REGNO = searchRequest.getCusthid().substring(0, 8);
	    			DRIVE_NO = searchRequest.getLicenseNum();
	    			SECURE_NO = searchRequest.getSecureNum();
	    		}
	    		
	    		
	    		body.put("API_KEY",identClientKey);
	    		body.put("API_ID","1320");
	    		body.put("BANKCD",searchRequest.getIdentifySp());
	    		body.put("NAME",searchRequest.getCustnm());
	    		body.put("REGNO",REGNO);
	    		body.put("DRIVE_NO",DRIVE_NO);
	    		body.put("SECURE_NO",SECURE_NO);
	    		body.put("ISSUE_DATE",ISSUE_DATE);

	    		
	    		resultApi = rest.sendRestApi("REQ_IDENTIFY_CERT",apiUrl, method, body, null);
	    		String resStatusCode = resultApi.get("resultStatus") !=null ? (String) resultApi.get("resultStatus") : "999";
	    		
	    		if("200 OK".equals(resStatusCode)) {
	    			Map<String, Object> resbody =  new HashMap<String, Object>();
					if(resultApi.get("resultBody") != null) {
						resbody = (Map<String, Object>) resultApi.get("resultBody");
						
						String RESULT_CD = resbody.get("RESULT_CD") == null ? "" : (String) resbody.get("RESULT_CD");
						String AGREEMENT = resbody.get("AGREEMENT") == null ? "" : (String) resbody.get("AGREEMENT");
						String DISAGREEMENT_REASON = resbody.get("DISAGREEMENT_REASON") == null ? "" : (String) resbody.get("DISAGREEMENT_REASON");

						if(StringUtils.isNotBlank(RESULT_CD) && "00000000".equals(RESULT_CD)) {							
							
							result.put("AGREEMENT", AGREEMENT);
							result.put("DISAGREEMENT_REASON", DISAGREEMENT_REASON);
							result.put("resultStatus", "200");
						} else {
							result.put("resultStatus", "999");
							return CustomApiResponse.success(ResponseCode.OK, result);
						}
						
					}
	    		}
	    		
	    		
	    		return CustomApiResponse.success(ResponseCode.OK, result);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
			}
	    }
		
		
		  /**
	     * 2024-12-19 
	     * - 계좌 진위 여부
	     *
	     * @param searchRequest               서치 params
	     * @param List<CompanyListEntity> 사업자 조회 리스트
	     */
		@ResponseBody
	    @PostMapping("/bankNumCert")
	    public ResponseEntity<CustomApiResponse<Map<String,Object>>> bankNumCert( HttpServletRequest request , HttpServletResponse response,
	    		@RequestBody  custCertDto searchRequest
	    		) throws IOException {
	    	
			Map<String,Object> resultApi = new HashMap<String,Object>();
			Map<String,Object> result = new HashMap<String,Object>();
	    	
	    	try {
	    		
	    		HttpSession session = request.getSession();
	    		
	    		String apiUrl = accBaseUrl + reqUrlAcc;
	    		String method = "POST";
	    		
	    		Map<String,Object> body = new HashMap<String,Object>();
	    		List<Map<String,Object>> reqDataList = new ArrayList<Map<String,Object>>();
	    		Map<String,Object> reqData = new HashMap<String,Object>();
	    		
	    		CommCodeSearchDto codeParam = new CommCodeSearchDto();
	    		codeParam.setCodeGroup("bank_co_cd_coocon");
	    		codeParam.setUserYn("Y");
	    		codeParam.setEtc1(searchRequest.getBankCd());	    		
	    		CodeEntity codeinfo = codeService.getCode(codeParam);
	    		
	    		
	    		int nextSeq = sequenceService.getNextSequence();
	    		String TRSC_SEQ_NO = String.format("%06d", nextSeq);
	    		if("TB".equals(serverSp)) {
	    			TRSC_SEQ_NO = "2" + TRSC_SEQ_NO;
	    		} else   {
	    			TRSC_SEQ_NO = "3" + TRSC_SEQ_NO;
	    		}
	    		
	    		reqData.put("BANK_CD", codeinfo.getCode()); // 은행 코드
	    		reqData.put("SEARCH_ACCT_NO", searchRequest.getBankNum());  // 계좌번호
	    		reqData.put("ACNM_NO", "");  // 빈값 전송 (사업자번호 또는 생년월일-예금주 실명 조회 시만 사용)
	    		reqData.put("ICHE_AMT", ""); // 이체금액 (빈값 전송)
	    		reqData.put("TRSC_SEQ_NO", TRSC_SEQ_NO); // 거래일련번호 중계 사용시 맨 앞자리 1 자리는 "0"으로 고정하여 세팅하여 주시고, 나머지 6 자리를 사용하시면 됩니다.
	    		reqDataList.add(reqData);
	    		
	    		body.put("SECR_KEY",accClientKey);
	    		body.put("KEY","ACCTNM_RCMS_WAPI");
	    		body.put("CHAR_SET","UTF-8");
	    		body.put("REQ_DATA",reqDataList);
	    		
	    		resultApi = rest.sendRestApi("REQ_ACCOUNT_CERT",apiUrl, method, body, null);
	    		String resStatusCode = resultApi.get("resultStatus") !=null ? (String) resultApi.get("resultStatus") : "999";
	    		
	    		if("200 OK".equals(resStatusCode)) {
	    			Map<String, Object> resbody =  new HashMap<String, Object>();
					if(resultApi.get("resultBody") != null) {
						resbody = (Map<String, Object>) resultApi.get("resultBody");
						result.put("resbody", resbody);
					}
	    		}
	    		
	    		
	    		return CustomApiResponse.success(ResponseCode.OK, result);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
			}
	    }		
		
		
		
		/**
		 * MyPage 에서 보는 [간편개통 요금제] 목록
		 * @param request
		 * @param param
		 * @return
		 * @throws IOException
		 */
		@ResponseBody
		@PostMapping(value = "/getAppliedPlanList")
		public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getAppliedPlanList( HttpServletRequest request, @RequestBody SearchPlanDto param) throws IOException {
			
			HttpSession 		session 	= request.getSession(false);
			UserDTO	loginInfo 	= (UserDTO)session.getAttribute("userInfo");
			
			try{
				
				List<PlanEntity> resultList = new ArrayList<PlanEntity>();
				
				// [신청 요금제] count 용.
				PlanOrderSearchDto searchRequest = new PlanOrderSearchDto();
				searchRequest.setOrderUserId((int)loginInfo.getId());
				searchRequest.setSearchOrderState(param.getSearchOrderState());
				
				List<String>	prodList = new ArrayList<String>();
				long 			resulCnt = service.getListCount(searchRequest);
				if(resulCnt != 0) {
					List<PlanOrderEntity> reqlist = service.getList(searchRequest);
					for(PlanOrderEntity itm : reqlist) {
						
						PlanEntity prod = planService.getDetail(itm.getPlanId());
						prod.setOrderState(itm.getOrderState());
						prod.setOrderStateNm(itm.getOrderStateNm());
						prod.setOrderStateDttm(itm.getOrderStateDttm());
						prod.setOpenCompDttm(itm.getOpenCompDttm());
						resultList.add(prod);
						
					}
				}
				
				return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);
			} catch (Exception e) {
				e.printStackTrace();
				return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
			}
		}		
		
		 
}
