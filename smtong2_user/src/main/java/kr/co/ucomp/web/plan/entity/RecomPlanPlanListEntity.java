package kr.co.ucomp.web.plan.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecomPlanPlanListEntity {

    private Integer id;
    private Integer mngId;
    private Integer planId;
    private Integer orderNo;
    private String recomTitle;
    
    private String mno;
    private String planType;
    private String planHostNm;
    private String planName;
    private String supDataVal;
    private String dailyData;
    private String supQos;
    private String normalPrice;
    private String salePrice;
    private String afterPrice;
    private String promotionPeriod;

    
    


}