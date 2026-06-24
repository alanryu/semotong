package kr.co.ucomp.web.point.controller;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.ucomp.common.encrypt.DaouEncrypt;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.restapi.entity.RestApiTokenMngEntity;
import kr.co.ucomp.common.restapi.mapper.RestApiMapper;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.entity.FileMngEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.csm.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.entity.SemotongReviewEntity;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.service.UserService;
import kr.co.ucomp.web.order.service.DailySequenceService;
import kr.co.ucomp.web.point.constant.DaouApiConstant;
import kr.co.ucomp.web.point.dto.PointAccDTO;
import kr.co.ucomp.web.point.dto.PointHistoryDTO;
import kr.co.ucomp.web.point.entity.PointAccEntity;
import kr.co.ucomp.web.point.entity.PointCashEntity;
import kr.co.ucomp.web.point.entity.PointHistoryEntity;
import kr.co.ucomp.web.point.entity.PointNpayEntity;
import kr.co.ucomp.web.point.service.PointAccService;
import kr.co.ucomp.web.point.service.PointCashService;
import kr.co.ucomp.web.point.service.PointHistoryService;
import kr.co.ucomp.web.point.service.PointNpayService;
import kr.co.ucomp.web.svc.event.entity.EvtEntity;

/**
 * 포인트 지급/차감 관리
 * @author sancho
 * @since 2025.03.09
 */
@Controller
@RequestMapping("/point/history")
@PreAuthorize("hasAnyAuthority('ALL', 'POINT_MNG')")
public class PointHistoryController {
	
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
	
	
	
	
	/**
	 * Point 관리 화면 이동
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String  historyList( HttpServletRequest request, Model model)  {
		return "pages/point/history/list";
	}
	
	@ResponseBody
	@PostMapping("/ajaxHistoryList")
	public ResponseEntity<CustomApiResponse<List<PointHistoryEntity>>> ajaxHistoryList(HttpServletRequest request, @RequestBody PointHistoryDTO param) throws IOException {
		List<PointHistoryEntity> resultList = new ArrayList<PointHistoryEntity>(); 
		try{
			int count = pointHistoryService.getPointHistoryCount(param); 
			if(count > 0) {
				resultList = pointHistoryService.getPointHistory(param);	
			}
			return CustomApiResponse.success(ResponseCode.OK, count, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getOneToOneList: " + e.getMessage());
		}
	}
	
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown (HttpServletRequest request, @RequestBody PointHistoryDTO dto) throws Exception 
	{
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		List<PointHistoryEntity> resultList = pointHistoryService.getPointHistory(dto);
		
		for (PointHistoryEntity itm  :  resultList) {

			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("drCrName"				, itm.getDrCrName()				);
			data.put("amount"				, itm.getAmount()				);
			data.put("crPointTypeName"		, itm.getCrPointTypeName()		);
			data.put("drPointTypeName"		, itm.getDrPointTypeName()		);
			data.put("username"				, itm.getUsername()				);
			data.put("kakaoUserId"			, itm.getKakaoUserId()			);
			data.put("memo"					, itm.getMemo()				);
			data.put("createname"			, itm.getCreateName()			);
			data.put("createDate"			, itm.getCreateDate()			);
			dataList.add(data);
		}
		
		// 엑셀 헤더 설정
		String[] headers = {"구분", "지급/차감 포인트", "사유", "사용", "회원명", "카카오ID", "메모", "담당자", "처리일"};
		
		byte[] excelData = fileService.getExcelData(headers,dataList);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}
	
	/**
	 * Detail화면, 입력화면 이동
	 * @param request
	 * @param searchId
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/edit/{searchId}")
	public String  historyDetail( 	HttpServletRequest request, @PathVariable("searchId") int searchId, Model model)  throws IOException {
		
		PointHistoryEntity record = new PointHistoryEntity();
		
		try{
			if(model.getAttribute("org.springframework.validation.BindingResult.record") != null) {
				record = (PointHistoryEntity) model.getAttribute("record");
				model.addAttribute("record", record); 
				model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
			} else {		
				//if(StringUtils.isNotBlank(searchId)) {
				if(searchId != 0) {
					//record = onetooneService.getDetail(Integer.valueOf(searchId));
					PointHistoryDTO param = new PointHistoryDTO();
					param.setSearchHistoryId(searchId);
					record = pointHistoryService.getPointHistoryById(param);
				}
				model.addAttribute("record", record); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "pages/point/history/edit";
	}
	
	/*
	 * 개별 지급/차감
	 */
	@PostMapping( value = "/historyInsert" )
	public String historyInsert (HttpServletRequest request, HttpServletResponse response
			,@Valid @ModelAttribute("recordForm") PointHistoryEntity record, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException{
	
		int insid = 0;
		
		if (bindingResult.hasErrors()) {
			// 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
			redirectAttributes.addFlashAttribute("record", record);
			return "redirect:/point/history/edit/"+insid;
		}
		try {
			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
			record.setCreateId(loginadminInfo.getId());
			record.setAdminGiftYn("Y");
			insid = pointHistoryService.insert(record);
			redirectAttributes.addFlashAttribute("procMsg", "sucess");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
		}
		
		return "redirect:/point/history/edit/"+insid;
	}
	
	
	@ResponseBody
	@PostMapping(value = "/ajaxUserList")
	public ResponseEntity<CustomApiResponse<List<PointAccEntity>>> ajaxUserList (HttpServletRequest request, @RequestBody PointAccDTO param) throws Exception 
	{
		
		//System.out.println(param.getKeyword());
		//System.out.println(param.getSearchType());
		List<PointAccEntity> resultList 		= null;
		try {
			resultList 		= pointAccService.getPointUserList(param);
			return CustomApiResponse.success(ResponseCode.OK, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventPlanList: " + e.getMessage());
		}
	}
	
	/*
	 * 일괄 지급/차감 - Excel
	 */
	@PostMapping(value="/historyListInsert")
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> historyListInsert(MultipartHttpServletRequest request, @RequestParam Map<String, Object> obj)
	{
		Map<String, Object> result = new HashMap<String, Object>();
		ResponseCode rCode = ResponseCode.OK;
		
		try {
			int insid 	= 0;
			int totcnt	= 0;
			int inscnt	= 0;
			MultipartFile pointFile = request.getFile("excelForPoint");
			if ( !StringUtils.isEmpty(pointFile.getOriginalFilename()) ) {
				
				
				//List<PointHistoryEntity> insList = parseExcelFile(pointFile);
				Map<String, Object> rtnMap = parseExcelFile(pointFile);
				if( rtnMap.get("rtnCode").equals("N") ) {
					//return CustomApiResponse.error(ResponseCode.BAD_REQUEST, (String) rtnMap.get("rtnMessage"));
					result.put("msg", (String) rtnMap.get("rtnMessage"));
					rCode = ResponseCode.ACCEPTED;
					//return CustomApiResponse.success(ResponseCode.ACCEPTED, result);
				}else {
					//rtnMap.put("rtnCode"	, "Y" );
					//rtnMap.put("rtnMessage"	, dataList );
					List<PointHistoryEntity> insList = (List<PointHistoryEntity>) rtnMap.get("rtnMessage");
					
					for(PointHistoryEntity record : insList) {
						//1.kakaoId로 계좌정보(tb_mbm_point.id) 얻어내기
						PointAccDTO accdto = new PointAccDTO();
						accdto.setSearchKakaoUserId(record.getKakaoUserId());
						PointAccEntity userAccInfo = pointAccService.getMyPoint(accdto);
						
						//2.insert 실행
						totcnt++;
						HttpSession session = request.getSession();
						AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
						record.setPointId(userAccInfo.getId());
						record.setCreateId(loginadminInfo.getId());
						record.setAdminGiftYn("Y");
						insid = pointHistoryService.insert(record);
						if(insid != 0 && insid > 0) inscnt++;
					}
					result.put("totcnt", totcnt);
					result.put("inscnt", inscnt);
					
					rCode = ResponseCode.OK;
				}
			}
			
			//return CustomApiResponse.success(ResponseCode.OK, result);
			return CustomApiResponse.success(rCode, result);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "document upload : " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "document upload : " + e.getMessage());
		}
	}
	
	public Map<String, Object> parseExcelFile(MultipartFile file) {
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		String consiYn = "Y";
		
		CommCodeSearchDto codeDrcr = new CommCodeSearchDto();
		codeDrcr.setCodeGroup("point_drcr");
		codeDrcr.setUserYn("Y");
		List<CodeEntity> drcrlist = codeService.getListCode(codeDrcr);
		
		CommCodeSearchDto codePType = new CommCodeSearchDto();
		codePType.setCodeGroup("point_type");
		codePType.setUserYn("Y");
		List<CodeEntity> typelist = codeService.getListCode(codePType);
		
		List<PointHistoryEntity> dataList = new ArrayList<>();
		
		try (InputStream inputStream = file.getInputStream();
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
			XSSFSheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue; // 첫 번째 행(헤더)은 건너뜀
				
				String kakaoUserId 	= row.getCell(0).getStringCellValue();
				String drCr 		= row.getCell(1).getStringCellValue().toUpperCase();
				
				//true 면 존재함
				boolean existsDrcr = drcrlist.stream().anyMatch(code -> code.getCode().equals(drCr));
				if(!existsDrcr) consiYn = "N";
								
				//int amount 			= (int) row.getCell(2).getNumericCellValue();
				Cell cell = row.getCell(2);
				if ( cell.getCellType() == CellType.STRING ) consiYn = "S";
				int amount = getAmount(cell);
				
				String crPointType 	= row.getCell(3).getStringCellValue().toUpperCase();
				boolean existsType = typelist.stream().anyMatch(code -> code.getCode().equals(crPointType));
				if(!existsType) consiYn = "N";
				
				String memo 		= row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "";
				
				PointHistoryEntity entity = new PointHistoryEntity();
				entity.setKakaoUserId(kakaoUserId);
				entity.setDrCr(drCr);
				entity.setCrPointType(crPointType);
				entity.setAmount(amount);
				entity.setMemo(memo);
				
				if(kakaoUserId != null && !kakaoUserId.equals("")) {
					dataList.add(entity);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(consiYn.equals("N")) {
			rtnMap.put("rtnCode"	, "N" );
			rtnMap.put("rtnMessage"	, "가이드에 맞지 않는 코드가 있습니다." );
		}else if(consiYn.equals("S")) {
			rtnMap.put("rtnCode"	, "N" );
			rtnMap.put("rtnMessage"	, "금액이 문자형입니다." );
		}else if(dataList == null || dataList.size() < 1) {
			rtnMap.put("rtnCode"	, "N" );
			rtnMap.put("rtnMessage"	, "입력 가능한 데이터가 없습니다." );
		}else {
			rtnMap.put("rtnCode"	, "Y" );
			rtnMap.put("rtnMessage"	, dataList );
		}
		return rtnMap;
	}
	
	
    public static int getAmount(Cell cell) {
        if (cell == null) {
            return 0;
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().replaceAll(",", ""));
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }
    
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
