package kr.co.ucomp.web.event.entity;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvtPlanEntity {

	private Integer 	id					;	//고유번호
	private Integer		eventId				;	//이벤트 관리 ID
	private Integer		planId		;			//요금제 id
}
