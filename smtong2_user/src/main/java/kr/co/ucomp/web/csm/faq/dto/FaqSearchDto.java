package kr.co.ucomp.web.csm.faq.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FaqSearchDto extends BaseSearchDto {
	private String displayYn;
	private String categoryId;
	private String notCategory;
}
