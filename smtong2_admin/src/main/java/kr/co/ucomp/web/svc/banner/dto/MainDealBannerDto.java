package kr.co.ucomp.web.svc.banner.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainDealBannerDto extends BaseSearchDto {

    private String type;
    private long main_deal;
    private long plan_mno;
    private long plan_id;

}
