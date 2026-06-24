package kr.co.ucomp.web.pmb.entity;

import java.time.LocalDateTime;

import kr.co.ucomp.web.pmb.entity.PlanReqMngEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanReqMngEntity {
    private Integer id;
    private String reqNm;
    private String reqPhonNum;
    private Integer reqProd;
    private String reqProdNm;
    private String reqSp;
    private Integer salePlanId;
    private LocalDateTime reqDate;
    private LocalDateTime createDate;
    private Integer createId;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;    
    private String createNm;    
    private String modifiedNm;
    private String planCompanyNm;
    private String planMno;
    
    private Integer normalPrice;
    private Integer salePrice;
    private Integer afterPrice;
    private String promotionPeriod;
    private String linkUrl;
    private String urlSmt;    
}
