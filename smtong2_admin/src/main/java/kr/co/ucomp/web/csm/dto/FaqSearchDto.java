package kr.co.ucomp.web.csm.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaqSearchDto extends BaseSearchDto {
	private String displayYn;
	private String categoryId;
}
