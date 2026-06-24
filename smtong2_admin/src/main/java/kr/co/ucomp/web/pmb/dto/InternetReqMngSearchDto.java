package kr.co.ucomp.web.pmb.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternetReqMngSearchDto extends BaseSearchDto {

    private String searchMno;
    private String searchState;
    private String searchSiteSp; // 인터넷 유입 사이트 구분(01: 세모통, 02:오늘의통신)
    private String searchIncomSp; // 신청 유입 구분(01:홈페이지, 02: 랜딩페이지)
    private String searchRdpMngId; // 랜딩 페이지 관리번호
    private String searchisNewYn; // 신규버전 여부
    private String outboundCenter; // 콜센터 구분
    private String keyword;

}
