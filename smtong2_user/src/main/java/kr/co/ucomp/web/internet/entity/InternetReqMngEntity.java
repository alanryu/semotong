package kr.co.ucomp.web.internet.entity;

import kr.co.ucomp.common.global.base.BaseTimeDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InternetReqMngEntity extends BaseTimeDTO {

    private long id;
    private long reqUser;

    private String inputName;
    private String inputNumber;
    private long inputPlanId;
    private Integer inputMno;

    private String reqState;
    private String counselContent;
    private String procContent;
    private String siteSp; // 인터넷 유입 사이트 구분(01: 세모통, 02:오늘의통신)
    private String createNm;
    private String modifiedNm;

    // 인터넷 외부랜딩 페이지 개발 관련 추가
    private String incomSp; // 인터넷 신청 유입 구분(01: 홈페이지, 02:랜딩페이지)
    private String incomUrl; // 인터넷 유입 url
    private Integer rdpMngId; // 생성자 사용자 로그인 id
    private String outboundCenter; // 콘센터 구분

    // 인터넷 가입 신규
    private String internetMnoId; // 상품명
    private String prodName; // 상품명
    private String combinationName; // 결합명
    private String prodDescript; // 한줄 설명
    private String tvProdName; // tv 상품명
    private Integer channelCount; // 채널수
    private String internetSpeed; // 인터넷 속도
    private Integer combinationPrice; // 가격
    private Integer combinationFreeblePrice; // 사은품 현금
    private String hopeConsTime; // 희망 상담 시간

}
