package kr.co.ucomp.web.pmb.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class ErrorReportEntity {
	
	private long 		id				;
	private long 		planId			;
	private String 		planName		;
	private String 		reportType		;
	private String 		reportTypeName	;
	private String 		reportContent	;
	private String 		memo			;
	private String 		processSp		;
	private String 		processSpName	;
	private long 		processManager	;
	private String 		managerName	;
		
	private LocalDate 	createDate		;
	private long 		createId		;
	private String 		username		;
	
	private LocalDate 	modifiedDate	;
	private long 		modifiedId		;
}
