package kr.co.ucomp.web.csm.info.dto;


import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SemotongBaekseoDto extends BaseSearchDto {

    private String title;
    private String contentSp;

}
