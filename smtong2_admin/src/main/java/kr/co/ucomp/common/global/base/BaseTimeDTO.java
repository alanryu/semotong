package kr.co.ucomp.common.global.base;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BaseTimeDTO {

    private LocalDate createDate;
    private LocalDate modifiedDate;
    private Long createId;
    private Long modifiedId;
}
