package kr.co.ucomp.web.plan.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesPlanEntity {

	private long id;
	private long comUserId;
	private String comUserNm;
	private String title;
	private String imagePc;
	private String imageMo;
	private boolean useYn;
	private long idx;
	private String url;
	private LocalDateTime createDate;
	private long createId;
	private LocalDateTime modifiedDate;
	private long modifiedId;
	private String orgImagePc;
}
