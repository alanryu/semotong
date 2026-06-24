package kr.co.ucomp.web.plan.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class ErrorReportEntity {
    private int id;
    private Integer planId;
    private String reportType;
    private String reportContent;
    private String memo;
    private String processSp;
    private Integer process_manager;
    private LocalDateTime createDate;
    private int createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private int modifiedId;
    private String modifiedNm;
}
