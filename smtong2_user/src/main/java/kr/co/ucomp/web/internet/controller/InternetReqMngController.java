package kr.co.ucomp.web.internet.controller;

import kr.co.ucomp.common.biztalk.KakaoBizTalkUtils;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.restapi.RestTempletUtil;
import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
import kr.co.ucomp.web.internet.dto.InternetPlanSearchDTO;
import kr.co.ucomp.web.internet.dto.InternetReqMngSearchDto;
import kr.co.ucomp.web.internet.entity.InternetPlanEntity;
import kr.co.ucomp.web.internet.entity.InternetPlanMnoEntity;
import kr.co.ucomp.web.internet.entity.InternetReqMngEntity;
import kr.co.ucomp.web.internet.service.InternetPlanNewService;
import kr.co.ucomp.web.internet.service.InternetPlanService;
import kr.co.ucomp.web.internet.service.InternetReqMngService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 이정민
 * @since 2024.12.31
 * @version v1.0
 */
@Controller
@RequestMapping("/pmb/internetreq")

public class InternetReqMngController {

	@Autowired
	InternetReqMngService internetReqMngService;
	@Autowired
	KakaoBizTalkUtils bizTalkService;
	@Autowired
	CommCodeMngService comCodeService;
	@Autowired
	InternetPlanService internetPlanService;
	@Autowired
	InternetPlanNewService internetPlanNewService;
	@Autowired
	RestTempletUtil restTempletUtil;
	@Autowired
	private CommCodeMngService commCodeMngService;

	@Value("${facebook.meta.url}")
	String facebookMetaUrl;

	@Value("${facebook.meta.access-token}")
	String facebookMetaAccToken;

	// 서버 sp
	@Value("${order.serverSp}")
	String serverSp;

	@Value("${app.base-url}")
	String baseUrl;

	@PostMapping("/create")
	public ResponseEntity<CustomApiResponse<String>> createInternetRequest(@RequestBody InternetReqMngEntity param) {
		if (internetReqMngService.insertInternetReqMng(param) != 0) {

			// 관리자 알림 전송 전송
			Map<String, String> headerMap = bizTalkService.makeKakaoBizTalkHeader();
			CommCodeSearchDto codeparam = new CommCodeSearchDto();
			codeparam.setCodeGroup("common_env_code");
			codeparam.setCode("admin_alam_internet");
			CodeEntity codeItm = comCodeService.getCode(codeparam);
			String sendMsg = codeItm.getEtc1();

			Integer mnoId = param.getInputMno();
			InternetPlanMnoEntity mnoInfo = internetPlanService.getInternetMno(mnoId);
			if (mnoInfo != null) {
				String alamTo = mnoInfo.getAlamRcvNum();

				String[] alamToList = alamTo.split("/");
				for (int i = 0; i < alamToList.length; i++) {
					String adminPhoneNum = alamToList[i] != null ? alamToList[i] : "";
					if (StringUtils.isNotBlank(adminPhoneNum)) {
						bizTalkService.sendSMSMsg(headerMap, sendMsg, "sms", adminPhoneNum);
					}
				}

			}

			return CustomApiResponse.success(ResponseCode.CREATED, 1, "CREATED");
		} else {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, ResponseCode.BAD_REQUEST.getMessage());
		}
	}

	@PostMapping("/createNew")
	public ResponseEntity<CustomApiResponse<String>> createInternetReqNew(HttpServletRequest request,
			@RequestBody InternetReqMngEntity param) {

		int planId = (int) param.getInputPlanId();
		InternetPlanSearchDTO sPlanParam = new InternetPlanSearchDTO();
		sPlanParam.setSearchId(planId);
		InternetPlanEntity internetplan = internetPlanNewService.getDetail(sPlanParam);

		CommCodeSearchDto commCodeSearchDto = new CommCodeSearchDto();
		commCodeSearchDto.setCode("150");
		commCodeSearchDto.setCodeGroup("admin_outbound_center");

		CodeEntity codeEntity = commCodeMngService.getCode(commCodeSearchDto);
		String newOutboundCenter = "INTERNET";

		System.out.println("codeEntity: " + codeEntity.getUseYn());

		// Y 일경우 콜센터1, 콜센터2로 각 각 라운드 로빈
		if (codeEntity.getUseYn().equals("Y") == true) {
			String outboundCenter = internetReqMngService.getLastOutboundCenter(); // 값이 INTERNET, INTERNET2 둘중 하나온다.
			if (outboundCenter.equals("INTERNET") == true) {
				newOutboundCenter = "INTERNET2";
			}
		}

		param.setOutboundCenter(newOutboundCenter);

		if (internetplan != null) {
			param.setInternetMnoId(internetplan.getInternetMnoId());
			param.setProdName(internetplan.getProdName());
			param.setCombinationName(internetplan.getCombinationName());
			param.setProdDescript(internetplan.getProdDescript());
			param.setTvProdName(internetplan.getTvProdName());
			param.setChannelCount(internetplan.getChannelCount());
			param.setInternetSpeed(internetplan.getInternetSpeed());
			param.setCombinationPrice(internetplan.getCombinationPrice());
			param.setCombinationFreeblePrice(internetplan.getCombinationFreeblePrice());
		}

		// 랜딩 페이지 신청 시 facebook 픽셀 전환 api 에 전송
		// url :
		// https://graph.facebook.com/v23.0/2132101073966419/events?access_token=EAAHNdZBmBmqsBPAZAFHEUEDIaZC3JINoTC6MoBf0ADkXjG2xth69NyGfUKmFlB7W8DktrZChlR65J4AhNxJUNdsi9hr2ltrIEB83tTtetHjyP4ZB2gREv6ZCxmlNqFq5WNzpxQPySvcxnL6leaAJSlYnHo57SAgiWjJ6tC8OO3hOFVCtFWRv3ZA5X6nwH8gMhVt2wZDZD

		if ("02".equals(param.getIncomSp())) {
			// 랜딩 페이지 신청의 경우만 적용
			String url = facebookMetaUrl + "=" + facebookMetaAccToken;
			String clientIp = CommonUtil.getClientIp(request);
			String userAgent = request.getHeader("User-Agent");
			Integer rdpMngId = param.getRdpMngId();
			String path = "/internet/landing/" + rdpMngId;
			String source_url = baseUrl + path;

			Map<String, Object> bodyMap = FacebookPayloadBuilder(param.getInputName(), param.getInputNumber(), clientIp,
					userAgent, source_url, path);

			try {
				Map<String, Object> result = restTempletUtil.sendRestApi("FB_META_FIX", url, "POST", bodyMap, null);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (internetReqMngService.insertInternetReqNewMng(param) != 0) {

			// 관리자 알림 전송 전송
			Map<String, String> headerMap = bizTalkService.makeKakaoBizTalkHeader();
			CommCodeSearchDto codeparam = new CommCodeSearchDto();
			codeparam.setCodeGroup("common_env_code");
			codeparam.setCode("admin_alam_internet");
			CodeEntity codeItm = comCodeService.getCode(codeparam);
			String sendMsg = codeItm.getEtc1();

			Integer mnoId = param.getInputMno();
			InternetPlanMnoEntity mnoInfo = internetPlanService.getInternetMno(mnoId);
			if (mnoInfo != null) {
				String alamTo = mnoInfo.getAlamRcvNum();

				String[] alamToList = alamTo.split("/");
				for (int i = 0; i < alamToList.length; i++) {
					String adminPhoneNum = alamToList[i] != null ? alamToList[i] : "";
					if (StringUtils.isNotBlank(adminPhoneNum)) {
						bizTalkService.sendSMSMsg(headerMap, sendMsg, "sms", adminPhoneNum);
					}
				}

			}

			return CustomApiResponse.success(ResponseCode.CREATED, 1, "CREATED");
		} else {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, ResponseCode.BAD_REQUEST.getMessage());
		}
	}

	@GetMapping("")
	public ResponseEntity<CustomApiResponse<List<InternetReqMngEntity>>> getInternetRequests(
			@RequestParam(required = true) InternetReqMngSearchDto param) {
		try {
			List<InternetReqMngEntity> list = internetReqMngService.getInternetReqMngList(param);
			long count = internetReqMngService.getInternetReqMngCount(param);
			return CustomApiResponse.success(ResponseCode.OK, count, list);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, ResponseCode.BAD_REQUEST.getMessage());
		}
	}

	/**
	 * 샘플 데이터
	 * {
	 * "data": [
	 * {
	 * "event_name": "Lead",
	 * "event_time": 1752117540,
	 * "event_source_url": "https://yourwebsite.com/pricing/plan-A",
	 * "action_source": "website",
	 * "user_data": {
	 * "ph": [
	 * "74234e98afe7498fb5daf1f36ac2d78acc339464f950703b8c019892f982b90b"
	 * ],
	 * "fn": [
	 * "f73bf715f4a26208acf2751f0509b0cbfd26d29d16943216646e1e52a2f5cdee"
	 * ],
	 * "client_ip_address": "192.168.0.1",
	 * "client_user_agent": "Mozilla/5.0 (Windows NT 10.0)"
	 * },
	 * "custom_data": {
	 * "path": "/pricing/plan-A" // ← path를 custom_data로 따로 전달
	 * },
	 * "event_id": "lead_1234_abc"
	 * }
	 * ],
	 * "test_event_code": "TEST27188"
	 * }
	 */
	public Map<String, Object> FacebookPayloadBuilder(String name, String phone, String clientIp, String userAgent,
			String source_url, String path) {

		Map<String, Object> result = new HashMap<>();

		// SHA256 해싱
		String hsname = CommonUtil.sha256HashForFacebook(name);
		String hsphone = CommonUtil.sha256HashForFacebook(phone);
		// 현재 시각의 UNIX timestamp (초 단위)
		long eventTime = System.currentTimeMillis() / 1000L;

		// user_data 구성
		Map<String, Object> userData = new HashMap<>();
		userData.put("fn", Collections.singletonList(hsname));
		userData.put("ph", Collections.singletonList(hsphone));
		userData.put("client_ip_address", clientIp);
		userData.put("client_user_agent", userAgent);

		// custom_data 구성
		Map<String, Object> customData = new HashMap<>();
		customData.put("path", path);

		// 이벤트 구성
		Map<String, Object> event = new HashMap<>();
		event.put("event_name", "Lead");
		event.put("event_time", eventTime); // UNIX timestamp
		event.put("event_source_url", source_url);
		event.put("action_source", "website");
		event.put("user_data", userData);
		event.put("custom_data", customData);
		event.put("event_id", "lead_" + eventTime);

		// data 배열에 추가
		List<Map<String, Object>> dataList = new ArrayList<>();
		dataList.add(event);

		// 최종 payload 구성
		result.put("data", dataList);
		result.put("test_event_code", "TEST27188");

		return result;

	}
}
