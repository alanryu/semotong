package kr.co.ucomp.web.svc.banner.contoroller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
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
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.service.PlanService;
import kr.co.ucomp.web.svc.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.svc.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.svc.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.svc.banner.entity.MainDealMstEntity;
import kr.co.ucomp.web.svc.banner.service.DealBannerService;
import lombok.extern.slf4j.Slf4j;

/**
 * Main Deal Mst
 * @author 김재희
 * @since 2024.12.20
 * @version v1.0
 *
 * Main Deal Banner
 * @author 김재희
 * @since 2024.12.21
 * @version v1.0
 */

@Controller
@RequestMapping(value = "/svc/mainbanner")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class DealBannerController {

    @Autowired 
    private DealBannerService dealBannerService;
    @Autowired
    private CompanyListService companyListService;
    @Autowired
    private PlanService planService;
    @Autowired
	private FileService fileService;
    
    /**
     * 메인배너(추천딜) 관리
     * @param request
     * @param model
     * @return
     */
    @GetMapping( value = "/dealList")
    public String dealList( HttpServletRequest request, Model model ) {

    	log.info("메인배너(추천딜) 리스트 진입");
    	
    	return "pages/svc/mainbanner/dealList";
    }
    
    /**
     * 검색결과
     * @param request
     * @param param
     * @return
     * @throws IOException
     */
    @PostMapping( value = "/dealListProc" )
    public ResponseEntity<CustomApiResponse<List<MainDealMstEntity>>> listProc ( HttpServletRequest request, @RequestBody MainDealMstDto param ) throws IOException { 
    	
    	try{
			
            long resultcnt = dealBannerService.listCount(param);
            List<MainDealMstEntity> resultList = new ArrayList<MainDealMstEntity>();
            if(resultcnt > 0 ) {
            	resultList = dealBannerService.mainDealMstList(param);
            	
            	/* 추천딜배너 하위 요금제 검색 */
            	MainDealBannerDto dto = new MainDealBannerDto();
            	for ( MainDealMstEntity temp : resultList ) {
            		dto.setMain_deal(temp.getId());
            		List<MainDealBannerEntity> dealBannerList = dealBannerService.mainPageDealBanner(dto);
            		temp.setList(dealBannerList);
            	}
            }
            
            return CustomApiResponse.success(ResponseCode.OK, resultcnt, resultList);

        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
    }
    
    /**
     * 입점사별 요금제 리스트
     * @param response
     * @param id
     * @return
     */
    @PostMapping(value = "/dealPlanList")
    public ResponseEntity<CustomApiResponse<List<PlanEntity>>> dealPlanList (HttpServletResponse response, @RequestParam("id") String id) {
    	
    	try{
    		
    		SearchPlanDto param = new SearchPlanDto();
    		param.setSearchCompany(Integer.parseInt(id));
    		List<PlanEntity> list = planService.getListWithoutLimit(param);
    		
    		return CustomApiResponse.success(ResponseCode.OK, list);
    		
        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
    }
    
    /**
     * 등록 화면 진입
     * @param request
     * @param model
     * @return
     */
    @GetMapping( value = "/dealInsert" )
    public String dealInsert( HttpServletRequest request, Model model ) {
    	
    	log.info("등록 화면 진입");
    	
    	/* 입점사 목록 불러오기 */
		CompanyListSearchDto param = new CompanyListSearchDto();
		param.setSearchUseYn(1);
		List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(param);
		model.addAttribute("companyList", companyList);
		
		return "pages/svc/mainbanner/dealForm";
    }
    
    /**
	 * 수정 화면 진입(추천딜)
	 * @param request
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping( value = "/dealUpdate/{id}")
	public String dealUpdate ( HttpServletRequest request, Model model, @PathVariable("id") Long id) {
		
		log.info("수정 화면 진입");
		
		/* 입점사 목록 불러오기 */
		CompanyListSearchDto param = new CompanyListSearchDto();
		param.setSearchUseYn(1);
		List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(param);
		model.addAttribute("companyList", companyList);
		
		MainDealMstEntity entity = dealBannerService.mainDealMst(id);
		if ( entity != null ) {
			MainDealBannerDto dto = new MainDealBannerDto();
			dto.setMain_deal(entity.getId());
			List<MainDealBannerEntity> dealBannerList = dealBannerService.mainPageDealBanner(dto);
			entity.setList(dealBannerList);
		}
		
		model.addAttribute("entity", entity);
		
		return "pages/svc/mainbanner/dealForm";
	}
	
	/**
	 * 등록 / 수정 프로세스
	 * @param request
	 * @param obj
	 * @return
	 */
	@PostMapping( value = "/dealInsupdProc" )
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> insupdProc( MultipartHttpServletRequest request, @RequestParam Map<String, Object> obj ) {
		
		try {
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			int id = 0;
			if ( !StringUtils.isEmpty(obj.get("id").toString()) ) {
				id = Integer.parseInt(obj.get("id").toString());
			}
			
			/* 세션 user get */
			HttpSession session = request.getSession();
			AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");
			
			MainDealMstEntity mainDealMstEntity = dealBannerService.mainDealMst(id);
			if ( mainDealMstEntity == null ) {
				mainDealMstEntity = new MainDealMstEntity();
			}
			
			mainDealMstEntity.setType(MapUtils.getString(obj, "type"));
			
			/* 게시 기간 */
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			String startDate = obj.get("std").toString() + " " + obj.get("stdHm").toString();
			String endDate = obj.get("etd").toString() + " " + obj.get("etdHm").toString();
			LocalDateTime std = LocalDateTime.parse(startDate, formatter);
			LocalDateTime etd = LocalDateTime.parse(endDate, formatter);
			
			mainDealMstEntity.setStart_date(std);
			mainDealMstEntity.setEnd_date(etd);
			
			mainDealMstEntity.setTitle(MapUtils.getString(obj, "title", ""));
			
			/* 타임딜 */
			if ( StringUtils.equals("08", MapUtils.getString(obj, "type")) ) {
				mainDealMstEntity.setDim_title(MapUtils.getString(obj, "dim_title"));
				mainDealMstEntity.setBtn_type(MapUtils.getString(obj, "btnType"));
				mainDealMstEntity.setExposure_time(MapUtils.getIntValue(obj, "exposure_time"));
				
				/* 파일 upload */
				MultipartFile pcFile = request.getFile("imagePc");
				if ( !StringUtils.isEmpty(pcFile.getOriginalFilename()) ) {
					String imagePc = fileService.FileUpload("banner", pcFile);
					log.info("PC 이미지 업로드 결과 : {}", imagePc);
					mainDealMstEntity.setPop_image(imagePc);
				}
			}
			
			mainDealMstEntity.setLink_type( MapUtils.getString(obj, "link_type", "") );
			mainDealMstEntity.setLink_url( MapUtils.getString(obj, "link_url", "") );
			
			mainDealMstEntity.setCreate_id(adminInfo.getId());
			mainDealMstEntity.setModified_id(adminInfo.getId());
			
			
			String itemArrayJson = (String) obj.get("itemArray");
			ObjectMapper objectMapper = new ObjectMapper();
			 List<Map<String, Object>> itemList = objectMapper.readValue(
			            itemArrayJson,
			            new TypeReference<List<Map<String, Object>>>() {}
			);
			 
			 
			
			/* id가 0보다 크면 수정 */
			if ( id > 0 ) {
				
				dealBannerService.updateMainDealMst(mainDealMstEntity);
				
				for (Map<String, Object> item : itemList) {
					
					String mainDealId = (String) item.get("mainDealId");
					String type = (String) item.get("type");
					String planContent = (String) item.get("planContent");
					String planMno = (String) item.get("planMno");
					String planId = (String) item.get("planId");
					String linkUrl = (String) item.get("linkUrl");
					Integer orderNo = (Integer) item.get("orderNo");
					
					MainDealBannerEntity mainDealBannerEntity = dealBannerService.mainDealBanner(Integer.parseInt(mainDealId));
					/* 객체 세팅 후 update */
					if ( mainDealBannerEntity != null ) {
						mainDealBannerEntity.setMain_deal(id);
						mainDealBannerEntity.setType(type);
						mainDealBannerEntity.setPlan_content(planContent);
						mainDealBannerEntity.setPlan_mno(Integer.parseInt(planMno));
						mainDealBannerEntity.setPlan_id(Integer.parseInt(planId));
						mainDealBannerEntity.setLink_url(linkUrl);
						mainDealBannerEntity.setOrder_no(orderNo);
						mainDealBannerEntity.setModified_id(adminInfo.getId());
						
						dealBannerService.updateMainDealBanner(mainDealBannerEntity);
					/* 추가된 요금제 */
					} else {
						mainDealBannerEntity = new MainDealBannerEntity();
						mainDealBannerEntity.setMain_deal(id);
						mainDealBannerEntity.setType(type);
						mainDealBannerEntity.setPlan_content(planContent);
						mainDealBannerEntity.setPlan_mno(Integer.parseInt(planMno));
						mainDealBannerEntity.setPlan_id(Integer.parseInt(planId));
						mainDealBannerEntity.setLink_url(linkUrl);
						mainDealBannerEntity.setOrder_no(orderNo);
						mainDealBannerEntity.setModified_id(adminInfo.getId());
						
						dealBannerService.insertMainDealBanner(mainDealBannerEntity);
					}
				}
			} else {
				
				dealBannerService.insertMainDealMst(mainDealMstEntity);
				for (Map<String, Object> item : itemList) {
					
					MainDealBannerEntity mainDealBannerEntity = new MainDealBannerEntity();
					String type = (String) item.get("type");
					String planContent = (String) item.get("planContent");
					String planMno = (String) item.get("planMno");
					String planId = (String) item.get("planId");
					String linkUrl = (String) item.get("linkUrl");
					Integer orderNo = (Integer) item.get("orderNo");
					
					/* 객체 세팅 후 update */
					if ( mainDealBannerEntity != null ) {
						mainDealBannerEntity.setMain_deal(mainDealMstEntity.getId());
						mainDealBannerEntity.setType(type);
						mainDealBannerEntity.setPlan_content(planContent);
						mainDealBannerEntity.setPlan_mno(Integer.parseInt(planMno));
						mainDealBannerEntity.setPlan_id(Integer.parseInt(planId));
						mainDealBannerEntity.setLink_url(linkUrl);
						mainDealBannerEntity.setOrder_no(orderNo);
						mainDealBannerEntity.setModified_id(adminInfo.getId());
						
						dealBannerService.insertMainDealBanner(mainDealBannerEntity);
					}
				}
			}
			
			return CustomApiResponse.success(ResponseCode.OK, result);
			
        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
	}
	
	/**
	 * 추천딜 배너 삭제, 추천딜 배너 연동 요금제 삭제
	 * @param response
	 * @param delId
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@DeleteMapping( value = "/dealDelete" )
	public ResponseEntity<CustomApiResponse<String>> dealDelete( HttpServletResponse response, @RequestParam("delId") String delId ) throws IOException {
    	
    	try {
    		dealBannerService.deleteMainDealMst(Integer.parseInt(delId));
    		dealBannerService.deleteMainDealBannerMstId(Integer.parseInt(delId));
    		 return CustomApiResponse.success(ResponseCode.OK, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
        }
    }
	
	/**
	 * 딜배너 요금제 삭제
	 * @param response
	 * @param delId
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@DeleteMapping( value = "/dealPlanDelete" )
	public ResponseEntity<CustomApiResponse<String>> dealPlanDelete( HttpServletResponse response, @RequestParam("delId") String delId ) throws IOException {
    	
    	try {
    		dealBannerService.deleteMainDealBanner(Integer.parseInt(delId));
    		 return CustomApiResponse.success(ResponseCode.OK, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
        }
    }
	
	@GetMapping( value = "/timeList")
    public String timeList( HttpServletRequest request, Model model ) {

    	log.info("메인배너(타임딜) 리스트 진입");
    	
    	return "pages/svc/mainbanner/timeList";
    }
	
	/**
     * 검색결과
     * @param request
     * @param param
     * @return
     * @throws IOException
     */
    @PostMapping( value = "/timeListProc" )
    public ResponseEntity<CustomApiResponse<List<MainDealMstEntity>>> timeListProc ( HttpServletRequest request, @RequestBody MainDealMstDto param ) throws IOException { 
    	
    	try{
			
            long resultcnt = dealBannerService.listCount(param);
            List<MainDealMstEntity> resultList = new ArrayList<MainDealMstEntity>();
            if(resultcnt > 0 ) {
            	resultList = dealBannerService.mainDealMstList(param);
            	
            	/* 타임딜배너 하위 요금제 검색 */
            	MainDealBannerDto dto = new MainDealBannerDto();
            	for ( MainDealMstEntity temp : resultList ) {
            		dto.setMain_deal(temp.getId());
            		List<MainDealBannerEntity> dealBannerList = dealBannerService.mainPageDealBanner(dto);
            		temp.setList(dealBannerList);
            	}
            }
            
            return CustomApiResponse.success(ResponseCode.OK, resultcnt, resultList);

        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
    }
    
    /**
     * 등록 화면 진입
     * @param request
     * @param model
     * @return
     */
    @GetMapping( value = "/timeInsert" )
    public String timeInsert( HttpServletRequest request, Model model ) {
    	
    	log.info("등록 화면 진입");
    	
    	/* 입점사 목록 불러오기 */
		CompanyListSearchDto param = new CompanyListSearchDto();
		param.setSearchUseYn(1);
		List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(param);
		model.addAttribute("companyList", companyList);
		
		return "pages/svc/mainbanner/timeForm";
    }
    
    /**
	 * 수정 화면 진입(타임딜)
	 * @param request
	 * @param model
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping( value = "/timeUpdate/{id}")
	public String timeUpdate ( HttpServletRequest request, Model model, @PathVariable("id") Long id) {
		
		log.info("수정 화면 진입");
		
		/* 입점사 목록 불러오기 */
		CompanyListSearchDto param = new CompanyListSearchDto();
		param.setSearchUseYn(1);
		List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(param);
		model.addAttribute("companyList", companyList);
		
		MainDealMstEntity entity = dealBannerService.mainDealMst(id);
		if ( entity != null ) {
			MainDealBannerDto dto = new MainDealBannerDto();
			dto.setMain_deal(entity.getId());
			List<MainDealBannerEntity> dealBannerList = dealBannerService.mainPageDealBanner(dto);
			entity.setList(dealBannerList);
			
			if ( !StringUtils.isEmpty(entity.getPop_image()) ) {
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					Map<String, Object> map = mapper.readValue(entity.getPop_image(), Map.class);
					entity.setPop_image(map.get("fileUrl").toString());
					entity.setOrgImagePc(map.get("orgFileNm").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		model.addAttribute("entity", entity);
		
		return "pages/svc/mainbanner/timeForm";
	}
}
