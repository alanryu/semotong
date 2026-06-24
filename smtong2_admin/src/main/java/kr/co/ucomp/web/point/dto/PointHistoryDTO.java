package kr.co.ucomp.web.point.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointHistoryDTO extends BaseSearchDto {

	private int 			searchHistoryId;
	private int 			searchUserId;
	private int 			searchPointId;
	
	private String			drCr;					//CR=자본증가,DR/CR, 차변(Debit)/대변(Credit)
	private String			pointType;				//checkbox 검색 Array 로 넘겨 조회 한다.
	
	private String			crPointType;			//거래 유형(point_type: ACT, OTH등)		//ACT, RET, FR, EXR, OTH : 개통, 가입유지, 친구추천, 우수후기, 기타
	private String			drPointType;			//거래 유형(point_type: DIS, EXN, EXC)
	
	private String			expirationPeriodYn;		//유효기간 지났니?
	
	private String			adminGiftYn;
	
	
	private int 			searchNpayId;
	private int 			searchCashId;
	
	
}
