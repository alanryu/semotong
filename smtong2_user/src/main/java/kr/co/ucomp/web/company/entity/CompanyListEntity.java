package kr.co.ucomp.web.company.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;



@Setter
@Getter
public class CompanyListEntity {
	private int id;
	private String name;
	private String companyNm;
	private String companyCode;
	private String companyMno;
	private String workTime;
	private String customerCenetr;
	private String companyAdress;
	private String logoImg;
	private String homepageUrl;
	private String orderNo;
	private Integer useYn;
	private LocalDateTime createDate;
	private int createId;
	private LocalDateTime modifiedDate;
	private int modifiedId;
    private String createNm;
    private String modifiedNm;
    private Integer compReviewCnt;
}
