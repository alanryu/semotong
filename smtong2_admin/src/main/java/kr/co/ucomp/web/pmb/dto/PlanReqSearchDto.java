package kr.co.ucomp.web.pmb.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class PlanReqSearchDto extends BaseSearchDto {
    private String searchreqSp;
    private String searchMno;
    private String searchCompanyList;
    private String searchSalePlanId;
}
