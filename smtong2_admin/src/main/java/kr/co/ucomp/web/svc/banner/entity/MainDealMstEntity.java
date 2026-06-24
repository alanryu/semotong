package kr.co.ucomp.web.svc.banner.entity;


import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainDealMstEntity {

    private long id;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private LocalDateTime create_date;
    private long create_id;
    private LocalDateTime modified_date;
    private long modified_id;
    private List<MainDealBannerEntity> list;
    private String createNm;
    private String title;
    private String type;
    private String btn_type;
    private Integer exposure_time;
    private String pop_image;
    private String orgImagePc;
    private String dim_title;
    private String link_url;
    private String link_type;
}
