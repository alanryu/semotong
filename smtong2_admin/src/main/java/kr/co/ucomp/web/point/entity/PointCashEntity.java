package kr.co.ucomp.web.point.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointCashEntity implements Serializable {
	
	private static final long serialVersionUID = 5319339547967794893L;
	private int 			id;
	private int 			pointId;
	private int 			userId;
	
	@NotEmpty(message = "회원을 선택해 주세요.")
	private String 			username;
	
	private String 			phoneNumber;
	private String 			kakaoUserId;
	
	private String 			bankCode;
	private String 			bankName;
	private String 			accountNo;
	private String 			accountName;
	private Integer			amount;
	private LocalDateTime	reqDate;
	private String			status;		//'신청,처리완료,반려(REQ, COM, REJ:Submitted,Completed,Rejected)'
	private String			statusName;
	
	private String 			memo;
	
	private LocalDateTime 	createDate;
	private Integer 		createId;
	private String 			createName;
	
	private LocalDateTime 	modifiedDate;
	private Integer 		modifiedId;
	private String			modifiedName;
	
}
