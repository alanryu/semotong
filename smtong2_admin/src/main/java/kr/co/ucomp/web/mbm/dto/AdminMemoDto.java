package kr.co.ucomp.web.mbm.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMemoDto extends BaseSearchDto {

    private Integer userId;
    private String memoType;

}
