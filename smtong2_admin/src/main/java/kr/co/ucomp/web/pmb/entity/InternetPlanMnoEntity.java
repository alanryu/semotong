package kr.co.ucomp.web.pmb.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternetPlanMnoEntity {
	private Integer id;
	private String name;
	private String mnoLogo;
	private String alamRcvNum;
	private Integer manager;
	private String useYn;
	private String newUseYn;
	private String newUiUseYn;
	private Integer orderNo;
    private int createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private int modifiedId;
    private String modifiedNm;
	private String outboundCenter;

}
