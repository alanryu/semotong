package kr.co.ucomp.web.pmb.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapingAPIDto extends BaseSearchDto {
	
	private Integer cycle_id;
	private Integer	wait_time;
	
	public String getStatus() {
		return "/status";
	}
	
	public String getStart() {
		return "/start";
	}
	
	public String getShutdown() {
		return "/shutdown";
	}
	
	
	public String serverUri;
}
