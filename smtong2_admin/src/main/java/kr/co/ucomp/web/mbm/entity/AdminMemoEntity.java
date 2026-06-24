package kr.co.ucomp.web.mbm.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class AdminMemoEntity {

    private long id;
    private String memoType;
    private String memo;
    private long userId;    
    private String etc1;
    private String etc2;
    private String etc3;    
    private LocalDateTime createDate;
    private int createId;
    private LocalDateTime modifiedDate;
    private int modifiedId;    
	private String createNm;
	private String modifiedNm;

}
