package kr.co.ucomp.web.csm.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class OnetooneEntity implements Serializable{

	private static final long serialVersionUID = 3614619166601217362L;
	
	private Integer id;
    private Integer requestUser;
    private String status;
    private String categoryId;
    private String inqueryTitle;
    private String inqueryContent;
    private Integer responseUser;
    private String responseContent;
    private Date responseDate;
    private String responseInquery;
    private Integer score;
    private String displayYn;
    private Date createDate;
    private Integer createId;
    private Date modifiedDate;
    private Integer modifiedId;
	private String createUserName;
	private String responseNm;
	private String categoryNm;    
    
}
