package kr.co.ucomp.web.csm.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnetooneDto extends BaseSearchDto {

    private String searhDisplayYn;
    private String searhState;
    private String searchCategory;
    private String searchScore;

}
