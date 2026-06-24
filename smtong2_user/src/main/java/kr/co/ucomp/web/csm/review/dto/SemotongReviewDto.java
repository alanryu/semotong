package kr.co.ucomp.web.csm.review.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SemotongReviewDto extends BaseSearchDto {

    private long createId;
    private String displayYn;
    private String reviewType;
    private String companyId;
    private String planId;

}
