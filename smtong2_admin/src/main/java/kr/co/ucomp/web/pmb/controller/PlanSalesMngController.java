package kr.co.ucomp.web.pmb.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.common.util.MaskingUtils;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.AdminUserSearchDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.AdminUserEntity;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.AdminUserService;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import kr.co.ucomp.web.order.dto.PlanOrderSearchDto;
import kr.co.ucomp.web.order.entity.OrderStateEntity;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;
import kr.co.ucomp.web.order.service.PlanOrderService;
import kr.co.ucomp.web.pmb.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.entity.SalesPlanEntity;
import kr.co.ucomp.web.pmb.entity.SalesPlanListEntity;
import kr.co.ucomp.web.pmb.service.PlanSalesMngService;
import kr.co.ucomp.web.pmb.service.PlanService;
import kr.co.ucomp.web.svc.banner.entity.BannerEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/pmb/plan-sales")
@PreAuthorize("hasAnyAuthority('ALL', 'SALES_MNG')")
public class PlanSalesMngController {

	@Autowired
	private PlanSalesMngService planSalesMngService;

	@Autowired
	private FileService fileService;

	@Autowired
	private CompanyListService companyListService;

	@Autowired
	private PlanService planService;

	@Autowired
	private PlanOrderService planOrderservice;

	@Autowired
	private AdminUserService adminUserService;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * 영업용 요금제 리스트 화면
	 *
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/list")
	public String listview(HttpServletResponse response, Model model) {

		return "pages/pmb/planSales/list";
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/listProc")
	public ResponseEntity<CustomApiResponse<List<SalesPlanEntity>>> listProc(HttpServletRequest request,
			@RequestBody PlanSalesSearchDto param) throws IOException {

		try {

			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

			/* 영업담당자의 경우 자신의 리스트만 불러오기 */
			if (StringUtils.equals("SALES", loginadminInfo.getAuthType())) {
				param.setSearchComUserId(loginadminInfo.getId());
			}

			long resultcnt = planSalesMngService.getCount(param);
			List<SalesPlanEntity> resultList = new ArrayList<SalesPlanEntity>();
			if (resultcnt > 0) {
				resultList = planSalesMngService.list(param);

				/* 이미지 url get */
				for (SalesPlanEntity temp : resultList) {
					if (!StringUtils.isEmpty(temp.getImagePc()) && !StringUtils.isEmpty(temp.getImageMo())) {
						Map<String, Object> map = MAPPER.readValue(temp.getImagePc(), Map.class);
						temp.setImagePc(map.get("fileUrl").toString());
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
	 * 영업용 요금제 등록/수정 화면
	 *
	 * @param request
	 * @param model
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/form")
	public String form(HttpServletRequest request, Model model,
			@RequestParam(value = "id", required = false, defaultValue = "0") long id) throws Exception {

		log.info("등록/수정 화면 진입");

		/* 입점사 목록 불러오기 */
		CompanyListSearchDto param = new CompanyListSearchDto();
		param.setSearchUseYn(1);
		List<CompanyListEntity> companyList = companyListService.getListCompanyListWithoutLimit(param);
		model.addAttribute("companyList", companyList);

		SalesPlanEntity entity = new SalesPlanEntity();

		if (id > 0) {
			entity = planSalesMngService.getDetail(id);

			if (entity != null) {

				if (!StringUtils.isEmpty(entity.getImagePc())) {
					Map<String, Object> map = MAPPER.readValue(entity.getImagePc(), Map.class);
					entity.setImagePc(map.get("fileUrl").toString());
					entity.setOrgImagePc(map.get("orgFileNm").toString());
				}
			}

			SalesPlanListEntity salesParam = new SalesPlanListEntity();
			salesParam.setSalesId(id);
			List<SalesPlanListEntity> salesPlanList = planSalesMngService.salePlanList(salesParam);
			model.addAttribute("salesPlanList", salesPlanList);
		} else {
			entity.setUseYn(true);
		}

		model.addAttribute("entity", entity);
		return "pages/pmb/planSales/form";
	}

	/**
	 * 등록/수정 프로세스
	 *
	 * @param request
	 * @param obj
	 * @return
	 */
	@PostMapping(value = "/insUpdProc")
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> insupdProc(MultipartHttpServletRequest request,
			@RequestParam Map<String, Object> obj) {

		try {

			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

			AdminUserDto salesAdmin = adminUserService.getDetailById(MapUtils.getString(obj, "salesId"));
			if (salesAdmin == null) {
				return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "영업자 아이디 확인해주세요.");
			}

			log.info("salesAdmin 정보: {}", ReflectionToStringBuilder.toString(salesAdmin, ToStringStyle.JSON_STYLE));

			Map<String, Object> resultList = new HashMap<String, Object>();

			long id = 0;
			if (!StringUtils.isEmpty(obj.get("id").toString())) {
				id = Long.parseLong(obj.get("id").toString());
			}

			/* 등록/수정 객체 setting */
			SalesPlanEntity entity = planSalesMngService.getDetail(id); // 기존데이터 get

			if (entity == null) {
				entity = new SalesPlanEntity();

				/* 영업용 url */
				String adiminId = salesAdmin.getAdminId();

				/* 생성갯수 + 1 */
				PlanSalesSearchDto param = new PlanSalesSearchDto();
				param.setSearchComUserId(salesAdmin.getId());
				long cnt = planSalesMngService.getCount(param);

				String url = adiminId + "/" + (cnt + 1);
				String encUrl = CommonUtil.encodeBase64UrlSafe(url);

				entity.setIdx(cnt + 1);
				entity.setUrl("/b2b/" + encUrl);
				entity.setCreateId(loginadminInfo.getId());

				entity.setComUserId(salesAdmin.getId());
			}

			entity.setTitle(MapUtils.getString(obj, "title", ""));

			String useYn = MapUtils.getString(obj, "useYn", "");
			if (!StringUtils.equals("", useYn)) {
				entity.setUseYn(true);
			} else {
				entity.setUseYn(false);
			}

			/* 수정 */
			if (id > 0) {

				String imageDelete = MapUtils.getString(obj, "imageDelete", "N");

				if (StringUtils.equals("Y", imageDelete)) {
					entity.setImagePc("");
				} else {
					/* 파일 upload */
					MultipartFile pcFile = request.getFile("imagePc");
					if (!StringUtils.isEmpty(pcFile.getOriginalFilename())) {
						String imagePc = fileService.FileUpload("salesImg/" + loginadminInfo.getAdminId(), pcFile);
						log.info("PC 이미지 업로드 결과 : {}", imagePc);
						entity.setImagePc(imagePc);
					}
				}

				entity.setModifiedId(loginadminInfo.getId());
				int resValue = planSalesMngService.update(entity);

				String salesPlanList = obj.get("bannerPlanListArr").toString();
				String planList = obj.get("planListArr").toString();

				int idx = 0;
				for (String str : salesPlanList.split(",")) {
					if (StringUtils.equals("0", str)) {
						SalesPlanListEntity salesPlanListEntity = new SalesPlanListEntity();
						salesPlanListEntity.setSalesId(entity.getId());
						salesPlanListEntity.setPlanId(Integer.parseInt(planList.split(",")[idx]));
						salesPlanListEntity.setOrderNo(idx);
						planSalesMngService.createByPlanList(salesPlanListEntity);
					}
					idx++;
				}

				/* 등록 */
			} else {

				/* 파일 upload */
				MultipartFile pcFile = request.getFile("imagePc");
				if (!StringUtils.isEmpty(pcFile.getOriginalFilename())) {
					String imagePc = fileService.FileUpload("salesImg/" + loginadminInfo.getAdminId(), pcFile);
					log.info("PC 이미지 업로드 결과 : {}", imagePc);
					entity.setImagePc(imagePc);
				}

				int resValue = planSalesMngService.create(entity);

				String planList = obj.get("planListArr").toString();

				if (!StringUtils.equals("", planList)) {
					int idx = 0;
					for (String str : planList.split(",")) {
						SalesPlanListEntity salesPlanListEntity = new SalesPlanListEntity();
						salesPlanListEntity.setSalesId(entity.getId());
						salesPlanListEntity.setPlanId(Integer.parseInt(str));
						salesPlanListEntity.setOrderNo(idx);
						planSalesMngService.createByPlanList(salesPlanListEntity);
						idx++;
					}
				}
			}

			return CustomApiResponse.success(ResponseCode.OK, resultList);
		} catch (Exception e) {

			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}
	}

	/**
	 * 요금제 검색
	 *
	 * @param dto
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/planlist")
	public ResponseEntity<CustomApiResponse<List<PlanEntity>>> planlist(@RequestBody SearchPlanDto dto) {

		try {

			/* 영업용 요금제 hidden_yn = 0 */
			// dto.setSearchHiddenYn("0");
			List<PlanEntity> list = new ArrayList<PlanEntity>();
			long totalCnt = planService.getListCount(dto);
			if (totalCnt > 0) {
				list = planService.getListWithoutLimit(dto);
			}

			return CustomApiResponse.success(ResponseCode.OK, totalCnt, list);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
		}
	}

	/**
	 * 요금제 배너 상품 삭제
	 *
	 * @param response
	 * @param delId
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@DeleteMapping(value = "/planDelete")
	public ResponseEntity<CustomApiResponse<String>> planDelete(HttpServletResponse response,
			@RequestParam("delId") String delId) throws IOException {

		try {

			String delArr[] = delId.split(",");

			for (String str : delArr) {
				planSalesMngService.planDelete(Long.parseLong(str));
			}

			return CustomApiResponse.success(ResponseCode.OK, "del ok");
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
		}
	}

	/**
	 * 사용/미사용 업데이트
	 *
	 * @param response
	 * @param id
	 * @param useYn
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@PostMapping(value = "/useYn")
	public ResponseEntity<CustomApiResponse<String>> useYn(HttpServletResponse response, @RequestParam("id") String id,
			@RequestParam("useYn") String useYn) throws IOException {

		try {

			SalesPlanEntity entity = planSalesMngService.getDetail(Long.parseLong(id));
			entity.setUseYn(StringUtils.equals("Y", useYn) ? true : false);

			int resValue = planSalesMngService.update(entity);

			return CustomApiResponse.success(ResponseCode.OK, String.valueOf(resValue));

		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
		}
	}

	/**
	 * 엑셀 다운로드
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping("/exceldown")
	public ResponseEntity<byte[]> downPlanMap(@RequestBody PlanSalesSearchDto param) throws Exception {

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		List<SalesPlanEntity> list = planSalesMngService.listWithoutLimit(param);

		if (!StringUtils.equals("ADMIN", param.getAuthType()) &&
				!StringUtils.equals("MANAGER", param.getAuthType())) {
			list = list.stream()
					.filter(entity -> {
						return entity.getComUserId() == Long.parseLong(param.getComUserId());
					})
					.collect(Collectors.toList());
		}

		int idx = list.size();
		for (SalesPlanEntity itm : list) {
			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("id", idx);
			data.put("comUserNm", itm.getComUserNm());
			data.put("title", itm.getTitle());
			data.put("url", "https://www.smtong.co.kr" + itm.getUrl());
			data.put("useYn", itm.isUseYn() ? "Y" : "N");
			data.put("createDate", itm.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			dataList.add(data);
			idx--;
		}

		// 엑셀 헤더 설정
		String[] headers = { "번호", "영업자", "제목", "url", "사용여부", "등록일" };

		byte[] excelData = fileService.getExcelData(headers, dataList);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=userList.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

	/**
	 * 신청내역 보기
	 *
	 * @param response
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/apply")
	public String apply(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("id") long id) {

		log.info("신청내역 페이지 진입");

		SalesPlanEntity entity = planSalesMngService.getDetail(id);
		model.addAttribute("entity", entity);

		return "pages/pmb/planSales/applyList";
	}

	/**
	 * 영업상품 신청내역 리스트
	 *
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@PostMapping(value = "/ajaxList")
	public ResponseEntity<CustomApiResponse<List<PlanOrderEntity>>> ajaxList(
			HttpServletRequest request,
			@RequestBody PlanOrderSearchDto param) throws IOException {

		try {
			long resulCnt = planOrderservice.getListCount(param);
			List<PlanOrderEntity> resultList = new ArrayList<PlanOrderEntity>();
			if (resulCnt > 0) {
				resultList = planOrderservice.getList(param);
			}

			return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/applyDetail")
	public String applyDetail(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(value = "orderId", defaultValue = "") String orderId) {

		PlanOrderEntity planOrderEntity = new PlanOrderEntity();
		planOrderEntity = planOrderservice.getDetail(Integer.parseInt(orderId));
		if (StringUtils.isNotBlank(planOrderEntity.getCardNum())) {
			planOrderEntity.setCardNum(CommonUtil.formatCardNumber(planOrderEntity.getCardNum()));
		}

		if (StringUtils.isNotBlank(planOrderEntity.getAuthOrgDt())) {
			planOrderEntity.setAuthOrgDt(CommonUtil.formatDate(planOrderEntity.getAuthOrgDt()));
		}

		if (StringUtils.isNotBlank(planOrderEntity.getDriverNumber())) {
			planOrderEntity.setDriverNumber(CommonUtil.formatLicenseNumber(planOrderEntity.getDriverNumber()));
		}

		if (StringUtils.isNotBlank(planOrderEntity.getCardValidDt())) {
			planOrderEntity.setCardValidDt(CommonUtil.formatYM(planOrderEntity.getCardValidDt()));
		}

		planOrderEntity.setOrderPhone(CommonUtil.formatPhoneNumber(planOrderEntity.getOrderPhone()));

		String recomSp = planOrderEntity.getRecomSp() == null ? "N" : planOrderEntity.getRecomSp();
		String recomUserId = "";
		if (!"N".equals(recomSp)) {
			if ("1".equals(recomSp)) {
				recomUserId = planOrderEntity.getRecomUserWEBId();
			} else if ("2".equals(recomSp)) {
				recomUserId = planOrderEntity.getRecomUserAdminId();
			}

			planOrderEntity.setRecomUserNm(recomUserId);
		}

		PlanOrderSearchDto stateParam = new PlanOrderSearchDto();
		stateParam.setOrderId(planOrderEntity.getOrderSeq());
		List<OrderStateEntity> orderStateList = planOrderservice.getListState(stateParam);

		model.addAttribute("orderReq", planOrderEntity);
		model.addAttribute("orderStateList", orderStateList);

		return "pages/pmb/planSales/applyDetail";
	}

	/**
	 * 엑셀 다운로드
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping("/applyExceldown")
	public ResponseEntity<byte[]> applyExceldown(HttpServletRequest request, @RequestBody PlanOrderSearchDto param)
			throws Exception {

		HttpSession session = request.getSession();
		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

		String authType = loginadminInfo.getAuthType() == null ? "" : loginadminInfo.getAuthType();

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		List<PlanOrderEntity> list = planOrderservice.getListWithoutLimit(param);
		int idx = list.size();
		for (PlanOrderEntity itm : list) {
			Map<String, Object> data = new LinkedHashMap<String, Object>();

			String reqNm = itm.getOrderNm();
			String phone = CommonUtil.formatPhoneNumber(itm.getOrderPhone());

			if ("SALES".equals(authType)) {
				reqNm = MaskingUtils.maskName(reqNm);
				phone = MaskingUtils.phoneMasking(phone);
			}

			data.put("id", idx);
			data.put("orderStateNm", itm.getOrderStateNm());
			data.put("entrType", itm.getEntrType().equals("01") ? "신규가입" : "번호이동");
			data.put("orderNm", reqNm);
			data.put("orderPhone", phone);
			data.put("telecomCd", itm.getTelecomCd());
			data.put("companyNm", itm.getCompanyNm());
			data.put("planNm", itm.getPlanNm());
			data.put("recomUserAdmin", itm.getRecomUserAdminId());
			data.put("pointPlanYn", itm.getPointPlanYn() == 1 ? "Y" : "N");
			data.put("orderDttm", itm.getOrderDttm());
			data.put("openCompDttm", itm.getOpenCompDttm());
			dataList.add(data);
			idx--;
		}

		// 엑셀 헤더 설정
		String[] headers = { "번호", "상태", "가입유형", "신청자", "연락처", "MNO", "사업자", "요금제", "추천인", "포인트 요금제", "신청일", "개통완료일" };

		byte[] excelData = fileService.getExcelData(headers, dataList);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=userList.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

	// =============================== 영업상품 단순 링크 신청 리스트

	/**
	 * 신청내역 보기
	 *
	 * @param response
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/reqList")
	public String reqList(HttpServletResponse response, Model model, @RequestParam("id") long id) {

		log.info("신청내역 페이지 진입");

		SalesPlanEntity entity = planSalesMngService.getDetail(id);
		model.addAttribute("entity", entity);

		return "pages/pmb/planSales/reqList";
	}

	/**
	 * 영업자 검색
	 *
	 * @param param
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/saleslist")
	public ResponseEntity<CustomApiResponse<List<AdminUserEntity>>> saleslist(@RequestBody Map<String, Object> param) {

		try {
			String keyword = MapUtils.getString(param, "keyword", "");

			AdminUserSearchDto searchDto = new AdminUserSearchDto();
			searchDto.setSearchAuthType("SALES"); // 영업자만 조회
			if (StringUtils.isNotEmpty(keyword)) {
				searchDto.setKeyword(keyword);
			}

			List<AdminUserEntity> salesUsers = adminUserService.getList(searchDto);

			return CustomApiResponse.success(ResponseCode.OK, (long) salesUsers.size(), salesUsers);
		} catch (Exception e) {
			log.error("영업자 리스트 조회 오류: ", e);
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "영업자 리스트 조회 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

}
