package kr.co.ucomp.common.restapi.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiLogEntity {
    private Integer id;
    private String apiCode;
    private String apiName;
    private String apiUrl;
    private String reqMsg;
    private String resBody;
    private String resMsg;
    private LocalDateTime createDate;

}
