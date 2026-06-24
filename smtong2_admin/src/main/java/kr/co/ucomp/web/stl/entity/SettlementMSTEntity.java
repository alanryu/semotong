package kr.co.ucomp.web.stl.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SettlementMSTEntity {

    private int companyId;
    private String companyNm;
    private String yyyymm;
    private String stlType;
    private int createId;
    private String createNm;
    private int stlCount;

  }