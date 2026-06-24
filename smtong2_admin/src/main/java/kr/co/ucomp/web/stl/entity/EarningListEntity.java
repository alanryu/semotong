package kr.co.ucomp.web.stl.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EarningListEntity {

	private long id;
	private long earningId;
	private String agencyName;
	private String appDate;
	private String openDate;
	private String mno;
	private String contractNum;
	private String accType;
	private String phoneNum;
	private String accName;
	private String planName;
	private String status;
	private LocalDateTime createDate;
	private long createId;
}
