package kr.co.ucomp.web.order.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import kr.co.ucomp.web.order.dto.PlanOrderSearchDto;
import kr.co.ucomp.web.order.entity.OrderStateEntity;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;
import kr.co.ucomp.web.order.service.PlanOrderService;
import kr.co.ucomp.web.order.service.OrderAPIGOGOService;
import kr.co.ucomp.web.order.service.OrderAPISHAKEService;
import kr.co.ucomp.web.pmb.service.PlanService;
import lombok.extern.slf4j.Slf4j;

/**
*
* @author 조일근
* @since 2024.12.25
* @version v1.0
*/
@Controller
@RequestMapping(value = "/pbm/order")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'REQ_MNG', 'SALES_MNG') and @permissionChecker.canAccessByPlanReq(authentication)")
public class PlanOrderController {

		@Autowired PlanOrderService service;
		@Autowired PlanService  planService;
		@Autowired CommCodeMngService codeService;
		@Autowired OrderAPIGOGOService orderServiceGOGO;
		@Autowired OrderAPISHAKEService orderServiceSHAKE;
		@Autowired CompanyListService companyListService;
		@Autowired FileService fileService;
	   /**
	  * 2025-02-20 조일근
	  * 02) 가입방식 선택
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @GetMapping("/list")
	 public String  list(  HttpServletRequest request , HttpServletResponse response,Model model)  {
		 CompanyListSearchDto searchRequest = new CompanyListSearchDto();
		searchRequest.setSearchUseYn(1);
		List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
		model.addAttribute("compnyList", compnyList);
		 
	 	return "pages/order/list";
	 }
	 
	 
	 /*
	  * 2025-02-20 조일근
	  * 주문 상세
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @GetMapping("/detail")
	 public String  detail(  HttpServletRequest request , HttpServletResponse response,Model model
			 ,@RequestParam(value="orderId", defaultValue = "") String orderId)  {
	 	
		 PlanOrderEntity planOrderEntity = new PlanOrderEntity();
		 planOrderEntity = service.getDetail(Integer.parseInt(orderId));
		 if(StringUtils.isNotBlank(planOrderEntity.getCardNum())) {
			 planOrderEntity.setCardNum(CommonUtil.formatCardNumber(planOrderEntity.getCardNum()));
		 }
		 
		 if(StringUtils.isNotBlank(planOrderEntity.getAuthOrgDt())) {
			 planOrderEntity.setAuthOrgDt(CommonUtil.formatDate(planOrderEntity.getAuthOrgDt()));
		 }
		 
		 if(StringUtils.isNotBlank(planOrderEntity.getDriverNumber() )) {
			 planOrderEntity.setDriverNumber(CommonUtil.formatLicenseNumber(planOrderEntity.getDriverNumber()));
		 }
		 
		 if(StringUtils.isNotBlank(planOrderEntity.getCardValidDt() )) {
			 planOrderEntity.setCardValidDt(CommonUtil.formatYM(planOrderEntity.getCardValidDt()));
		 }
		 
		 planOrderEntity.setOrderPhone(CommonUtil.formatPhoneNumber(planOrderEntity.getOrderPhone()));
		 
		 
		 String recomSp = planOrderEntity.getRecomSp() == null ? "N" : planOrderEntity.getRecomSp();
		 String recomUserId = "";
		 if(!"N".equals(recomSp)) {
			 if("1".equals(recomSp)) {
				 recomUserId = planOrderEntity.getRecomUserWEBId();
			 } else if("2".equals(recomSp)) {
				 recomUserId = planOrderEntity.getRecomUserAdminId();
			 }
			 
			 planOrderEntity.setRecomUserNm(recomUserId);	 
		 }
		 
		CommCodeSearchDto cmmCodeParam = new CommCodeSearchDto();
		cmmCodeParam.setCodeGroup("orde_send_yn");
		cmmCodeParam.setUserYn("Y");
		cmmCodeParam.setCode(String.valueOf(planOrderEntity.getCompanyId()));    		
 		CodeEntity codeinfo = codeService.getCodeInfo(cmmCodeParam);
 		String orderSendYn = codeinfo == null ? "N" : codeinfo.getEtc1();
 		
		 
		 
		 PlanOrderSearchDto stateParam = new PlanOrderSearchDto();
		 stateParam.setOrderId(planOrderEntity.getOrderSeq());
		 List<OrderStateEntity> orderStateList = service.getListState(stateParam);
		 
		 model.addAttribute("orderReq", planOrderEntity);
		 model.addAttribute("orderStateList", orderStateList);
		 model.addAttribute("orderSendYn", orderSendYn);
		 
		 
		 
	 	return "pages/order/edit";
	 }
	 
	 
	 
	 
	 //===================================================== REST API 호출 ajax====================================================================================
		@ResponseBody
		@PostMapping(value = "/ajaxList")
		public ResponseEntity<CustomApiResponse<List<PlanOrderEntity>>> ajaxList(
				HttpServletRequest request,
				@RequestBody PlanOrderSearchDto param
		) throws IOException {

			try{
				
				HttpSession session = request.getSession();
				AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
				if ( !StringUtils.equals("ADMIN", loginadminInfo.getAuthType()) && !StringUtils.equals("MANAGE", loginadminInfo.getAuthType()) ) {
					param.setCompanyId(loginadminInfo.getCompanyCode());
				}
				
				long resulCnt = service.getListCount(param);
				List<PlanOrderEntity> resultList = new ArrayList<PlanOrderEntity>(); 
				if(resulCnt>0) {
					resultList = service.getList(param);
				}

				return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

			} catch (Exception e) {

				e.printStackTrace();
				// TODO: handle exception
				return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

			}

		}

		
		  /**
	     * 2024-12-19 
	     * - 주문 전송
	     *
	     * @param searchRequest               서치 params
	     * @param List<CompanyListEntity> 사업자 조회 리스트
	     */
		@ResponseBody
	    @PostMapping("/orderSend")
	    public ResponseEntity<CustomApiResponse<Map<String,Object>>> orderSendTest( HttpServletRequest request , HttpServletResponse response,
	    		@RequestBody Map<String, String> param
	    		) throws IOException {
	    	
			Map<String,Object> result = new HashMap<String,Object>();
	    	
	    	try {
	    		
	    		String OrderId = param.get("orderId");
	    		String companyCd = param.get("companyCd");
	    		
	    		
	    		if("19".equals(companyCd)) {
	    			 // 고고 모바일
	    			 result = orderServiceGOGO.sendOrder(Integer.parseInt(OrderId));	 
	    		 } else if("22".equals(companyCd)) {
	    			 // 쉐이크 모바일
	    			 result = orderServiceSHAKE.sendOrder(Integer.parseInt(OrderId));
	    		 }
	    		
	    		return CustomApiResponse.success(ResponseCode.OK, result);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
			}
	    }	
		
		
		
		 /**
		 * 엑셀 다운로드
		 * @param param
		 * @return
		 * @throws Exception
		 */
		@ResponseBody
	    @PostMapping("/excelDown")
		public ResponseEntity<byte[]> applyExceldown(@RequestBody PlanOrderSearchDto param) throws Exception {
	    	
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>(); 
			List<PlanOrderEntity> list = service.getListWithoutLimit(param);
	    	for (PlanOrderEntity itm  :  list) {
	        	Map<String, Object> data = new LinkedHashMap<String, Object>();
	        	data.put("id", itm.getId());
	        	data.put("orderStateNm", itm.getOrderStateNm());
	        	data.put("entrType", itm.getEntrType().equals("01") ? "신규가입" : "번호이동");
	        	data.put("orderNm", itm.getOrderNm());
	        	data.put("orderPhone", itm.getOrderPhone() );
	        	data.put("telecomCd", itm.getTelecomCd());
	        	data.put("companyNm", itm.getCompanyNm());
	        	data.put("planNm", itm.getPlanNm());
	        	data.put("recomUserAdmin", itm.getRecomUserAdminId());
	        	data.put("pointPlanYn", itm.getPointPlanYn() ==0 ? "N" : "Y");
	        	data.put("orderDttm", CommonUtil.formatDateTime(itm.getOrderDttm()));
	        	data.put("openCompDttm", itm.getOpenCompDttm());
	        	dataList.add(data);    		
	    	}

	    	
	    	// 엑셀 헤더 설정
	    	String[] headers = {"No","상태", "가입유형", "신청자", "연락처", "MNO", "사업자", "요금제", "추천인", "포인트요금제여부", "신청일", "개통완료일"};
	    	
	        byte[] excelData = fileService.getExcelData(headers,dataList);

	        
	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=userList.xlsx")
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(excelData);
	    }		
		
		 
}
