package kr.co.ucomp.web.mypage.dto;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2299034788503509952L;
	private long 			id;
    private String 			email;
    private String 			username;
    private String	 		activeYn;
    private String 			phoneNumber;
    private String 			ageGroup;
    private Integer			ageGroupVal;
    private String 			birthDay;
    private String 			birthYear;
    private String 			kakaoUserId;
    private LocalDateTime 	joinDate;
    private String 			disableResn;
    private boolean 		piAgreeYn;
    private LocalDateTime 	piAgreeDttm;
    private boolean 		polAgreeYn;
    private LocalDateTime 	polAgreeDttm;
    private boolean 		smsAgreeYn;
    private LocalDateTime 	smsAgreeDttm;
    private boolean 		emailAgreeYn;
    private LocalDateTime 	emailAgreeDttm;
    private String 			memberStat;
    private LocalDate createDate;
    private LocalDate modifiedDate;
    private Long createId;
    private Long modifiedId;
    private Integer channelYn;
}
