package kr.co.ucomp.web.pmb.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlanBenefitEntity {

    private Integer id;
    private String title;
    private String content;
    private String displayYn;
    private LocalDateTime createDate;
    private Integer createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;
    private String modifiedNm;

    private String provider;
    private String name;
    private String icon;
    private String memo;
    private String benefitUrl;
    private String benefitImage;
    private String benefitContent;
    private String displayPeriodStart;
    private String displayPeriodEnd;

    private String majorCategoryId;
    private String minorCategoryId;
    private String majorCategoryName;
    private String minorCategoryName;

    private Integer planCount;

}