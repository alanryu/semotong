package kr.co.ucomp.web.svc.event.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvtWinnerEntity {

	private Integer 	id					;	//고유번호
	private String		title				;   //이벤트 제목
	private String		content				;   //이벤트 내용			
	private Integer		disable				;   //비활성화 여부 비활성 true / 활성 false	
	private String		createDate			;   //생성 날짜	datetime
	private Integer		createId			;   //생성자 사용자 로그인 id
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date		modifiedDate		;   //수정 날짜				
	private Integer		modifiedId			;   //생성자 사용자 로그인 id
}
