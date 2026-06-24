package kr.co.ucomp.web.point.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointAccEntity {

	private int 			id;
	private int 			userId;
	private Integer 		balance;
	
	private String 			status;
	private String 			comment;
	private int 			lastHistoryId;
	private LocalDateTime	lastHistoryDate;
	private int 			lastNpayId;
	private LocalDateTime	lastNpayDate;
	private int 			lastCashId;
	private LocalDateTime	lastCashDate;
	
	private LocalDateTime 	createDate;
	private Integer 		createId;
	private LocalDateTime 	modifiedDate;
	private Integer 		modifiedId;
	
	//계정 조회, 여기에 사용자 정보 추가 한다.
	private String 			username;
	private String 			phoneNumber;
	private String 			kakaoUserId;
	private LocalDateTime 	joinDate;
	private String 			birthDay;		//String 이다. 주의 
	
	private Integer 		amount;			//계산용

}
