package kr.co.ucomp.web.svc.event.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvtEntity {

	private Integer 	id					;	//고유번호
	private String		eventCategory		;   //이벤트 분류
	private String		eventCategoryNm		;   //이벤트 분류
	private String		eventMngSp			;   //이벤트 입력 구분( 이미지 등록: i/내용 등록 : c)
	private String		title				;   //이벤트 제목
	private String		content				;   //이벤트 내용			
	private String		eventThumbnail		;   //이벤트 썸네일
	private String		eventImage			;
	private Integer		manager				;   //담당자 / 유저 아이디	
	
	private LocalDateTime		startDate			;   //이벤트 시작일			
	private LocalDateTime		endDate				;   //이벤트 종료일
	
	private String		stringStartDate	;
	private String		stringEndDate	;
	
	private String		useYn			;   //게시여부
	
	private LocalDateTime		createDate			;   //생성 날짜	datetime
	private Integer		createId			;   //생성자 사용자 로그인 id
	private LocalDateTime		modifiedDate		;   //수정 날짜				
	private Integer		modifiedId			;   //생성자 사용자 로그인 id
	
	
	private String		eventNoticeSp				; // 유위사항 등록 방법
	private String		eventNoticeImage			; // 유위사항 이미지
	private String		eventNoticeContent			; // 유위사항 내용
	
}
