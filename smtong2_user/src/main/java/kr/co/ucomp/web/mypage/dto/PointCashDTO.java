package kr.co.ucomp.web.mypage.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointCashDTO extends BaseSearchDto {

	private long 			searchUserId;
	private String			status;
	
}

