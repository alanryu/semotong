package kr.co.ucomp.web.svc.chatbot.dto;



import java.util.List;

import kr.co.ucomp.common.global.base.BaseTimeDTO;
import lombok.Getter;
import lombok.Setter;


/**
 * tb_svc_chatbot_main_plan DTO
 * 
 * @author 이정민
 * @since 2024.12.27
 * @version v2.0
 */
@Setter
@Getter
public class searchChatbotGroupPlanDTO extends BaseTimeDTO{    
    private Integer chatbotGroupId;

}

