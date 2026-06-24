package kr.co.ucomp.web.stl.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EarningSearchDto extends BaseSearchDto {

	private long searchEarningId;
	private long searchCompanyId;
	private String searchGenerateDate;
	private String searchCompanyList;
}
