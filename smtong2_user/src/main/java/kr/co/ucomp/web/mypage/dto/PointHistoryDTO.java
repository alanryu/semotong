package kr.co.ucomp.web.mypage.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointHistoryDTO extends BaseSearchDto {

	private long 			searchHistoryId;
	
	private long 			searchUserId;
	private String			drCr;					//CR=자본증가,DR/CR, 차변(Debit)/대변(Credit)
	private String			pointType;				//거래 유형(point_type: ACT, OTH등)		//ACT, RET, FR, EXR, OTH : 개통, 가입유지, 친구추천, 우수후기, 기타
	private String			expirationPeriodYn;		//유효기간 지났니?
	
	private String			searchOrderType;		//포인트 이력 계산을 위해 order by 구분
	
}
