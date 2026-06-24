package kr.co.ucomp.web.point.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointHistoryEntity implements Serializable {
 
	
	private static final long serialVersionUID = -2902062865173535715L;
	private int 			id;
	private int 			pointId;
	private int 			userId;
	
	@NotEmpty(message = "회원을 선택해 주세요.")
	private String 			username;
	
	private String 			phoneNumber;
	private String 			kakaoUserId;
	
	@NotEmpty(message = "지급/차감 구분을 선택해 주세요.")
	private String 			drCr;
	private String 			drCrName;
	
	@NotEmpty(message = "사유를 선택해 주세요.")
	private String 			crPointType;		/* 대변, 적립 자본증가 */
	private String 			crPointTypeName;
	private String 			drPointType;		/* 차변, 지급 소멸 */
	private String 			drPointTypeName;
	
	private String 			adminGiftYn;
	
	@NotNull(message = "포인트를 입력해 주세요.")
	private Integer			amount;				// 현재 발생한 point 량
	private Integer			availAmount;
	private Integer			balanceAfter;		// 현재 발생한 point 로 합산 balance(계좌 잔액)
	private String 			memo;
	
	private String 			expirationPeriodYn;
	
	private LocalDateTime 	createDate;
	private Integer 		createId;
	private String 			createName;
	
	private int 			inBoundSum;
	private int 			dedSum;
	private int 			remSum;
	private int 			outBoundSum;
	
	private Integer			displayAmount;
	private Integer			actAmt;
	private String 			detailDrPointTypeName;
	
	
}
