package kr.co.ucomp.web.svc.banner.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerPlanEntity {

	private Integer 	id					;	//고유번호
	private Integer		bannerId			;	//배너 관리 ID
	private Integer		planId				;	//요금제 ID
	private String		useYn				;	//사용여부
	private String 		uuid				;	//요금제 uuid
	private String 		planName			;	//요금제 명
	private Integer		orderNo				;	//정렬순서
}
