package kr.co.ucomp.web.csm.entity;
import java.io.Serializable;
import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FaqEntity implements Serializable {
	
	private static final long serialVersionUID = 8067161800271344210L;
	private int id;
	@NotEmpty(message = "질문을 입력해주세요")
	@Size(max = 500, message = "최대 500글자까지 입력할 수 있습니다")
	private String inquiry;
	@NotEmpty(message = "답변을 입력해주세요")
	private String ansContent;
	@NotEmpty(message = "카테고리를 선택해주세요")
	private String faqCategory;
	private String displayYn;
	private Date createDate;
	private int createId;
	private Date modifiedDate;
	private int modifiedId;
	private String createNm;
	private String modifiedNm;
	private String faqCategoryNm;
}
