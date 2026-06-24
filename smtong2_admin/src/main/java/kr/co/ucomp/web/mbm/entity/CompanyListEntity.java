package kr.co.ucomp.web.mbm.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;



@Setter
@Getter
public class CompanyListEntity implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	private int id;
	
	@NotEmpty(message = "사업자명을 입력해주세요")
	@Size(max = 50, message = "최대 50글자까지 입력할 수 있습니다")
	private String name;
	
	private String companyMngCode;
	
	@NotEmpty(message = "회사명을 입력해주세요")
	@Size(max = 50, message = "최대 50글자까지 입력할 수 있습니다")
	private String companyNm;
	private String companyCode;
	@NotNull(message = "통신사구분을 선택해 주세요")
	private String companyMno;
	private String workTime;
	private String customerCenetr;
	private String companyAdress;
	private String logoImg;
	private String homepageUrl;
	
	@NotEmpty(message = "정렬순서를 입력해 주세요")
	private String orderNo;
	
	@NotNull(message = "사용여부를 선택해 주세요")
	private Integer useYn;
	private LocalDateTime createDate;
	private Integer createId;
	private LocalDateTime modifiedDate;
	private Integer modifiedId;
    private String createNm;
    private String modifiedNm;
    private Integer allyPartnetYn;
    private Integer simpleOpenYn;
}
