package kr.co.ucomp.web.csm.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


@Setter
@Getter
public class NoticeEntity implements Serializable {
	
	private static final long serialVersionUID = -5327418237300543526L;
	
	private int id;
	@NotEmpty(message = "제목을 입력해주세요")
	@Size(max = 500, message = "최대 500글자까지 입력할 수 있습니다")
	private String title;
	@NotEmpty(message = "공지사항 컨텐츠를 입력해주세요")
	private String content;
	private String displayYn;
	private String topYn;
	private Date createDate;
	private Integer createId;
	private Date modifiedDate;
	private Integer modifiedId;
	private String createUserName;
	private String modifiedUserName;
}
