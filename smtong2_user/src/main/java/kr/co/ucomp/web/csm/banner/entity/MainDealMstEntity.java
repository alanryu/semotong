package kr.co.ucomp.web.csm.banner.entity;


import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class MainDealMstEntity {

    private long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date start_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date end_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date create_date;
    private long create_id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date modified_date;
    private long modified_id;
    private String title;
    private String type;
    private String btn_type;
    private Integer exposure_time;
    private String pop_image;
    private String orgImagePc;
	private Long eventId;
	private String dim_title;
	private String link_url;
	private String link_type;
	
}
