package kr.co.ucomp.web.csm.banner.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerPlanEntity {

	private Integer 	id					;	//고유번호
	private Integer		bannertId			;	//배너 관리 ID
	private Integer		planId		;			//요금제 ID
}
