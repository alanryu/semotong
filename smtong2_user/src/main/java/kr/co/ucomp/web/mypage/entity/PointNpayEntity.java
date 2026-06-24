package kr.co.ucomp.web.mypage.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointNpayEntity {

	private int 			id;
	private int 			pointId;
	private int 			userId;
	
	private String 			npayApiType;		//'적립/취소/망취소/내역/리스트 구분' point, cancel, net-cancel, tx, list
	private String 			npayApiTypeName;
	private Integer 		reqAmount;
	private String			userKey;		// 다우 발번 사용자key
	private String			partnerTxNo;	// 세모통 발번 거래 번호 partnerTxNo
	private String			txNo;			// 다우   발번 거래 번호 txNo
	
	private String 			memo;
	private String 			daouCode;		//	전송 결과 코드
	private String 			daouMessage;
	
	private LocalDateTime 	createDate;
	private Integer 		createId;
	private LocalDateTime 	modifiedDate;
	private Integer 		modifiedId;
	
}
