package kr.co.ucomp.web.svc.banner.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class PopupEntity {

  private Long id; // 고유 ID
  private String status; // 현재상태(노출, 대기, 종료)
  private String name; // 항목명
  private String content; // 팝업 내용
  private String imagePcUrl; // 팝업 이미지 경로 PC
  private String imageMobileUrl; // 팝업 이미지 경로 Mobile

  private LocalDateTime startDate; // 노출 시작일시
  private LocalDateTime endDate; // 노출 종료일시

  private String startDateStr; // type="date" 으로 LocalDateTime 에러남, String 으로 받아서 처리
  private String endDateStr; // type="date" 으로 LocalDateTime 에러남, String 으로 받아서 처리

  private String linkUrl; // 클릭 시 이동할 URL
  private String displayUrl; // 팝업이 노출되는 URL
  private String useYn; // 사용여부
  private String target; // 링크 타겟(_blank, _self)
  private Integer sort; // 순서
  private String memo; // 관리자 메모
  private LocalDateTime createDate; // 생성날짜
  private Integer createId; // 생성자 사용자 로그인 id
  private LocalDateTime modifiedDate; // 수정날짜
  private Integer modifiedId; // 수정자 사용자 로그인 id
  private String comGroup;
  private String userName; // 등록자

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
    if (startDateStr != null && !startDateStr.isEmpty()) {
      this.startDate = LocalDate.parse(startDateStr).atStartOfDay();
    }
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
    if (endDateStr != null && !endDateStr.isEmpty()) {
      this.endDate = LocalDate.parse(endDateStr).atTime(23, 59, 59);
    }
  }

  public String getStatusDisplay() {
    switch (status) {
      case "wait":
        return "대기";
      case "active":
        return "노출";
      case "close":
        return "종료";
      default:
        return "전체";
    }
  }
}