package kr.co.ucomp.web.mbm.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEntity {
    private Integer id;
    private String email;
    private String username;
    private String phoneNumber;
    private String ageGroup;
    private String ageGroupVal;
    private String birthDay;
    private String birthYear;
    private String kakaoUserId;
    private LocalDateTime joinDate;
    private String activeYn;
    private String disableResn;
    private String piAgreeYn;
    private LocalDateTime piAgreeDttm;
    private String polAgreeYn;
    private LocalDateTime polAgreeDttm;
    private String smsAgreeYn;
    private LocalDateTime smsAgreeDttm;
    private String emailAgreeYn;
    private LocalDateTime emailAgreeDttm;
    private String memberStat;
    private LocalDateTime createDate;
    private int createId;
    private LocalDateTime modifiedDate;
    private int modifiedId;
    private LocalDateTime disableDttm;
    private int disableMngId;
    
    private String disableMngNm;
    private String createNm;
    private String modifiedNm;
    private String memberStatNm;
    
    private String memberStatNow;
    private Integer channelYn;
    
}
