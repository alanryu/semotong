package kr.co.ucomp.web.pmb.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class InternetReqMngEntity {

    private Integer id; // 고유 아이디
    private Integer reqUser; // 신청자 user id
    private String inputName; // 신청자 이름
    private String inputNumber; // 신청자 전화번호
    private Integer inputPlanId; // 신청상품 id
    private String reqState; // 신청상태
    private String counselContent; // 상담내용
    private String procContent; // 처리내용
    private LocalDateTime createDate; // 생성 날짜
    private Integer createId; // 생성자 사용자 로그인 id
    private LocalDateTime modifiedDate; // 수정 날짜
    private Integer modifiedId; // 수정자 사용자 로그인 id
    private String siteSp; // 인터넷 유입 사이트 구분(01: 세모통, 02:오늘의통신)
    private String incomSp; // 인터넷 신청 유입 구분(01: 홈페이지, 02:랜딩페이지)
    private String incomUrl; // 인터넷 유입 url
    private Integer rdpMngId; // 생성자 사용자 로그인 id
    private String outboundCenter; // 콜센터 구분, INTERNET(인터넷 신청(세모통)) / INTERNET2(인터넷 신청(세모통)2)

    private String internetPlanMno;
    private String internetPlanNm;
    private String internetSpeed;
    private String channelCount;
    private String normalPrice;
    private String combinationPrice;
    private String normalFreeblePrice;
    private String combinationFreeblePrice;

    private String internetPlanMnoNew;
    private String tvProdName;
    private String internetPlanNmNew;
    private String internetSpeedNew;
    private String channelCountNew;
    private String combinationPriceNew;
    private String combinationFreeblePriceNew;
    private String hopeConsTime;

    private String relComp;
    private String isNew;

}
