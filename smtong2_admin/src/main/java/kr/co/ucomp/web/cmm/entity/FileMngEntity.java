package kr.co.ucomp.web.cmm.entity;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileMngEntity {
	private int id;
	private String jobType;
	private String jobSeq;
	private String orgFileNm;
	private String sysFileNm;
	private String fileUrl;
	private String fileInfo;
	private String etc1;
	private String etc2;
	private String etc3;
	private LocalDateTime createDate;
}
