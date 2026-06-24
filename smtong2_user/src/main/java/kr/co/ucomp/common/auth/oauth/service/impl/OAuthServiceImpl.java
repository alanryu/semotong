package kr.co.ucomp.common.auth.oauth.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.ucomp.common.auth.oauth.dto.KakaoIdResponseDto;
import kr.co.ucomp.common.auth.oauth.dto.KakaoOAuthServiceTermsDTO;
import kr.co.ucomp.common.auth.oauth.dto.KakaoOAuthTokenDTO;
import kr.co.ucomp.common.auth.oauth.dto.KakaoOAuthUserDTO;
import kr.co.ucomp.common.auth.oauth.dto.KakaoProperties;
import kr.co.ucomp.common.auth.oauth.dto.NaverOAuthTokenDTO;
import kr.co.ucomp.common.auth.oauth.dto.NaverOAuthUserDTO;
import kr.co.ucomp.common.auth.oauth.dto.NaverProperties;
import kr.co.ucomp.common.auth.oauth.dto.ServiceTermDTO;
import kr.co.ucomp.common.auth.oauth.service.OAuthService;
import kr.co.ucomp.common.biztalk.KakaoBizTalkUtils;
import kr.co.ucomp.common.restapi.entity.RestApiLogEntity;
import kr.co.ucomp.common.restapi.mapper.RestApiMapper;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.mypage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


/**
 * ?redirect_uri={3000/kakao/auth}
 *
 * axios -> 8080/api/kakao/auth?code=---
 * ---- server ----
 *
 * /api/kakao/auth/access?redirect_uri={3000/kakao/auth}
 *
 */

/**
 * Spring
 * React href -> Method GET uri /oauth/authorize?redirect_uri=http://loaclhost:3000/kakao/auth
 *
 */




/**
 * OAuthService Interface에 정의된
 * 메소드를 구현하는 파일.
 *
 * @author 이정민
 * @since 2024.12.18
 * @version v1.1
 *
 * 2024.12.23 (월)
 * kakao sync의 선택사항인, 생일을 동의하지 않았을 경우,
 * DB 저장이 nullnull로 되는 문제 해결
 *
 */
@Service("OAuthService")
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {
	private final UserService 		userService;
	private final WebClient 		kakaoWebClient;
	private final WebClient 		naverWebClient;
	private final KakaoProperties 	kakaoProperties;
	private final NaverProperties 	naverProperties;	
	private int exp = 3600;

	@Autowired
	private KakaoBizTalkUtils kakaoBizTalkUtils;
	@Autowired
	private RestApiMapper restApiMapper;
	
	/**************************************************************************************************************** naver ***/ 
	
	@Override
	public NaverOAuthTokenDTO getNaverAccessToken(String code) {		//OK
		
		NaverOAuthTokenDTO tokenDTO = null;
		String naverTokenUri 		= naverProperties.getToken().getUri();
		String naverClientId		= naverProperties.getClient().getId();
		String naverClientSecret 	= naverProperties.getClient().getSecret();
		String naverRedirectURI		= naverProperties.getClient().getRedirect();

		//String redirectURI = URLEncoder.encode("http://127.0.0.1:8080/auth/naver/callback", "UTF-8");
		//String redirectURI = "http://127.0.0.1:8080/auth/naver/callback";
		
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type"		, "authorization_code"	);
		formData.add("client_id"		, naverClientId			);
		formData.add("client_secret"	, naverClientSecret		);
		formData.add("redirect_uri"		, naverRedirectURI		);
		formData.add("code", code);
		try {
			return naverWebClient.post()
					.uri(naverTokenUri)
					.body(BodyInserters.fromFormData(formData))
					.retrieve()
					.onStatus(status -> status.is5xxServerError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("Naver 서버 에러: " + body))
					)
					.onStatus(status -> status.is4xxClientError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("Naver 에러: " + body))
					)
					.bodyToMono(NaverOAuthTokenDTO.class)
					.block();
		} catch (WebClientResponseException e) {
			String errorBody = e.getResponseBodyAsString();
			log.error("Naver 인증 에러: {} - {}", e.getStatusCode(), errorBody);
			throw new RuntimeException("Naver 인증 실패: " + errorBody);
		}
	}
	
	@Override
	public Map<String, Object> getNaverUser(String accessToken) {
		
		String naverProfileUri = naverProperties.getProfile().getUri();
		
		Map<String, Object> map = new HashMap<>();
		
		try {
			Objects.requireNonNull(accessToken, "Access token must not be null");

			NaverOAuthUserDTO naverResponse = naverWebClient.post()
					.uri(naverProfileUri)
					.header("Authorization", "Bearer " + accessToken)
					.retrieve()
					.onStatus(status -> status.is4xxClientError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("Naver 사용자 정보 조회 클라이언트 에러: " + body))
					)
					.onStatus(status -> status.is5xxServerError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("Naver 사용자 정보 조회 서버 에러: " + body))
					)
					.bodyToMono(NaverOAuthUserDTO.class)
					.block();
			
			map.put("id", naverResponse.getResponse().getId());
			
//			System.out.println("---------------naverResponse:" + naverResponse);
//			System.out.println("-" + naverResponse.getResponse().getAge());
//			System.out.println("-" + naverResponse.getResponse().getBirthday());
//			System.out.println("-" + naverResponse.getResponse().getBirthyear());
//			System.out.println("-" + naverResponse.getResponse().getEmail());
//			System.out.println("-" + naverResponse.getResponse().getGender());
//			System.out.println("-" + naverResponse.getResponse().getId());
//			System.out.println("-" + naverResponse.getResponse().getMobile());
//			System.out.println("-" + naverResponse.getResponse().getName());
//			System.out.println("-" + naverResponse.getResponse().getNickname());
//			System.out.println("-" + naverResponse.getResponse().getProfile_image());
//			System.out.println("---------------naverResponse:" + naverResponse);
			//---------------naverResponse:NaverOAuthUserDTO(resultcode=00, message=success, ..................
			
			return map;
	
		} catch (WebClientResponseException e) {
			log.error("Naver 사용자 정보 조회 실패: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
			throw new RuntimeException("카카오 사용자 정보 조회 실패: " + e.getMessage());
		} catch (Exception e) {
			log.error("Naver 사용자 처리 중 예외 발생", e);
			throw new RuntimeException("Naver 사용자 처리 실패: " + e.getMessage());
		}
	}
	
	/**************************************************************************************************************** naver ***/
	
	
	
	
	/**
	 *
	 * 카카오 인증 Token을 받아오는 메소드
	 * @param code
	 * @return KakaoOAuthTokenDTO
	 * {
	 *   "token_type" : "bearer",
	 *   "access_token" : "IJ3gCisVsIGaPwyy2Fle7TcT-51NO7BWAAAAAQo8JJsAAAGT3q8EAKew61y3DOUZ",
	 *   "expires_in" : "21599"
	 * }
	 */
	@Override
	public KakaoOAuthTokenDTO getAccessToken(String code, String kakaoRedirectUri) {
		String kakaoClientId = kakaoProperties.getClient().getId();
		String kakaoGetTokenUri = kakaoProperties.getToken().getUri();

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "authorization_code");
		formData.add("client_id", kakaoClientId);
		formData.add("redirect_uri", kakaoRedirectUri);
		formData.add("code", code);
		try {
			return kakaoWebClient.post()
					.uri(kakaoGetTokenUri)
					.body(BodyInserters.fromFormData(formData))
					.retrieve()
					.onStatus(status -> status.is5xxServerError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("카카오 서버 에러: " + body))
					)
					.onStatus(status -> status.is4xxClientError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("클라이언트 에러: " + body))
					)
					.bodyToMono(KakaoOAuthTokenDTO.class)
					.block();
		} catch (WebClientResponseException e) {
			String errorBody = e.getResponseBodyAsString();
			log.error("카카오 인증 에러: {} - {}", e.getStatusCode(), errorBody);
			throw new RuntimeException("카카오 인증 실패: " + errorBody);
		}
	}


	/**
	 * @param accessToken
	 * @return
	 */
	@Override
	public Map<String, Object> getKakaoUser(String accessToken) {
		String kakaoGetUserInfoUri = kakaoProperties.getUserInfo().getUri();

		try {
			Objects.requireNonNull(accessToken, "Access token must not be null");

			KakaoOAuthUserDTO kakaoResponse = kakaoWebClient.post()
					.uri(kakaoGetUserInfoUri)
					.header("Authorization", "Bearer " + accessToken)
					.retrieve()
					.onStatus(status -> status.is4xxClientError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("카카오 사용자 정보 조회 클라이언트 에러: " + body))
					)
					.onStatus(status -> status.is5xxServerError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("카카오 사용자 정보 조회 서버 에러: " + body))
					)
					.bodyToMono(KakaoOAuthUserDTO.class)
					.block();

			
			ObjectMapper objectMapper = new ObjectMapper();
			String reqBody = objectMapper.writeValueAsString(kakaoResponse);
			
			RestApiLogEntity logparam = new RestApiLogEntity();
			logparam.setApiCode("KAKAO_LOGIN");
			logparam.setApiName("KAKAO_LOGIN");
			logparam.setApiUrl(kakaoGetUserInfoUri);
			logparam.setReqMsg("");
			logparam.setResBody(reqBody);
			logparam.setResMsg("");
			restApiMapper.createLog(logparam);
			
			String chkphoneNumber = kakaoResponse.getKakaoAccount().getPhoneNumber(); 
			String chkuserName = kakaoResponse.getKakaoAccount().getName();
			
			if(StringUtils.isBlank(chkuserName) || StringUtils.isBlank(chkphoneNumber)) {
				Map<String, Object> errMap = new HashMap<String, Object>();
				errMap.put("errYn", "Y");
				errMap.put("chkphoneNumber", chkphoneNumber);
				errMap.put("chkuserName", chkuserName);
				errMap.put("errMsg", "카카오 사용자 정보에 오류가 있습니다.");
				return errMap;
			}
			
			
			Map<String, Object> terms = updateKakaoTermsCol(accessToken);
			Map<String, Object> map = new HashMap<>();

			UserDTO user = userService.getUserByKakaoId("kakao_" + kakaoResponse.getId().toString());
			if (user == null) {
				user = createUserByKakaoUser(kakaoResponse, terms);
				
				/* 공백 제거 */
				String phoneNumber = kakaoResponse.getKakaoAccount().getPhoneNumber().replaceAll("\\s+", ""); 
				
				/* +82을 0으로 대체 */
				if (phoneNumber.startsWith("+82")) {
					phoneNumber = "0" + phoneNumber.substring(3);
		        }
				
				/* 하이픈 제거 */
				phoneNumber = phoneNumber.replaceAll("-","");
				
				/* 회원가입 알림 템플릿 찾기 */
				CommCodeSearchDto param = new CommCodeSearchDto();
				param.setCodeGroup("biz_template");
				param.setCode("bizp_2025031216022647794416639");
				
				/* 받는사람 세팅 */
				Map<String, String> variable = new HashMap<String, String>();
				variable.put("userNm", kakaoResponse.getKakaoAccount().getName());
				variable.put("to", phoneNumber);
				
				/* 메세지 발송 */
				String result = kakaoBizTalkUtils.sendBizMessage(param, variable);
				
			}else if("DROP".equals(user.getMemberStat())) {
				//탈퇴 후 재가입
				UserDTO reJoinUser = userService.getUserById(user.getId());
				
				//여기서 명시적으로 재활성화 한다.
				reJoinUser.setMemberStat("ACTIVE");
				reJoinUser.setActiveYn("1");
				userService.reJoinUserToggle(reJoinUser);
				
				
				UserDTO upateParam = new UserDTO();
				upateParam.setId(user.getId());
				upateParam.setUsername(kakaoResponse.getKakaoAccount().getName());
				upateParam.setPhoneNumber(kakaoResponse.getKakaoAccount().getPhoneNumber());
				upateParam.setEmail(kakaoResponse.getKakaoAccount().getEmail());
				upateParam.setBirthDay(Objects.toString(kakaoResponse.getKakaoAccount().getBirthyear(), "") + Objects.toString(kakaoResponse.getKakaoAccount().getBirthday(), ""));
				upateParam.setBirthYear(Objects.toString(kakaoResponse.getKakaoAccount().getBirthyear(), ""));
				upateParam.setKakaoUserId("kakao_" + kakaoResponse.getId().toString());
				
				if ( !StringUtils.isEmpty(kakaoResponse.getKakaoAccount().getAgeRange()) ) {
					String ageGroup = kakaoResponse.getKakaoAccount().getAgeRange().substring(0,2) + "대";
					String ageGroupVal = kakaoResponse.getKakaoAccount().getAgeRange().substring(0,2);
					upateParam.setAgeGroup(ageGroup);
					upateParam.setAgeGroupVal(Integer.valueOf(ageGroupVal));
				}
				
				userService.updateUser(upateParam);
				
				user = userService.getUserByKakaoId("kakao_" + kakaoResponse.getId().toString());
				
				/* 공백 제거 */
				String phoneNumber = kakaoResponse.getKakaoAccount().getPhoneNumber().replaceAll("\\s+", ""); 
				
				
				/* +82을 0으로 대체 */
				if (phoneNumber.startsWith("+82")) {
					phoneNumber = "0" + phoneNumber.substring(3);
		        }
				
				/* 하이픈 제거 */
				phoneNumber = phoneNumber.replaceAll("-","");
				
				/* 회원가입 알림 템플릿 찾기 */
				CommCodeSearchDto param = new CommCodeSearchDto();
				param.setCodeGroup("biz_template");
				param.setCode("bizp_2025031216022647794416639");
				
				/* 받는사람 세팅 */
				Map<String, String> variable = new HashMap<String, String>();
				variable.put("userNm", kakaoResponse.getKakaoAccount().getName());
				variable.put("to", phoneNumber);
				
				/* 메세지 발송 */
				String result = kakaoBizTalkUtils.sendBizMessage(param, variable);
				
			} else {
				UserDTO upateParam = new UserDTO();
				upateParam.setId(user.getId());
				upateParam.setUsername(kakaoResponse.getKakaoAccount().getName());
				upateParam.setPhoneNumber(kakaoResponse.getKakaoAccount().getPhoneNumber());
				upateParam.setEmail(kakaoResponse.getKakaoAccount().getEmail());
				upateParam.setBirthDay(Objects.toString(kakaoResponse.getKakaoAccount().getBirthyear(), "") + Objects.toString(kakaoResponse.getKakaoAccount().getBirthday(), ""));
				upateParam.setBirthYear(Objects.toString(kakaoResponse.getKakaoAccount().getBirthyear(), ""));
				upateParam.setKakaoUserId("kakao_" + kakaoResponse.getId().toString());
				
				if ( !StringUtils.isEmpty(kakaoResponse.getKakaoAccount().getAgeRange()) ) {
					String ageGroup = kakaoResponse.getKakaoAccount().getAgeRange().substring(0,2) + "대";
					String ageGroupVal = kakaoResponse.getKakaoAccount().getAgeRange().substring(0,2);
					upateParam.setAgeGroup(ageGroup);
					upateParam.setAgeGroupVal(Integer.valueOf(ageGroupVal));
				}
				
				userService.updateUser(upateParam);
			}
			
			//System.out.println("---------------:" + user.get("memberStat"));
			
			map.put("errYn", "N");
			map.put("user", user);
			map.put("expires_in", String.valueOf(exp));
			map.put("kakao_id", "kakao_" + kakaoResponse.getId().toString());

			return map;

		} catch (WebClientResponseException e) {
			log.error("카카오 사용자 정보 조회 실패: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
			throw new RuntimeException("카카오 사용자 정보 조회 실패: " + e.getMessage());
		} catch (Exception e) {
			log.error("카카오 사용자 처리 중 예외 발생", e);
			throw new RuntimeException("카카오 사용자 처리 실패: " + e.getMessage());
		}
	}


	@Override
	public UserDTO createUserByKakaoUser(KakaoOAuthUserDTO kakaoOAuthUserDTO, Map<String,Object> map) {
		try {
			String ageFormat = "";
			if ( !StringUtils.isEmpty(kakaoOAuthUserDTO.getKakaoAccount().getAgeRange()) ) {
				ageFormat = kakaoOAuthUserDTO.getKakaoAccount().getAgeRange().substring(0,2) + "대";
			}
			
			
			UserDTO user = new UserDTO();
			user.setUsername(kakaoOAuthUserDTO.getKakaoAccount().getName());
			user.setEmail(kakaoOAuthUserDTO.getKakaoAccount().getEmail());
			user.setAgeGroup(ageFormat);
			user.setBirthDay(Objects.toString(kakaoOAuthUserDTO.getKakaoAccount().getBirthyear(), "") +Objects.toString(kakaoOAuthUserDTO.getKakaoAccount().getBirthday(), ""));
			user.setBirthYear(Objects.toString(kakaoOAuthUserDTO.getKakaoAccount().getBirthyear(), ""));
			user.setPhoneNumber(kakaoOAuthUserDTO.getKakaoAccount().getPhoneNumber());
			user.setJoinDate(OffsetDateTime.parse(kakaoOAuthUserDTO.getSynchedAt()).toLocalDateTime());
			user.setKakaoUserId("kakao_" + kakaoOAuthUserDTO.getId().toString());
			user.setActiveYn("1");
			user.setMemberStat("ACTIVE");
			user.setEmailAgreeYn((Boolean) map.get("email"));
			user.setPiAgreeYn((Boolean) map.get("pi"));
			user.setPolAgreeYn((Boolean) map.get("pol"));
			user.setSmsAgreeYn((Boolean) map.get("sms"));
			user.setCreateId(1L); //임시
			String dateTimeString = kakaoOAuthUserDTO.getSynchedAt();
			dateTimeString = dateTimeString.replaceAll("Z", "");
			if(StringUtils.isNoneBlank(dateTimeString)) {
				LocalDateTime tmpdateTime = LocalDateTime.parse(dateTimeString);
				user.setPiAgreeDttm(tmpdateTime);
				user.setPolAgreeDttm(tmpdateTime);
				user.setSmsAgreeDttm(tmpdateTime);
				user.setEmailAgreeDttm(tmpdateTime);
			}
			
			String ageGroup = user.getAgeGroup();
			if(StringUtils.isNoneBlank(ageGroup)) {
				int ageGroupVal = Integer.parseInt(ageGroup.replaceAll("대", ""));
				user.setAgeGroupVal(ageGroupVal);
			}

			if (userService.createUser(user)) {
				return userService.getUserByKakaoId("kakao_"+kakaoOAuthUserDTO.getId().toString());
			} else {
				throw new RuntimeException("유저 생성 실패");
			}
		} catch (Exception e) {
			log.error("카카오 유저 생성 중 예외 발생", e);
			throw new RuntimeException("카카오 유저 생성 실패: " + e.getMessage());
		}
	}

	@Override
	public Map<String, Object> updateKakaoTermsCol(String accessToken) {
		String kakaoGetTermsInfoUri = kakaoProperties.getTermsInfo().getUri();

		try {
			KakaoOAuthServiceTermsDTO response = kakaoWebClient.get()
					.uri(kakaoGetTermsInfoUri)
					.header("Authorization", "Bearer " + accessToken)
					.retrieve()
					.onStatus(status -> status.is4xxClientError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("카카오 약관 정보 조회 클라이언트 에러: " + body))
					)
					.onStatus(status -> status.is5xxServerError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
									.map(body -> new RuntimeException("카카오 약관 정보 조회 서버 에러: " + body))
					)
					.bodyToMono(KakaoOAuthServiceTermsDTO.class)
					.block();

			Map<String, Object> map = new HashMap<>();

			for (ServiceTermDTO term : response.getServiceTerms()) {
				String[] tagParts = term.getTag().split("_");
				String termType = tagParts[tagParts.length - 1];
				boolean agreeValue = term.isAgreed() ? true : false;

				switch (termType.toLowerCase()) {
					case "email":
						map.put("email", agreeValue);
						break;  // break 문 추가
					case "pi":
						map.put("pi", agreeValue);
						break;
					case "sms":
						map.put("sms", agreeValue);
						break;
					case "pol":
						map.put("pol", agreeValue);
						break;
				}
			}
			return map;

		} catch (WebClientResponseException e) {
			log.error("카카오 약관 정보 조회 실패: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
			throw new RuntimeException("카카오 약관 정보 조회 실패: " + e.getMessage());
		} catch (Exception e) {
			log.error("카카오 약관 정보 처리 중 예외 발생", e);
			throw new RuntimeException("카카오 약관 정보 처리 실패: " + e.getMessage());
		}
	}

	//2015.01.17 admin key 사용으로 탈퇴 처리한다.
	@Override
	public boolean disconnectKakaoUser(String kakaoId) {
		try {
			String kakaoDropUserUri = kakaoProperties.getUserDrop().getUri();
			String kakaoClientId = kakaoProperties.getClient().getId();
			String kakaoClientAkey 		= kakaoProperties.getClient().getAkey();	//Admin Key
			Long kakaoDropUserId = Long.parseLong(kakaoId.split("_")[1]);

			MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
			formData.add("target_id_type"	, "user_id");
			formData.add("target_id"		, String.valueOf(kakaoDropUserId));

			// header
			//Authorization: Bearer ${ACCESS_TOKEN} //토큰 있다면 이렇게
			
			// header
			//Authorization: KakaoAK ${SERVICE_APP_ADMIN_KEY} 		//Admin Key 사용 시 
			//Content-Type: application/x-www-form-urlencoded;charset=utf-8 
			
			KakaoIdResponseDto response =  kakaoWebClient.post()
					.uri(kakaoDropUserUri)
					.header("Authorization", "KakaoAK " + kakaoClientAkey)
					.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
					.body(BodyInserters.fromFormData(formData))
					.retrieve()
					.onStatus(status -> status.is5xxServerError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
							.map(body -> new RuntimeException("카카오 연결끊기 서버 에러: " + body))
					)
					.onStatus(status -> status.is4xxClientError(), clientResponse ->
							clientResponse.bodyToMono(String.class)
							.map(body -> new RuntimeException("카카오 연결끊기 클라이언트 에러: " + body))
					)
					.bodyToMono(KakaoIdResponseDto.class)
					.block();
			if (response == null) {
				return false;
			}
			else {
				return true;
			}
		}catch ( Exception e ) {
			log.error("카카오 연결 끊기 실패", e);
			// {"msg":"NotRegisteredUserException","code":-101}  <-- 이미 탈퇴자
			return false;

		}
	}
	

	//https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#unlink @@참고 하기
	// TEST 용 폐기
	@Override
	public boolean sanchoDisconnectKakaoUser(String kakaoId) {
		try {
			String kakaoDropUserUri 	= kakaoProperties.getUserDrop().getUri();
			//String kakaoClientId 		= kakaoProperties.getClient().getId();		//이건 REST API 키 임..... 죈장.
			String kakaoClientAkey 		= kakaoProperties.getClient().getAkey();	//Admin Key 
			Long kakaoDropUserId 	= Long.parseLong(kakaoId.split("_")[1]);

			WebClient webClient = WebClient.builder()
					.baseUrl("https://kapi.kakao.com")
					.defaultHeader("Authorization", "KakaoAK " + kakaoClientAkey)
					.defaultHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
					.build();
			
			// MultiValueMap 생성
			MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
			formData.add("target_id_type"	, "user_id");
			formData.add("target_id"			, String.valueOf(kakaoDropUserId));
			
			System.out.println(formData);

			//KakaoIdResponseDto response =  webClient.post()
			Mono<String> responseMono =  webClient.post()
				.uri("/v1/user/unlink")
				.bodyValue(formData)
				.retrieve()
				.onStatus(status -> status.is5xxServerError(), clientResponse ->
					clientResponse.bodyToMono(String.class)
						.map(body -> new RuntimeException("카카오 연결끊기 서버 에러: \n" + body))
						)
				.onStatus(status -> status.is4xxClientError(), clientResponse ->
					clientResponse.bodyToMono(String.class)
						.map(body -> new RuntimeException("카카오 연결끊기 클라이언트 에러: \n" + body))
						)
				.bodyToMono(String.class);
			
			if (responseMono == null) {
				return false;
			}else {
				return true;
			}
		}catch ( Exception e ) {
			log.error("카카오 연결 끊기 실패", e);
			return false;
		}
	}
	
	/**
	 * Kakao User Info API Response validation check
	 * @param response
	 */
	private Boolean validateKakaoResponse(KakaoOAuthUserDTO response) {
		if (response == null) {
			throw new IllegalStateException("No response received from Kakao API");
		}

		KakaoOAuthUserDTO.KakaoAccount account = response.getKakaoAccount();
		if (account == null) {
			throw new IllegalStateException("Kakao account information is missing");
		}

		if (!account.isEmailVerified() || account.getEmail() == null) {
			throw new IllegalStateException("Email is not verified or missing");
		}

		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/*  ---- 전통적 방식의 POST
	@Override
	public NaverOAuthTokenDTO getNaverAccessToken(String code) {		//OK
		
		NaverOAuthTokenDTO tokenDTO = null;
		
		try {
			String naverTokenUri 		= naverProperties.getToken().getUri();
			String naverClientId		= naverProperties.getClient().getId();
			String naverClientSecret 	= naverProperties.getClient().getSecret();
	
			String redirectURI = URLEncoder.encode("http://127.0.0.1:8080/auth/naver/callback", "UTF-8");
			String apiURL = "https://nid.naver.com/oauth2.0/token";
	
			String postParams = "grant_type=authorization_code"
				+ "&client_id=" 	+ naverClientId
				+ "&client_secret=" + naverClientSecret
				+ "&redirect_uri=" 	+ redirectURI
				+ "&code=" + code
				;
			
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = postParams.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {  // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuilder res = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				res.append(inputLine);
			}
			br.close();
			if (responseCode == 200) {
				//System.out.println(res.toString());
				ObjectMapper objectMapper = new ObjectMapper();
				tokenDTO = objectMapper.readValue(res.toString(), NaverOAuthTokenDTO.class);
			}
		} catch (Exception e) {
			e.printStackTrace(); // Exception 로깅
		}
		
		return tokenDTO;
	}
	*/
	
}
