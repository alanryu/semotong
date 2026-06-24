package kr.co.ucomp.web.cmm.entity;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeEntity {
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
	private Date createDate;
	private int createId;
	private String createNm;
	private Date modifiedDate;
	private int modifiedId;
	private String modifiedNm;
}
