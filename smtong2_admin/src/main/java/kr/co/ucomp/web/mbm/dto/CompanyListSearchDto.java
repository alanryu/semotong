package kr.co.ucomp.web.mbm.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyListSearchDto extends BaseSearchDto {
	Integer searchUseYn;
	String orderSp;
	Integer allyPartnetYn;
	Integer simpleOpenYn;
	private long searchCompanyCode;

}
