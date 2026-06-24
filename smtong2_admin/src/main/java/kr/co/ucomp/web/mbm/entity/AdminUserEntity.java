package kr.co.ucomp.web.mbm.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminUserEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5157590202215432874L;
	
	private int id;
	@NotEmpty(message = "관리자 아이디를 입력 해주세요")
	@Size(max = 30, message = "최대 30글자까지 입력할 수 있습니다")
	private String adminId;
	
	private String password;
	
	private String userName;
	
	private String adminUserName;
	
	@NotEmpty(message = "계정분류를 선택해 해주세요")
	private String authType;
	
	@NotEmpty(message = "소속을 입력해 해주세요")
	@Size(max = 30, message = "최대 100글자까지 입력할 수 있습니다")
	private String comGroup;
	
	private String phoneNumber;
	@NotEmpty(message = "이메일을 입력해 해주세요")
	@Size(max = 30, message = "최대 30글자까지 입력할 수 있습니다")
	private String email;
	
	private String allAuthYn;
	private String adminMemAuthYn;
	private String userMngYn;
	private String companyMngYn;
	private String prodMngYn;
	private String reqMngYn;
	private String settleMngYn;
	private String serviceMngYn;
	
	private String pointMngYn;
	private String salesMngYn;
	private String kakaoMsgMngYn;
	

	private String manageId;
	private String disable;
	
	private LocalDateTime createDate;
	private int createId;
	private LocalDateTime modifiedDate;
	private int modifiedId;
	private String createNm;
	private String modifiedNm;
	private String authTypeNm;

	//@NotEmpty(message = "사업자를 입력해 해주세요")
	private String companyCode; // 입점사코드
	private String companyNm;  // 입점사명
	private String businessNm; // 입점사 사업자명
	private String businessCode; // 입점사 사업자코드
	private String companyMno; // 입점사 mno
	
	
	
	
	
}
