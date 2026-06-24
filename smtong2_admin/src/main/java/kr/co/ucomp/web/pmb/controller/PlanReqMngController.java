package kr.co.ucomp.web.pmb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.common.util.MaskingUtils;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import kr.co.ucomp.web.pmb.dto.PlanReqSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanReqMngEntity;
import kr.co.ucomp.web.pmb.service.PlanReqMngService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/pmb/plan-request")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'REQ_MNG', 'SALES_MNG') and @permissionChecker.canAccessByPlanReq(authentication)")
public class PlanReqMngController {

    @Autowired
    private PlanReqMngService service;
    @Autowired
    CompanyListService companyListService;
    @Autowired
    FileService fileService;

    @ResponseBody
    @PostMapping("/ajaxlist")
    public ResponseEntity<CustomApiResponse<List<PlanReqMngEntity>>> ajaxlist(
            HttpServletResponse response,
            @RequestBody PlanReqSearchDto searchRequest) throws IOException {
        try {
            long cnt = service.getListCount(searchRequest);
            List<PlanReqMngEntity> list = null;
            if (cnt > 0) {
                list = service.getList(searchRequest);
            }
            return CustomApiResponse.success(ResponseCode.OK, cnt, list);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/detail")
    public ResponseEntity<CustomApiResponse<PlanReqMngEntity>> getDetail(
            @RequestParam("id") Integer id) {
        try {
            PlanReqMngEntity detail = service.getDetail(id);
            if (detail == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND);
            }
            return CustomApiResponse.success(ResponseCode.OK, detail);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/update")
    public ResponseEntity<CustomApiResponse<PlanReqMngEntity>> update(
            @RequestBody PlanReqMngEntity request) {
        try {
            service.update(request);
            return CustomApiResponse.success(ResponseCode.OK, request);
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> delete(
            @PathVariable("id") Integer id) {
        try {
            service.delete(id);
            return CustomApiResponse.success(ResponseCode.OK, "삭제 완료");
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 요금제 신청 리스트 화면
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

        return "pages/pmb/planReq/list";
    }

    @PostMapping(value = "/exceldown")
    public ResponseEntity<byte[]> exceldown(HttpServletRequest request, @RequestBody PlanReqSearchDto param)
            throws Exception {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

        List<PlanReqMngEntity> resultList = service.getListWithOutLimit(param);

        HttpSession session = request.getSession();
        AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

        String authType = loginadminInfo.getAuthType() == null ? "" : loginadminInfo.getAuthType();

        for (PlanReqMngEntity itm : resultList) {

            String reqNm = itm.getReqNm();
            String phone = itm.getReqPhonNum();
            phone = "0" + phone.split(" ")[1];
            if ("SALES".equals(authType)) {
                reqNm = MaskingUtils.maskName(reqNm);
                phone = MaskingUtils.phoneMasking(phone);
            }
            String id = itm.getId().toString();

            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("id", id);
            data.put("reqSp", itm.getReqSp().equals("L") ? "단순랜딩" : "영업링크");
            data.put("reqNm", reqNm);
            data.put("reqPhonNum", phone);
            data.put("planMno", itm.getPlanMno());
            data.put("planCompanyNm", itm.getPlanCompanyNm());
            data.put("reqProdNm", itm.getReqProdNm());
            data.put("createDate", itm.getCreateDate());

            dataList.add(data);
        }

        // 엑셀 헤더 설정
        String[] headers = { "번호", "유입구분", "신청자", "연락처", "MNO", "사업자", "요금제", "신청일" };

        byte[] excelData = fileService.getExcelData(headers, dataList);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

}