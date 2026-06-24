package kr.co.ucomp.web.internet.entity;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class InternetRdpMngEntity {

    private Integer id;
    private String title;
    private String topBanner;
    private String footBanner;
    private String footBarText;
    private String btnCta;
    private String btnColor;
    private String useYn;
    private String rdUrl;
    private LocalDateTime createDate;
    private Integer createId;
	private String	createNm;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;
    private String	modifiedNm		;
    
    
    private MultipartFile topFile;
    private MultipartFile footFile;
    
}