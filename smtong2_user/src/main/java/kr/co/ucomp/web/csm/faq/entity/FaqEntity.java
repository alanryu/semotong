package kr.co.ucomp.web.csm.faq.entity;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FaqEntity {
	private int id;
	private String inquiry;
	private String ansContent;
	private String faqCategory;
	private String faqCategoryNm;
	private String displayYn;
	private Date createDate;
	private int createId;
	private String createNm;
	private Date modifiedDate;
	private int modifiedId;
	private String modifiedNm;
}
