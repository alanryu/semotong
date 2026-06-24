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
import kr.co.ucomp.web.point.dto.PointCashDTO;
import kr.co.ucomp.web.point.entity.PointAccEntity;
import kr.co.ucomp.web.point.entity.PointHisDetCalcEntity;
import kr.co.ucomp.web.point.entity.PointHistoryDetailEntity;
import kr.co.ucomp.web.point.entity.PointHistoryEntity;
import kr.co.ucomp.web.point.entity.PointCashEntity;
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
@RequestMapping("/point/cash")
@PreAuthorize("hasAnyAuthority('ALL', 'POINT_MNG')")
public class PointCashController {
	
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
	 * Cash 관리 화면 이동
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String  npayList( HttpServletRequest request, Model model)  {
		return "pages/point/cash/list";
	}
	
	@ResponseBody
	@PostMapping("/ajaxCashList")
	public ResponseEntity<CustomApiResponse<List<PointCashEntity>>> ajaxCashList(HttpServletRequest request, @RequestBody PointCashDTO param) throws IOException {
		List<PointCashEntity> resultList = new ArrayList<PointCashEntity>(); 
		try{
			int count = pointCashService.getPointCashCount(param); 
			if(count > 0) {
				resultList = pointCashService.getPointCash(param);	
			}
			return CustomApiResponse.success(ResponseCode.OK, count, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ajaxCashList: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown (HttpServletRequest request, @RequestBody PointCashDTO dto) throws Exception 
	{
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		List<PointCashEntity> resultList = pointCashService.getPointCash(dto);
		for (PointCashEntity itm  :  resultList) {
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			
			data.put("statusName"			, itm.getStatusName()				);
			data.put("username"				, itm.getUsername()				);
			data.put("kakaoUserId"			, itm.getKakaoUserId()			);
			
			data.put("amount"				, itm.getAmount()			);
			data.put("bankName"				, itm.getBankName()			);
			data.put("accountNo"			, itm.getAccountNo()			);
			
			data.put("reqDate"				, itm.getReqDate()			);
			data.put("modifiedDate"			, itm.getModifiedDate()			);
			data.put("modifiedName"			, itm.getModifiedName()			);

			data.put("memo"					, itm.getMemo()					);
			dataList.add(data);
		}
		// 엑셀 헤더 설정
		String[] headers = {"상태", "회원명", "카카오ID", "전환금액", "은행명", "계좌번호", "신청일", "처리일", "담당자",  "메모"};
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
		PointCashEntity record = new PointCashEntity();
		try{
			if(model.getAttribute("org.springframework.validation.BindingResult.record") != null) {
				record = (PointCashEntity) model.getAttribute("record");
				model.addAttribute("record", record); 
				model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
			} else {		
				//if(StringUtils.isNotBlank(searchId)) {
				if(searchId != 0) {
					//record = onetooneService.getDetail(Integer.valueOf(searchId));
					PointCashDTO param = new PointCashDTO();
					param.setSearchCashId(searchId);
					record = pointCashService.getPointCashById(param);
				}
				model.addAttribute("record", record); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "pages/point/cash/edit";
	}
	
	
	/**
	 * 취소 처리
	 * 
	 * @param request
	 * @param response
	 * @param record
	 * @param bindingResult
	 * @param redirectAttributes
	 * @return
	 * @throws IOException
	 */
	@PostMapping( value = "/cancelInsert" )
	public String evtPlanInsert (HttpServletRequest request, HttpServletResponse response, @Valid @ModelAttribute("recordForm") PointCashEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException{
	
		int insid = 0;
		if (bindingResult.hasErrors()) {
			// 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
			redirectAttributes.addFlashAttribute("record", record);
			return "redirect:/point/cash/edit/"+insid;
		}
		try {
			
			
			
			//
			String resStatusCode 		= "";//cancelres.get("resultStatus") 	!=null ? (String) cancelres.get("resultStatus") 	: "999";	//세모통 res code
			String restMsg 				= "";//cancelres.get("resultMsg") 		!=null ? (String) cancelres.get("resultMsg") 		: "999";	//세모통 res msg
			
			Map<String, Object> resbody =  new HashMap<String, Object>();
			
			Map<String,Object> 	result 				= new HashMap<String,Object>();
			String procMsg 			= "COM";
			String procMsgDetail	= "완료처리 되었습니다.";
					
			// DB 처리 -------------------------------------------------------------------------------- DB 처리
			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
			
			//record 의 id가 덧씌워진다. 따로 빼둔다.
			
			// 1. point_cash - update 처리
			record.setModifiedId(		loginadminInfo.getId()	);
			int insNpayId = pointCashService.updateCash(record);
			
			// 2. point account 원장 정리 - OK
			PointAccEntity updPoint = new PointAccEntity();
			updPoint.setAmount(			record.getAmount()		);
			updPoint.setLastCashId(		record.getId()			);
			updPoint.setId(				record.getPointId()		);
			int updPointId = pointAccService.update(updPoint);
			
			if(record.getStatus().equals("REJ")) {
				// 3. history 원복 - String gbn N
				//String memo = "npayId:" + npayid + "cancel 처리 2";
				calcHistory("C", loginadminInfo, record.getAmount(),    0,   record.getId(), record.getMemo());
				
				procMsg			= "REJ";
				procMsgDetail	= "반려 되었습니다.";
			}
			// DB 처리 -------------------------------------------------------------------------------- DB 처리
			
			//반려시 취소 톡 발송
			if(record.getStatus().equals("REJ")) {
				
				// 03.20 ---------------------------- 취소 톡 발송 
				// 1.인증번호
				String kakaotoken = bizTalkService.getKakaoBizTalkToken();
				
				// 2.템플릿 찾기
				CommCodeSearchDto param =  new CommCodeSearchDto();
				
				param.setCodeGroup("biz_template");
				param.setCode("cashback_refuse");
				
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
				
				//String mngDate 		= CommonUtil.getDatetime("yyyy-MM-dd HH:mm:ss");
				String createDate	= record.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));	
				
				variable.put("userName"		, record.getUsername()		);
				variable.put("createDate"	, createDate				);
				variable.put("memo"			, record.getMemo()			);
				//variable.put("mngDate}"		, mngDate					);
				
				String sendMsg = bizTalkService.sendBizTemplate(param, variable);
				
				// 03.20 ---------------------------- 취소 톡 발송
			}
			
			
			
			
			
			
			
			
			
			
			redirectAttributes.addFlashAttribute("procMsg"			, procMsg);
			redirectAttributes.addFlashAttribute("procMsgDetail"	, procMsgDetail);
			
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		}
		
		return "redirect:/point/cash/edit/"+insid;
	}
	
	
	
	
	
	
	
	/** *****************************************************************************************************************************
	 * History 정리
	 ***************************************************************************************************************************** */
	public void calcHistory(String gbn, AdminUserDto loginadminInfo, int pp, int npayId, int cashId, String memo) {
		
		try {
			//1. 가용 포인트들 조회
	 		PointHistoryDetailDTO pointHParam = new PointHistoryDetailDTO();
	 		//pointHParam.setSearchNpayId(npayId);		//이것만으로 충분 할듯
	 		pointHParam.setSearchCashId(cashId);
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
