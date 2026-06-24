package kr.co.ucomp.web.pmb.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternetPlanEntity {

	private Integer	id				;	//고유 아이디
	private String	prodName		;	//상품명
	private Integer	internetMnoId	;	//인터넷 통신사 관리번호
	private String	internetMno		;	//인터넷 통신사 명
	private String	channelCount	;	//채널수
	private String	internetSpeed	;	//인터넷 속도
	private Integer	normalPrice		;	//정상금액
	private Integer	combinationPrice;	//결합금액	
	private Integer	normalFreeblePrice	;	// 정상금액 사은품 금액
	private Integer	combinationFreeblePrice	;	// 결합금액 사은품 금액
	private String	siteSp			;	//인터넷 유입 사이트 구분(01: 세모통, 02:오늘의통신) 
	private String	useYn			;	//사용여부				varchar(1)
	private Integer	manager         ;   // 담당자
	private String	createDate		;	//생성 날짜				datetime
	private String	createNm		;	//
	private Integer	createId		;	//생성자 사용자  id		int
	private String	modifiedDate	;	//수정 날짜				datetime
	private Integer	modifiedId		;	//생성자 사용자  id		int
	private String	modifiedNm		;	//
	private String stressTxt;
	
	// 인터넷 리뉴얼 개발 
	private String	combinationName		;	//결함명
	private String	prodDescript		;	//한중 설명
	private String	tvProdName		;	//tv 상품명	
	private Integer	orderNo				;	//순번	
}
