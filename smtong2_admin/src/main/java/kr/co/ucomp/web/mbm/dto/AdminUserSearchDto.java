package kr.co.ucomp.web.mbm.dto;



import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserSearchDto extends BaseSearchDto {
	private String searchMenuType;
	private String searchAuthType;
	private String searchAdminid;
	private String searchDisabledYn;
	private String searchCompanyMno;
	
}
