package kr.co.ucomp.web.chatbot.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatbotGroupPlanEntity {

    private int id;                 	// 고유 아이디    
    private Integer chatbot_group_id; // 챗봇그룹 관리 id
    private Integer plan;			// 요금제 관리 id
    private String planMemo;		// 메모
    private Boolean recomYn;		// 세모통 추천 여부
    private Boolean lowPriceYn;   // 최저가 보장 여부
    private Boolean maxBenefitYn; // 최다혜택 여부
    private Integer orderSeq;    // 정렬 순서
    private Integer createId;
    private String createNm;
    private LocalDateTime modifiedDate;
    private Integer modifiedId;
    private String modifiedNm;

}
