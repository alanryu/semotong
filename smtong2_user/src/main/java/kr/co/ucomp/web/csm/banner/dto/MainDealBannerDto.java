package kr.co.ucomp.web.csm.banner.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MainDealBannerDto extends BaseSearchDto {

    private String type;
    private long main_deal;
    private long plan_mno;
    private long plan_id;

}
