package kr.co.ucomp.web.csm.banner.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
public class MainDealMstDto extends BaseSearchDto {

    private Date start_date;
    private Date end_date;
    private String type;

}
