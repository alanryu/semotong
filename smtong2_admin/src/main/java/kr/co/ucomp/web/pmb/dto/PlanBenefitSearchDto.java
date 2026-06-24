package kr.co.ucomp.web.pmb.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanBenefitSearchDto extends BaseSearchDto {
	private String displayYn;
	private Integer planId;

	private String majorCategoryId; // 대분류
	private String minorCategoryId; // 중분류
	private String provider; // 제공처
	private String searchStatus; // 상태(전체, 진행중, 종료)

	private String displayPeriodStart; // 노출기간 시작일자
	private String displayPeriodEnd; // 노출기간 종료일자

}
