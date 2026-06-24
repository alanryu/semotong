package kr.co.ucomp.web.internet.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternetPlanEntity {
	
	private Integer	id				;	//고유 아이디			bigint	
	private String	internetMnoId	;	//인터넷 통신사 코드	varchar(30)
	private String	prodName		;	//상품명	varchar(1000)
	private Integer	channelCount	;
	private String	internetSpeed	;
	private String	internetSpeedName	;
	
	private Integer	normalPrice				;
	private Integer	combinationPrice		;
	private Integer	normalFreeblePrice		;
	private Integer	combinationFreeblePrice	;
	
	private String	useYn			;	//사용여부				varchar(1)
	private String	manager	;
	
	private String	createDate		;	//생성 날짜				datetime
	private String	createNm		;	//
	private Integer	createId		;	//생성자 사용자  id		int
	private String	modifiedDate	;	//수정 날짜				datetime
	private Integer	modifiedId		;	//생성자 사용자  id		int
	private String	modifiedNm		;	//	
	private String stressTxt;
	
	// 인터넷 리뉴얼 개발 
	private String	combinationName	;	//결합명
	private String	tvProdName		;	//tv 상품명	
	private Integer	orderNo			;	//순번
	private String	prodDescript	;	//한줄 설명
}
