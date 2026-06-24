package kr.co.ucomp.web.csm.onetoone.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class OnetooneEntity {
	private long 	id;
	private long 	requestUser;
	private String 	status;
	private String 	categoryId;
	private String 	categoryName;
	private String 	inqueryTitle;
	private String 	inqueryContent;
	private long 	responseUser;
	private String 	responseContent;
	private Date 	responseDate;
	private String 	responseInquery;
	private int 	score;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date 	createDate;
	private long 	createId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date 	modifiedDate;
	private long 	modifiedId;
}
