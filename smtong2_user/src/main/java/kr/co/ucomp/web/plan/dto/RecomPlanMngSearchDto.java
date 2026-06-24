package kr.co.ucomp.web.plan.dto;



import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecomPlanMngSearchDto   extends BaseSearchDto {
	private Integer useYn;
	private Integer mngid;
}
