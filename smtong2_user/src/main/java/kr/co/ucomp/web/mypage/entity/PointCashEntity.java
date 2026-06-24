package kr.co.ucomp.web.mypage.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointCashEntity {

	private int 			id;
	private int 			pointId;
	private int 			userId;
	private String 			bankCode;
	private String 			bankName;
	private String 			accountNo;
	private String 			accountName;
	private Integer			amount;
	private LocalDateTime	reqDate;
	private String			status;		//'신청,처리완료,반려(REQ, COM, REJ:Submitted,Completed,Rejected)'
	private String 			memo;
	
	private LocalDateTime 	createDate;
	private Integer 		createId;
	
}
