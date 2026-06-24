package kr.co.ucomp.web.svc.event.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvtPlanEntity {

	private Integer 	id					;	//고유번호
	private Integer		eventId				;	//이벤트 관리 ID
	private Integer		planId		;			//요금제 id
	private Integer		orderNo		;			//요금제 id
	
	private String		uuid				;
	private String		planName			;
	
}
