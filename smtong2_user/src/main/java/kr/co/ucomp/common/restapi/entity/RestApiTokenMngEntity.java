package kr.co.ucomp.common.restapi.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiTokenMngEntity {
    private String tokenCode;
    private String tokenName;
    private String tokenVal;
    private LocalDateTime createDttm;
    private LocalDateTime expiredDttm;
}
