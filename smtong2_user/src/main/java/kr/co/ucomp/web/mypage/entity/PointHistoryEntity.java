package kr.co.ucomp.web.mypage.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointHistoryEntity {

	private int 			id;
	private int 			pointId;
	private int 			userId;
	
	private String 			drCr;
	private String 			crPointType;		/* 대변, 적립 자본증가 */
	private String 			crPointTypeName;
	private String 			drPointType;		/* 차변, 지급 소멸 */
	private String 			drPointTypeName;
	private String 			adminGiftYn;
	
	private Integer			amount;				// 현재 발생한 point 량
	private Integer			availAmount;
	private Integer			balanceAfter;		// 현재 발생한 point 로 합산 balance(계좌 잔액)
	
	private int 			npayId;				// Npay 지급 발생(tb_mbm_point_npay.id)
	private int 			npayAmount;
	private String 			npayStatus;
	
	private int 			cashId;				// Cash 지급 발생(tb_mbm_point_cash.id)
	private int 			cashAmount;
	private String 			cashStatus;
	
	private String 			expirationPeriodYn;
	private String 			memo;
	
	private LocalDateTime 	createDate;
	private Integer 		createId;
	
	
	private Integer			displayAmount;
	private Integer			actAmt;
	private String 			detailDrPointTypeName;
	
}
