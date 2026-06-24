package kr.co.ucomp.web.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class custCertDto {
	private String custnm;
	private String custhid;
	private String custphoneNum;
	private String txId;
	
	private String issudt;
	private String licenseNum;
	private String secureNum;
	private String identifySp;
	
	
	private String bankCd;
	private String bankNum;
	
}
