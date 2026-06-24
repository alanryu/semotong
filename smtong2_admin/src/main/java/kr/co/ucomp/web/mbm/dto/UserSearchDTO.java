package kr.co.ucomp.web.mbm.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserSearchDTO extends BaseSearchDto {
	String searchUserState;
	String searchAgeGroup;
    String searchUserId;
    String searchKakaoId;
    String searchChannelYn;
}
