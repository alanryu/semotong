package kr.co.ucomp.web.svc.banner.contoroller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import kr.co.ucomp.web.svc.banner.dto.BannerPlanSearchDTO;
import kr.co.ucomp.web.svc.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.svc.banner.entity.BannerEntity;
import kr.co.ucomp.web.svc.banner.entity.BannerPlanEntity;
import kr.co.ucomp.web.svc.banner.service.BannerPlanService;
import kr.co.ucomp.web.svc.banner.service.BannerService;
import lombok.extern.slf4j.Slf4j;


/**
 *
 * @author 조일근
 * @since 2024.12.20
 * @version v1.0
 */

@Controller
@RequestMapping(value = "/svc/mainbanner")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class BannerController {

	@Autowired
	private BannerService bannerService;
	@Autowired
	private FileService fileService;
	@Autowired
    private CompanyListService companyListService;
	@Autowired
	private PlanService planService;
	@Autowired
	private BannerPlanService bannerPlanService;

	/**
	 * 메인 배너관리
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String list ( HttpServletRequest request, Model model ) {
		
		log.info("메인배너 리스트 진입");
				
		return "pages/svc/mainbanner/list";
	}
	
	/**
	 * 검색 결과
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/listProc")
	public ResponseEntity<CustomApiResponse<List<BannerEntity>>> listProc ( HttpServletRequest request, @RequestBody BannerSearchDto param ) throws IOException { 
		
		try{
        	/* json parsing */
			ObjectMapper mapper = new ObjectMapper();
			
            long resultcnt = bannerService.listCount(param);
            List<BannerEntity> resultList = new ArrayList<BannerEntity>();
            if(resultcnt > 0 ) {
            	resultList = bannerService.list(param);
            	
            	/* 이미지 url get */
            	for ( BannerEntity temp : resultList ) {
            		if ( !StringUtils.isEmpty(temp.getImagePc()) && !StringUtils.isEmpty(temp.getImageMo()) ) {
            			Map<String, Object> map = mapper.readValue(temp.getImagePc(), Map.class);
            			temp.setImagePc(map.get("fileUrl").toString());
            			map = mapper.readValue(temp.getImageMo(), Map.class);
            			temp.setImageMo(map.get("fileUrl").toString());
            		}
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
	@GetMapping("/insert")
	public String insert ( HttpServletRequest request, Model model ) {
		
		log.info("등록 화면 진입");
		
		/* 입점사 목록 불러오기 */
		CompanyListSearchDto param = new CompanyListSearchDto();
		param.setSearchUseYn(1);
		List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(param);
		model.addAttribute("companyList", companyList);
		
		return "pages/svc/mainbanner/form";
	}
	
	/**
	 * 수정 화면 진입
	 * @param request
	 * @param model
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/update/{id}")
	public String update ( HttpServletRequest request, Model model, @PathVariable("id") Long id) {
		
		log.info("수정 화면 진입");
		
		String returnStr = "pages/svc/mainbanner/form";
		
		BannerEntity entity = bannerService.getDetail(id);
		
		String imagePcJson = entity.getImagePc();
		String imageMoJson = entity.getImageMo();
		if ( !StringUtils.isEmpty(imagePcJson) ) {
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				Map<String, Object> map = mapper.readValue(imagePcJson, Map.class);
				entity.setImagePc(map.get("fileUrl").toString());
				entity.setOrgImagePc(map.get("orgFileNm").toString());
				map = mapper.readValue(imageMoJson, Map.class);
				entity.setImageMo(map.get("fileUrl").toString());
				entity.setOrgImageMo(map.get("orgFileNm").toString());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		model.addAttribute("entity", entity);
		
		/* 결합 / 챗봇 배너 수정 */
		if ( StringUtils.equals("05", entity.getType()) || StringUtils.equals("06", entity.getType()) ) {
			
			returnStr = "pages/svc/mainbanner/cbForm";
		} else {
			/* 요금제배너일경우 등록된 요금제 불러오기 */
			if ( StringUtils.equals("02", entity.getType()) ) {
				BannerPlanSearchDTO dto = new BannerPlanSearchDTO();
				dto.setSearchBannerId(entity.getId());
				List<BannerPlanEntity> bannerPlanList = bannerPlanService.bannerPlanList(dto);
				model.addAttribute("bannerPlanList", bannerPlanList);
			}
			
			/* 입점사 목록 불러오기 */
			CompanyListSearchDto param = new CompanyListSearchDto();
			param.setSearchUseYn(1);
			List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(param);
			model.addAttribute("companyList", companyList);
		}
		
		return returnStr;
	}
	
	/**
	 * 등록 / 수정 프로세스
	 * @param request
	 * @param obj
	 * @return
	 */
	@PostMapping("/insupdProc")
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> insupdProc( MultipartHttpServletRequest request, @RequestParam Map<String, Object> obj ) {
	
		try {
        	
			Map<String, Object> resultList = new HashMap<String, Object>();
			
			int id = 0;
			if ( !StringUtils.isEmpty(obj.get("id").toString()) ) {
				id = Integer.parseInt(obj.get("id").toString());
			}
			
			/* 등록/수정 객체 setting */
			BannerEntity entity = bannerService.getDetail(id);		// 기존데이터 get
			if ( entity == null ) {
				entity = new BannerEntity();
				entity.setType(obj.get("type").toString());
			}
			entity.setBannerName(obj.get("bannerName").toString());	// 항목명
			entity.setStatus(obj.get("status").toString());			// 게시기간
			/* 기간설정일경우 기간 세팅 */
			if ( StringUtils.equals("TERM", obj.get("status").toString()) ) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				String startDate = obj.get("std").toString() + " " + obj.get("stdHm").toString();
				String endDate = obj.get("etd").toString() + " " + obj.get("etdHm").toString();
				LocalDateTime std = LocalDateTime.parse(startDate, formatter);
				LocalDateTime etd = LocalDateTime.parse(endDate, formatter);
				entity.setStartDate(std);
				entity.setEndDate(etd);
			} else {
				entity.setStartDate(null);
				entity.setEndDate(null);
			}
			entity.setBannerAlt(obj.get("bannerAlt").toString());
			entity.setUrl(obj.get("url").toString());
			
			
			
			entity.setLogoColor(obj.get("logoColor") == null ?"" : obj.get("logoColor").toString());
			entity.setBgColor(obj.get("bgColor") == null ?"" : obj.get("bgColor").toString());
			
			
			/* 세션 user get */
			HttpSession session = request.getSession();
			AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");
			entity.setCreateId(adminInfo.getId());
			
			/* 파일 upload */
			/* PC용 이미지 */
			MultipartFile pcFile = request.getFile("imagePc");
			if ( !StringUtils.isEmpty(pcFile.getOriginalFilename()) ) {
				String imagePc = fileService.FileUpload("banner", pcFile);
				log.info("PC 이미지 업로드 결과 : {}", imagePc);
				entity.setImagePc(imagePc);
			}
			
			/* 모바일용 이미지 */
			MultipartFile moFile = request.getFile("imageMo");
			if ( !StringUtils.isEmpty(moFile.getOriginalFilename()) ) {
			String imageMo = fileService.FileUpload("banner", moFile);
				log.info("PC 이미지 업로드 결과 : {}", imageMo);
				entity.setImageMo(imageMo);
			}
			
			/* 수정 */
			if ( id > 0 ) {
				entity.setId(id);
				long resStat = bannerService.update(entity);
				
				/* 요금제 소개 배너일 경우 요금제 등록/수정 */
				if ( StringUtils.equals("02", entity.getType()) ) {
					
					String bannerPlanList = obj.get("bannerPlanListArr").toString();
					String planList = obj.get("planListArr").toString();
					String planChks = obj.get("planChkArr").toString();
					String planOrder = obj.get("planOrderArr").toString();
					
					BannerPlanSearchDTO bannerPlanSearchDTO = new BannerPlanSearchDTO();
					int idx = 0;
					for ( String str : bannerPlanList.split(",") ) {
						/* 수정 */
						if ( !StringUtils.equals("0", str) ) {
							bannerPlanSearchDTO.setSearchId(Integer.parseInt(str));
							BannerPlanEntity bannerPlanEntity = bannerPlanService.bannerPlan(bannerPlanSearchDTO);
							bannerPlanEntity.setUseYn(planChks.split(",")[idx]);
							bannerPlanEntity.setOrderNo(Integer.parseInt(planOrder.split(",")[idx]));
							
							bannerPlanService.update(bannerPlanEntity);
						/* 등록 */
						} else {
							BannerPlanEntity bannerPlanEntity = new BannerPlanEntity();
							bannerPlanEntity.setBannerId(entity.getId());
							bannerPlanEntity.setPlanId(Integer.parseInt(planList.split(",")[idx]));
							bannerPlanEntity.setUseYn(planChks.split(",")[idx]);
							bannerPlanEntity.setOrderNo(Integer.parseInt(planOrder.split(",")[idx]));
							bannerPlanService.create(bannerPlanEntity);
						}
						
						idx++;
					}
				}
				
				if ( resStat > 0 ) {
					return CustomApiResponse.success(ResponseCode.OK, resultList);
				} else {
					return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
				}
			/* 등록 */
			} else {
				long resStat = bannerService.create(entity);
				
				/* 요금제 소개 배너일 경우 요금제 등록 */
				if ( StringUtils.equals("02", entity.getType()) ) {
					
					String planList = obj.get("planListArr").toString();
					String planChks = obj.get("planChkArr").toString();
					
					if ( !StringUtils.equals("", planList) && !StringUtils.equals("", planChks) ) {
						int idx = 0;
						for ( String str : planList.split(",") ) {
							BannerPlanEntity bannerPlanEntity = new BannerPlanEntity();
							bannerPlanEntity.setBannerId(entity.getId());
							bannerPlanEntity.setPlanId(Integer.parseInt(str));
							bannerPlanEntity.setUseYn(planChks.split(",")[idx]);
							bannerPlanService.create(bannerPlanEntity);
							idx++;
						}
					}
				}
				
				if ( resStat > 0 ) {
					return CustomApiResponse.success(ResponseCode.OK, resultList);
				} else {
					return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
				}
			}
        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
	}
	
	/**
	 * row 삭제
	 * @param response
	 * @param delId
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
    @DeleteMapping(value = "/delete")
    public ResponseEntity<CustomApiResponse<String>> delete( HttpServletResponse response, @RequestParam("delId") String delId ) throws IOException {
    	
    	try {
    		
    		BannerEntity entity = bannerService.getDetail(Integer.parseInt(delId));
    		
    		/* 변경 할 데이터가 노출상태인지 확인 */
			boolean flag = false;
			if ( StringUtils.equals("ALWAYS", entity.getStatus()) ) {
				flag = true;
			} else {
				LocalDateTime now = LocalDateTime.now();
				if ( entity.getEndDate().isAfter(now) && entity.getStartDate().isBefore(now) ) {
					flag = true;
				}
			}
			
			/* 상태가 노출중인 배너의 사용여부 컨트롤시 */
			if ( flag ) {
				/* 사용중인 배너 갯수 체크 (자신을 제외한) */
    			int useCnt = bannerService.getUseCnt(entity);
    			
    			/* 노출상태이고 사용으로 체크된 배너가 없으면 업데이트할 수 없다. */
				if ( useCnt == 0 ) {
					return CustomApiResponse.success(ResponseCode.OK, "del fail");
				} else {
					bannerService.delete(Integer.parseInt(delId));
					return CustomApiResponse.success(ResponseCode.OK, "del ok");
				}
			} else {
				bannerService.delete(Integer.parseInt(delId));
				return CustomApiResponse.success(ResponseCode.OK, "del ok");
			}
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
        }
    }
	
	/**
	 * 사용/미사용 업데이트
	 * @param response
	 * @param id
	 * @param useYn
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
    @PostMapping(value = "/useYn")
    public ResponseEntity<CustomApiResponse<String>> useYn( HttpServletResponse response, @RequestParam("id") String id, @RequestParam("useYn") String useYn ) throws IOException {
    	
    	try {
    		
    		BannerEntity entity = bannerService.getDetail(Integer.parseInt(id));
    		
    		if ( entity != null ) {
    			
    			/* 변경 할 데이터가 노출상태인지 확인 */
    			boolean flag = false;
    			if ( StringUtils.equals("ALWAYS", entity.getStatus()) ) {
    				flag = true;
    			} else {
    				LocalDateTime now = LocalDateTime.now();
    				if ( entity.getEndDate().isAfter(now) && entity.getStartDate().isBefore(now) ) {
    					flag = true;
    				}
    			}
    			
    			/* 상태가 노출중인 배너의 사용여부 컨트롤시 */
    			if ( flag ) {
    				/* 사용중인 배너 갯수 체크 (자신을 제외한) */
        			int useCnt = bannerService.getUseCnt(entity);
        			
        			/* 미사용 */
        			if ( StringUtils.equals("N", useYn) ) {
        				/* 노출상태이고 사용으로 체크된 배너가 없으면 업데이트할 수 없다. */
        				if ( useCnt == 0 ) {
        					return CustomApiResponse.success(ResponseCode.OK, "01");
        				} else {
        					entity.setUseYn(useYn);
        		    		bannerService.update(entity);
        				}
        			} else if ( StringUtils.equals("Y", useYn) ) {
        				// 사용 가능 개수 제한 99 로 하여 제한을 없앰 20260605
        				/* 요금제(02), 이벤트 배너(04), 결합배너(05), 챗봇배너(06)는 3개 나머지는 2개 */
        				int stndCnt = 99;
        				if ( StringUtils.equals("02", entity.getType()) || 
        					 StringUtils.equals("04", entity.getType()) || 
        					 StringUtils.equals("05", entity.getType()) || 
        					 StringUtils.equals("06", entity.getType()) ) {
        					stndCnt = 99;
        				}
        				
        				/* 노출상태이고 사용으로 체크된 배너가 기준갯수이상이면 업데이트할 수 없다. */
        				if ( useCnt >= stndCnt ) {
        					return CustomApiResponse.success(ResponseCode.OK, "02");
        				} else {
            				entity.setUseYn(useYn);
        		    		bannerService.update(entity);
        				}
        			}
    			} else {
    				entity.setUseYn(useYn);
		    		bannerService.update(entity);
    			}
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK, "03");
    		
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
        }
    }
	
	/**
	 * 요금제 검색
	 * @param dto
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/planlist" )
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> planlist ( @RequestBody SearchPlanDto dto ) {
		
		try {
			
			List<PlanEntity> list = new ArrayList<PlanEntity>();
			long totalCnt = planService.getListCount(dto);
			if ( totalCnt > 0 ) {
				list = planService.getListWithoutLimit(dto);
			}
			
			return CustomApiResponse.success(ResponseCode.OK, totalCnt, list);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
		}
	}
	
	/**
	 * 순서 정렬 (요금제 소개 배너)
	 * @param params
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/listorder" )
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> listorder ( @RequestParam("orderIds") String orderIds ) {
		
		try {
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			if ( !StringUtils.isEmpty(orderIds) ) {
				int sort = 1;
				BannerEntity entity = new BannerEntity();
				for ( String str : orderIds.split(",") ) {
					entity = bannerService.getDetail(Integer.parseInt(str));
					entity.setSort(sort);
					bannerService.update(entity);
					sort++;
				}
			}
			
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
		}
	}
	
	/**
	 * 결합 / 챗봇 배너 등록화면 진입
	 * @param rquest
	 * @param model
	 * @return
	 */
	@GetMapping( value = "/cbInsert" )
	public String cbInsert ( HttpServletRequest rquest, Model model ) {
		
		log.info("결합 / 챗봇 배너 등록화면 진입");
		
		return "pages/svc/mainbanner/cbForm";
	}
	
	/**
	 * 요금제 배너 상품 삭제
	 * @param response
	 * @param delId
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@DeleteMapping( value = "/planDelete" )
	public ResponseEntity<CustomApiResponse<String>> planDelete( HttpServletResponse response, @RequestParam("delId") String delId ) throws IOException {
    	
    	try {
    		
    		String delArr[] = delId.split(",");
    		
    		for ( String str : delArr ) {
    			bannerPlanService.delete(Integer.parseInt(str));
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
        }
    }
	
	
	
	
	
	
	/**
	 * 등록 화면 진입
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/insert2")
	public String insert2 ( HttpServletRequest request, Model model ) {
		
		log.info("빅배너 등록 화면 진입");
		
		/* 입점사 목록 불러오기 */
		CompanyListSearchDto param = new CompanyListSearchDto();
		param.setSearchUseYn(1);
		List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(param);
		model.addAttribute("companyList", companyList);
		
		return "pages/svc/mainbanner/form2";
	}
	
	
	
	/**
	 * 수정 화면 진입
	 * @param request
	 * @param model
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/update2/{id}")
	public String update2 ( HttpServletRequest request, Model model, @PathVariable("id") Long id) {
		
		log.info("수정 화면 진입");
		
		String returnStr = "pages/svc/mainbanner/form2";
		
		BannerEntity entity = bannerService.getDetail(id);
		
		String imagePcJson = entity.getImagePc();
		String imageMoJson = entity.getImageMo();
		if ( !StringUtils.isEmpty(imagePcJson) ) {
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				Map<String, Object> map = mapper.readValue(imagePcJson, Map.class);
				entity.setImagePc(map.get("fileUrl").toString());
				entity.setOrgImagePc(map.get("orgFileNm").toString());
				map = mapper.readValue(imageMoJson, Map.class);
				entity.setImageMo(map.get("fileUrl").toString());
				entity.setOrgImageMo(map.get("orgFileNm").toString());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		model.addAttribute("entity", entity);
		
	
		
		return returnStr;
	}
	
	
}
