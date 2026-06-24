package kr.co.ucomp.web.order.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.ucomp.common.encrypt.AESEncryptUtilGOGO;
import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.order.entity.OrderStateEntity;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;
import kr.co.ucomp.web.order.mapper.PlanOrderMapper;
import kr.co.ucomp.web.order.service.OrderAPIGOGOService;

@Service("sendOrderService")
public class OrderAPIGOGOServiceImpl implements OrderAPIGOGOService {

	 @Autowired private PlanOrderMapper mapper;
	 @Autowired CommCodeMngService codeService;
	 @Autowired private RestTempletUtil rest;
	 
	 
	// 고고 모바일 
	@Value("${gogo.api.base-url}") String BaseUrl;
	@Value("${gogo.api.auth-key}") String ClientKey;
	@Value("${gogo.api.order-req-url}") String OrderReqUrl;
	
	@Override
	public Map<String, Object> sendOrder(int id) {
		
		Map<String,Object> result = new HashMap<String,Object>();		
		Map<String,Object> resultApi = new HashMap<String,Object>();
		PlanOrderEntity order = mapper.getDetail(id);
		CommCodeSearchDto cmmCodeParam = new CommCodeSearchDto();
		
		String errMsg = "";
		
		try {
			
			
			cmmCodeParam = new CommCodeSearchDto();
			cmmCodeParam.setCodeGroup("orde_send_yn");
			cmmCodeParam.setUserYn("Y");
			cmmCodeParam.setCode(String.valueOf(order.getCompanyId()));    		
    		CodeEntity codeinfo = codeService.getCodeInfo(cmmCodeParam);
    		
    		if(!"Y".equals(codeinfo.getEtc1())) {
    			result.put("errMsg", "N");
    			return result;
    		}
    		

			Map<String,Object> sendParam = new HashMap<String,Object>();
			
			sendParam.put("order_id", order.getOrderSeq());
			sendParam.put("plan_idx", order.getPlanCd());
			sendParam.put("join_type", "01".equals(order.getEntrType()) ? "1" : "2");  // entr_type 가입방식. 가능한 값: - 01: 신규가입  - 02: 번호이동 - 03:기기변경
			sendParam.put("client_name", "1");
			sendParam.put("c_name", order.getOrderNm());		
			sendParam.put("jumin", AESEncryptUtilGOGO.encrypt(order.getUserSsn()) );		
			sendParam.put("agent_name", "");
			sendParam.put("agent_jumin", "");
			/*
			 dis_auth_join_div	부정가입 방지 구분
			driver_number	운전면허번호
			auth_org_dt	발급일자
			 */
			
			sendParam.put("auth2_type", "01".equals(order.getDisAuthJoinDiv()) ? "1" : "2" ); //부정가입방지 확인정보 중 신분증 구분. 가 능한 값: - 01: 주민등록증 - 02: 운전면허 증
			
			// 신분증 유효성 
			if("01".equals(order.getDisAuthJoinDiv())) {
				// 주민 등록증
				sendParam.put("jumin_date", order.getAuthOrgDt());
				sendParam.put("driver_number", "");
				sendParam.put("driver_date", "");
			} else {
				// 운전면허증
				sendParam.put("jumin_date", "");
				sendParam.put("driver_number", AESEncryptUtilGOGO.encrypt(order.getDriverNumber()));
				sendParam.put("driver_date", order.getAuthOrgDt());
			}
			
			sendParam.put("citation", "C".equals(order.getIdentityMethod()) ? "C" : "K"); // identity_method 본인인증방식. 가능한 값: - C: 신용카드 -S:간편인증 - X: 공인인증서
			sendParam.put("di", "");
			sendParam.put("marketing_yn", "1");
			
			if("01".equals(order.getEntrType())) {
				//  01: 신규가입
				sendParam.put("hope_hp", order.getNewPhoneNum1());
				sendParam.put("hope_hp2", order.getNewPhoneNum2());
				sendParam.put("move_telecom", "");
				sendParam.put("move_telecom_code", "");
				sendParam.put("move_telecom_sub", "");
				sendParam.put("move_hp", "");
				
			} else {
				
				String preTelecomCd = order.getPreTelecomCd();
				String preMvnoCd = order.getPreMvnoCd();
				
				if(preTelecomCd.endsWith("M")) {
					sendParam.put("move_telecom", "알뜰폰");
					
					if(StringUtils.isNotBlank(preMvnoCd)) {
						cmmCodeParam = new CommCodeSearchDto();
						cmmCodeParam.setUserYn("Y");
						cmmCodeParam.setCodeGroup("pre_mvno_cd");
						cmmCodeParam.setCode(preMvnoCd);
						String preMvnoNm = codeService.getCodeInfo(cmmCodeParam) != null ? codeService.getCodeInfo(cmmCodeParam).getCodeName() : "";					
						sendParam.put("move_telecom_sub", preMvnoNm);
					}
				} else {
					preTelecomCd = "LGT".equals(preTelecomCd) ? "LG U+" : preTelecomCd;
					sendParam.put("move_telecom", preTelecomCd);
					sendParam.put("move_telecom_sub", "");
				}
				
				sendParam.put("move_telecom_code", preMvnoCd);
				
				
				
				
				
				sendParam.put("move_hp", order.getPrePhoneNum());
				sendParam.put("hope_hp", "");
				sendParam.put("hope_hp2", "");
				
			}
			
			
			//유심구매방법 : 가능한값 -> 01 : 유심 택배요청 , 02: 유심구매, 03: esim
			if("01".equals(order.getUsimPayType())) {
				// 유심 택배 요청
				sendParam.put("usim_pay_type", "2"); 
				sendParam.put("usim_pay_model", "");
				sendParam.put("usim_pay_number", "");
				
				sendParam.put("esim_model", "");
				sendParam.put("esim_memory", "");
				sendParam.put("esim_number", "");
				sendParam.put("esim_imei1", "");
				sendParam.put("esim_imei2", "");
				sendParam.put("esim_eid", "");
				
				
			} else if("02".equals(order.getUsimPayType())) {
				// 유심구매
				sendParam.put("usim_pay_type", "1");
				if(StringUtils.isNotBlank(order.getUsimModelNm())) {
					sendParam.put("usim_pay_model", order.getUsimModelNm());
				} else {
					sendParam.put("usim_pay_model",order.getUsimSerialNum().substring(0, 5));
				}
				
				sendParam.put("usim_pay_number", order.getUsimSerialNum());
				
				sendParam.put("esim_model", "");
				sendParam.put("esim_memory", "");
				sendParam.put("esim_number", "");
				sendParam.put("esim_imei1", "");
				sendParam.put("esim_imei2", "");
				sendParam.put("esim_eid", "");
				
			} else if("03".equals(order.getUsimPayType())) {
				// Esim
				sendParam.put("usim_pay_type", "6");
				sendParam.put("usim_pay_model", "");
				sendParam.put("usim_pay_number", "");
				
				cmmCodeParam = new CommCodeSearchDto();
				cmmCodeParam.setUserYn("Y");
				cmmCodeParam.setCodeGroup("esim_model");
				cmmCodeParam.setCode(order.getEsimModel());
				String esimModel = codeService.getCodeInfo(cmmCodeParam) != null ? codeService.getCodeInfo(cmmCodeParam).getCodeName() : "";
				
				sendParam.put("esim_model", esimModel);
				sendParam.put("esim_memory", order.getEsimMemory());
				sendParam.put("esim_number", order.getEsimNumber());
				sendParam.put("esim_imei1", order.getEsimImei1());
				sendParam.put("esim_imei2", order.getEsimImei2());
				sendParam.put("esim_eid", order.getEsimEid());
				
			}
			
			sendParam.put("pay_tel", order.getOrderPhone());
			sendParam.put("pay_tel2", "");
			sendParam.put("pay_email", order.getOrderEmail());
			sendParam.put("pay_zipcode", order.getOrderZipCd());
			sendParam.put("pay_addr1", order.getOrderAddr1());
			sendParam.put("pay_addr2", order.getOrderAddr2());
			
			//청구서 발송 방식. 가능한 값: - 01: 우편발송 - 02: 이메일발송 - 03: MMS(문자) - 04: 해피콜에서 확인
			sendParam.put("bill_type", "02".equals(order.getBillCd()) ? "1" : "2");
			
			//자동이체 결제방식. 가능한 값: - 01: 은행 계좌이체 - 02: 신용카드결제
			sendParam.put("auto_payment", "01".equals(order.getSettleMethod()) ? "1" : "2" );
			
			if("01".equals(order.getSettleMethod())) {
				// 01: 은행 계좌이체
				String bankCode = order.getBankCd();
				
				cmmCodeParam = new CommCodeSearchDto();
				cmmCodeParam.setUserYn("Y");				
				cmmCodeParam.setCodeGroup("bank_co_cd_gogo");
				cmmCodeParam.setEtc1(bankCode);
				String gogoBankCode = codeService.getCodeInfo(cmmCodeParam) != null ? codeService.getCodeInfo(cmmCodeParam).getCode() : "";	
				
				sendParam.put("pay_bank_name", order.getOwnerNm());
				sendParam.put("pay_bank_relation", "1");
				sendParam.put("pay_bank_code", gogoBankCode);
				sendParam.put("pay_bank_number", AESEncryptUtilGOGO.encrypt(order.getBankAcctNo()));
				sendParam.put("pay_bank_jumin",order.getUserSsn());
				sendParam.put("pay_bank_relation_tel", "");
				
				sendParam.put("pay_card_name", "");
				sendParam.put("pay_card_relation", "");
				sendParam.put("pay_card_code", "");
				sendParam.put("pay_card_number", "");
				sendParam.put("pay_card_yy", "");
				sendParam.put("pay_card_mm", "");
				sendParam.put("pay_card_jumin", "");
			} else {
				sendParam.put("pay_bank_name", "");
				sendParam.put("pay_bank_relation", "");
				sendParam.put("pay_bank_code", "");
				sendParam.put("pay_bank_number", "");
				sendParam.put("pay_bank_jumin", "");
				sendParam.put("pay_bank_relation_tel", "");
				
				// 02: 카드 자동이체
				String cardCode = order.getCardCd();
				
				cmmCodeParam = new CommCodeSearchDto();
				cmmCodeParam.setUserYn("Y");				
				cmmCodeParam.setCodeGroup("card_co_cd_gogo");
				cmmCodeParam.setEtc1(cardCode);
				String gogoCardCode = codeService.getCodeInfo(cmmCodeParam) != null ? codeService.getCodeInfo(cmmCodeParam).getCode() : "ETC";
				
				String cardValidDt = order.getCardValidDt();
				String cardValidYY = cardValidDt.substring(0,4);
				String cardValidMM = cardValidDt.substring(4);
				
				sendParam.put("pay_card_name", order.getOwnerNm());
				sendParam.put("pay_card_relation", "1");
				sendParam.put("pay_card_code", gogoCardCode);
				sendParam.put("pay_card_number", AESEncryptUtilGOGO.encrypt(order.getCardNum()));
				sendParam.put("pay_card_yy", cardValidYY);
				sendParam.put("pay_card_mm", cardValidMM);
				sendParam.put("pay_card_jumin", order.getUserSsn());
			}
			
			
			
			sendParam.put("state", "1");
			sendParam.put("final_date", "");
			sendParam.put("open_tel", "");
			sendParam.put("account_number", "");
			
			sendParam.put("cert_sel", "C".equals(order.getIdentityMethod()) ? "C" : "K"); // identity_method 본인인증방식. 가능한 값: - C: 신용카드 -S:간편인증 - X: 공인인증서
			sendParam.put("c_providerDevCd", "");
			sendParam.put("ci", order.getIdentityCi());
			sendParam.put("ip_addr", order.getCustomerIp());
			sendParam.put("request_date", order.getOrderDttm());
			
			
			
			Map<String,String> sendHeader = new HashMap<String,String>();
			
			String apiUrl = BaseUrl + OrderReqUrl;
			String method = "POST";
			
			sendHeader.put("Authorization", ClientKey);
    		
    		resultApi = rest.sendRestApi("REQ_ORDERSEND_GOGO",apiUrl, method, sendParam, sendHeader);
    		String resStatusCode = resultApi.get("resultStatus") !=null ? (String) resultApi.get("resultStatus") : "999";
    		String orderSendStatus = "";
    		String orderSendYn = "N";
    		String orderIdHost = "";
    		if("200".equals(resStatusCode.subSequence(0, 3))) {
    			Map<String, Object> resbody =  new HashMap<String, Object>();
				if(resultApi.get("resultBody") != null) {
					resbody = (Map<String, Object>) resultApi.get("resultBody");
					String sendResCode = resbody.get("errCode") == null ? "9999" : (String) resbody.get("errCode");
					String sendResMsg = resbody.get("errMessage") == null ? "9999" : (String) resbody.get("errMessage");
					result.put("resbody", resbody);
					if("0000".equals(sendResCode)) {
						errMsg = "sucess";
						orderSendStatus = "주문전송";
						orderSendYn = "Y";
						orderIdHost = resbody.get("o_no") == null ? "" : (String) resbody.get("o_no");
					} else {
						orderSendStatus = sendResMsg;
					}
					
				} else {
					orderSendStatus = "주문 전송 실패 - 전송 결과 없음";
				}
    		} else {
    			orderSendStatus = "주문 전송 실패 - 전송 오류";
    		}
    		
    		// 1. 주문 업데이트
    		if("Y".equals(orderSendYn)) {
    			PlanOrderEntity updateSendinfo = new PlanOrderEntity();
        		updateSendinfo.setId(order.getId());
        		updateSendinfo.setModifiedId(999);
        		updateSendinfo.setOrderSendYn(orderSendYn);
        		updateSendinfo.setOrderSendDttm(CommonUtil.getDatetime("yyyyMMddHHmmss"));
        		updateSendinfo.setOrderState("01");
        		updateSendinfo.setOrderStateDttm(CommonUtil.getDatetime("yyyyMMddHHmmss"));
        		updateSendinfo.setOrderStateHost("1");
        		updateSendinfo.setOrderSendState(orderSendStatus);
        		updateSendinfo.setOrderIdHost(orderIdHost);
        		updateSendinfo.setIdentityCi(" ");
        		mapper.update(updateSendinfo);
        		
        		
        		// 주문 히스토리 생성
        		OrderStateEntity orderHist = new OrderStateEntity();
        		orderHist.setOrderId(order.getOrderSeq());
        		orderHist.setOrderState("01");
        		orderHist.setOrderStateHost("1");
        		orderHist.setOrderMemo("주문 전송 성공");
        		mapper.createOrderState(orderHist);

    		} else {
    			
    			PlanOrderEntity updateSendinfo = new PlanOrderEntity();
        		updateSendinfo.setId(order.getId());
        		updateSendinfo.setModifiedId(999);
        		updateSendinfo.setOrderSendState(orderSendStatus);
        		updateSendinfo.setOrderSendYn("N");
        		mapper.update(updateSendinfo);
    		}
    		
    		
    		
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errMsg = e.getMessage();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errMsg = e.getMessage();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errMsg = e.getMessage();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errMsg = e.getMessage();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errMsg = e.getMessage();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errMsg = e.getMessage();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errMsg = e.getMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errMsg = e.getMessage();
		}
		
		
		result.put("errMsg", errMsg);
		
		// TODO Auto-generated method stub
		return result;
	}

}
