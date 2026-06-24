package kr.co.ucomp.web.plan.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlanReqMngEntity {
    private Integer 		id;
    private String 			reqNm;
    private String 			reqPhonNum;		//reqPhonNum reqPhon e Num  주의.... ㅜ.ㅜ
    private Integer 		reqProd;
    private String	 		reqSp;
    private LocalDateTime 	reqDate;
    private Integer	 		salePlanId;
    private LocalDateTime 	createDate;
    private Integer 		createId;
    private LocalDateTime 	modifiedDate;
    private Integer 		modifiedId;
}
