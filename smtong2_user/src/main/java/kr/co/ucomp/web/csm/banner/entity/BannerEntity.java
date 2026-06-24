package kr.co.ucomp.web.csm.banner.entity;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BannerEntity {
    private Integer id;
    private String status;
    private String type;
    private String bannerName;
    private String imagePc;
    private String imageMo;
    private Integer sort;
    private String url;
    private String bannerAlt;
    private String urlTarget;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createDate;
    private String createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private String modifiedId;
    private String modifiedNm;
    private Integer bannPlanCnt;   
    private String bannPlanIds;       
    
    private String logoColor;
    private String bgColor;
    
    private List<PlanEntity> bannerPlanList;
}
