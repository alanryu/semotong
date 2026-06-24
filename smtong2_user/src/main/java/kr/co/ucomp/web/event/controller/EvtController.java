package kr.co.ucomp.web.event.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import kr.co.ucomp.web.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.event.dto.EvtSearchDTO;
import kr.co.ucomp.web.event.dto.EvtWinnerSearchDTO;
import kr.co.ucomp.web.event.entity.EvtEntity;
import kr.co.ucomp.web.event.entity.EvtPlanEntity;
import kr.co.ucomp.web.event.entity.EvtWinnerEntity;
import kr.co.ucomp.web.event.service.EvtPlanService;
import kr.co.ucomp.web.event.service.EvtService;
import kr.co.ucomp.web.event.service.EvtWinnerService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;


/**
 * Event Controller - 
 */
@Controller
@RequestMapping( value = "/event" )
@Slf4j
public class EvtController {
	
	@Autowired
	private EvtService 			evtService;
	
	@Autowired
	private EvtPlanService 		evtPlanService;
	
	@Autowired
	private EvtWinnerService 	evtWinnerService;
	
	@Autowired PlanService planService;
	
	
	
	
	@Value("${jasypt.encryptor.algorithm}") String algorithm;
	@Value("${jasypt.encryptor.password}") String password;
	

	
	@GetMapping("/eventList")
	public String  eventList( HttpServletRequest request, Model model) {
		
		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		
		return "pages/event/eventList";
	}
	
	
	@ResponseBody
	@PostMapping(value = "/getEventList")
	public ResponseEntity<CustomApiResponse<List<EvtEntity>>> getEventPlanList(HttpServletRequest request ,@RequestBody EvtSearchDTO param) throws IOException {
		
		if( param.getSearchOrderType() == null || "".equals(param.getSearchOrderType()) ) param.setSearchOrderType("NEW");		//기본 최신순
		Long totCnt  					= null;
		List<EvtEntity> resultList 		= null;
		System.out.println(param);
		try {
			totCnt  			= evtService.evtCount(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= evtService.evtList(param);
				
				SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
				
				//날짜 형식 바꾸기 잘몰라서 여기다 만듬.
				for(EvtEntity e : resultList) {
					e.setStrStart(newDtFormat.format(e.getStartDate()));
					e.setStrEnd(newDtFormat.format(e.getEndDate()));
				}
				
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "event list: " + e.getMessage());
		}
	}
	
	@GetMapping("/eventDetail/{id}")
	public String  eventDetail( HttpServletRequest request, Model model, @PathVariable("id") Integer id) {
		
		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		
		EvtSearchDTO param = new EvtSearchDTO();
		param.setSearchId(id);
		EvtEntity ent = evtService.evtById(param);
		model.addAttribute("ent", ent);
		return "pages/event/eventDetail";
	}
	@GetMapping("/eventDetailING/{id}")
	public String  eventDetailING( HttpServletRequest request, Model model, @PathVariable("id") Integer id) {
		EvtSearchDTO param = new EvtSearchDTO();
		param.setSearchId(id);
		EvtEntity ent = evtService.evtById(param);
		model.addAttribute("ent", ent);
		model.addAttribute("gbn", "ING");
		return "pages/event/eventDetail";
	}
	@GetMapping("/eventDetailTER/{id}")
	public String  eventDetailTER( HttpServletRequest request, Model model, @PathVariable("id") Integer id) {
		EvtSearchDTO param = new EvtSearchDTO();
		param.setSearchId(id);
		EvtEntity ent = evtService.evtById(param);
		model.addAttribute("ent", ent);
		model.addAttribute("gbn", "TER");
		return "pages/event/eventDetail";
	}
	
	/**
	 * Event 목록 조회
	 *  - 이벤트 종류에 따라 조회 하며, 파라미터를 'all' 로 할 경우 전체 목록을 조회 한다.
	 * @param request
	 * @param param	/evtList/JOIN,RECOM,COMBINE,UNITE 또는 all
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/evt/list")
	public ResponseEntity<CustomApiResponse<List<EvtEntity>>> getEventList (HttpServletRequest request, @RequestBody EvtSearchDTO param) throws Exception 
	{
		param.setSearchCategory(param.getSearchCategory().toUpperCase());
		
		Long totCnt  				= null;
		List<EvtEntity> resultList 	= null;
		
		try {
			totCnt  		= evtService.evtCount(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= evtService.evtList(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing getEventList: " + e.getMessage());
		}
	}

	/**
	 * Event 상세 내역을 조회 한다.
	 * @param request
	 * @param param /detail/1
	 * @return
	 * @throws Exception
	 */
	@GetMapping( value = "/evt/detail/{searchId}" )
	public ResponseEntity<CustomApiResponse<EvtEntity>> getEventDetail (HttpServletRequest request
											, @PathVariable("searchId") int searchId
										) throws Exception 
	{
		EvtSearchDTO param = new EvtSearchDTO();
		param.setSearchId(searchId);
		try {
			EvtEntity result = evtService.evtById(param);
			if (result == null) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND);
			}
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventDetail: " + e.getMessage());
		}
	}
	
	/**
	 * event 에 맵핑 된 요금제 정보 조회
	 */
	@ResponseBody
	@PostMapping(value = "/getEvtPlanlist")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getReviewList(HttpServletRequest request, @RequestBody EvtPlanSearchDTO param) throws IOException {
		try{
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
			long resulCnt = evtPlanService.evtCount(param);
			if(resulCnt > 0) {
				List<EvtPlanEntity> epList = evtPlanService.evtList(param);
				
				List<String> pList = new ArrayList<>();
				for(EvtPlanEntity itm : epList) {
					pList.add( itm.getPlanId() + "" );
				}
			
				SearchPlanDto planParam = new SearchPlanDto();
				planParam.setSearchplanIdList(pList);
				planParam.setSearchOrderType("event");
				
				resultList = planService.getAllListByPlanIds(planParam);
			}
			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Event Plan(이벤트별 요금제) 목록 조회   ---------------------------------------------------------------------------------------- Event Plan
	 *  - 전체 또는 이벤트id(searchEventId)
	 * @param request
	 * @param 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/evtPlan/list")
	public ResponseEntity<CustomApiResponse<List<EvtPlanEntity>>> getEventPlanList (HttpServletRequest request, @RequestBody EvtPlanSearchDTO param) throws Exception 
	{
		Long totCnt  					= null;
		List<EvtPlanEntity> resultList 	= null;
		try {
			totCnt  			= evtPlanService.evtCount(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= evtPlanService.evtList(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventPlanList: " + e.getMessage());
		}
	}

	/**
	 * Event Plan(이벤트별 요금제) 상세 내역을 조회 한다.
	 * @param request
	 * @param param /detail/1
	 * @return
	 * @throws Exception
	 */
	@PostMapping( value = "/evtPlan/detail" )
	public ResponseEntity<CustomApiResponse<EvtPlanEntity>> getEventPlanDetail (HttpServletRequest request, @RequestBody EvtPlanSearchDTO param
										) throws Exception 
	{

		int a = (param.getSearchId() 		== null) ? 0 :param.getSearchId()		;
		int b = (param.getSearchEventId() 	== null) ? 0 :param.getSearchEventId()	;
		int c = (param.getSearchPlanId() 	== null) ? 0 :param.getSearchPlanId()	;
		//HashMap<String, String> rtnmap = new HashMap<String, String>();
		//rtnmap.put("data", "id, eventid, planid 중 하나 이상 필수.");
		//  success(ResponseCode.OK, rtnmap);
		if(a == 0 && b == 0 && c == 0) return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		try {
			EvtPlanEntity result = evtPlanService.evtById(param);
			if (result == null) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND);
			}
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventPlanDetail: " + e.getMessage());
		}
	}
	
	
	
	
	
	

	/**
	 * Event Winner(이벤트별 당첨자) 목록 조회   ---------------------------------------------------------------------------------------- Event Winner
	 *  - 전체 only
	 * @param request
	 * @param 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/evtWinner/list")
	public ResponseEntity<CustomApiResponse<List<EvtWinnerEntity>>> getEventWinnerList (HttpServletRequest request, @RequestBody EvtWinnerSearchDTO param
											) throws Exception 
	{
		Long totCnt  						= null;
		List<EvtWinnerEntity> resultList 	= null;
		
		try {
			totCnt  			= evtWinnerService.evtCount(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= evtWinnerService.evtList(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventWinnerList: " + e.getMessage());
		}
	}

	/**
	 * Event Winner(이벤트 당첨자) 상세 내역을 조회 한다.
	 * @param request
	 * @param param /detail/1
	 * @return
	 * @throws Exception
	 */
	@PostMapping( value = "/evtWinner/detail" )
	public ResponseEntity<CustomApiResponse<EvtWinnerEntity>> getEventWinnerDetail (HttpServletRequest request, @RequestBody EvtWinnerSearchDTO param
										) throws Exception 
	{
		int searchid = (param.getSearchId() == null) ? 0 : param.getSearchId();
		if(searchid == 0) return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		try {
			EvtWinnerEntity result = evtWinnerService.evtById(param);
			if (result == null) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND);
			}
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventWinnerDetail: " + e.getMessage());
		}
	}

}
