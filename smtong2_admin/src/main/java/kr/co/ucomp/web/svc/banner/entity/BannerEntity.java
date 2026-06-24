package kr.co.ucomp.web.svc.banner.entity;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerEntity {
    private Integer id;
    private String status;
    private String type;
    private String bannerName;
    private String imagePc;
    private String orgImagePc;
    private String imageMo;
    private String orgImageMo;
    private Integer sort;
    private String url;
    private String bannerAlt;
    private String urlTarget;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createDate;
    private Integer createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private String modifiedId;
    private String modifiedNm;
    private Integer bannPlanCnt;   
    private String bannPlanIds;       
    private List<PlanEntity> bannerPlanList;
    private String useYn;
    
    private String logoColor;
    private String bgColor;
}
