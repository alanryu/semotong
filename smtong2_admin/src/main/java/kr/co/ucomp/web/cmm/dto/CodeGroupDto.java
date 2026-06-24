package kr.co.ucomp.web.cmm.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeGroupDto {
	private String codeGroup;
	private String codeGroupName;
	private String codeGroupDesc;
	private String useYn;
	private int createId;
	private int modifiedId;

}
