package kr.co.ucomp.web.svc.banner.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class MainDealBannerEntity {

    private long id;
    private long main_deal;
    private String type;
    private String plan_content;
    private long plan_mno;
    private long plan_id;
    private String link_url;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date create_date;
    private long create_id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date modified_date;
    private long modified_id;
    private String plan_name;
    private int sale_price; 
    private String logo_img;
    private int order_no;

}
