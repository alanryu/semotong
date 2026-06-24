package kr.co.ucomp.web.mypage.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.auth.oauth.service.OAuthService;
import kr.co.ucomp.common.config.LoginRequired;
import kr.co.ucomp.common.encrypt.DaouEncrypt;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.restapi.entity.RestApiTokenMngEntity;
import kr.co.ucomp.common.restapi.mapper.RestApiMapper;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.mypage.constant.DaouApiConstant;
import kr.co.ucomp.web.mypage.dto.PointDTO;
import kr.co.ucomp.web.mypage.dto.PointHistoryDTO;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.mypage.entity.PointCashEntity;
import kr.co.ucomp.web.mypage.entity.PointEntity;
import kr.co.ucomp.web.mypage.entity.PointHistoryDetailEntity;
import kr.co.ucomp.web.mypage.entity.PointHistoryEntity;
import kr.co.ucomp.web.mypage.entity.PointNpayEntity;
import kr.co.ucomp.web.mypage.service.PointCashService;
import kr.co.ucomp.web.mypage.service.PointHistoryService;
import kr.co.ucomp.web.mypage.service.PointNpayService;
import kr.co.ucomp.web.mypage.service.PointService;
import kr.co.ucomp.web.mypage.service.UserService;
import kr.co.ucomp.web.order.service.DailySequenceService;
/**
 *
 * @author 이정민
 * @since 2024.12.11
 * @version v1.0
 */
@Controller
@RequestMapping("/scash")
public class ScashController {
	
	@Autowired private UserService userService;
	@Autowired private OAuthService oAuthService;
	@Autowired private RestApiMapper restApiMapper;
	
	@Value("${naver.auth.uri}"				) String naverAuthUri;
	@Value("${naver.client.id}"				) String naverClientId;
	@Value("${naver.client.secret}"			) String naverClientSecret;
	@Value("${naver.client.redirect}"		) String naverRedirectUri;
	
	@Value("${daou.url}"					) String daouUrl;
	@Value("${daou.partner-code}"			) String daouPartnerCode;
	@Value("${daou.key.api-key}"			) String daouApiKey;
	@Value("${daou.key.enc-key}"			) String daouEncKey;
	@Value("${daou.key.iv-key}"				) String daouIvKey;
	
	@Autowired private RestTempletUtil rest;
	
	@Autowired CommCodeMngService 		codeService;
	@Autowired DailySequenceService 	sequenceService;
	@Autowired PointService				pointService;
	@Autowired PointHistoryService		pointHistoryService;
	@Autowired PointNpayService			pointNpayService;
	@Autowired PointCashService			pointCashService;
	
	
		
	/**
	 * S캐쉬 이동
	 */
	@LoginRequired
	@GetMapping("/myscash")
	public String  nrearview( HttpServletRequest request, Model model)  {
		return "pages/mypage/myscash";
	}
	
	@ResponseBody
	@PostMapping(value="/getMyPoint")
	public ResponseEntity<CustomApiResponse<PointEntity>> getMyPoint( HttpServletRequest request, HttpSession session, @RequestBody Map<String, Object> param) throws Exception {
		try {
			UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
			// [Point]
			//loginInfo.getId()
			PointDTO pointParam = new PointDTO();
			pointParam.setSearchUserId(loginInfo.getId());
			PointEntity pointEnt = pointService.getMyPoint(pointParam);
			long resulCnt = 0;
			if(pointEnt != null) resulCnt = 1;
			// --------------
			session.setAttribute("pointId", pointEnt.getId());
			session.setAttribute("balance", pointEnt.getBalance());
			// --------------
			return CustomApiResponse.success(ResponseCode.OK, resulCnt, pointEnt);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Naver login : id값 session 에 저장
	 * @return
	 * @throws URISyntaxException
	 */
	@GetMapping("/naverlogin")
	public ResponseEntity<Object> naverlogin() throws URISyntaxException {
		
		String naverLoginUrl = naverAuthUri + "?" + "response_type=code" + "&client_id=" + naverClientId + "&redirect_uri=" + naverRedirectUri;
		
		// - /naver/callback") 에서 
		// - return "redirect:/users/mycash";		//model.addAttribute("callback"	, "Y");
		
		URI redirectUri = new URI(naverLoginUrl);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(redirectUri);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}
	
	/**
	 * 적립, 전환
	 * 1. /v1/npay/members/nid
	 * 2. /v1/npay/point 
	 * Naver -> 세모통 발급한 Client ID, Client Secret, 세모통이 조회한 회원 유니크 ID
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value="/pointNpay")
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> pointNpayApi( HttpServletRequest request, HttpSession session, @RequestBody Map<String, Object> param) throws Exception {
		
		String				DAOU_API			= "DAOU_MEMBERS_NID";
		String				DAOU_API_NAME		= "다우 네이버페이 회원 정보 조회";
		Map<String,Object> 	result 				= new HashMap<String,Object>();
		
		String		spp		= (String) param.get("point");
		int			pp 		= Integer.parseInt(spp);
		int			bal		= (int) session.getAttribute("balance");
		
		if(pp > bal) {
			result.put("resultStatus"		, "998"							);
			result.put("resultMessage"		, "S캐쉬가 모자랍니다."		);
			return CustomApiResponse.success(ResponseCode.OK, result);
		}
		
		try {
			
			
			
//			// ----------------------------------------------------------------------------------- test   
//			// DATA 처리 --------------------------------------------------------------------------------------------------------------------------  //
//			
//			// 1. Npay 나중에 트리거 작동 고려 - 어차피 History는 update만 하니, 이거 먼저 하고 id 받아서 history의 npay_id 업뎃 해준다. 
//			PointNpayEntity insEnt = new PointNpayEntity();
//			insEnt.setPointId(			(int) session.getAttribute("pointId")	);
//			insEnt.setNpayApiType(		"point"									);
//			insEnt.setReqAmount(		pp										);
//			insEnt.setUserKey(			(String) session.getAttribute("naverId"));
//			insEnt.setPartnerTxNo(		"250305093143PAKED7sancho"								);
//			insEnt.setTxNo(				"250305093143PAKED7-S1a8R000366"									);		//{"txNo":"250305093143PAKED7-S1a8R000366","point":5000}
//			insEnt.setMemo(				"History Detail test"										);		//괜히 만든듯....
//			//insEnt.setDaouCode(dcode);
//			//insEnt.setDaouMessage(dmessage);
//			
//			UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
//			insEnt.setCreateId(			(int) loginInfo.getId()					);
//			int npayid = pointNpayService.insertNpay(insEnt);
//			
//			// 2.History data 처리, 여기서 DR/CR 등 update 
//			calcHistory("N", session, pp, npayid, 0);
//			
//			result.put("resultStatus"		, "200"			);
//			result.put("resultMessage"		, "success"		);
//			
//			return CustomApiResponse.success(ResponseCode.OK, result);
//			// DATA 처리 --------------------------------------------------------------------------------------------------------------------------  //
//			// ----------------------------------------------------------------------------------- test
			
			String resStatusCode 		= "";	//세모통 res code
			String restMsg 				= "";	//세모통 res msg
			
			String token			= daouAccessToken();
			if(token == null || token.equals("")) {
				result.put("resultStatus"		, "997"			);
				result.put("resultMessage"		, "토큰정보를 얻어 오지 못했습니다."		);
			}else {
				
				// OPEN API 호출 URL 정보 설정
				String apiUrl 			= daouUrl + DaouApiConstant.DAOU_MEMBERS_NID.URL;
				String method 			= DaouApiConstant.DAOU_MEMBERS_NID.METHOD;
				
				//Header
				Map<String,String> headerMap = new HashMap<>();
				headerMap.put("Pointbox-Partner-Code"		, daouPartnerCode		);		//header에 파트너코드
				headerMap.put("Authorization"				, "Bearer " + token		);		//header에 token
				
				//body
				Map<String, Object> bodyMap = new HashMap<> ();
				bodyMap.put("uniqueId"			, DaouEncrypt.encrypt((String) session.getAttribute("naverId")) 	);
				bodyMap.put("clientId"			, DaouEncrypt.encrypt(naverClientId) 								);
				bodyMap.put("clientSecret"		, DaouEncrypt.encrypt(naverClientSecret) 							);
				
				// -- /v1/npay/members/nid
				Map<String, Object>  nidres = rest.sendRestApi(DAOU_API, apiUrl, method, bodyMap, headerMap);
				
				//{resultStatus=200 OK, resultBody={maskingId=ja******, point=2468, userKey=8000510401527398}, resultMsg=sucess}
				resStatusCode 		= nidres.get("resultStatus") 	!=null ? (String) nidres.get("resultStatus") 	: "999";	//세모통 res code
				restMsg 			= nidres.get("resultMsg") 		!=null ? (String) nidres.get("resultMsg") 		: "999";	//세모통 res msg
				
				Map<String, Object> resbody =  new HashMap<String, Object>();
				
				if("200 OK".equals(resStatusCode)) {
					if(nidres.get("resultBody") != null) {
						resbody = (Map<String, Object>) nidres.get("resultBody");
						//다우는 실패 시 code, message 를 준다.
						String dcode 		= resbody.get("code") 		== null ? "" : (String) resbody.get("code");
						String dmessage 	= resbody.get("message") 	== null ? "" : (String) resbody.get("message");
						
						if(StringUtils.isNotBlank(dcode)) {
							//통신성공, 에러코드 : 다우 code 가 있다. 뭔가 잘못된 메세지가 있다.
							result.put("resultStatus"		, dcode			);
							result.put("resultMessage"		, dmessage		);
							return CustomApiResponse.success(ResponseCode.OK, result);
						} else {
							//통신성공, 답신성공
							//result.put("resultStatus"		, "200"			);
							resStatusCode = "200";
							result.put("resultMessage"		, "success"		);
							result.put("resultBody"			, resbody		);
							//return CustomApiResponse.success(ResponseCode.OK, result);		// 한번 더 보내자 /v1/npay/point
						}
					}
				}else {
					
					String dcode 		= "";
					String dmessage 	= "";
					
					String resultMsg = (String) nidres.get("resultMsg");
					
					Pattern pattern = Pattern.compile("^(\\d+\\s*\\d*):\\s*\"(\\{.*})\"");
					Matcher matcher = pattern.matcher(resultMsg);
					
					if (matcher.find()) {
						String statusCode = matcher.group(1).trim();	// "400 400"
						String jsonPart = matcher.group(2);				// {"code":"41019","message":"월 적립 한도 초과"}
	
						System.out.println("Status Code: " + statusCode);
						System.out.println("JSON Part: " + jsonPart);
	
						try {
							// JSON을 Map<String, Object> 형태로 변환
							ObjectMapper objectMapper = new ObjectMapper();
							Map<String, Object> messageBody = objectMapper.readValue(jsonPart, Map.class);
	
							dcode 		= messageBody.get("code") 		== null ? "" : (String) messageBody.get("code");
							dmessage 	= messageBody.get("message") 	== null ? "" : (String) messageBody.get("message");
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						dcode = "998";
						dmessage = "사용자 정보를 확인 중 오류 입니다.";
					}
					
					result.put("resultStatus"		, dcode			);
					result.put("resultMessage"		, dmessage		);
					
				}
				
				// 실 전환 요청 다시 보내 
				if(resStatusCode.equals("200")) {
					DAOU_API			= "DAOU_POINT";
					DAOU_API_NAME		= "다우 네이버페이 포인트 적립";
					
					apiUrl 				= daouUrl + DaouApiConstant.DAOU_POINT.URL;
					method 				= DaouApiConstant.DAOU_POINT.METHOD;
					
					//Header 재사용.
					//headerMap
					
					//body 재사용.
					bodyMap.clear();
				 	
					String 	userKey				= (String) resbody.get("userKey");
					String 	partnerTxNo			= getPartnerTxNo();
					//String 	pp 					= (String) param.get("point");
	
					bodyMap.put("userKey"			, DaouEncrypt.encrypt(userKey) 										);	//userKey		text	O/O			네이버페이 유니크 아이디
					bodyMap.put("partnerTxNo"		, partnerTxNo														);	//partnerTxNo	text	O/X			제휴사 거래 번호*
					bodyMap.put("point"				, pp																);	//point			number	O/X			적립 포인트
					
					
					// -- /v1/npay/point
					Map<String, Object>  pointres = rest.sendRestApi(DAOU_API, apiUrl, method, bodyMap, headerMap);
					resStatusCode 		= pointres.get("resultStatus") 	!=null ? (String) pointres.get("resultStatus") 	: "999";	//세모통 res code
					restMsg 			= pointres.get("resultMsg") 	!=null ? (String) pointres.get("resultMsg") 	: "999";	//세모통 res msg
					
					if("200 OK".equals(resStatusCode)) {
						if(pointres.get("resultBody") != null) {
							
							
							//통신성공, 답신성공
							result.put("resultStatus"		, "200"			);
							result.put("resultMessage"		, bal - pp		);
							
							resbody = (Map<String, Object>) pointres.get("resultBody");
							String txNo = "";
							
							result.put("resultBody"			, resbody		);
							txNo = (String) resbody.get("txNo");
							
							// DATA 처리 --------------------------------------------------------------------------------------------------------------------------  //
							
							// 1. Npay 나중에 트리거 작동 고려 - 어차피 History는 update만 하니, 이거 먼저 하고 id 받아서 history의 npay_id 업뎃 해준다. 
							PointNpayEntity insEnt = new PointNpayEntity();
							insEnt.setPointId(			(int) session.getAttribute("pointId")	);
							insEnt.setNpayApiType(		"point"									);
							insEnt.setReqAmount(		pp										);
							insEnt.setUserKey(			(String) session.getAttribute("naverId"));
							insEnt.setPartnerTxNo(		partnerTxNo								);
							insEnt.setTxNo(				txNo									);		//{"txNo":"250305093143PAKED7-S1a8R000366","point":5000}
							insEnt.setMemo(				""										);		//괜히 만든듯....//관리자에서 쓰지 뭐... 어때
							//insEnt.setDaouCode(dcode);
							//insEnt.setDaouMessage(dmessage);
							
							UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
							insEnt.setCreateId(			(int) loginInfo.getId()					);
							int npayid = pointNpayService.insertNpay(insEnt);
							
							// 2.History data 처리, 여기서 DR/CR 등 update 
							calcHistory("N", session, pp, npayid, 0);
							
							// DATA 처리 --------------------------------------------------------------------------------------------------------------------------  //
							
							
							// [Point]
							//loginInfo.getId()
							PointDTO pointParam = new PointDTO();
							pointParam.setSearchUserId(loginInfo.getId());
							PointEntity pointEnt = pointService.getMyPoint(pointParam);
							long resulCnt = 0;
							if(pointEnt != null) resulCnt = 1;
							// --------------
							session.setAttribute("pointId", pointEnt.getId());
							session.setAttribute("balance", pointEnt.getBalance());
							// --------------
						}
					}else {
						
						String dcode 		= "";
						String dmessage 	= "";
						
						String resultMsg = (String) pointres.get("resultMsg");
						
						Pattern pattern = Pattern.compile("^(\\d+\\s*\\d*):\\s*\"(\\{.*})\"");
						Matcher matcher = pattern.matcher(resultMsg);
						
						if (matcher.find()) {
							String statusCode = matcher.group(1).trim();	// "400 400"
							String jsonPart = matcher.group(2);				// {"code":"41019","message":"월 적립 한도 초과"}
	
							System.out.println("Status Code: " + statusCode);
							System.out.println("JSON Part: " + jsonPart);
	
							try {
								// JSON을 Map<String, Object> 형태로 변환
								ObjectMapper objectMapper = new ObjectMapper();
								Map<String, Object> messageBody = objectMapper.readValue(jsonPart, Map.class);
	
								dcode 		= messageBody.get("code") 		== null ? "" : (String) messageBody.get("code");
								dmessage 	= messageBody.get("message") 	== null ? "" : (String) messageBody.get("message");
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							dcode = "998";
							dmessage = "포인트 적립 중 오류 입니다.";
						}
						
						result.put("resultStatus"		, dcode			);
						result.put("resultMessage"		, dmessage		);
						
					}
				}
				
				
			}
			
			
			return CustomApiResponse.success(ResponseCode.OK, result);
			
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing /pointNpay: " + e.getMessage());
		}
		
	}
	
	
	
	/**
	 * DAOU_AUTH_TOKEN 획득
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String daouAccessToken() {
		
		String				DAOU_AUTH_TOKEN		= "DAOU_AUTH_TOKEN";
		String				DAOU_TOKEN_NAME		= "다우 접근 토큰";
		String 				rtnStr 				= "";
		Map<String,Object> 	templeteResult 		= new HashMap<String,Object>();
		
		String 				apiUrl 		= daouUrl + DaouApiConstant.DAOU_AUTH_TOKEN.URL;
		String 				method 		= DaouApiConstant.DAOU_AUTH_TOKEN.METHOD;
		
		//Header
		Map<String,String> headerMap = new HashMap<>();
		headerMap.put("Pointbox-Partner-Code", daouPartnerCode);		//header에 파트너코드
		
		//body
		Map<String, Object> bodyMap = new HashMap<> ();
		bodyMap.put("apiKey", daouApiKey);
		
		// --- RestTemplete ---
		RestApiTokenMngEntity tknEnt = restApiMapper.getToken(DAOU_AUTH_TOKEN);					// === ID 조회
		// --- RestTemplete ---
		
		if(tknEnt != null) {
			rtnStr = tknEnt.getTokenVal();
//			System.out.println("$$$$$$$$$$$$$$$$$$$$$");
//			System.out.println("select token_val:" + rtnStr);
//			System.out.println("$$$$$$$$$$$$$$$$$$$$$");
		}
		
		try {
			if(tknEnt == null || rtnStr.equals("")) {
				// --- RestTemplete ---
				templeteResult			= rest.sendRestApi(DAOU_AUTH_TOKEN, apiUrl, method, bodyMap, headerMap);
				// --- RestTemplete ---
				String resStatusCode 	= templeteResult.get("resultStatus") 	!=null ? (String) templeteResult.get("resultStatus") 	: "999";
				String resultMsg 		= templeteResult.get("resultMsg") 		!=null ? (String) templeteResult.get("resultMsg") 		: "999";
				if("200 OK".equals(resStatusCode)) {
					Map<String, Object> resbody =  new HashMap<String, Object>();
					if(templeteResult.get("resultBody") != null) {
						resbody = (Map<String, Object>) templeteResult.get("resultBody");
						
						String tkval	= (String) resbody.get("accessToken");					//다우 API 사양서 /v1/auth/token 응답 파라미터
						String expval	= (String) resbody.get("expiresIn");
						
						DateTimeFormatter formatter	= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
						LocalDateTime expDateTime	= LocalDateTime.parse(expval, formatter);
						
						
						rest.deleteToken(DAOU_AUTH_TOKEN);										// === 기존 토큰 삭제
						
						RestApiTokenMngEntity resttk = new RestApiTokenMngEntity();
						resttk.setTokenCode(DAOU_AUTH_TOKEN);
						resttk.setTokenName(DAOU_TOKEN_NAME);
						resttk.setTokenVal(tkval);
						resttk.setExpiredDttm(expDateTime);		//20250226135119	//"20250227105112"
						rest.createToken(resttk);												// === 신규 토큰 생성
						
						rtnStr = tkval;
						
//						System.out.println("$$$$$$$$$$$$$$$$$$$$$ 2222222222222222222222");
//						System.out.println("new token_val:" + tkval);
//						System.out.println("$$$$$$$$$$$$$$$$$$$$$ 2222222222222222222222");
					}
				}
			}
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return rtnStr;
	}
	
	// 세모통 발번 거래번호, 다우 지정 양식
	public String getPartnerTxNo() {
		String rtn = "";
		String 		today 		= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
		int 		nextSeq 	= sequenceService.getNextSequence();
	 	String 		nextSeqStr 	= String.format("%07d", nextSeq);
	 	String 		orderSeq 	= today + daouPartnerCode + nextSeqStr;
	 	rtn = orderSeq;
	 	
		return rtn;
	}


	
	
	
	
	
	
	
	// ---------------------------------------------------------------------------------------------------------------------------------- Point To Cash ----- //
	
	/**
	 * Cash
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value="/pointCash")
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> pointCash( HttpServletRequest request, HttpSession session, @RequestBody PointCashEntity param) throws Exception {
		
		Map<String,Object> 	result 				= new HashMap<String,Object>();
		
		int			pp		= param.getAmount();
		int			bal		= (int) session.getAttribute("balance");
		
		if(pp > bal) {
			result.put("resultStatus"		, "998"							);
			result.put("resultMessage"		, "S캐쉬가 모자랍니다."			);
			return CustomApiResponse.success(ResponseCode.OK, result);
		}
		CommCodeSearchDto codeParam = new CommCodeSearchDto();
		codeParam.setCodeGroup("bank_co_cd_coocon");
		codeParam.setUserYn("Y");
		codeParam.setEtc1(param.getBankCode());				
		CodeEntity codeinfo = codeService.getCode(codeParam);
		
		param.setBankCode(	codeinfo.getCode()			);		// 은행 코드
		param.setBankName(	codeinfo.getCodeName()		);		// 은행명
		param.setStatus(	"REQ"						);
		//신청,처리완료,반려(REQ, COM, REJ:Submitted,Completed,Rejected)
		
		try {
			// DATA 처리 --------------------------------------------------------------------------------------------------------------------------  //
			
			// 1. Cash 저장 
			UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
			param.setPointId(			(int) session.getAttribute("pointId")	);
			param.setCreateId(			(int) loginInfo.getId()					);
			int cashid = pointCashService.insertCash(param);
			
			// 2.History data 처리, 여기서 DR/CR 등 update 
			calcHistory("C", session, pp, 0, cashid);
			
			// DATA 처리 --------------------------------------------------------------------------------------------------------------------------  //
			
			
			
			// [Point]
			//loginInfo.getId()
			PointDTO pointParam = new PointDTO();
			pointParam.setSearchUserId(loginInfo.getId());
			PointEntity pointEnt = pointService.getMyPoint(pointParam);
			long resulCnt = 0;
			if(pointEnt != null) resulCnt = 1;
			// --------------
			session.setAttribute("pointId", pointEnt.getId());
			session.setAttribute("balance", pointEnt.getBalance());
			// --------------
			
			
			result.put("resultStatus"		, "200"							);
			result.put("resultMessage"		, bal - pp						);
			
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing /pointNpay: " + e.getMessage());
		}
	}
	// ---------------------------------------------------------------------------------------------------------------------------------- Point To Cash ----- //
	
	
	
	/** *****************************************************************************************************************************
	 * History 정리
	 ***************************************************************************************************************************** */
	public void calcHistory(String gbn, HttpSession session, int pp, int npayId, int cashId) {
		
		// *** balance는 인입 시 이미 체크 했다. *** // 
		//1. 가용 포인트들 조회(Detail 은 필요 없음. 자투리 계산 않해도 됨)
		UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");
		PointHistoryDTO pointHParam = new PointHistoryDTO();
		pointHParam.setSearchUserId(		loginInfo.getId()	);
		pointHParam.setDrCr(				"CR"				);		//대변만(가용자산만)
		pointHParam.setSearchOrderType(		"ASC"				);		//오래 된 포인트 부터 계산 적용 //ORDER BY h.id ASC
		List<PointHistoryEntity> pointHEnt = pointHistoryService.getMyPointHistory(pointHParam);
		
		//2. 가용 포인트들의 각 포인트별 차/대변 맞춤.
		//2-1. 자투리는 avail_amount 에 표시하고 재계산한다.
		List<PointHistoryEntity> 		updEnt = new ArrayList<PointHistoryEntity>();
		List<PointHistoryDetailEntity> 	insEnt = new ArrayList<PointHistoryDetailEntity>();
		int npp 	= pp;
		int nowamt 	= 0;
		for(PointHistoryEntity itm : pointHEnt) {
			
			PointHistoryDetailEntity 	insDetail = new PointHistoryDetailEntity();
			//nowamt = itm.getAvailAmount() != null && itm.getAvailAmount() != 0 ? itm.getAvailAmount() : itm.getAmount();
			nowamt = itm.getAvailAmount();
			
			insDetail.setHistoryId(		itm.getId()		);	// - Detail
			insDetail.setTotAmt(		pp				);	// - Detail
			insDetail.setCreateId(		(int) loginInfo.getId());	// - Detail
			
			if(npp >= nowamt) {
				npp = npp - nowamt;
				itm.setDrCr(			"DR"		);	//Point
				itm.setAvailAmount(		0			);	//Point
				insDetail.setActAmt(		nowamt		);	// - Detail	
				insDetail.setRemAmt(		npp			);	// - Detail
				if(gbn.equals("N")) {
					itm.setDrPointType	(	"EXN"		);	//Point			//EXN,EXC,DIS : N교환/Csach교환/사용기간지남
					insDetail.setDrPointType(	"EXN"		);	// - Detail
					insDetail.setNpayId		(	npayId		);	// - Detail
				}else if(gbn.equals("C")) {
					itm.setDrPointType	(	"EXC"		);	//Point			//EXN,EXC,DIS : N교환/Csach교환/사용기간지남
					insDetail.setDrPointType(	"EXC"		);	// - Detail
					insDetail.setCashId		(	cashId		);	// - Detail
				}
				updEnt.add(itm);
				insEnt.add(insDetail);
				if(npp == 0) break;
			}else {
				//잔액 남아 있기에 "CR" 유지
				itm.setAvailAmount(nowamt - npp);
				insDetail.setActAmt(		npp			);	// - Detail	: 자투리 금액
				insDetail.setRemAmt(		0			);	// - Detail
				if(gbn.equals("N")) {
					insDetail.setDrPointType(	"EXN"		);	// - Detail
					insDetail.setNpayId		(	npayId		);	// - Detail
				}else if(gbn.equals("C")) {
					insDetail.setDrPointType(	"EXC"		);	// - Detail
					insDetail.setCashId		(	cashId		);	// - Detail
				}
				updEnt.add(itm);	//avail_amount 만 update 해준다.
				insEnt.add(insDetail);
				
				break;
			}
		}
		if ( updEnt != null && updEnt.size() >0 ) {
			for(PointHistoryEntity itm : updEnt) {
				int rt = pointHistoryService.update(itm);
			}
		}
		if ( insEnt != null && insEnt.size() >0 ) {
			for(PointHistoryDetailEntity itm : insEnt) {
				int rt = pointHistoryService.insertDetail(itm);
			}
		}
	}
	
	

}
