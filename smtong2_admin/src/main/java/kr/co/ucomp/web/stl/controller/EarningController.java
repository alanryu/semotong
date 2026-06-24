package kr.co.ucomp.web.stl.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import kr.co.ucomp.web.stl.dto.EarningSearchDto;
import kr.co.ucomp.web.stl.entity.EarningEntity;
import kr.co.ucomp.web.stl.entity.EarningListEntity;
import kr.co.ucomp.web.stl.service.EarningService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/stl/earning") 
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SETTLE_MNG') and @permissionChecker.canAccessByPlanReq(authentication)")
public class EarningController {

	@Autowired 
	private CompanyListService companyListService;
	
	@Autowired
	private EarningService earningService;
	
	@Autowired
	private FileService fileService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	/**
	 * 실적관리 리스트
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping( value = "/list")
    public String list( HttpServletRequest request, Model model ) {

    	log.info("실적관리 리스트 진입");
    	
    	HttpSession session = request.getSession();
		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
		
		/* 입점사 불러오기 */
    	CompanyListSearchDto searchRequest = new CompanyListSearchDto();
		searchRequest.setSearchUseYn(1);
		if ( !StringUtils.equals("ADMIN", loginadminInfo.getAuthType()) && !StringUtils.equals("MANAGE", loginadminInfo.getAuthType()) ) {
			searchRequest.setSearchCompanyCode(Long.parseLong(loginadminInfo.getCompanyCode()));
		}
    	
    	
		List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
		model.addAttribute("compnyList", compnyList);
    	
    	return "pages/stl/earning/list";
    }
	
	/**
	 * 실적관리 검색 결과
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@PostMapping( value = "/listProc" )
	public ResponseEntity<CustomApiResponse<List<EarningEntity>>> listProc ( HttpServletRequest request, @RequestBody EarningSearchDto param ) throws IOException { 
		
		try{
			
			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
			
			if ( !StringUtils.equals("ADMIN", loginadminInfo.getAuthType()) && !StringUtils.equals("MANAGE", loginadminInfo.getAuthType()) ) {
				param.setSearchCompanyId(Long.parseLong(loginadminInfo.getCompanyCode()));
			}
			
        	/* json parsing */
			ObjectMapper mapper = new ObjectMapper();
			
            long resultcnt = earningService.listCount(param);
            List<EarningEntity> resultList = new ArrayList<EarningEntity>();
            if(resultcnt > 0 ) {
            	resultList = earningService.list(param);
            }
            
            return CustomApiResponse.success(ResponseCode.OK, resultcnt, resultList);

        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
	}
	
	/**
	 * 이전 데이터 존재여부 판단
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@PostMapping( value = "/existence" )
	public ResponseEntity<CustomApiResponse<EarningEntity>> existence ( HttpServletRequest request, @RequestBody EarningSearchDto param ) throws IOException { 
		
		try{
			EarningEntity entity = earningService.selectEarning(param);
            
            return CustomApiResponse.success(ResponseCode.OK, entity);

        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
	}
	
	
	/**
	 * 실적 업로드
	 * @param request
	 * @param uploadMapData
	 * @param hostId
	 * @param generateDate
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = "/uploadExcel" )
	public ResponseEntity<CustomApiResponse<Map<String,Object>>> uploadExcel( HttpServletRequest request, @RequestPart(value="uploadMapData",required = false) MultipartFile uploadMapData,
			@RequestPart("hostId") Integer hostId, @RequestPart("generateDate") String generateDate
	) throws IOException {
		
		Map<String,Object> uploadRes = new HashMap<String,Object>();
		try{
			
			try {
				
				HttpSession session = request.getSession();
				AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
				
				EarningSearchDto param = new EarningSearchDto();
				param.setSearchCompanyId(hostId);
				param.setSearchGenerateDate(generateDate);
				EarningEntity entity = earningService.selectEarning(param);
				
				/* 기존실적 업데이트 */
				if ( entity != null && entity.getId() > 0 ) {
					
					/* 기존 데이터 삭제 */
					earningService.deleteEarningList(entity.getId());
					
					/* 실적관리 업로드 */
					earningService.excelUpload(uploadMapData, loginadminInfo, entity.getId());
				/* 실적 신규 등록 */
				} else {
					
					/* 입점사 업로드 */
					entity = new EarningEntity();
					entity.setCompanyId(hostId);
					entity.setGenerateDate(generateDate);
					entity.setCreateId(loginadminInfo.getId());
					
					int resultVal = earningService.createEarning(entity);
					
					/* 실적관리 업로드 */
					if ( entity.getId() > 0 ) {
						
						earningService.excelUpload(uploadMapData, loginadminInfo, entity.getId());
					}
				}
					
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			return CustomApiResponse.success(ResponseCode.OK, uploadRes);

		} catch (IllegalArgumentException e) {

			return CustomApiResponse.error(ResponseCode.BAD_REQUEST);

		} catch (Exception e) {

			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}
	}
	
	/**
	 * 실적관리 상세
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping( value = "/detail" )
	public String detail( HttpServletRequest request, Model model, @RequestParam("id") String id ) {

    	log.info("실적관리 상세 리스트 진입");
    	
    	EarningSearchDto param = new EarningSearchDto();
    	param.setSearchEarningId(Long.parseLong(id));
    	EarningEntity entity = earningService.selectEarning(param);
    	
    	model.addAttribute("entity", entity);
    	return "pages/stl/earning/detail";
    }
	
	/**
	 * 실적관리 상세 리스트
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@PostMapping( value = "/detailProc" )
	public ResponseEntity<CustomApiResponse<List<EarningListEntity>>> detailProc ( HttpServletRequest request, @RequestBody EarningSearchDto param ) throws IOException { 
		
		try{
        	/* json parsing */
			ObjectMapper mapper = new ObjectMapper();
			
            long resultcnt = earningService.detailListCount(param);
            List<EarningListEntity> resultList = new ArrayList<EarningListEntity>();
            if(resultcnt > 0 ) {
            	resultList = earningService.detailList(param);
            }
            
            return CustomApiResponse.success(ResponseCode.OK, resultcnt, resultList);

        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
	}
	
	/**
	 * 실적관리 상세 엑셀 다운로드
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping( value = "/excelDownload")
	public ResponseEntity<byte[]> excelDownload(@RequestBody EarningSearchDto param) throws Exception {
    	
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>(); 
    	List<EarningListEntity> list =  earningService.detailListWithoutLimit(param);
    	for (EarningListEntity itm  :  list) {
        	Map<String, Object> data = new LinkedHashMap<String, Object>();
        	
        	data.put("agencyName", itm.getAgencyName());
        	data.put("appDate", itm.getAppDate());
        	data.put("openDate", itm.getOpenDate());
        	data.put("mno", itm.getMno());
        	data.put("contractNum", itm.getContractNum());
        	data.put("accType", itm.getAccType());
        	data.put("phoneNum", itm.getPhoneNum());
        	data.put("accName", itm.getAccName());
        	data.put("planName", itm.getPlanName());
        	data.put("status", itm.getStatus());
        	
        	dataList.add(data);    		
    	}

    	
    	// 엑셀 헤더 설정
    	String[] headers = {"신청대리점", "신청일", "개통일", "통신망", "계약번호", "가입유형", "가입자 번호", "가입자", "요금제(상품)", "상태"};
    	
        byte[] excelData = fileService.getExcelData(headers,dataList);

        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=userList.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
}
