package kr.co.ucomp.web.point.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.ucomp.common.biztalk.KakaoBizTalkUtils;
import kr.co.ucomp.common.encrypt.DaouEncrypt;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.restapi.entity.RestApiTokenMngEntity;
import kr.co.ucomp.common.restapi.mapper.RestApiMapper;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.service.UserService;
import kr.co.ucomp.web.order.service.DailySequenceService;
import kr.co.ucomp.web.point.constant.DaouApiConstant;
import kr.co.ucomp.web.point.dto.PointHistoryDetailDTO;
import kr.co.ucomp.web.point.dto.PointNpayDTO;
import kr.co.ucomp.web.point.entity.PointAccEntity;
import kr.co.ucomp.web.point.entity.PointHisDetCalcEntity;
import kr.co.ucomp.web.point.entity.PointHistoryDetailEntity;
import kr.co.ucomp.web.point.entity.PointHistoryEntity;
import kr.co.ucomp.web.point.entity.PointNpayEntity;
import kr.co.ucomp.web.point.service.PointAccService;
import kr.co.ucomp.web.point.service.PointCashService;
import kr.co.ucomp.web.point.service.PointHistoryService;
import kr.co.ucomp.web.point.service.PointNpayService;

/**
 * 포인트 Npay 관리
 * @author sancho
 * @since 2025.03.09
 */
@Controller
@RequestMapping("/point/npay")
@PreAuthorize("hasAnyAuthority('ALL', 'POINT_MNG')")
public class PointNpayController {
	
	@Autowired private UserService userService;
	
	@Autowired private RestApiMapper restApiMapper;
	
	@Value("${daou.url}"					) String daouUrl;
	@Value("${daou.partner-code}"			) String daouPartnerCode;
	@Value("${daou.key.api-key}"			) String daouApiKey;
	@Value("${daou.key.enc-key}"			) String daouEncKey;
	@Value("${daou.key.iv-key}"				) String daouIvKey;
	
	@Autowired private RestTempletUtil rest;
	
	@Autowired CommCodeMngService 		codeService;
	@Autowired DailySequenceService 	sequenceService;
	@Autowired PointAccService			pointAccService;
	@Autowired PointHistoryService		pointHistoryService;
	@Autowired PointNpayService			pointNpayService;
	@Autowired PointCashService			pointCashService;
	
	@Autowired FileService fileService;
	
	@Autowired private KakaoBizTalkUtils bizTalkService;
	
	
	
	
	/**
	 * Point 관리 화면 이동
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String  npayList( HttpServletRequest request, Model model)  {
		return "pages/point/npay/list";
	}
	
	@ResponseBody
	@PostMapping("/ajaxNpayList")
	public ResponseEntity<CustomApiResponse<List<PointNpayEntity>>> ajaxNpayList(HttpServletRequest request, @RequestBody PointNpayDTO param) throws IOException {
		List<PointNpayEntity> resultList = new ArrayList<PointNpayEntity>(); 
		try{
			int count = pointNpayService.getPointNpayCount(param); 
			if(count > 0) {
				resultList = pointNpayService.getPointNpay(param);	
			}
			return CustomApiResponse.success(ResponseCode.OK, count, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ajaxNpayList: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown (HttpServletRequest request, @RequestBody PointNpayDTO dto) throws Exception 
	{
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		List<PointNpayEntity> resultList = pointNpayService.getPointNpay(dto);
		for (PointNpayEntity itm  :  resultList) {
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("username"				, itm.getUsername()				);
			data.put("kakaoUserId"			, itm.getKakaoUserId()			);
			data.put("partnerTxNo"			, itm.getPartnerTxNo()			);
			data.put("amount"				, itm.getReqAmount()			);
			data.put("createDate"			, itm.getCreateDate()			);
			data.put("npayApiTypeName"		, itm.getNpayApiTypeName()		);
			data.put("memo"					, itm.getMemo()					);
			dataList.add(data);
		}
		// 엑셀 헤더 설정
		String[] headers = {"회원명", "카카오ID", "요청번호", "전환금액", "전환일시", "상태", "메모"};
		byte[] excelData = fileService.getExcelData(headers,dataList);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}
	
	/**
	 * Npay Detail화면, 입력화면 이동
	 * @param request
	 * @param searchId
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/edit/{searchId}")
	public String npayDetail( 	HttpServletRequest request, @PathVariable("searchId") int searchId, Model model)  throws IOException {
		PointNpayEntity record = new PointNpayEntity();
		try{
			if(model.getAttribute("org.springframework.validation.BindingResult.record") != null) {
				record = (PointNpayEntity) model.getAttribute("record");
				model.addAttribute("record", record); 
				model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
			} else {		
				//if(StringUtils.isNotBlank(searchId)) {
				if(searchId != 0) {
					//record = onetooneService.getDetail(Integer.valueOf(searchId));
					PointNpayDTO param = new PointNpayDTO();
					param.setSearchNpayId(searchId);
					record = pointNpayService.getPointNpayById(param);
					
					param = new PointNpayDTO();
					param.setSearchPartnerTxNo(record.getPartnerTxNo());
					int cancelCheckEnt = pointNpayService.getPointNpayByPartnerTxNo(param);
					if(cancelCheckEnt > 1 ) {
						record.setDayAfter("OUTTER");
					}
				}
				model.addAttribute("record", record); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "pages/point/npay/edit";
	}
	
	
	/**
	 * 취소 요청
	 * /v1/npay/point/cancel
	 * @param request
	 * @param response
	 * @param record
	 * @param bindingResult
	 * @param redirectAttributes
	 * @return
	 * @throws IOException
	 */
	@PostMapping( value = "/cancelInsert" )
	public String evtPlanInsert (HttpServletRequest request, HttpServletResponse response, @Valid @ModelAttribute("recordForm") PointNpayEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException{
	
		String				DAOU_API			= "DAOU_POINT_CANCEL";
		String				DAOU_API_NAME		= "다우 네이버페이 포인트 적립 취소";
		//System.out.println(record);
		int insid = 0;
		if (bindingResult.hasErrors()) {
			// 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
			redirectAttributes.addFlashAttribute("record", record);
			return "redirect:/point/npay/edit/"+insid;
		}
		try {
			
			
//			//  ---------------------------------------------------------------------------------- test 
			// DB 처리 -------------------------------------------------------------------------------- DB 처리

			// DB 처리 -------------------------------------------------------------------------------- DB 처리
//			//  ---------------------------------------------------------------------------------- test
			
			String token			= daouAccessToken();
			
			// OPEN API 호출 URL 정보 설정
			String apiUrl 			= daouUrl + DaouApiConstant.DAOU_POINT_CANCEL.URL;
			String method 			= DaouApiConstant.DAOU_POINT_CANCEL.METHOD;
			
			//Header
			Map<String,String> headerMap = new HashMap<>();
			headerMap.put("Pointbox-Partner-Code"		, daouPartnerCode		);		//header에 파트너코드
			headerMap.put("Authorization"				, "Bearer " + token		);		//header에 token
			
			//body
			Map<String, Object> bodyMap = new HashMap<> ();
			bodyMap.put("userKey"			, DaouEncrypt.encrypt((String) record.getUserKey() ) 				);
			bodyMap.put("txNo"				, record.getTxNo() 													);
			//bodyMap.put("clientSecret"		, record.getPartnerTxNo() 											);		// 망취소(Naver 오류 일 때 50000 번대 오류들)
			
			// -- /v1/npay/point/cancel
			Map<String, Object>  cancelres = rest.sendRestApi(DAOU_API, apiUrl, method, bodyMap, headerMap);
			
			//
			String resStatusCode 		= cancelres.get("resultStatus") 	!=null ? (String) cancelres.get("resultStatus") 	: "999";	//세모통 res code
			String restMsg 				= cancelres.get("resultMsg") 		!=null ? (String) cancelres.get("resultMsg") 		: "999";	//세모통 res msg
			
			Map<String, Object> resbody =  new HashMap<String, Object>();
			
			Map<String,Object> 	result 				= new HashMap<String,Object>();
			String procMsg 			= "";
			String procMsgDetail	= "";
			
			
			if("200 OK".equals(resStatusCode)) {
				if(cancelres.get("resultBody") != null) {
					//답신성공
					procMsg			= "sucess";
					
					// DB 처리 -------------------------------------------------------------------------------- DB 처리
					HttpSession session = request.getSession();
					AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
					
					record.setModifiedId(loginadminInfo.getId());
					// 1. npay cancel 로 insert 처리
					//int insNpayId = pointNpayService.insertNpay(record);
					// 1. update 처리로 바꿈
					int insNpayId = pointNpayService.updateNpay(record);
					
					// 2. point account 원장 정리 - OK
					PointAccEntity updPoint = new PointAccEntity();
					updPoint.setAmount(		record.getReqAmount()	);	
					updPoint.setLastNpayId(	insNpayId				);
					updPoint.setId(			record.getPointId()		);
					int updPointId = pointAccService.update(updPoint);
					
					// 3. history 원복 - String gbn N
					int npayid = record.getId();
					//String memo = "npayId:" + npayid + "cancel 처리 2";
					calcHistory("N", loginadminInfo, record.getReqAmount(), npayid, 	0, record.getMemo());
					// DB 처리 -------------------------------------------------------------------------------- DB 처리
					
					// 03.20 ---------------------------- 취소 톡 발송 
					// 1.인증번호
					String kakaotoken = bizTalkService.getKakaoBizTalkToken();
					
					// 2.템플릿 찾기
					CommCodeSearchDto param =  new CommCodeSearchDto();
					
					param.setCodeGroup("biz_template");
					param.setCode("NPoint_cancel");
					
					// +82을 0으로 대체
					String phoneNumber = record.getPhoneNumber();
					if (phoneNumber.startsWith("+82")) {
						phoneNumber = "0" + phoneNumber.substring(4);
						//:"0 1047500106",
					}
					
					// 하이픈 제거
					phoneNumber = phoneNumber.replaceAll("-","");
					  
					// 3.받는사람 세팅 
					Map<String, String> variable = new HashMap<String, String>();
					variable.put("to"			, phoneNumber	);
					
					String mngDate 		= CommonUtil.getDatetime("yyyy-MM-dd HH:mm:ss");
					String createDate	= record.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));	
					
					variable.put("userName"		, record.getUsername()		);
					variable.put("createDate"	, createDate				);
					variable.put("memo"			, record.getMemo()			);
					variable.put("mngDate"		, mngDate					);
					
					String sendMsg = bizTalkService.sendBizTemplate(param, variable);
					
					// 03.20 ---------------------------- 취소 톡 발송
					
				}
			}else {
				// -- 다우전송 => 400 400: "{"code":"41014","message":"이미 취소된 거래"}"
				// -- 이걸 우리 템플릿은 999로 받음
				// -- {resultStatus=999, resultBody={}, resultMsg=400 400: "{"code":"41014","message":"이미 취소된 거래"}"}
				//restMsg
				procMsg			= "fail";
				procMsgDetail	= restMsg;
				
				insid = record.getId();
			}
			
			
			redirectAttributes.addFlashAttribute("procMsg"			, procMsg);
			redirectAttributes.addFlashAttribute("procMsgDetail"	, procMsgDetail);
			
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		}
		
		return "redirect:/point/npay/edit/"+insid;
	}
	
	
	
	
	

	/**
	 * DAOU_AUTH_TOKEN 획득
	 * @return token String
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
			System.out.println("$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("select token_val:" + rtnStr);
			System.out.println("$$$$$$$$$$$$$$$$$$$$$");
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
						
						System.out.println("$$$$$$$$$$$$$$$$$$$$$ 2222222222222222222222");
						System.out.println("new token_val:" + tkval);
						System.out.println("$$$$$$$$$$$$$$$$$$$$$ 2222222222222222222222");
					}
				}
			}
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return rtnStr;
	}
	
	
	
	
	/** *****************************************************************************************************************************
	 * History 정리
	 ***************************************************************************************************************************** */
	public void calcHistory(String gbn, AdminUserDto loginadminInfo, int pp, int npayId, int cashId, String memo) {
		
		try {
			//1. 가용 포인트들 조회
	 		PointHistoryDetailDTO pointHParam = new PointHistoryDetailDTO();
	 		pointHParam.setSearchNpayId(npayId);		//이것만으로 충분 할듯
			pointHParam.setSearchOrderType(		"ASCDET"				);		//오래 된 포인트 부터 계산 적용 //ORDER BY h.id ASC
			List<PointHisDetCalcEntity> pointHEnt = pointHistoryService.getPointHistoryDetail(pointHParam);
			
			//2. 가용 포인트들의 각 포인트별 차/대변 맞춤.
			//2-1. 자투리는 avail_amount 에 표시하고 재계산한다.
			List<PointHistoryEntity>		updEnt = new ArrayList<PointHistoryEntity>();
			List<PointHistoryDetailEntity>	insEnt = new ArrayList<PointHistoryDetailEntity>();
			int npp 	= pp;
			int nowamt 	= 0;
			for(PointHisDetCalcEntity itm : pointHEnt) {
				PointHistoryEntity cpitm = new PointHistoryEntity();
				cpitm.setId(			itm.getHistoryId()		);		
				cpitm.setDrCr(			"CR"					);
				//cpitm.setDrPointType(	""						);	//null 화?!
				cpitm.setAvailAmount(	itm.getAvailAmount() + itm.getActAmt()	);		//기존 잔액 뭉개지며, 처음 amount 로 돌아 간다.
				cpitm.setMemo(			memo					);
				updEnt.add(cpitm);
				
				PointHistoryDetailEntity cpdetailitm = new PointHistoryDetailEntity();
				cpdetailitm.setHistoryId(		itm.getHistoryId()	);
				cpdetailitm.setDrPointType(		"CAN"				);
				cpdetailitm.setTotAmt(			itm.getTotAmt()		);
				cpdetailitm.setActAmt(			itm.getActAmt()		);
				cpdetailitm.setRemAmt(			0					);
				cpdetailitm.setNpayId(			itm.getNpayId()		);
				cpdetailitm.setCashId(			itm.getCashId()		);
				cpdetailitm.setCreateId(		loginadminInfo.getId());
				insEnt.add(cpdetailitm);
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	

}
