package kr.co.ucomp.web.csm.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.validation.constraints.NotEmpty;

@Setter
@Getter
public class PolicyContentEntity  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 298686292937311261L;
	
	private Integer id;
	private String title;
	private String content;
	@NotEmpty(message = "카테고리를 선택해주세요")
	private String contentCategory;
	private String appDate;
	private String dispYn;
	private LocalDateTime createDate;
	private int createId;
	private LocalDateTime modifiedDate;
	private int modifiedId;
	private String createNm;
	private String modifiedNm;
}
