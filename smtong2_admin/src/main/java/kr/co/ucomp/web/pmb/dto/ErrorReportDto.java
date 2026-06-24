package kr.co.ucomp.web.pmb.dto;


import java.util.List;
import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorReportDto extends BaseSearchDto {

	private String searchKeyword			;
	private List<String> searchProcessSp	;
	
}
