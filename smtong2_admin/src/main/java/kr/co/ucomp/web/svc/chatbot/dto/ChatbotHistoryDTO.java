package kr.co.ucomp.web.svc.chatbot.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
*
* tb_svc_chatbot_history DTO
* 
* @author 이정민
* @since 2024.12.17
* @version v1.0
*/

@Getter
@Setter
public class ChatbotHistoryDTO{
	private Long id;
	private Boolean mvnoUseYn;
	private String preMno;
	private String prevPayment;
	private String selData;
	private int selCompany;
	private int selPlanId;
	private Long user;
    private LocalDate createDate;
}
