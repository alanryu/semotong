package kr.co.ucomp.web.pmb.entity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class PlanFreebieMappingEntity {

    private Integer id;
    private Integer planListId;
    private Integer planfreebieId;
    private Integer orderNo;
    private LocalDateTime createDate;
    private Integer createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;
    private String modifiedNm;
    private String planName;
    private String freebieTitle;
    private String freebieContent;
    
}