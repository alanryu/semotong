package kr.co.ucomp.web.plan.entity;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
public class PlanFreebieMappingEntity {

    private int id;
    private int planListId;
    private int planfreebieId;
    private Integer orderNo;
    private LocalDateTime createDate;
    private int createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private int modifiedId;
    private String modifiedNm;
    private String planName;
    private String freebieContent;
    private String freebieTitle;
    
}