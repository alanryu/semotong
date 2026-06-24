package kr.co.ucomp.web.pmb.dto;



import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanUpdateDto implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1704919002453611821L;
	private Long id;
    private String uuid;
    private String planCode;
    private Boolean dispYn;
    private Boolean saleStatus;
    private Boolean useYn;
    private Boolean newYn;
    private String urlSmt;
    private String planNameSmt;
    private Boolean imageBadgeSpecial;
    private Boolean imageBadgeItComb;
    private Boolean imageBadgeAddData;
    private String imageBadgeAddDataNm;
    private Boolean imageBadgeSimpleOpen;
    private Boolean imageBadgeLowest;
    private Boolean combinationEnable;
    private String suppContent;
    private String noSuppContent;
    private String noticeContent;
    private String eventBannerImagePc;
    private String eventBannerImageMo;    
    private String eventBannerImageUrl;
    private String eventBannerImageTarget;
    private Integer modifiedId;
    
    private MultipartFile filePc;
    private MultipartFile fileMo;
    
    
    private String updateErrYn;
    
    private String pointPlanYn;
    private String planNameSmtSub;
    private String secondPrice;
    
    private String newNumReqUrl;
    private String numMoveReqUrl;

    private String planOrderSp;
    private String searchOrderListSp;
    private Integer recomOrder1; // 일반 알뜰폰 요금제 정렬 순서
    private Integer recomOrder2; // 추천 요금제 정렬 순서
    private Integer recomOrder3; // 가성비 요금제 정렬 순서
    private Integer recomOrder4; // 인기 요금제 정렬 순서

	private String pcUrl;
	private String moUrl;
	private String supCallBugaVal;
	
	
	private String planTag1;   //요금제 태그 1
	private String planTag2;   //요금제 태그 2
	private String planTag3;   //요금제 태그 3
	private String planTag4;   //요금제 태그 4
	private String planTag5;   //요금제 태그 5
}
