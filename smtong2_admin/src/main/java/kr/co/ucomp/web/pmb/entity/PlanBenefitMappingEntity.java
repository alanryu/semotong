package kr.co.ucomp.web.pmb.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
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

    private String mno;
    private String afterPrice;
    private String salePrice;
    private String promotionPeriod;
    private String supDataTotal;
    private String supQos;

    private String hostNm;

    private String dispYn;
    private String saleStatus;

}