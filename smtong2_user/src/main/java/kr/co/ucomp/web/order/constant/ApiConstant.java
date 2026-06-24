package kr.co.ucomp.web.order.constant;

public interface ApiConstant {
	// 쿠콘 APIs
	//================================ 1.카카오 본인 인증 ==========================================  
	//토큰발급
	public static interface COOCON_API_GET_AUTH_TOKEN {
		public static final String URL ="/oauth/2.0/token";
		public static final String METHOD ="POST";
	}
	
	//토큰폐기
	public static interface COOCON_API_REMOVE_AUTH_TOKEN {
		public static final String URL ="/oauth/2.0/revoke";
		public static final String METHOD ="POST";
	}
	
	/*카카오 본인인증
	* 원문 : TEXT
	* 요청방식 채널 메시지
	* */
	public static interface COOCON_API_AUTH_KAKAO_K1110 {
		public static final String URL ="/v2/certification/kakao/sign/request/K1110";
		public static final String METHOD ="POST";
	}
	
	/*카카오 본인인증
	* 원문 : TEXT
	* 요청방식 : 앱투앱
	* */
	public static interface COOCON_API_AUTH_KAKAO_K1120 {
		public static final String URL ="/v2/certification/kakao/sign/request/K1120";
		public static final String METHOD ="POST";
	}	
	
	//본인인증 상태확인
	public static interface COOCON_API_AUTH_KAKAO_STATE {
		public static final String URL ="/v2/certification/kakao/sign/request/status";
		public static final String METHOD ="POST";
	}
	
	//본인인증 검증
	public static interface COOCON_API_AUTH_KAKAO_VERIFY {
		public static final String URL ="/v2/certification/kakao/sign/request/verify";
		public static final String METHOD ="POST";
	}
	//================================ 카카오 본인 인증 ==========================================

	
	//2. 신분증 진위여부
	public static interface COOCON_API_CERT_INDENTIFY {
		public static final String URL ="/sol/gateway/scrap_wapi_std.jsp";
		public static final String POST ="POST";
	}
	
	
	//3. 계좌 실명조회
	public static interface COOCON_API_CERT_ACCOUNT {
		public static final String URL ="/sol/gateway/acctnm_rcms_wapi.jsp";
		public static final String POST ="POST";
	}


}
