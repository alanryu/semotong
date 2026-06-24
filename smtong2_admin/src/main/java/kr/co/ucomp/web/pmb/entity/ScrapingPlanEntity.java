package kr.co.ucomp.web.pmb.entity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScrapingPlanEntity {

    private int id;
    private String uuid;
    private String telecom;
    private String mno;
    private String planType;
    private String planName;
    private String normalPrice;
    private String salePrice;
    private String afterPrice;
    private String promotionPeriod;
    private Integer data;
    private Integer qos;
    private String voiceCall;
    private String bagaTell;
    private String message;
    private String combination;
    private String benefit;
    private String freebies;
    private String url;
    private String businessName;
    private String etc;
    private Integer saleStatus;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
}
