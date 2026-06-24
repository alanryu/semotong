package kr.co.ucomp.web.pmb.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesPlanListEntity {

	private long id;
	private long salesId;
	private long planId;
	private String uuid;
	private String planName;
	private int orderNo;
}
