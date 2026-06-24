package kr.co.ucomp.web.stl.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EarningEntity {

	private long id;
	private long companyId;
	private String companyName;
	private int cnt;
	private String createName;
	private String generateDate;
	private LocalDateTime createDate;
	private long createId;
}
