package kr.co.ucomp.web.svc.event.controller;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import kr.co.ucomp.web.svc.banner.entity.BannerEntity;
import kr.co.ucomp.web.svc.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.svc.event.dto.EvtSearchDTO;
import kr.co.ucomp.web.svc.event.entity.EvtEntity;
import kr.co.ucomp.web.svc.event.entity.EvtPlanEntity;
import kr.co.ucomp.web.svc.event.service.EvtPlanService;
import kr.co.ucomp.web.svc.event.service.EvtService;
import lombok.extern.slf4j.Slf4j;


/**
 * 2025.01.20 : sancho
 * @since 2025.01.20
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/svc/event")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class EventController {


	@Autowired private EvtService 			evtService			;
	@Autowired private EvtPlanService 		evtPlanService		;
	@Autowired private FileService 			fileService			;
	@Autowired private CompanyListService 	companyListService	;
	
	/**
	 * 이벤트 리스트
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String list ( HttpServletRequest request, Model model ) {
		
		log.info("event 리스트 진입");
				
		return "pages/svc/event/list";
	}

	/**
	 * 조회조건에 따라 목록 조회
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value = "/ajaxList")
	public ResponseEntity<CustomApiResponse<List<EvtEntity>>> getEventPlanList (HttpServletRequest request, @RequestBody EvtSearchDTO param) throws Exception 
	{
		Long totCnt  					= null;
		List<EvtEntity> resultList 		= null;
		try {
			totCnt  			= evtService.evtCount(param);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= evtService.evtList(param);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventPlanList: " + e.getMessage());
		}
	}
	
	/**
	 * UseYn 수정
	 * @param request
	 * @param ent
	 * @return
	 * @throws Exception
	 */
	@PostMapping( value = "/ajaxUseYn" )
	public ResponseEntity<CustomApiResponse<EvtEntity>> updateEvent (HttpServletRequest request, @RequestBody EvtEntity ent) throws Exception 
	{
		try {
			//별도 서비스를 만든다. IFNULL() 함수 사용에는 부적절 한듯...
			evtService.updateUseYn(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "Event updateUseYn: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Event updateUseYn: " + e.getMessage());
		}
	}
	
	/**
	 * Event 상세를 삭제한다.
	 * @param request
	 * @param ent
	 * @return
	 * @throws Exception
	 */
	@PostMapping( value = "/ajaxDel" )
	public ResponseEntity<CustomApiResponse<EvtEntity>> deleteEvent (HttpServletRequest request, @RequestBody EvtEntity ent) throws Exception
	{
		try {
			evtService.delete(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "deleteEvent: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "deleteEvent: " + e.getMessage());
		}
	}
	
	/**
	 * 수정 페이지
	 * @param request
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/goUpdatePage/{id}")
	public String goUpdatepate ( HttpServletRequest request, @PathVariable("id") int id, Model model ) {
		
		log.info("event goUpdatepate - id:" + id);
		
		EvtSearchDTO param = new EvtSearchDTO();
		param.setSearchId(id);
		EvtEntity result = evtService.evtById(param);
		
		/* 팝업 조회조건, [입점사] 목록 불러오기 */
		CompanyListSearchDto comparam = new CompanyListSearchDto();
		comparam.setSearchUseYn(1);
		List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(comparam);
		
		/* 이벤트에 연결 된 요금제 조회 */
		EvtPlanSearchDTO ePlanParam = new EvtPlanSearchDTO();
		ePlanParam.setSearchEventId(id);
		List<EvtPlanEntity> evtPlanList = evtPlanService.evtPlanList(ePlanParam);
		
		if(result != null) {
			System.out.println(result.getStartDate() );
			System.out.println(result.getEndDate() );
		}
		
		model.addAttribute("result"			, result);
		model.addAttribute("companyList"	, companyList);
		model.addAttribute("evtPlanList"	, evtPlanList);
		
		
		
		return "pages/svc/event/edit";
	}
	
	
	
	//insupdProc - insert 기능 마저 테스트 후 다음 넘어가자
	@PostMapping("/insupdProc")
	public ResponseEntity<CustomApiResponse<EvtEntity>> insupdProc( MultipartHttpServletRequest request, @RequestParam Map<String, Object> obj ) {
		
		try {
			
			HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
			
			EvtEntity ent			= new EvtEntity();
			int nowId				= 0;
			if(obj.get("id") != null && !"".equals(obj.get("id")) ) nowId =  Integer.parseInt( obj.get("id").toString() );
			ent.setId(nowId);
			ent.setEventCategory(		(String) obj.get("eventCategory")		);
			
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
			ent.setStartDate(LocalDateTime.parse(obj.get("startDate").toString() , formatter) );
			ent.setEndDate(	LocalDateTime.parse(obj.get("endDate").toString() , formatter) 	);
			
			ent.setTitle((String) obj.get("title"));
			ent.setEventMngSp((String) obj.get("eventMngSp"));
			ent.setContent(	(String) obj.get("content"));
			
			if(obj.get("manager") != null && !"".equals(obj.get("manager")) ) {
				ent.setManager(	Integer.parseInt( obj.get("manager").toString() ));
			}else {
				ent.setManager(loginadminInfo.getId());
			}
			
			ent.setEventThumbnail((String) obj.get("eventThumbnail"));
			ent.setEventImage(	(String) obj.get("eventImage")	);
			
			ent.setUseYn("Y");	//기획 없음
			
			
			ent.setEventNoticeSp((String) obj.get("eventNoticeSp"));
			ent.setEventNoticeContent((String) obj.get("eventNoticeContent"));
			
			
			//file 경로용 mapper
			ObjectMapper mapper = new ObjectMapper();
			
			//배너 이미지 등록
			MultipartFile thumFile = request.getFile("eventThumbnailU");
			if ( !StringUtils.isEmpty(thumFile.getOriginalFilename()) ) {
				String imageThum = fileService.FileUpload("event", thumFile);
				
				Map<String, Object> map = new ObjectMapper().readValue(imageThum, new TypeReference<Map<String, Object>>() { });
				
				//log.info("배너 이미지 업로드 결과 : {}", imageThum);
				log.info("배너 이미지 업로드 결과 map.get(fileUrl) : {}", map.get("fileUrl"));
				
				ent.setEventThumbnail(map.get("fileUrl").toString());
			}
			//eventImage
			MultipartFile eventImageFile = request.getFile("eventImageU");
			if ( !StringUtils.isEmpty(eventImageFile.getOriginalFilename()) ) {
				String imageEvent = fileService.FileUpload("event", eventImageFile);
				Map<String, Object> map = new ObjectMapper().readValue(imageEvent, new TypeReference<Map<String, Object>>() { });
				log.info("이벤트 이미지 업로드 결과 map.get(fileUrl): {}", map.get("fileUrl"));
				ent.setEventImage(map.get("fileUrl").toString());
			}
			
			
			
			
			//event Notice Image
			MultipartFile eventNoticeImageFile = request.getFile("eventNoticeImageU");
			if ( !StringUtils.isEmpty(eventNoticeImageFile.getOriginalFilename()) ) {
				String imageNoticeEvent = fileService.FileUpload("event", eventNoticeImageFile);
				Map<String, Object> map = new ObjectMapper().readValue(imageNoticeEvent, new TypeReference<Map<String, Object>>() { });
				log.info("이벤트 유의사항 이미지 업로드 결과 map.get(fileUrl): {}", map.get("fileUrl"));
				ent.setEventNoticeImage(map.get("fileUrl").toString());
			}
			
			if(ent.getId() > 0) {
				
				ent.setModifiedId(loginadminInfo.getId());
				evtService.update(ent);
				
			}else {
				
				ent.setCreateId(loginadminInfo.getId());
				if ( StringUtils.equals("TIMEDEAL", ent.getEventCategory()) ) {
					
					EvtSearchDTO timeDealSearch = new EvtSearchDTO();
					timeDealSearch.setSearchCategory("TIMEDEAL");
					long timeDealCnt = evtService.evtCount(timeDealSearch);
					
					if ( timeDealCnt > 0 ) {
						return CustomApiResponse.success(ResponseCode.ACCEPTED, ent);
					} else {
						evtService.create(ent);
					}
				} else {
					evtService.create(ent);
				}
			}
			
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "insupdProc: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "insupdProc: " + e.getMessage());
		}
		
	}
	
	
	//
	@PostMapping( value = "/evtPlanInsert" )
	public ResponseEntity<CustomApiResponse<EvtPlanEntity>> evtPlanInsert (HttpServletRequest request, @RequestBody EvtPlanEntity ent) throws Exception
	{
		try {
			evtPlanService.create(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "evtPlanInsert: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "evtPlanInsert: " + e.getMessage());
		}
	}
	
	@PostMapping( value = "/evtPlanDelete" )
	public ResponseEntity<CustomApiResponse<EvtPlanEntity>> evtPlanDelete (HttpServletRequest request, @RequestBody EvtPlanEntity ent) throws Exception
	{
		try {
			evtPlanService.create(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "evtPlanInsert: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "evtPlanInsert: " + e.getMessage());
		}
	}
	
	//@RequestBody EvtEntity ent) //title
	@PostMapping( value = "/planDelete" )
	public ResponseEntity<CustomApiResponse<String>> planDelete( HttpServletRequest request, @RequestBody EvtEntity body ) throws IOException {
    	
		try {
			String ids = body.getTitle();
			
			String delArr[] = ids.split(",");
			EvtPlanEntity ent = new EvtPlanEntity();
			for ( String str : delArr ) {
				ent.setId(Integer.parseInt(str));
				evtPlanService.delete(ent);
			}
			
			return CustomApiResponse.success(ResponseCode.OK, "del ok");
		}  catch (IllegalArgumentException e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "planDelete: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "planDelete: " + e.getMessage());
		}
	}
	
	
	
	
	/**
	 * 순서 정렬 (이벤트에 연결된 요금제)
	 * @param params
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/listPlanOrder" )
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> listPlanOrder ( @RequestParam("orderIds") String orderIds ) {
		
		try {
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			if ( !StringUtils.isEmpty(orderIds) ) {
				int sort = 1;
				EvtPlanEntity entity = new EvtPlanEntity();
				for ( String str : orderIds.split(",") ) {
					EvtPlanSearchDTO param = new EvtPlanSearchDTO();
					param.setSearchId(Integer.parseInt(str));
					entity = evtPlanService.evtById(param);
					entity.setOrderNo(sort);
					evtPlanService.update(entity);
					sort++;
				}
			}
			
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
		}
	}
	
	
	
}
