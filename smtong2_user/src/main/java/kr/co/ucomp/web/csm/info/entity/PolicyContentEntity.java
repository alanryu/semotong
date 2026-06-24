package kr.co.ucomp.web.csm.info.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
public class PolicyContentEntity {
	private int id;
	private String title;
	private String content;
	private String contentCategory;
	private Date appDate;
	private Date createDate;
	private Date modifiedDate;
	private int modifiedId;
	private String createNm;
	private String modifiedNm;
}
