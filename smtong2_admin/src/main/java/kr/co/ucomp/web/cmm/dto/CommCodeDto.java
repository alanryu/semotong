package kr.co.ucomp.web.cmm.dto;

import java.util.Date;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommCodeDto {
	private String codeGroup;
	private String code;
	private String codeName;
	private String codeDesc;
	private int orderNo;
	private String etc1;
	private String etc2;
	private String etc3;
	private String etc4;
	private String etc5;	
	private String useYn;
	private int createId;
	private int modifiedId;

}
