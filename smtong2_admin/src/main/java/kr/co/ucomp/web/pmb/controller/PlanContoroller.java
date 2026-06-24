package kr.co.ucomp.web.pmb.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanDataEntity;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.service.PlanBenefitService;
import kr.co.ucomp.web.pmb.service.PlanFreebieService;
import kr.co.ucomp.web.pmb.service.PlanService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author 조일근
 * @since 2024.12.25
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/pbm/plan")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'PROD_MNG')")
public class PlanContoroller {

	@Autowired
	PlanService planService;
	@Autowired
	FileService fileService;
	@Autowired
	PlanBenefitService benefitService;
	@Autowired
	PlanFreebieService freebieservice;
	@Autowired
	CompanyListService companyListService;

	/**
	 * 요금제 리스트 화면
	 *
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String listview(HttpServletResponse response, Model model) {
		CompanyListSearchDto searchRequest = new CompanyListSearchDto();
		searchRequest.setSearchUseYn(1);
		List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
		model.addAttribute("compnyList", compnyList);

		return "pages/pmb/plan/list";
	}

	/**
	 * 2024-12-18 조일근
	 *
	 * @param CommCodeSearchDto
	 * @param List<CodeGroupEntity> 코드그룹 리스트
	 */
	@GetMapping("/edit")
	public String editview(HttpServletResponse response, Model model,
			@RequestParam(value = "searchId", defaultValue = "") String searchId, RedirectAttributes redirectAttributes) {

		PlanEntity record = new PlanEntity();
		CompanyListSearchDto searchRequest = new CompanyListSearchDto();
		searchRequest.setSearchUseYn(1);
		List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
		model.addAttribute("compnyList", compnyList);

		if (model.getAttribute("org.springframework.validation.BindingResult.record") != null) {
			record = (PlanEntity) model.getAttribute("record");
			model.addAttribute("record", record);
			model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
		} else {
			if (StringUtils.isNoneBlank(searchId)) {
				record = planService.getDetail(Integer.parseInt(searchId));
			}

		}

		String imagePcJson = record.getEventBannerImagePc();
		String imageMoJson = record.getEventBannerImageMo();

		if (!StringUtils.isEmpty(imagePcJson)) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				Map<String, Object> map = mapper.readValue(imagePcJson, Map.class);
				record.setEventBannerImagePc(map.get("orgFileNm").toString());
				record.setEventBannerImagePcUrl(map.get("fileUrl").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!StringUtils.isEmpty(imageMoJson)) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				Map<String, Object> map = mapper.readValue(imageMoJson, Map.class);
				record.setEventBannerImageMo(map.get("orgFileNm").toString());
				record.setEventBannerImageMoUrl(map.get("fileUrl").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		model.addAttribute("record", record);

		return "pages/pmb/plan/edit";
	}

	/**
	 * 2024-12-18 조일근
	 *
	 * @param CommCodeSearchDto
	 * @param List<CodeGroupEntity> 코드그룹 리스트
	 */
	@GetMapping("/view")
	public String view(HttpServletResponse response, Model model,
			@RequestParam(value = "searchId", defaultValue = "") String searchId) {

		PlanEntity record = new PlanEntity();
		CompanyListSearchDto searchRequest = new CompanyListSearchDto();
		searchRequest.setSearchUseYn(1);
		List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
		model.addAttribute("compnyList", compnyList);

		if (StringUtils.isNoneBlank(searchId)) {
			record = planService.getDetail(Integer.parseInt(searchId));
		}

		String imagePcJson = record.getEventBannerImagePc();
		String imageMoJson = record.getEventBannerImageMo();

		if (!StringUtils.isEmpty(imagePcJson)) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				Map<String, Object> map = mapper.readValue(imagePcJson, Map.class);
				record.setEventBannerImagePc(map.get("orgFileNm").toString());
				record.setEventBannerImagePcUrl(map.get("fileUrl").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!StringUtils.isEmpty(imageMoJson)) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				Map<String, Object> map = mapper.readValue(imageMoJson, Map.class);
				record.setEventBannerImageMo(map.get("orgFileNm").toString());
				record.setEventBannerImageMoUrl(map.get("fileUrl").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		model.addAttribute("record", record);

		return "pages/pmb/plan/view";
	}

	@ResponseBody
	@PostMapping(value = "/ajaxList")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> ajaxList(
			HttpServletRequest request,
			@RequestBody SearchPlanDto param) throws IOException {

		try {
			long resulCnt = planService.getListCount(param);
			List<PlanEntity> resultList = new ArrayList<PlanEntity>();
			if (resulCnt > 0) {
				resultList = planService.getList(param);
			}

			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}

	@ResponseBody
	@PostMapping("/exceldownlist")
	public ResponseEntity<byte[]> downPlanMap(@RequestBody SearchPlanDto param) throws Exception {

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		List<PlanEntity> list = planService.getList(param);
		for (PlanEntity itm : list) {
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("id", itm.getId());
			data.put("dispYn", itm.getDispYn() ? "노출" : "비노출");
			data.put("saleStatus", itm.getSaleStatus() ? "판매중" : "판매정지");
			data.put("newYn", itm.getNewYn() ? "신규" : "기존");
			data.put("mno", itm.getMno());
			data.put("planType", itm.getPlanType());
			data.put("hostNm", itm.getHostNm());
			data.put("planName", StringUtils.isBlank(itm.getPlanNameSmt()) ? itm.getPlanName() : itm.getPlanNameSmt());
			data.put("normalPrice", itm.getNormalPrice());
			data.put("salePrice", itm.getSalePrice());
			data.put("promotionPeriod", itm.getPromotionPeriod());
			data.put("supDataVal", itm.getSupDataVal());
			data.put("supQos", itm.getSupQos());
			data.put("supCallVal", itm.getSupCallVal() == 9999 ? "무제한" : itm.getSupCallVal());
			data.put("supSmsVal", itm.getSupSmsVal() == 9999 ? "무제한" : itm.getSupSmsVal());

			data.put("marketingType", itm.getMarketingType());
			data.put("modifiedDate", itm.getModifiedDate());
			data.put("planTag1Name", itm.getPlanTag1Name());
			data.put("planTag2Name", itm.getPlanTag2Name());
			data.put("planTag3Name", itm.getPlanTag3Name());
			data.put("planTag4Name", itm.getPlanTag4Name());
			data.put("planTag5Name", itm.getPlanTag5Name());
			dataList.add(data);
		}

		// 엑셀 헤더 설정
		String[] headers = { "번호", "노출여부", "상태", "신규여부", "MNO", "네트워크", "사업자", "요금제", "기본료", "할인금액", "할인기간", "데이터", "QoS",
				"전화", "메시지",
				"RM/RS", "최근 수정일", "Tag1", "Tag2", "Tag3", "Tag4", "Tag5" };

		byte[] excelData = fileService.getExcelData(headers, dataList);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=userList.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

	@ResponseBody
	@PostMapping(value = "/ajaxUpdate")
	public ResponseEntity<CustomApiResponse<PlanUpdateDto>> ajaxUpdate(
			HttpServletRequest request,
			@RequestBody PlanUpdateDto param) throws IOException {

		try {

			/*
			 * long cnt = planService.getListCountByPlanCode(param); if(cnt == 0) {
			 * planService.updataPlanCode(param); param.setUpdateErrYn("N"); return
			 * CustomApiResponse.success(ResponseCode.OK, param); } else {
			 * param.setUpdateErrYn("D"); return CustomApiResponse.success(ResponseCode.OK,
			 * param); }
			 */

			planService.updataPlanCode(param);
			param.setUpdateErrYn("N");
			return CustomApiResponse.success(ResponseCode.OK, param);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String update(
			HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute("PlanUpdateDto") PlanUpdateDto record, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) throws IOException {

		try {

			if (bindingResult.hasErrors()) {
				// 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
				redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
				redirectAttributes.addFlashAttribute("record", record);
				return "redirect:/pbm/plan/edit";
			}

			String fileuploadResPc = "";
			String fileuploadResMp = "";

			try {
				if (!record.getFilePc().isEmpty() || record.getFilePc() != null) {
					if (StringUtils.isNoneBlank(record.getFilePc().getOriginalFilename())) {
						fileuploadResPc = fileService.FileUpload("planbanner", record.getFilePc());
					}

				}

				if (!record.getFileMo().isEmpty() || record.getFileMo() != null) {
					if (StringUtils.isNoneBlank(record.getFileMo().getOriginalFilename())) {
						fileuploadResMp = fileService.FileUpload("planbanner", record.getFileMo());
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

			record.setModifiedId(loginadminInfo.getId());

			record.setEventBannerImagePc(fileuploadResPc);
			record.setEventBannerImageMo(fileuploadResMp);
			planService.update(record);

			redirectAttributes.addFlashAttribute("procMsg", "sucess");

			return "redirect:/pbm/plan/edit?searchId=" + record.getId();

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
			return "redirect:/pbm/plan/edit?searchId=";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
			return "redirect:/pbm/plan/edit?searchId=";

		}

	}

	@DeleteMapping("/delete")
	public ResponseEntity<CustomApiResponse<String>> deleteErrorReport(
			HttpServletRequest request, @RequestParam("delId") int delId) throws IOException {

		try {
			planService.delete(delId);
			return CustomApiResponse.success(ResponseCode.OK, "삭제 완료");

		} catch (IllegalArgumentException e) {

			return CustomApiResponse.error(ResponseCode.BAD_REQUEST);

		} catch (Exception e) {

			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}

	// ================================ 요금제 코드 매핑 관련

	@GetMapping("/excel/downPlanMap")
	public ResponseEntity<byte[]> downPlanMap(@RequestParam(name = "id", required = false, defaultValue = "1") int id)
			throws Exception {

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		SearchPlanDto param = new SearchPlanDto();
		param.setSearchCompany(id);
		List<PlanEntity> list = planService.getMapListExcel(param);
		String comp_nm = "";
		for (PlanEntity itm : list) {
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("uuid", itm.getUuid());
			data.put("telecom", itm.getHostNm());
			data.put("planType", itm.getPlanType());
			data.put("mno", itm.getMno());
			data.put("planNm", itm.getPlanName());
			data.put("normalPrice", itm.getNormalPrice());
			data.put("salelPrice", itm.getSalePrice());
			data.put("afterPrice", itm.getAfterPrice());
			data.put("promotionPeriod", itm.getPromotionPeriod());
			data.put("planCode", itm.getPlanCode());
			comp_nm = itm.getHostNm();
			dataList.add(data);
		}

		// 엑셀 헤더 설정
		String[] headers = { "uuid", "통신사", "타입", "MNO", "요금제명", "금액", "할인액", "최종금액", "promo 기간", "실제요금제코드" };

		byte[] excelData = fileService.getExcelData(headers, dataList);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=planList.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

	@PostMapping(value = "/excel/uploadPlanMap", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> uploadPlanMap(
			HttpServletRequest request,
			@RequestPart(value = "uploadMapData", required = false) MultipartFile uploadMapData,
			@RequestPart("hostId") Integer hostId) throws IOException {

		Map<String, Object> uploadRes = new HashMap<String, Object>();
		try {

			try {
				uploadRes = planService.planMapUpload(uploadMapData);

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

	// 02.03 sancho
	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown(HttpServletRequest request, @RequestBody SearchPlanDto param)
			throws Exception {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		List<PlanEntity> resultList = planService.getList(param);

		for (PlanEntity itm : resultList) {

			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("dispYn", itm.getDispYn());
			data.put("saleStatus", itm.getSaleStatus());
			data.put("newYn", itm.getNewYn());
			data.put("mno", itm.getMno());
			data.put("planType", itm.getPlanType());
			data.put("hostNm", itm.getHostNm());
			data.put("planName", itm.getPlanName());
			data.put("normalPrice", itm.getNormalPrice());
			data.put("salePrice", itm.getSalePrice());
			data.put("promotionPeriod", itm.getPromotionPeriod());
			data.put("supDataVal", itm.getSupDataVal());
			data.put("supQos", itm.getSupQos());
			data.put("marketingType", itm.getMarketingType());
			data.put("modifiedDate", itm.getModifiedDate());

			dataList.add(data);
		}

		// 엑셀 헤더 설정
		String[] headers = { "노출여부", "상태", "신규여부", "MNO", "네트워크", "사업자", "요금제", "기본료", "할인금액", "할인기간", "데이터", "QoS",
				"RM/RS", "최근 수정일" };

		byte[] excelData = fileService.getExcelData(headers, dataList);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

	// ============================================ 요금제 추천수 및 정렬 순서 관리
	/**
	 * 요금제 추천수 및 정렬 순서 관리 리스트 화면
	 *
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/planOrderMng/list")
	public String listOrderMng(HttpServletResponse response, Model model) {
		CompanyListSearchDto searchRequest = new CompanyListSearchDto();
		searchRequest.setSearchUseYn(1);
		List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
		model.addAttribute("compnyList", compnyList);

		return "pages/pmb/planOrderMng/list";
	}

	// 02.03 sancho
	@PostMapping(value = "/exceldownOrder")
	public ResponseEntity<byte[]> exceldownOrder(HttpServletRequest request, @RequestBody SearchPlanDto param)
			throws Exception {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		List<PlanEntity> resultList = planService.getList(param);

		for (PlanEntity itm : resultList) {

			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("id", itm.getId());
			data.put("hostNm", itm.getHostNm());
			data.put("uuid", itm.getUuid());
			data.put("planName", itm.getPlanName());
			data.put("dispYn", itm.getDispYn() ? "Y" : "N");
			data.put("saleStatus", itm.getSaleStatus() ? "Y" : "N");
			data.put("mno", itm.getMno());
			data.put("planType", itm.getPlanType());
			data.put("normalPrice", itm.getNormalPrice());
			data.put("salePrice", itm.getSalePrice());
			data.put("afterPrice", itm.getAfterPrice());
			data.put("promotionPeriod", itm.getPromotionPeriod());
			data.put("supDataVal", itm.getSupDataVal());
			data.put("supQos", itm.getSupQos());
			data.put("marketingType", itm.getMarketingType());
			data.put("recomOrder", "0");

			dataList.add(data);
		}

		// 엑셀 헤더 설정
		String[] headers = { "요금제Id", "사업자", "uuid", "요금제", "노출여부", "상태", "MNO", "네트워크", "기본료", "할인금액", "할인후금액", "할인기간",
				"데이터", "QoS", "RM/RS", "추천수" };

		byte[] excelData = fileService.getExcelData(headers, dataList);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

	@PostMapping(value = "/excel/uploadPlanMapOrder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> uploadPlanMapOrder(
			HttpServletRequest request,
			@RequestPart(value = "uploadMapData", required = false) MultipartFile uploadMapData,
			@RequestPart("uploadOrderListSp") String uploadOrderListSp) throws IOException {

		Map<String, Object> uploadRes = new HashMap<String, Object>();
		try {

			try {

				uploadOrderListSp = uploadOrderListSp.substring(1);

				uploadRes = planService.planMapUploadOrder(uploadOrderListSp, uploadMapData);

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
	 * 2024-12-18 조일근
	 *
	 * @param CommCodeSearchDto
	 * @param List<CodeGroupEntity> 코드그룹 리스트
	 */
	@GetMapping("/editOrg")
	public String editOrg(HttpServletResponse response, Model model,
			@RequestParam(value = "searchUUid", defaultValue = "") String searchUUid, RedirectAttributes redirectAttributes) {

		PlanDataEntity record = new PlanDataEntity();
		CompanyListSearchDto searchRequest = new CompanyListSearchDto();
		searchRequest.setSearchUseYn(1);
		List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
		model.addAttribute("compnyList", compnyList);

		if (model.getAttribute("org.springframework.validation.BindingResult.record") != null) {
			record = (PlanDataEntity) model.getAttribute("record");
			model.addAttribute("record", record);
			model.addAttribute("BindingResult", model.getAttribute("org.springframework.validation.BindingResult.record"));
		} else {
			if (StringUtils.isNoneBlank(searchUUid)) {
				record = planService.getDetailOrg(searchUUid);
			}

		}

		model.addAttribute("searchUUid", searchUUid);
		model.addAttribute("record", record);

		return "pages/pmb/plan/editOrg";
	}

	@PostMapping(value = "/createOrg")
	public String createOrg(
			HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute("PlanDataEntity") PlanDataEntity record, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) throws IOException {

		try {

			if (bindingResult.hasErrors()) {
				// 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
				redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
				redirectAttributes.addFlashAttribute("record", record);
				return "redirect:/pbm/plan/editOrg";
			}

			CompanyListEntity comp = companyListService.getCompanyList(record.getCompanyId());

			String unixTime = CommonUtil.getUnixTime();
			String uuid = comp.getCompanyMngCode() + "_" + unixTime;
			record.setUuid(uuid);
			record.setIsProtected(true);
			record.setHiddenYn(true);
			record.setSaleStatus("1");
			record.setDataCreateSp("1");

			planService.createOrg(record);

			redirectAttributes.addFlashAttribute("procMsg", "sucess");

			return "redirect:/pbm/plan/list";

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
			return "redirect:/pbm/plan/editOrg";

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
			return "redirect:/pbm/plan/editOrg";

		}

	}

	@PostMapping(value = "/updateOrg")
	public String updateOrg(
			HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute("PlanDataEntity") PlanDataEntity record, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) throws IOException {

		try {

			if (bindingResult.hasErrors()) {
				// 유효성 검증 실패 시 오류와 데이터를 Flash Attribute로 전달
				redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.record", bindingResult);
				redirectAttributes.addFlashAttribute("record", record);
				return "redirect:/pbm/plan/editOrg";
			}

			planService.updateOrg(record);

			return "redirect:/pbm/plan/edit?searchId=" + record.getPlanId();

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
			return "redirect:/pbm/plan/editOrg?searchUUid=" + record.getUuid();

		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("procMsg", e.getMessage());
			return "redirect:/pbm/plan/editOrg?searchUUid=" + record.getUuid();

		}

	}

	/**
	 * 요금제 조회조건 리스트 가져오기
	 *
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@PostMapping(value = "/ajaxPlanCond")
	public ResponseEntity<CustomApiResponse<List<String>>> ajaxPlanCond(
			HttpServletRequest request,
			@RequestBody SearchPlanDto param) throws IOException {

		try {
			List<String> resultList = planService.selectPlanSearchCond(param);

			return CustomApiResponse.success(ResponseCode.OK, resultList.size(), resultList);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}

	@ResponseBody
	@PostMapping(value = "/ajaxTagUpdate")
	public ResponseEntity<CustomApiResponse<PlanUpdateDto>> ajaxTagUpdate(
			HttpServletRequest request,
			@RequestBody PlanUpdateDto param) throws IOException {

		try {

			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

			param.setModifiedId(loginadminInfo.getId());

			planService.updataPlanTag(param);
			param.setUpdateErrYn("N");
			return CustomApiResponse.success(ResponseCode.OK, param);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping(value = "/excel/uploadPlanTag", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> uploadPlanTag(
			HttpServletRequest request,
			@RequestPart(value = "uploadTagData", required = false) MultipartFile uploadMapData) throws IOException {

		Map<String, Object> uploadRes = new HashMap<String, Object>();
		try {

			try {
				uploadRes = planService.planTagUpload(uploadMapData);

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

}
