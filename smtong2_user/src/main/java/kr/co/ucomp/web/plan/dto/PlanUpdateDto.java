package kr.co.ucomp.web.plan.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlanUpdateDto {
	
    private Long id;
    private String uuid;
    private String planCode;
    private Boolean saleStatus;
    private Boolean useYn;
    private Boolean newYn;
    private String urlSmt;        
    private Boolean imageBadgeSpecial;
    private Boolean imageBadgeItComb;
    private Boolean imageBadgeAddData;
    private Boolean imageBadgeSimpleOpen;
    private Boolean combinationEnable;
    private String suppContent;
    private String noSuppContent;
    private String eventBannerImagePc;
    private String eventBannerImageMo;    
    private String eventBannerImageUrl;
    private String eventBannerImageTarget;
    private Integer modifiedId;

}
