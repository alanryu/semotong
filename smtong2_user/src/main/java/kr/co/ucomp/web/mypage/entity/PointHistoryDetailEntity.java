package kr.co.ucomp.web.mypage.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointHistoryDetailEntity {

	private int 			id;
	private int 			historyId;
	
	private String 			drPointType;		// EXN, EXC, CAN 
	private String 			drPointTypeName;
	
	private Integer			totAmt;				// 현 거래 총 금액 5000, 10000 등
	private Integer			actAmt;				// 현재 row 계산 금액 point history row 의 avail_amount
	private Integer			remAmt;				// 남은 차액, 앞으로 더 깍아야 할 총 금액 > 5500 > 4500 > 3500 > ...
	
	private int 			npayId;				// Npay 지급 발생(tb_mbm_point_npay.id)
	private int 			cashId;				// Cash 지급 발생(tb_mbm_point_cash.id)
	
	private LocalDateTime 	createDate;
	private Integer 		createId;
	
}
