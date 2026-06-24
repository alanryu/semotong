package kr.co.ucomp.common.cardCert.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kcb.module.v3.exception.OkCertException;
import kcb.org.json.JSONObject;
import kr.co.ucomp.common.restapi.entity.RestApiLogEntity;
import kr.co.ucomp.common.restapi.mapper.RestApiMapper;
import kr.co.ucomp.web.order.controller.PlanOrderController;
import lombok.extern.slf4j.Slf4j;


/**
*
* @author 조일근
* @since 2024.12.25
* @version v1.0
*/
@Controller
@RequestMapping(value = "/cardcert")
@Slf4j

public class cardCertController {

	
		// 카드 본인 인증
			@Value("${coocon.cardcert.api.base-url}") String cardBaseUrl;
			@Value("${coocon.cardcert.api.cp-cd}") String cardCPCD;
			@Value("${coocon.cardcert.api.ret-url}") String cardRetUrl;
			@Value("${coocon.cardcert.api.target-server}") String cardTargetServer;
			@Value("${coocon.cardcert.api.req-site-nm}") String reqSiteNm;
			@Value("${coocon.cardcert.api.license-dir}") String licenseDir;
			
			@Autowired private RestApiMapper restApiMapper;
	
	   /**
		  * 2025-02-20 조일근
		  * 01) 카드인증 2단계 
		  * @param CommCodeSearchDto
		  * @param 
	 * @throws UnsupportedEncodingException 
	 * @throws OkCertException 
		  */
		 @GetMapping("/cardcert_popup")
		 public String  cardcert_popup(  HttpServletRequest request , HttpServletResponse response,Model model) throws UnsupportedEncodingException, OkCertException  {
		 	
			 String license = licenseDir + "/"+ cardCPCD + "_CID_01_" + cardTargetServer +"_AES_license.dat";
			 String svcName = "CID_CARD_POPUP_START";
			 String popupUrl = cardBaseUrl + "/popup/main/start.do";
			 
			 
			 System.out.println("license file ===>>>>>>>>>>>>>>>>>" + license);
			 
		    JSONObject reqJson = new JSONObject();
		    reqJson.put("RTN_URL", cardRetUrl);
		    reqJson.put("REQ_SITE_NM", reqSiteNm);
		    
		    String reqStr = reqJson.toString();
		    
		    
		    kcb.module.v3.OkCert okcert = new kcb.module.v3.OkCert();
		    
		    String resultStr = okcert.callOkCert(cardTargetServer, cardCPCD, svcName, license,  reqStr);
		    

		    
		    JSONObject resJson = new JSONObject(resultStr);
		    
		    String RSLT_CD =  resJson.getString("RSLT_CD");
		    String RSLT_MSG = resJson.getString("RSLT_MSG");
		    
			String MDL_TKN = "";
			
			boolean succ = false;
		    if ("T300".equals(RSLT_CD) && resJson.has("MDL_TKN") ) { // 정상적으로 모듈 호출 성공한 경우
		            MDL_TKN = resJson.getString("MDL_TKN");
					succ = true;
		    }
			    
		    
		    model.addAttribute("popupUrl", popupUrl);
		    model.addAttribute("CP_CD", cardCPCD);
		    model.addAttribute("MDL_TKN", MDL_TKN);
		    
		    model.addAttribute("RSLT_CD", RSLT_CD);
		    model.addAttribute("RSLT_MSG", RSLT_MSG);
		    
		    
		    
		    HttpSession session = request.getSession();
		    session.removeAttribute("CUST_CERT_SP");
			session.removeAttribute("CUST_CERT_CI");
			session.removeAttribute("CUST_CERT_DI");
			session.removeAttribute("CUST_CERT_NAME");
			session.removeAttribute("CUST_CERT_BIRTHDAY");
			 
			 
		 	return "pages/cardcert/cardcert_popup";
		 }
		 
		 
		  /**
		  * 2025-02-20 조일근
		  * 01) 카드인증 2단계 
		  * @param CommCodeSearchDto
		  * @param 
		 * @throws OkCertException 
		  */
		 @GetMapping("/cardcert_res")
		 public String  cardcert_res(  HttpServletRequest request , HttpServletResponse response,Model model) throws OkCertException  {
		 	
			 
			 HttpSession session = request.getSession();
			 String certYn = "N";
			 
			 String MDL_TKN = request.getParameter("MDL_TKN");
			 
			 String license = licenseDir + "/"+ cardCPCD + "_CID_01_" + cardTargetServer +"_AES_license.dat";
			 String svcName = "CID_CARD_POPUP_RESULT";
			 
			 JSONObject reqJson = new JSONObject();
			reqJson.put("MDL_TKN", MDL_TKN);
			
			String reqStr = reqJson.toString();
			
			/**************************************************************************
			okcert3 실행
			**************************************************************************/
			kcb.module.v3.OkCert okcert = new kcb.module.v3.OkCert();
			String resultStr = okcert.callOkCert(cardTargetServer, cardCPCD, svcName, license,  reqStr);
			
			JSONObject resJson = new JSONObject(resultStr);
			
			String TX_SEQ_NO = resJson.getString("TX_SEQ_NO");
		    String RSLT_CD =  resJson.getString("RSLT_CD");
		    String RSLT_MSG =  resJson.getString("RSLT_MSG");
			
			String CRD_CD = "";
			String CI_RQST_DT_TM = "";
		    String RSLT_NAME = "";
		    String RSLT_BIRTHDAY = "";
		    String RSLT_SEX_CD = "";
		    String RSLT_NTV_FRNR_CD = "";
		    String DI = "";
			String CI = "";
			String CI_UPDATE = "";
			
			String RETURN_MSG = "";
			
			if ( resJson.has("CRD_CD") && !resJson.isNull("CRD_CD"))	CRD_CD = resJson.getString("CRD_CD");
			if ( resJson.has("RETURN_MSG") && !resJson.isNull("RETURN_MSG"))	RETURN_MSG = resJson.getString("RETURN_MSG");
				//***************************************************************************************
				// RSLT_CD (결과코드)가 T000 인 경우, 인증에 성공한 것이므로 그에 맞는 비즈니스 처리 요망. 그외 결과코드는 설명서 참고.
				//***************************************************************************************
			if ("T000".equals(RSLT_CD)) {
				CI_RQST_DT_TM = resJson.getString("CI_RQST_DT_TM");
			    RSLT_NAME =  resJson.getString("RSLT_NAME");
			    RSLT_BIRTHDAY =  resJson.getString("RSLT_BIRTHDAY");
			    RSLT_SEX_CD =  resJson.getString("RSLT_SEX_CD");
			    RSLT_NTV_FRNR_CD =  resJson.getString("RSLT_NTV_FRNR_CD");
			    DI =  resJson.getString("DI");
				CI =  resJson.getString("CI");
				CI_UPDATE =  resJson.getString("CI_UPDATE");
				session.setAttribute("CUST_CERT_SP", "CARD");
				session.setAttribute("CUST_CERT_CI", CI);
				session.setAttribute("CUST_CERT_DI", CI);
				session.setAttribute("CUST_CERT_NAME", RSLT_NAME);
				session.setAttribute("CUST_CERT_BIRTHDAY", RSLT_BIRTHDAY);
				certYn = "Y";
				
				
				RestApiLogEntity logparam = new RestApiLogEntity();
				logparam.setApiCode("CARD_CERT_RES");
				logparam.setApiName("신용카드 인증 결과");
				logparam.setApiUrl("/cardcert/cardcert_res");
				logparam.setReqMsg(resJson.toString());
				logparam.setResBody("");
				logparam.setResMsg("");
				restApiMapper.createLog(logparam);
				
			}
			
			
			model.addAttribute("certYn", certYn);
			model.addAttribute("RSLT_CD", RSLT_CD);
			model.addAttribute("RSLT_MSG", RSLT_MSG);
			
			 
		 	return "pages/cardcert/cardcert_res";
		 }
		 

		 
}
