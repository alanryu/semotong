package kr.co.ucomp.web.order.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStateEntity {
	private Integer id;
	private String orderId;
	private String orderState;
	private String orderStateHost;
	private String orderMemo;
	private String accountNum;
	private String openCompDttm;
	private String openTelNum;
	private LocalDateTime createDate;
}
