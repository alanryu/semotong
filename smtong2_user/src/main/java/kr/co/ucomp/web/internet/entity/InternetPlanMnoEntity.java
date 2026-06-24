package kr.co.ucomp.web.internet.entity;

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
    private int createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private int modifiedId;
    private String modifiedNm;

}
