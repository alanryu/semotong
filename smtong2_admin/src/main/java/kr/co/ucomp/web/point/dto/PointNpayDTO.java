package kr.co.ucomp.web.point.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointNpayDTO extends BaseSearchDto {

	private int 			searchUserId;
	private int 			searchNpayId;
	private String 			npayApiType;		//'적립/취소/망취소/내역/리스트 구분' point, cancel, net-cancel, tx, list
	
	private String 			searchPartnerTxNo;
	
}

