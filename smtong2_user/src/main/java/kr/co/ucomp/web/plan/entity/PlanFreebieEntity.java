package kr.co.ucomp.web.plan.entity;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
public class PlanFreebieEntity {

    private int id;
    private String content;
    private String displayYn;
    private LocalDateTime createDate;
    private int createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private int modifiedId;
    private String modifiedNm;
}