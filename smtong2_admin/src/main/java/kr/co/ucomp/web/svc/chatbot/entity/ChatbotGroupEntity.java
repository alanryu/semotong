package kr.co.ucomp.web.svc.chatbot.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatbotGroupEntity {

    private int id;                 	// 고유 아이디
    private String groupName;       	// 그룹명
    private String mvno;            	// 통신사
    private int supData;            	// 제공 데이터
    private int subQos;            	 // 제공 QOS
    private int orderNo;            	 // 제공 QOS
    private String groupMemo;       	// 그룹메모
    private LocalDateTime createDate;  // 생성 날짜
    private int createId;          	 // 생성자 사용자 로그인 id
    private LocalDateTime modifiedDate; // 수정 날짜
    private int modifiedId;         // 수정자 사용자 로그인 id
    private int regCnt;				// 요금제 등록 개수
    private String title;

}
