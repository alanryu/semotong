package kr.co.ucomp.web.plan.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
public class PlanBenefitMappingEntity {

    private int id;
    private int planListId;
    private int planBenefitsId;
    private Integer orderNo;
    private LocalDateTime createDate;
    private int createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;
    private String modifiedNm;
    private String planName;
    private String benefitTitle;
    private String benefitContent;

    private String provider;
    private String majorCategoryId;
    private String minorCategoryId;

    private String majorCategoryName;
    private String minorCategoryName;

    private String hostNm;
    private String benefitIcon;
    private String benefitUrl;
    private String benefitImage;
    private String benefitContent2;

    private String icon;

}