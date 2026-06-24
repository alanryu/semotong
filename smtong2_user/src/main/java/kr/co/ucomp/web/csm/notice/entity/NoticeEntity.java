package kr.co.ucomp.web.csm.notice.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
public class NoticeEntity {
	private int id;
	private String title;
	private String content;
	private String displayYn;
	private String topYn;
	private Date createDate;
	private int createId;
	private Date modifiedDate;
	private int modifiedId;
	private String createNm;
	private String modifiedNm;
}
