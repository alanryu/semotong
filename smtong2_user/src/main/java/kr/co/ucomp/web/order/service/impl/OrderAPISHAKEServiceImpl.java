package kr.co.ucomp.web.order.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.ucomp.common.encrypt.KISA_SEED_CBC;
import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.order.entity.OrderStateEntity;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;
import kr.co.ucomp.web.order.mapper.PlanOrderMapper;
import kr.co.ucomp.web.order.service.OrderAPISHAKEService;

@Service("sendOrderServiceSHAKE")
public class OrderAPISHAKEServiceImpl implements OrderAPISHAKEService {

	 @Autowired private PlanOrderMapper mapper;
	 @Autowired CommCodeMngService codeService;
	 @Autowired private RestTempletUtil rest;
	 
	 
	// 쉐이크 모바일 
	@Value("${minigate.api.base-url}") String BaseUrl;
	@Value("${minigate.api.auth-key}") String ClientKey;
	@Value("${minigate.api.client-id}") String ClientId;
	@Value("${minigate.api.order-req-url}") String OrderReqUrl;
	@Value("${minigate.api.enc-key}") String encryptKey;
	
	
	
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
    		CodeEntity codeinfo = codeService.getCode(cmmCodeParam);
    		
    			
    		
    		
    		if(!"Y".equals(codeinfo.getEtc1())) {
    			result.put("errMsg", "N");
    			return result;
    		}

			Map<String,Object> sendParam = new HashMap<String,Object>();
			
			
			
			
			
			sendParam.put("providerSrlNo",order.getId()); // 주문 관리 번호(제휴사 등록번호)
			sendParam.put("custAuthCi",KISA_SEED_CBC.encrypt(encryptKey, order.getIdentityCi()) ); // ci 값
			sendParam.put("custAuthNm",KISA_SEED_CBC.encrypt(encryptKey, order.getOrderNm())); // 본인인증 이름
			sendParam.put("custAuthIdntNo",KISA_SEED_CBC.encrypt(encryptKey, order.getUserSsn())); //본인인증 주민등록번호
			sendParam.put("custAuthTlphNo",KISA_SEED_CBC.encrypt(encryptKey, order.getOrderPhone())); //본인인증 핸드폰 번호
			sendParam.put("gdCd",order.getPlanCd()); // 요금제코드
			sendParam.put("authYn","Y"); // 본인인증 완료여부(Y:고정값)
			sendParam.put("isForeign","0"); // 내/외국인(0: 국내, 1: 국외)
			sendParam.put("remoteAddr",order.getCustomerIp()); // ip정보
			sendParam.put("authCd","S".equals(order.getIdentityMethod()) ? "KAKAO" : "C"); // [본인인증 종류] (T: 간편인증 KAKAO: 카카오, TOSS: 토스 , NAVER: 네이버 / C: 카드)
			sendParam.put("applCd","OL"); // 신청구분(OL: 온라인신청 고정)
			sendParam.put("applStat","03"); // 신청상태(03: 신청완료)
			sendParam.put("applCustNm",KISA_SEED_CBC.encrypt(encryptKey, order.getOrderNm())); //신청인 성명
			sendParam.put("custTypeCd","I"); //고객유형(B: 법인, I: 개인, O: 개인사업자)
			sendParam.put("custDtlTypeCd","N1"); // [고객세부유형] F : 외국인 , F1 : 순수외국인, F2 : 재외국인,F3 : 외국국적동포(시민권자) , N1 : 내국인,N2 : 재외국민
			sendParam.put("custWorldCd","K"); //내국인외국인구분코드(K: 내국인, F: 외국인)
			sendParam.put("custIdntNoIndCdPc0","01"); //고객식별번호구분코드(신규일 경우 필수) (01: 개인/개인사업자, 05: 외국인)
			sendParam.put("custIdntNoIndCdSmart","01"); //고객식별번호구분코드(번호이동일 경우 필수) 01 : 주민등록번호
			sendParam.put("custIdntNo",KISA_SEED_CBC.encrypt(encryptKey, order.getUserSsn())); //고객식별번호(주민번호)
			sendParam.put("slsCmpnCd","MGM"); // 판매회사코드(MGM)
			sendParam.put("crprNo",""); // 법인번호(사용 안함)
			sendParam.put("myslAgreYn","Y"); // 인증방식 본인동의여부(Y)
			
			

			// 신분증 유효성 (01: 주민등록증 - 02: 운전면허 증)
			if("01".equals(order.getDisAuthJoinDiv())) {
				// 주민 등록증
				sendParam.put("authDiv","REGID"); //[인증방식]	REGID : 주민등록증, DRIVE : 운전면허증
				sendParam.put("rsdcrtIssuDate", order.getAuthOrgDt()); // 인증방식 발급일자
				sendParam.put("lcnsNo",""); //면허번호(인증방식이 운전면허증일 경우)
				sendParam.put("lcnsRgnCd",""); //면허지역코드(인증방식이 운전면허증일 경우)
			} else {
				// 운전면허증
				sendParam.put("authDiv","DRIVE"); //[인증방식]	REGID : 주민등록증, DRIVE : 운전면허증
				sendParam.put("rsdcrtIssuDate", order.getAuthOrgDt()); // 인증방식 발급일자
				sendParam.put("lcnsNo",KISA_SEED_CBC.encrypt(encryptKey, order.getDriverNumber())); //면허번호(인증방식이 운전면허증일 경우)
				sendParam.put("lcnsRgnCd",order.getDriverNumber().substring(0, 2)); //면허지역코드(인증방식이 운전면허증일 경우)
			}
			
			
			
			// 가입방식 (01: 신규가입, 02 번호이동)
			if("01".equals(order.getEntrType())) {
				sendParam.put("joinType","2"); // 가입유형(1: 번호이동, 2: 신규가입)
				sendParam.put("applTp","NAC"); // 신청유형(신규가입: NAC, 번호이동: MNP)
				sendParam.put("tlphNoPtrn1",order.getNewPhoneNum1());
				sendParam.put("tlphNoPtrn2",order.getNewPhoneNum2());
				
				
				sendParam.put("nowSbscTlcmCd",""); // 현재가입텔레콤코드(번호이동인 경우) 
				sendParam.put("nowCustTlphNo",""); // 현재고객전화번호
				
			} else {
				sendParam.put("joinType","1"); // 가입유형(1: 번호이동, 2: 신규가입)
				sendParam.put("applTp", "MNP"); // 신청유형(신규가입: NAC, 번호이동: MNP)
				
				sendParam.put("tlphNoPtrn1","");
				sendParam.put("tlphNoPtrn2","");
				sendParam.put("nowSbscTlcmCd",order.getPreMvnoCd()); // 현재가입텔레콤코드(번호이동인 경우) 
				sendParam.put("nowCustTlphNo",KISA_SEED_CBC.encrypt(encryptKey,order.getPrePhoneNum())); // 현재고객전화번호(번호이동인 경우)
				
			}
			
			sendParam.put("applEmailAdrsNm",KISA_SEED_CBC.encrypt(encryptKey, order.getOrderEmail())); //신청인 이메일주소명
			sendParam.put("applTlphNo",KISA_SEED_CBC.encrypt(encryptKey, order.getOrderPhone())); //신청인 전화번호
			sendParam.put("applZipNo",order.getOrderZipCd()); // 신청인 우편번호
			sendParam.put("applAddr1",KISA_SEED_CBC.encrypt(encryptKey, order.getOrderAddr1())); // 신청인 기본주소
			sendParam.put("applAddr2",order.getOrderAddr2()); // 신청인 상세주소
			sendParam.put("memberOrderStat","00"); //고객최종상태(00: 신청)
			sendParam.put("providerNm","세모통"); // 제휴사명(세모통)
			sendParam.put("providerCd","1018"); // 제휴사코드(1018)
			sendParam.put("adultYn","Y"); //성인여부(Y/N)
			
			
			//청구서 발송 방식. 01: 우편발송 - 02: 이메일발송 - 03: MMS(문자) - 04: 해피콜에서 확인
			sendParam.put("rqsshtPprfrmCd","02".equals(order.getBillCd()) ? "CB" : "MB"); // [청구서양식코드] (MB: MMS , CB: 이메일, LX: 우편)
			sendParam.put("rqsshtEmlAdrsNm",KISA_SEED_CBC.encrypt(encryptKey, order.getOrderEmail())); // 청구서 이메일주소명
			sendParam.put("emailBillAgreYn","02".equals(order.getBillCd()) ? "Y" : ""); // 이메일청구동의여부(Y/N)
			sendParam.put("billZipNo",""); // 청구서 우편번호(청구서 우편 사용 안함)
			sendParam.put("billAddr1",""); // 청구서 기본주소(청구서 우편 사용 안함)
			sendParam.put("billAddr2",""); // 청구서 상세주소(청구서 우편 사용 안함)
			
			//자동이체 결제방식. 가능한 값: - 01: 은행 계좌이체 - 02: 신용카드결제
			sendParam.put("blpymMthdCd", "01".equals(order.getSettleMethod()) ? "D" : "C" );// [납부방법코드] (C:카드,D:계좌)
			sendParam.put("duedatDateIndCd", "25" );// [납기일자구분코드] 18,21,25
			
			if("01".equals(order.getSettleMethod())) {
				// 01: 은행 계좌이체
				String bankCode = order.getBankCd();				
				cmmCodeParam = new CommCodeSearchDto();
				cmmCodeParam.setUserYn("Y");				
				cmmCodeParam.setCodeGroup("bank_co_cd_shake");
				cmmCodeParam.setEtc1(bankCode);
				String bankCodeSHAKE = codeService.getCode(cmmCodeParam) != null ? codeService.getCode(cmmCodeParam).getCode() : "";
				String bankNameSHAKE = codeService.getCode(cmmCodeParam) != null ? codeService.getCode(cmmCodeParam).getCodeName() : "";

				sendParam.put("crdtCardCd","");// 카드사(납부방법이 카드일 경우)
				sendParam.put("crdtCardExprDate",""); // 신용카드만기일자(납부방법이 카드일 경우)
				sendParam.put("blpymMthdIdntNo",KISA_SEED_CBC.encrypt(encryptKey, order.getBankAcctNo())); // 납부방법식별번호
				
				sendParam.put("bankCd",bankCodeSHAKE); //은행코드
				sendParam.put("bankNm",bankNameSHAKE); //은행명
				
				
			} else {
				// 02: 카드 자동이체
				sendParam.put("crdtCardCd",order.getCardCd()); // 카드사(납부방법이 카드일 경우)
				sendParam.put("crdtCardExprDate",order.getCardValidDt()); // 신용카드만기일자(납부방법이 카드일 경우)
				sendParam.put("blpymMthdIdntNo",KISA_SEED_CBC.encrypt(encryptKey, order.getCardNum())); // 납부방법식별번호
				
				sendParam.put("bankCd",""); //은행코드
				sendParam.put("bankNm",""); //은행명
				
				
			}
			sendParam.put("blpymCustNm",KISA_SEED_CBC.encrypt(encryptKey, order.getOwnerNm())); // 납부고객명
			sendParam.put("blpymCustIdntNo",KISA_SEED_CBC.encrypt(encryptKey, order.getUserSsn())); //납부고객식별번호(주민번호)
						
			sendParam.put("mngmAgncId","VMG0016"); // 관리대리점아이디(VMG0016)
			sendParam.put("cntpntCd","V000080413"); // 접점코드(V000080413)
			// 대리인 사용 안함
			sendParam.put("agntRltnCd",""); // 대리인관계코드(미성년자 가입일 경우) 01:부 ,02:모, 03:후견인,04:연대보증인, 05:가족 ,06:친척 ,07: 친구,08: 회사동료 ,10: 그 외 ,11: 한정 위탁, 12: 지정 위탁
			sendParam.put("agntCustNm",""); // 대리인고객명
			sendParam.put("agentCustIdntNo",""); // 대리인 주민번호
			sendParam.put("agentTlphNo",""); // 대리인 핸드폰번호

			
			String usimPayType = order.getUsimPayType();
			
			
			//유심구매방법 : 가능한값 -> 01 : 유심 택배요청 , 02: 유심구매, 03: esim
			if("01".equals(order.getUsimPayType())) {
				// 택배배송
				sendParam.put("usimType","USIM"); // 유심 유형(ESIM, USIM)
				sendParam.put("usimPurchaseYn","N" ); // 유심 구매여부(Y: 구매, N: 비구매)
				sendParam.put("usimPurchaseCd",""); // 유심 구매처코드(유심 구매일 경우) 
				sendParam.put("usimNo",""); // 유심 번호(유심 구매일 경우)
				sendParam.put("deliveryCustNm",KISA_SEED_CBC.encrypt(encryptKey, order.getRecvNm())); // 수령인 성명
				sendParam.put("deliveryCustTlphNo",KISA_SEED_CBC.encrypt(encryptKey, order.getRecvPhone())); // 수령인 휴대폰번호
				sendParam.put("deliveryZipNo",StringUtils.isBlank(order.getRecvZipCd()) ? order.getOrderZipCd() : order.getRecvZipCd());  // 수령인 우편번호				
				String dlbyaddr =  StringUtils.isBlank(order.getRecvAddr1()) ? order.getOrderAddr1() : order.getRecvAddr1();
				sendParam.put("deliveryAddr1",KISA_SEED_CBC.encrypt(encryptKey, dlbyaddr)); // 수령인 주소
				sendParam.put("deliveryAddr2",StringUtils.isBlank(order.getRecvAddr2()) ? order.getOrderAddr2() : order.getRecvAddr2()); // 수령인상세주소

				sendParam.put("deviceModel","");// esim 모델
				sendParam.put("eid","");// esim eid
				sendParam.put("imei1","");// esim imei1
				sendParam.put("imei2","");// esim imei2
				
			} else if("02".equals(order.getUsimPayType())) {
				// 유심구매
				sendParam.put("usimType","USIM"); // 유심 유형(ESIM, USIM)
				sendParam.put("usimPurchaseYn","02".equals(usimPayType) ? "Y" : "N" ); // 유심 구매여부(Y: 구매, N: 비구매)
				sendParam.put("usimPurchaseCd",order.getUsimBuy()); // 유심 구매처코드(유심 구매일 경우) 
				sendParam.put("usimNo",KISA_SEED_CBC.encrypt(encryptKey,order.getUsimSerialNum())); // 유심 번호(유심 구매일 경우)
				
				sendParam.put("deliveryCustNm",KISA_SEED_CBC.encrypt(encryptKey, "-")); // 수령인 성명
				sendParam.put("deliveryCustTlphNo",KISA_SEED_CBC.encrypt(encryptKey, "-"));// 수령인 휴대폰번호
				sendParam.put("deliveryZipNo","-"); // 수령인 우편번호				
				sendParam.put("deliveryAddr1",KISA_SEED_CBC.encrypt(encryptKey, "-")); // 수령인 주소
				sendParam.put("deliveryAddr2","-"); // 수령인상세주소
				
				sendParam.put("deviceModel","");// esim 모델
				sendParam.put("eid","");// esim eid
				sendParam.put("imei1","");// esim imei1
				sendParam.put("imei2","");// esim imei2
				
			} else if("03".equals(order.getUsimPayType())) {
				// Esim
				sendParam.put("usimType","ESIM"); // 유심 유형(ESIM, USIM)
				sendParam.put("usimPurchaseYn","N" ); // 유심 구매여부(Y: 구매, N: 비구매)
				sendParam.put("usimPurchaseCd",""); // 유심 구매처코드(유심 구매일 경우) 
				sendParam.put("usimNo",""); // 유심 번호(유심 구매일 경우)
				
				sendParam.put("deliveryCustNm",KISA_SEED_CBC.encrypt(encryptKey, "-")); // 수령인 성명
				sendParam.put("deliveryCustTlphNo",KISA_SEED_CBC.encrypt(encryptKey, "-"));// 수령인 휴대폰번호
				sendParam.put("deliveryZipNo","-"); // 수령인 우편번호				
				sendParam.put("deliveryAddr1",KISA_SEED_CBC.encrypt(encryptKey, "-")); // 수령인 주소
				sendParam.put("deliveryAddr2","-"); // 수령인상세주소
				
				cmmCodeParam = new CommCodeSearchDto();
				cmmCodeParam.setUserYn("Y");
				cmmCodeParam.setCodeGroup("esim_model");
				cmmCodeParam.setCode(order.getEsimModel());
				String esimModel = codeService.getCode(cmmCodeParam) != null ? codeService.getCode(cmmCodeParam).getCodeName() : "";
				
				sendParam.put("deviceModel",esimModel);// esim 모델
				sendParam.put("eid",KISA_SEED_CBC.encrypt(encryptKey,order.getEsimEid()));// esim eid
				sendParam.put("imei1",KISA_SEED_CBC.encrypt(encryptKey,order.getEsimImei1()));// esim imei1
				sendParam.put("imei2",KISA_SEED_CBC.encrypt(encryptKey,order.getEsimImei2()));// esim imei2
			}
			
			
			
			Map<String,String> sendHeader = new HashMap<String,String>();
			
			String apiUrl = BaseUrl + OrderReqUrl;
			String method = "POST";
			
			sendHeader.put("Authorization", ClientKey);
			sendHeader.put("Client-Id", ClientId);
    		
			resultApi = rest.sendRestApi("REQ_ORDERSEND_SHAKE",apiUrl, method, sendParam, sendHeader);
    		String resStatusCode = resultApi.get("resultStatus") !=null ? (String) resultApi.get("resultStatus") : "999";
    		String orderSendStatus = "";
    		String orderSendYn = "";
    		String orderIdHost = "";
    		if("200".equals(resStatusCode.subSequence(0, 3))) {
    			Map<String, Object> resbody =  new HashMap<String, Object>();
				if(resultApi.get("resultBody") != null) {
					resbody = (Map<String, Object>) resultApi.get("resultBody");
					String sendResCode = resbody.get("resultCode") == null ? "9999" : (String) resbody.get("resultCode");
					String sendResMsg = resbody.get("resultMsg") == null ? "9999" : (String) resbody.get("resultMsg");
					result.put("resbody", resbody);
					if("A000".equals(sendResCode)) {
						errMsg = "sucess";
						orderSendStatus = "주문전송";
						orderSendYn = "Y";
						orderIdHost = "";
					} else {
						orderSendStatus = sendResMsg;
						errMsg = sendResMsg;
					}
					
				} else {
					orderSendStatus = "주문 전송 실패 - 전송 결과 없음";
					errMsg = orderSendStatus;
				}
    		} else {
    			orderSendStatus = "주문 전송 실패 - 전송 오류";
    			errMsg = orderSendStatus;
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


