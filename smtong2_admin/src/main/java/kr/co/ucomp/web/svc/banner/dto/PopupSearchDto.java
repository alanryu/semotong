package kr.co.ucomp.web.svc.banner.dto;

import kr.co.ucomp.common.global.base.BaseSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopupSearchDto extends BaseSearchDto {

  // 검색 키워드
  private String keyword;

  // 상태별 검색 (all, wait, active, close)
  private String status;

  // 게시기간 검색
  private String publishStartDt;
  private String publishEndDt;

  // 사용 여부 필터
  private String useYn;

  // 검색 조건 초기화 메소드
  public void resetSearchConditions() {
    this.keyword = null;
    this.status = null;
    this.publishStartDt = null;
    this.publishEndDt = null;
    this.useYn = null;
  }

  // searchStartDt에 대한 8자리 날짜 검증을 추가한 setter
  public void setPublishStartDt(String publishStartDt) {
    if (publishStartDt != null && publishStartDt.length() == 10) {
      this.publishStartDt = publishStartDt + " 00:00:00";
    } else {
      this.publishStartDt = publishStartDt;
    }
  }

  public void setPublishEndDt(String publishEndDt) {
    if (publishEndDt != null && publishEndDt.length() == 10) {
      this.publishEndDt = publishEndDt + " 23:59:59";
    } else {
      this.publishEndDt = publishEndDt;
    }
  }
}
