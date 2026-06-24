package kr.co.ucomp.web.cmm.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileMngDto extends BaseSearchDto {
	
	private int searchId;
	private String jobType;
	private String jobSeq;
	private String orgFileNm;
	private String sysFileNm;
	private String fileUrl;
	private String fileInfo;
	private String etc1;
	private String etc2;
	private String etc3;

}
