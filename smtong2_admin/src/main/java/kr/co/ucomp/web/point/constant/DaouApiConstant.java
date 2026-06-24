package kr.co.ucomp.web.point.constant;

public interface DaouApiConstant {
	
	//================================ 다우기술 APIs ==========================================  
	/**
	 * 토큰발급
	 */
	public static interface DAOU_AUTH_TOKEN {
		public static final String URL		="/v1/auth/token";
		public static final String METHOD 	="POST";
	}
	/**
	 * Naver 회원 정보 조회
	 */
	public static interface DAOU_MEMBERS_NID {
		public static final String URL		="/v1/npay/members/nid";
		public static final String METHOD 	="POST";
	}
	/**
	 * Naver 회원 정보 조회 - 쓸일 없음 일단 명기함.
	 */
	public static interface DAOU_MEMBERS_CI {
		public static final String URL		="/v1/npay/members/CI";
		public static final String METHOD 	="POST";
	}
	/**
	 * 네이버페이 포인트를 적립합니다.
	 */
	public static interface DAOU_POINT {
		public static final String URL		="/v1/npay/point";
		public static final String METHOD 	="POST";
	}
	/**
	 * 네이버페이 포인트 적립 취소
	 */
	public static interface DAOU_POINT_CANCEL {
		public static final String URL		="/v1/npay/point/cancel";
		public static final String METHOD 	="POST";
	}
	/**
	 * 네이버페이 포인트 적립망 취소 - 네이버 오류 시 해당 거래 취소
	 */
	public static interface DAOU_POINT_NET_CANCEL {
		public static final String URL		="/v1/npay/point/net-cancel";
		public static final String METHOD 	="POST";
	}
	/**
	 * 제휴사 거래 번호로 거래 내역 조회
	 */
	public static interface DAOU_TX {
		public static final String URL		="/v1/tx";
		public static final String METHOD 	="POST";
	}
	/**
	 * 요청 기간의 거래 내역 목록 조회
	 */
	public static interface DAOU_TX_LIST {
		public static final String URL		="/v1/tx/list";
		public static final String METHOD 	="POST";
	}
	//================================ 다우기술 APIs ==========================================
	

}
