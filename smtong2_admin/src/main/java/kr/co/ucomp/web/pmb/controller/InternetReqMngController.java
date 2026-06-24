package kr.co.ucomp.web.pmb.controller;

import kr.co.ucomp.common.biztalk.KakaoBizTalkUtils;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.DebugUtil;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.common.util.MaskingUtils;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.pmb.dto.InternetReqMngSearchDto;
import kr.co.ucomp.web.pmb.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.pmb.entity.InternetReqMngEntity;
import kr.co.ucomp.web.pmb.service.InternetPlanService;
import kr.co.ucomp.web.pmb.service.InternetReqMngService;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 이정민
 * @since 2024.12.31
 * @version v1.0
 */
@Controller
@AllArgsConstructor
@RequestMapping("/pmb/internetreq")
@PreAuthorize("hasAnyAuthority('ALL', 'REQ_MNG') and @permissionChecker.canAccessByInternetReq(authentication)")
public class InternetReqMngController {
	private final InternetReqMngService service;
	@Autowired
	InternetPlanService internetPlanService;
	@Autowired
	FileService fileService;
	@Autowired
	CommCodeMngService codeService;
	@Autowired
	private KakaoBizTalkUtils bizTalkService;
	@Autowired
	CommCodeMngService comCodeService;

	@ResponseBody
	@PostMapping("/ajaxList")
	public ResponseEntity<CustomApiResponse<List<InternetReqMngEntity>>> ajaxList(
			@RequestBody InternetReqMngSearchDto param) {
		try {
			List<InternetReqMngEntity> list = new ArrayList<InternetReqMngEntity>();
			long count = service.getListCount(param);
			if (count > 0) {
				list = service.getList(param);
			}

			return CustomApiResponse.success(ResponseCode.OK, count, list);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, ResponseCode.BAD_REQUEST.getMessage());
		}
	}

	@ResponseBody
	@GetMapping("/ajaxCompanyList")
	public ResponseEntity<CustomApiResponse<List<InternetPlanMnoEntity>>> ajaxCompanyList(
			@RequestParam(value = "useYn", required = false) String useYn,
			@RequestParam(value = "isNewYn", required = false) String isNewYn,
			@RequestParam(value = "outboundCenter", required = false) String outboundCenter
	) {
		try {
			if (useYn == null)
				useYn = "";
			if (isNewYn == null)
				isNewYn = "N";
			List<InternetPlanMnoEntity> compnyList = internetPlanService.getInternetPlanMno(useYn, isNewYn, "");

			return CustomApiResponse.success(ResponseCode.OK, compnyList);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, ResponseCode.BAD_REQUEST.getMessage());
		}
	}

	/**
	 *
	 * 인터넷 신청정보 업데이트
	 *
	 * @param CodeGroupDto
	 * @param CodeGroupDto
	 */
	@ResponseBody
	@PostMapping("/update")
	public ResponseEntity<CustomApiResponse<InternetReqMngEntity>> updateInfo(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody InternetReqMngEntity record) throws IOException {
		try {

			int reqid = record.getId();

			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

			record.setModifiedId(loginadminInfo.getId());

			InternetReqMngEntity info = service.getDetail(reqid);
			if (info == null) {
				// 정보 없음
				return CustomApiResponse.error(ResponseCode.VALIDATION_ERROR, "인터넷 신청 정보가 존재하지 않습니다.");
			}

			service.update(record);
			return CustomApiResponse.success(ResponseCode.OK, 0, record);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * 인터넷 신청 리스트 화면
	 *
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/list/{siteSp}")
	public String listview(HttpServletResponse response, @PathVariable("siteSp") String siteSp, Model model) {

		List<InternetPlanMnoEntity> compnyList = internetPlanService.getInternetPlanMno("Y", "N", "");
		model.addAttribute("compnyList", compnyList);

		model.addAttribute("siteSp", siteSp);

		CommCodeSearchDto codeparam = new CommCodeSearchDto();
		codeparam.setPage(1);
		codeparam.setRecordSize(999);
		codeparam.setCodeGroup("interner_req_state");
		List<CodeEntity> codeList = comCodeService.getListCode(codeparam);
		model.addAttribute("stateList", codeList);

		return "pages/pmb/internetreq/list";
	}

	/**
	 * 인터넷 신청 리스트 화면
	 *
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/edit/{siteSp}")
	public String listview(@RequestParam(value = "searchId", defaultValue = "") String searchId,
			@PathVariable("siteSp") String siteSp,
			HttpServletRequest request, HttpServletResponse response, Model model) {

		HttpSession session = request.getSession();
		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

		CommCodeSearchDto codeparam = new CommCodeSearchDto();
		codeparam.setPage(1);
		codeparam.setRecordSize(999);
		codeparam.setCodeGroup("interner_req_state");
		List<CodeEntity> codeList = comCodeService.getListCode(codeparam);
		model.addAttribute("stateList", codeList);

		InternetReqMngEntity record = service.getDetail(Integer.valueOf(searchId));

		String AuthType = loginadminInfo.getAuthType();
		if ("MKT".equals(AuthType)) {
			try {
				String maskReqName = record.getInputName();
				maskReqName = MaskingUtils.maskName(record.getInputName());
				record.setInputName(maskReqName);
				String maskReqMoblNum = record.getInputNumber();
				maskReqMoblNum = MaskingUtils.phoneMasking(record.getInputNumber());
				record.setInputNumber(maskReqMoblNum);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		model.addAttribute("record", record);
		model.addAttribute("siteSp", siteSp);

		return "pages/pmb/internetreq/edit";
	}

	@PostMapping(value = "/exceldown")
	public ResponseEntity<byte[]> exceldown(HttpServletRequest request, @RequestBody InternetReqMngSearchDto param)
			throws Exception {

		HttpSession session = request.getSession();
		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		List<InternetReqMngEntity> resultList = null;
		long count = service.getListCount(param);
		if (count > 0) {
			resultList = service.getList(param);
		}

		// List<InternetReqMngEntity> resultList = service.getListWithOutLimit(param);
		// DebugUtil.dumpOneLine(resultList);

		for (InternetReqMngEntity itm : resultList) {
			String reqState = itm.getReqState();
			String reqStateNm = "상담신청";
			if ("REQ".equals(reqState)) {
				reqStateNm = "상담신청";
			}

			if ("CALL".equals(reqState)) {
				reqStateNm = "해피콜";
			}

			if ("DEFER".equals(reqState)) {
				reqStateNm = "보류";
			}

			if ("REQCOMP".equals(reqState)) {
				reqStateNm = "접수완료";
			}

			if ("ABS".equals(reqState)) {
				reqStateNm = "부재";
			}

			if ("CANCEL".equals(reqState)) {
				reqStateNm = "고객취소";
			}
			if ("COMPLET".equals(reqState)) {
				reqStateNm = "개통완료";
			}

			String AuthType = loginadminInfo.getAuthType();
			if ("MKT".equals(AuthType)) {
				try {
					String maskReqName = itm.getInputName();
					maskReqName = MaskingUtils.maskName(itm.getInputName());
					itm.setInputName(maskReqName);
					String maskReqMoblNum = itm.getInputNumber();
					maskReqMoblNum = MaskingUtils.phoneMasking(itm.getInputNumber());
					itm.setInputNumber(maskReqMoblNum);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			Map<String, Object> data = new LinkedHashMap<String, Object>();
			data.put("incomSp",
					"01".equals(itm.getIncomSp()) ? "홈페이지" : ("02".equals(itm.getIncomSp()) ? "랜딩 페이이지" : "수동등록"));
			data.put("incomUrl", "01".equals(itm.getIncomSp()) ? "" : itm.getIncomUrl());
			data.put("relComp", itm.getRelComp());
			data.put("reqState", reqStateNm);
			if ("01".equals(itm.getIncomSp()) == true) {
				data.put("getInternetPlanMnoNew", itm.getInternetPlanMnoNew());
			} else {
				data.put("getInternetPlanMno", itm.getInternetPlanMno());
			}

			data.put("internetPlanNm", itm.getInternetPlanNm());
			data.put("inputName", itm.getInputName());
			data.put("inputNumber", itm.getInputNumber());
			data.put("createDate", itm.getCreateDate());
			data.put("modifiedDate", itm.getModifiedDate());
			data.put("counselContent", itm.getCounselContent());
			data.put("procContent", itm.getProcContent());

			dataList.add(data);
		}

		// 엑셀 헤더 설정
		String[] headers = { "유입경로", "유입 URL", "등록처", "상태", "MNO", "신청상품", "신청자", "전화번호", "신청일시", "완료일시", "상담내용", "처리결과" };

		byte[] excelData = fileService.getExcelData(headers, dataList);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

	@PostMapping(value = "/exceldownUpload")
	public ResponseEntity<byte[]> exceldownUpload(HttpServletRequest request) throws Exception {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("No", "1");
		data.put("reqNm", "홍길동");
		data.put("reqHp", "010-1234-5678");
		data.put("relComp", "WA/GON");

		dataList.add(data);

		// 엑셀 헤더 설정
		String[] headers = { "No", "신청자", "전화번호", "등록처" };

		byte[] excelData = fileService.getExcelData(headers, dataList);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelData);
	}

	@PostMapping(value = "/excel/uploadReqData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> uploadPlanMap(
			HttpServletRequest request,
			@RequestPart(value = "uploadMapData", required = false) MultipartFile uploadMapData,
			@RequestPart(value = "isNew") String isNew,
			@RequestPart(value = "outboundCenter") String outboundCenter) throws IOException {

		Map<String, Object> uploadRes = new HashMap<String, Object>();
		try {

			try {

				System.out.println(outboundCenter);
				uploadRes = service.reqExcelUpload(uploadMapData, isNew, outboundCenter.equals("1") ? "INTERNET" : "INTERNET2");

				long saveCnt = (long) uploadRes.get("saveCnt");

				if (saveCnt > 0) {
					// 관리자 알림 전송 전송
					String token = bizTalkService.getKakaoBizTalkToken();
					CommCodeSearchDto codeparam = new CommCodeSearchDto();
					codeparam.setCodeGroup("common_env_code");
					codeparam.setCode("admin_alam_internet");
					CodeEntity codeItm = comCodeService.getCodeInfo(codeparam);
					String sendMsg = codeItm.getEtc1();

					List<InternetPlanMnoEntity> mnoInfolst = internetPlanService.getInternetPlanMno("Y", "Y", "0");
					InternetPlanMnoEntity mnoInfo = mnoInfolst.get(0);
					if (mnoInfo != null) {
						String alamTo = mnoInfo.getAlamRcvNum();

						String[] alamToList = alamTo.split("/");
						for (int i = 0; i < alamToList.length; i++) {
							String adminPhoneNum = alamToList[i] != null ? alamToList[i] : "";
							if (StringUtils.isNotBlank(adminPhoneNum)) {
								bizTalkService.sendSMSMsg(token, sendMsg, "sms", adminPhoneNum);
							}
						}

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
	 *
	 * 인터넷 신청정보 업데이트
	 *
	 * @param CodeGroupDto
	 * @param CodeGroupDto
	 */
	@ResponseBody
	@PostMapping("/updateAlamRcv")
	public ResponseEntity<CustomApiResponse<InternetPlanMnoEntity>> updateAlamRcv(HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody InternetPlanMnoEntity record) throws IOException {
		try {

			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

			internetPlanService.updataAlamRcvNum(record);
			return CustomApiResponse.success(ResponseCode.OK, 0, record);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}

	}

	// ===================== 인터넷 신청 신규 버전

	/**
	 * 인터넷 신청 리스트 화면
	 *
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/listNew")
	public String listNew(HttpServletResponse response, Model model) {

		List<InternetPlanMnoEntity> compnyList = internetPlanService.getInternetPlanMno("Y", "Y", "");
		model.addAttribute("compnyList", compnyList);

		CommCodeSearchDto codeparam = new CommCodeSearchDto();
		codeparam.setPage(1);
		codeparam.setRecordSize(999);
		codeparam.setCodeGroup("interner_req_state");
		List<CodeEntity> codeList = comCodeService.getListCode(codeparam);
		model.addAttribute("stateList", codeList);
		model.addAttribute("menuId", "0201");

		return "pages/pmb/internetreqNew/list";
	}

	/**
	 * 인터넷 신청 2 리스트 화면
	 *
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/listNew2")
	public String listNew2(HttpServletResponse response, Model model) {

		List<InternetPlanMnoEntity> compnyList = internetPlanService.getInternetPlanMno("Y", "Y", "");
		model.addAttribute("compnyList", compnyList);

		CommCodeSearchDto codeparam = new CommCodeSearchDto();
		codeparam.setPage(1);
		codeparam.setRecordSize(999);
		codeparam.setCodeGroup("interner_req_state");
		List<CodeEntity> codeList = comCodeService.getListCode(codeparam);
		model.addAttribute("stateList", codeList);
		model.addAttribute("menuId", "02012");
		return "pages/pmb/internetreqNew/list";
	}

	/**
	 * 인터넷 신청 리스트 화면
	 *
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/editNew")
	public String editNew(@RequestParam(value = "searchId", defaultValue = "") String searchId,
			HttpServletRequest request, HttpServletResponse response, Model model) {

		HttpSession session = request.getSession();
		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

		System.out.println(loginadminInfo.getAuthType());

		CommCodeSearchDto codeparam = new CommCodeSearchDto();
		codeparam.setPage(1);
		codeparam.setRecordSize(999);
		codeparam.setCodeGroup("interner_req_state");
		List<CodeEntity> codeList = comCodeService.getListCode(codeparam);
		model.addAttribute("stateList", codeList);

		InternetReqMngEntity record = service.getDetail(Integer.valueOf(searchId));

		String AuthType = loginadminInfo.getAuthType();
		if ("MKT".equals(AuthType)) {
			try {
				String maskReqName = record.getInputName();
				maskReqName = MaskingUtils.maskName(record.getInputName());
				record.setInputName(maskReqName);
				String maskReqMoblNum = record.getInputNumber();
				maskReqMoblNum = MaskingUtils.phoneMasking(record.getInputNumber());
				record.setInputNumber(maskReqMoblNum);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		model.addAttribute("record", record);

		return "pages/pmb/internetreqNew/edit";
	}
}
