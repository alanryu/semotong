package kr.co.ucomp.web.pmb.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.cmm.entity.ColumnInfoEntity;
import kr.co.ucomp.web.pmb.dto.ScrapingAPIDto;
import kr.co.ucomp.web.pmb.dto.ScrapingLogSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.entity.ScrapingAPIEntity;
import kr.co.ucomp.web.pmb.entity.ScrapingLogEntity;
import kr.co.ucomp.web.pmb.entity.ScrapingPlanEntity;
import kr.co.ucomp.web.pmb.service.ScrapingLogService;
import kr.co.ucomp.web.pmb.service.ScrapingPlanService;

@Controller
@RequestMapping("pmb/scraping")
@PreAuthorize("hasAnyAuthority('ALL', 'PROD_MNG')")
public class ScrapingController {
	
	@Autowired
	private ScrapingPlanService scrapingPlanService;
	
	@Autowired
	private ScrapingLogService scrapingLogService;
	
	@Value("${scraping.server}") String scrapingUri;
	
	@Autowired 
	private FileService fileService;
	
	
	@GetMapping("/list")
	public String  listview( HttpServletResponse response,Model model) {
		model.addAttribute("scrapingUri", scrapingUri);
		//System.out.println("scrapingUri: " + scrapingUri); // 값 확인
		return "pages/pmb/scraping/list";
	}
	
	/**
	 * /shutdown
	 * @param request
	 * @param param
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/scrapingShutdown")
	private ResponseEntity<CustomApiResponse<ScrapingAPIEntity>> scrapingShutdown(HttpServletRequest request, @RequestBody ScrapingAPIDto param){

		try {
			ScrapingAPIEntity rtnResult = new ScrapingAPIEntity();
			//final String uri = "http://logs.smtong.co.kr:5001" + param.getShutdown();		// <------------ /shutdown
			//final String uri = scrapingUri + param.getShutdown();		// <------------ /shutdown
			final String uri = param.getServerUri() + param.getShutdown();		// <------------ /shutdown
			
			// HTTP 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			// Body set
			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			//body.add("wait_time", param.getWait_time() + "");
			
			// Message
			HttpEntity<?> requestMessage = new HttpEntity<>(body, headers);
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<ScrapingAPIEntity> rtn = restTemplate.postForEntity(uri, requestMessage, ScrapingAPIEntity.class);
			
			rtnResult.setCycle_id(rtn.getBody().getCycle_id());
			rtnResult.setMessage(rtn.getBody().getMessage());
			rtnResult.setStatus(rtn.getBody().getStatus());
			
			System.out.println("======================== scrapingShutdown ========================");
			System.out.println("->" + rtnResult.getStatus());
			System.out.println("->" + rtnResult.getCycle_id());
			System.out.println("->" + rtnResult.getMessage());
			System.out.println("==================================================================");
			
			return CustomApiResponse.success(ResponseCode.OK, 0, rtnResult);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "pmb/scraping/statusInfo: " + e.getMessage());
		}
	}
	
	/**
	 *  /start
	 * @param request
	 * @param param
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/scrapingStart")
	private ResponseEntity<CustomApiResponse<ScrapingAPIEntity>> scrapingStart(HttpServletRequest request, @RequestBody ScrapingAPIDto param){

		try {
			ScrapingAPIEntity rtnResult = new ScrapingAPIEntity();
			//final String uri = scrapingUri + param.getStart();			// <------------ /start
			final String uri = param.getServerUri() + param.getStart();			// <------------ /start
			
			// HTTP 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
//			// Body set
//			MultiValueMap<String, Integer> body = new LinkedMultiValueMap<>();
//			body.add("wait_time", param.getWait_time()	);
			
			// Body set (단건으로 설정)
			Map<String, Integer> body = new HashMap<>();
			body.put("wait_time", param.getWait_time());
			
			// Message
			HttpEntity<?> requestMessage = new HttpEntity<>(body, headers);
			
			RestTemplate restTemplate = new RestTemplate();
			
			ResponseEntity<ScrapingAPIEntity> rtn = restTemplate.postForEntity(uri, requestMessage, ScrapingAPIEntity.class);
			
			if (rtn.getBody() != null) {
				rtnResult.setCycle_id(rtn.getBody().getCycle_id());
				rtnResult.setMessage(rtn.getBody().getMessage());
				rtnResult.setStatus(rtn.getBody().getStatus());
			}
			
			Date now = new Date();
			String nowTime = now.toString();
			
			System.out.println("======================== scrapingStart ======================== : " + nowTime);
			
			return CustomApiResponse.success(ResponseCode.OK, 0, rtnResult);
		} catch (HttpStatusCodeException e) { // HTTP 400, 500 오류 처리
			System.err.println("API 오류 발생: " + e.getStatusCode());
			System.err.println("응답 내용: " + e.getResponseBodyAsString());

			ScrapingAPIEntity errorResponse = new ScrapingAPIEntity();
			errorResponse.setMessage("Error: " + e.getStatusCode());
			errorResponse.setStatus(e.getStatusCode() + "");
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, e.getResponseBodyAsString());
			
		} catch (Exception e) { // 기타 예외 처리
			e.printStackTrace();
			System.err.println("Error in scrapingStart: " + e.getMessage());

			ScrapingAPIEntity errorResponse = new ScrapingAPIEntity();
			errorResponse.setMessage("Error: " + e.getMessage());
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * /status
	 * @param request
	 * @param param
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/scrapingStatus")
	private ResponseEntity<CustomApiResponse<ScrapingAPIEntity>> scrapingStatus(HttpServletRequest request, @RequestBody ScrapingAPIDto param){
		try {
			
			
				
			//System.out.println("scrapingUri: " + scrapingUri.getServer()); // 값 확인
			//System.out.println("scrapingUri - in scrapingStatus: " + scrapingUri.getServer()); // 값 확인
			//System.out.println("scrapingServerUri - in scrapingStatus: " +  scrapingUri);
			//System.out.println("scrapingUri - in scrapingStatus: " + scrapingUri); // 값 확인
			
			
			
			ScrapingAPIEntity rtnResult = new ScrapingAPIEntity();
			
			//final String uri = scrapingUri + param.getStatus();
			final String uri = param.getServerUri() + param.getStatus();
			
			RestTemplate restTemplate = new RestTemplate();
			
			ResponseEntity<ScrapingAPIEntity> rtn = restTemplate.getForEntity(uri, ScrapingAPIEntity.class);
			
			rtnResult.setCycle_id(rtn.getBody().getCycle_id());
			rtnResult.setMessage(rtn.getBody().getMessage());
			rtnResult.setStatus(rtn.getBody().getStatus());
			
			System.out.println("======= status =======");
			System.out.println("->" + rtnResult.getStatus());
			System.out.println("->" + rtnResult.getCycle_id());
			System.out.println("->" + rtnResult.getMessage());
			System.out.println("======================");
			
			
			
			//ScrapingAPIEntity rtnResult = new ScrapingAPIEntity();
			//rtnResult = scrapingAPIService.SendApi("scrapingStatus", param);
			
			
			
			return CustomApiResponse.success(ResponseCode.OK, 0, rtnResult);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "pmb/scraping/scrapingStatus: " + e.getMessage());
		}
	}
	
	
	@ResponseBody
	@PostMapping(value = "/ajaxList")
	public ResponseEntity<CustomApiResponse<List<ScrapingLogEntity>>> getEventPlanList (HttpServletRequest request, @RequestBody ScrapingLogSearchDto param) throws Exception 
	{
		Long totCnt  							= null;
		List<ScrapingLogEntity> resultList 		= null;
		try {
			totCnt  			= scrapingLogService.getScrapingLogListCnt(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= scrapingLogService.getScrapingLogList(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "pmb/scraping/ajaxList: " + e.getMessage());
		}
	}
	
	/**
	 * UUID 로 id 찾기 (tb_pmb_plan_list에서)
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value = "/ajaxGetPlanId")
	public PlanEntity ajaxGetPlanId (HttpServletRequest request, @RequestBody ScrapingLogSearchDto param) throws Exception 
	{
		PlanEntity result 		= null;
		try {
			result 		= scrapingLogService.getPlanId(param);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}
	
	
	/*
	
	@ResponseBody
	@PostMapping(value = "/ajaxList")
	public ResponseEntity<CustomApiResponse<List<ScrapingLogEntity>>> getEventPlanList (HttpServletRequest request, @RequestBody ScrapingLogSearchDto param) throws Exception 
	{
		Long totCnt  							= null;
		List<ScrapingLogEntity> resultList 		= null;
		try {
			totCnt  			= scrapingLogService.getScrapingLogListCnt(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= scrapingLogService.getScrapingLogList(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
	 */
	
	
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown (HttpServletRequest request, @RequestBody ScrapingLogSearchDto param) throws Exception 
	{
		try {
				
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			List<ScrapingLogEntity> resultList = scrapingLogService.getScrapingLogList(param);
			for (ScrapingLogEntity itm  :  resultList) {
				Map<String, Object> data = new LinkedHashMap<String, Object>();
				data.put("logTime"				, itm.getLogTime()		);
				data.put("cycleId"				, itm.getCycleId()		);
				data.put("logTypeName"			, itm.getLogTypeName()	);
				data.put("functionName"			, itm.getFunctionName()	);
				data.put("message"				, itm.getMessage()		);
				dataList.add(data);
			}
			
			// 엑셀 헤더 설정
			String[] headers = {"일시", "싸이클 번호", "로그 타입", "사이트명", "메시지"};
			
			byte[] excelData = fileService.getExcelData(headers,dataList);
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(excelData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**																																-- 않씀
	 * UUID 로 id 찾기 (tb_pmb_plan_list에서)
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value = "/ajaxGetPlanIds")
	public List<PlanEntity> ajaxGetPlanIds (HttpServletRequest request, @RequestBody ScrapingLogSearchDto param) throws Exception 
	{
		List<PlanEntity> result 		= null;
		try {
			result 		= scrapingLogService.getPlanIds(param);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}
	
	/**																																-- 않씀
	 * COLUMN 정보 조회 한다(tb_pmb_plan_list 에서)  않씀.
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value = "/ajaxGetColumnInfo")
	public ColumnInfoEntity ajaxGetColumnInfo (HttpServletRequest request) throws Exception 
	{
		ColumnInfoEntity result 		= null;
		try {
			result 		= scrapingLogService.getPlanListColumnInfo();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/*
	=================================
	===== scraping Plan Mapper =====
	=================================	
	- endpoint
	pmb/scraping/plan
	*/
	
	@GetMapping("/plan")
	public ResponseEntity<CustomApiResponse<List<ScrapingPlanEntity>>> getScrapingPlans(ScrapingLogSearchDto param) throws Exception {
		long cnt = scrapingLogService.getScrapingLogListCnt(param);
		if(cnt != 0) {
			List<ScrapingPlanEntity> response = scrapingPlanService.getScrapingPlanList();
			return CustomApiResponse.success(ResponseCode.OK, cnt, response);
		}
		else {
			return CustomApiResponse.error(ResponseCode.NOT_FOUND, "요금제 정보가 없습니다.");
		}
	}

	
	/*
	================================
	===== scraping Log Mapper =====
	================================
	- endpoint
	pmb/scraping/log
	*/
	
	/**
	 *
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/log")
	public ResponseEntity<CustomApiResponse<List<ScrapingLogEntity>>> getscrapingLog(ScrapingLogSearchDto param) throws Exception
	{
		long cnt = scrapingLogService.getScrapingLogListCnt(param);
		if (cnt != 0){
			try{
				// TODO select 추가
				List<ScrapingLogEntity> response = scrapingLogService.getScrapingLogList(param);
				return CustomApiResponse.success(ResponseCode.OK, cnt, response);
			} catch (Exception e){
				return CustomApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage());
			}
		}
		else {
			return CustomApiResponse.error(ResponseCode.NOT_FOUND, "로그가 존재하지 않습니다.");
		}
	}

	@PostMapping("/log")
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> insertScrapingLog(
			@RequestParam ScrapingLogEntity scrapingLogEntity
			) {

		Map<String, Object> response = new HashMap<>();
		long id = scrapingLogService.insertScrapingLog(scrapingLogEntity);
		response.put("request", scrapingLogEntity);
		response.put("id", id);
		return CustomApiResponse.success(ResponseCode.CREATED,1, response);
	}
}
