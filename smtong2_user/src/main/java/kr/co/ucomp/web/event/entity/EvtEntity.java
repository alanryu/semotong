package kr.co.ucomp.web.event.entity;

import java.util.Date;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvtEntity {

	private Integer 	id					;//고유번호                       고유 아이디
	private String		eventCategory		;//이벤트 분류                    이벤트 분류  공통코드 : evt_cate
	private String		eventMngSp			;//이벤트 입력 구분               이벤트 입력 구분( 이미지 등록: i/내용 등록 : c)
	private String		title				;//이벤트 제목                    제목
	private String		eventThumbnail		;//이벤트 썸네일 JSON DATA        이벤트 썸네일 JSON DATA
	private String		eventImage			;//이벤트 전체 이미지             이벤트 전체 이미지(event_mng_sp->i일 경우)
	private String		content				;//이벤트 내용                    
	private Integer		manager				;//담당자 / 유저 아이디           담당자 / 유저 아이디
	
	private Date		startDate			;   //이벤트 시작일			
	private Date		endDate				;   //이벤트 종료일
	
	private String		strStart		;   //이벤트 시작일
	private String		strEnd			;   //이벤트 종료일
	
	private String		createDate			;   //생성 날짜	datetime
	private Integer		createId			;   //생성자 사용자 로그인 id
	private Date		modifiedDate		;   //수정 날짜				
	private Integer		modifiedId			;   //생성자 사용자 로그인 id
	
	
	private String		eventNoticeSp				; // 유위사항 등록 방법
	private String		eventNoticeImage			; // 유위사항 이미지
	private String		eventNoticeContent			; // 유위사항 내용
}
