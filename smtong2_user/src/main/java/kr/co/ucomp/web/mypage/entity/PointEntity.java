package kr.co.ucomp.web.mypage.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointEntity {

	private int 			id;
	private int 			userId;
	private Integer 		balance;
	
	private String 			status;
	private String 			comment;
	private String 			lastHistoryId;
	private LocalDateTime	lastHistoryDate;
	private String 			lastNpayId;
	private LocalDateTime	lastNpayDate;
	private String 			lastCashId;
	private LocalDateTime	lastCashDate;
	
	private LocalDateTime 	createDate;
	private Integer 		createId;
	private LocalDateTime 	modifiedDate;
	private Integer 		modifiedId;

}
