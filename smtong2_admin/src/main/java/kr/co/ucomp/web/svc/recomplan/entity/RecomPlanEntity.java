package kr.co.ucomp.web.svc.recomplan.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecomPlanEntity {

    private Integer id;
    private String title;
    private String subTitlePc;
    private String subTitleMo;
    private String iconImage;
    private String descript1;
    private String descript2;
    private String descript3;
    private String descript4;
    private String descript5;
    private Integer useYn;
    private Integer orderNo;
    private Integer planLisCnt;
    private LocalDateTime createDate;
    private Integer createId;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;
    private String	createNm;	//
    private String	modifiedNm;	//
    
    

}