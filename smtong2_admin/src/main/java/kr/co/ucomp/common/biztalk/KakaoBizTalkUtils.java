package kr.co.ucomp.common.biztalk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;

import org.apache.commons.text.StringSubstitutor;


@Slf4j
@Service
public class KakaoBizTalkUtils {

	@Value("${bizppurio.kakao.url}")
    private String bizTalkUri;
	
	@Value("${bizppurio.kakao.id}")
    private String clientId;
	
	@Value("${bizppurio.kakao.password}")
    private String clientSecret;
	
	@Value("${bizppurio.kakao.fromNum}")
    private String fromNum;
	
	@Value("${bizppurio.kakao.senderkey}")
	private String senderkey;
	
	@Autowired private RestTempletUtil restTempletUtil;
	
	@Autowired private CommCodeMngService commCodeMngService;
	
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String tokenUrl = "/v1/token";
	private static final String messageSendUrl = "/v3/message";;
	
	
	@SuppressWarnings("unchecked")
	public String getKakaoBizTalkToken() {
		
		String token = "";
		try {
			
			String normalTxt = clientId + ":" + clientSecret;
			String base64Encord = CommonUtil.encodeBase64(normalTxt);
			
			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-type", "application/json; charset=utf-8");
			headerMap.put("Authorization", "Basic " + base64Encord);
			
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap = restTempletUtil.sendRestApi("GET_BIZ_TOKEN", bizTalkUri + tokenUrl, "POST", null, headerMap);
			
			if ( tempMap != null ) {
				
				if( !StringUtils.equals("999", tempMap.get("resultStatus").toString()) ) {
					Map<String, Object> resultBody = (Map<String, Object>)tempMap.get("resultBody");
					token = resultBody.get("accesstoken").toString();
					
				}
			}
			
		} catch (Exception e) {
			log.debug("KakaoBizTalkToken Error !!");
		}
		
		return token;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public String sendSMSMsg(String token,String Msg,String msgType,String toNum) {
		
		try {
			
			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-type", "application/json; charset=utf-8");
			headerMap.put("Authorization", "Bearer " + token);
			
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			Map<String, Object> contentMap = new HashMap<String, Object>();
			Map<String, String> messageMap = new HashMap<String, String>();
			

			messageMap.put("message", Msg);
			contentMap.put(msgType, messageMap);
			
			bodyMap.put("account",clientId);
			bodyMap.put("type",msgType);
			bodyMap.put("from",fromNum);
			bodyMap.put("to",toNum);
			bodyMap.put("content","");
			bodyMap.put("content",contentMap);
			bodyMap.put("refkey","SMT_"+CommonUtil.getDatetime("yyyyMMddHHmmssSSS"));
			
			
			
			
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap = restTempletUtil.sendRestApi("SEND_BIZ_SMS_MSG", bizTalkUri + messageSendUrl, "POST", bodyMap, headerMap);
			
			if ( tempMap != null ) {
				
				if( !StringUtils.equals("999", tempMap.get("resultStatus").toString()) ) {
					Map<String, Object> resultBody = (Map<String, Object>)tempMap.get("resultBody");
					token = resultBody.get("accesstoken").toString();
				}
			}
			
		} catch (Exception e) {
			log.debug("KakaoBizTalkToken Error !!");
		}
		
		return token;
	}
	
	
	
	
	public String sendBizMessage( CommCodeSearchDto param, Map<String, String> variable) {
		
		
		CodeEntity code = commCodeMngService.getCodeInfo(param);
		
		/* etc5 템플릿 메시지를 가져온다. */
		String templateMessage = code.getEtc5();
		
		StringSubstitutor substitutor = new StringSubstitutor(variable);
		
		log.info("템플릿 치환 :: {}", substitutor.replace(templateMessage));
		
		return substitutor.replace(templateMessage);
	}
	
	
	
	
	
	

	@SuppressWarnings("unchecked")
	public String sendBizTemplate( CommCodeSearchDto param, Map<String, String> variable) {

		String returnStr = "";
		
		try {
			
			String url = bizTalkUri + "/v3/message";
			
			/*======== header setting =======*/
			Map<String, String> headerMap = makeKakaoBizTalkHeader();
			
			/*======== body setting =======*/
			/* 템플릿 가져오기 */
			CodeEntity code = commCodeMngService.getCodeInfo(param);
			
			/* etc5:템플릿 메시지 치환 */
			StringSubstitutor substitutor = new StringSubstitutor(variable);
	        
			/* at(알림톡) */
			Map<String, Object> content = makeContent(code, substitutor);
			
			Map<String, Object> bodyMap = makeBody();
			bodyMap.put("to", MapUtils.getString(variable, "to"));
			bodyMap.put("content", content);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap = restTempletUtil.sendRestApi("SEND_TEMPLATE", url, "POST", bodyMap, headerMap);
			
			if ( resultMap.get("resultBody") != null ) {
				
			}
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return returnStr;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> makeKakaoBizTalkHeader() {
		
		Map<String, String> header = new HashMap<String, String>();
		
		try {
			
			String normalTxt = clientId + ":" + clientSecret;
			String base64Encord = CommonUtil.encodeBase64(normalTxt);
			
			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-type", "application/json; charset=utf-8");
			headerMap.put("Authorization", "Basic " + base64Encord);
			
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap = restTempletUtil.sendRestApi("GET_BIZ_TOKEN", bizTalkUri + "/v1/token", "POST", null, headerMap);
			
			if ( tempMap != null ) {
				
				if( !StringUtils.equals("999", tempMap.get("resultStatus").toString()) ) {
					
					Map<String, Object> resultBody = (Map<String, Object>)tempMap.get("resultBody");
					String token = resultBody.get("accesstoken").toString();
					header.put("Content-type", "application/json; charset=utf-8");
					header.put("Authorization", "Bearer " + token);
				}
			}
			
		} catch (Exception e) {
			log.debug("KakaoBizTalkToken Error !!");
		}
		
		return header;
	}
	
	
	
	public Map<String, Object> makeBody() {
		
		/* refkey */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String refkey = "BIZP" + sdf.format(new Date());
		
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		bodyMap.put("account", "semotong");
		bodyMap.put("refkey", refkey);
		bodyMap.put("type", "at");
		bodyMap.put("from", "01000000000");
		
		return bodyMap;
	}
	
	public Map<String, Object> makeContent(CodeEntity code, StringSubstitutor substitutor) {
		
		String templateMessage = code.getEtc5();
		
		Map<String, Object> content = new HashMap<String, Object>();
		
		/* 카카오 메시지 */
		Map<String, Object> at = new HashMap<String, Object>();
		at.put("senderkey", senderkey);
		at.put("templatecode", code.getCode());
		at.put("message", substitutor.replace(templateMessage));

		/* 버튼 */
		List<Map<String, Object>> buttonList = new ArrayList<Map<String, Object>>();
		Map<String, Object> button = new HashMap<String, Object>();
		button.put("name", code.getEtc1());
		button.put("type", "WL");
		button.put("url_pc", code.getEtc3());
		button.put("url_mobile", code.getEtc2());
		buttonList.add(button);
		at.put("button", buttonList);
		
		/* 링크 */
		Map<String, Object> link = new HashMap<String, Object>();
		link.put("url_mobile", "https://www.smtong.co.kr/");
		link.put("url_pc", "https://www.smtong.co.kr");
		at.put("link", link);
		
		content.put("at", at);
		
		return content;
	}
	
	
	
}
