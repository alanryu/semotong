package kr.co.ucomp.web.csm.banner.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PopupEntity {
  private Long id;
  private String name;
  private String imagePcUrl;
  private String imageMobileUrl;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String linkUrl;
  private int sort;
  private String target;
}
