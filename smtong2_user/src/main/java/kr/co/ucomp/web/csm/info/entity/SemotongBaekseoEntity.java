package kr.co.ucomp.web.csm.info.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
public class SemotongBaekseoEntity {
    private long id;
    private String contentSp;
    private String displaySp;
    private LocalDateTime displayStartDttm;
    private LocalDateTime displayEndDttm;
    private String image;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private long createId;
    private LocalDateTime modifiedDate;
    private long modifiedId;

}
