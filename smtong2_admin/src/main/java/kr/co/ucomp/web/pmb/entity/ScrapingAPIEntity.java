package kr.co.ucomp.web.pmb.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapingAPIEntity {
	
	private String 			status;
	private Integer			cycle_id;
	private String 			message;
	
	
}
