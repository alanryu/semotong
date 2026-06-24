package kr.co.ucomp.web.point.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointAccDTO extends BaseSearchDto {

	private int 			searchUserId;
	private String 			searchKakaoUserId;

}
