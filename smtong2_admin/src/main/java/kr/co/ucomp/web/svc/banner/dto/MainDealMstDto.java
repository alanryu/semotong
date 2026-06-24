package kr.co.ucomp.web.svc.banner.dto;

import java.util.Date;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainDealMstDto extends BaseSearchDto {

    private String start_date;
    private String end_date;
    private Date create_date;
    private Integer isDispStatusAll;
	private Integer isDispStatusBef;
	private Integer isDispStatusDsp;
	private Integer isDispStatusEnd;
	private String type;

}
