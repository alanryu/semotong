package kr.co.ucomp.web.cmm.entity;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeGroupEntity {
	private String codeGroup;
	private String codeGroupName;
	private String codeGroupDesc;
	private String useYn;
	private Date createDate;
	private int createId;
	private String createNm;
	private Date modifiedDate;
	private int modifiedId;
	private String modifiedNm;
}
