package kr.co.ucomp.web.stl.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SettlementMngEntity {

    private long id;
    private int companyId;
    private String companyNm;
    private String yyyymm;
    private String stlType;
    private String mno;
    private String network;
    private String planName;
    private String dataPlan;
    private BigDecimal basicFee;
    private BigDecimal discountAmount;
    private String discountPeriod;
    private String applicant;
    private String contactNumber;
    private Date activationDate;
    private LocalDateTime createDate;
    private int createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private int modifiedId;
    private String modifiedNm;
}