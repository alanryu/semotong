package kr.co.ucomp.web.pmb.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapingLogSearchDto extends BaseSearchDto {
	private Long 	searchId;
	private Integer searchCycleId;
	private String	searchLogType;
	private String	searchfunctionName;
	private String	searchMessage;
	
	private String	searchUuid;
}
