package kr.co.ucomp.web.company.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyListSearchDto extends BaseSearchDto {

	private int useYn;
	private String type;
}
