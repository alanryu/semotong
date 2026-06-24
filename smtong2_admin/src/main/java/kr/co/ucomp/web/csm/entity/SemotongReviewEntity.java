package kr.co.ucomp.web.csm.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class SemotongReviewEntity {
	
	private long 		id;
	private String		reviewType;
	private String 		content;
	private String 		contentShort;
	private int 		score;
	private String 		displayYn;
	private long 		manageId;
	private String 		adminName;
	private String 		manageMemo;
	private long 		planId;
	private String 		planName;
	private String 		companyId;
	private String 		companyName;
	private LocalDate 	createDate;
	private long 		createId;
	private String 		username;
	private LocalDate 	modifiedDate;
	private long 		modifiedId;
}
