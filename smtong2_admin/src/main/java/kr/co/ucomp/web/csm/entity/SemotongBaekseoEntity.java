package kr.co.ucomp.web.csm.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SemotongBaekseoEntity {
	private long 				id;
	private String 				contentSp;
	private String 				contentSpNm;
	
	private String 				displaySp;
	
	private LocalDateTime 		displayStartDttm;
	private LocalDateTime 		displayEndDttm;
	
	private String 				image;
	private String 				title;
	private String 				content;

	private LocalDateTime 		createDate;
	private long 				createId;

	private String 				userName;
	
	private LocalDateTime 		modifiedDate;
	private long 				modifiedId;

}
