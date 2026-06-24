package kr.co.ucomp.web.pmb.entity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlanCateMngEntity {

	private String codeGroup;
	private String code;
	private String codeName;
	private String codeDesc;
	private String etc1;
    private Integer planCnt;
    private String useYn;
    private String orderNo;
    private Integer createId;
    private Integer modifiedId;
    
	private String cateSp;
    private String cateCode;
    private String cateName;
        
    private Integer tagCount;
    private String tagCode;
    private String tagName;
    
}