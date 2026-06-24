package kr.co.ucomp.web.pmb.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapingLogEntity {
	private long 			id;
	private int 			cycleId;
	private String 			logType;
	private String 			logTypeName;
	private String 			functionName;
	private LocalDateTime 	logTime;
	private String 			message;
}
