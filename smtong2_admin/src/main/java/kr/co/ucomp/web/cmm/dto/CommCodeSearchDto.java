package kr.co.ucomp.web.cmm.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommCodeSearchDto extends BaseSearchDto {
	private String codeGroup;
	private String code;
	private String userYn;
	private String etc1;
	private String etc2;
	private String etc3;
	private String etc4;
	private String notCode;
	
}
