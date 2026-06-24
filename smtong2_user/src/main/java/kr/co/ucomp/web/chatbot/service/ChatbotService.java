package kr.co.ucomp.web.chatbot.service;


import java.util.List;

import kr.co.ucomp.web.chatbot.dto.ChatbotMainPlanDTO;
import kr.co.ucomp.web.chatbot.entity.ChatbotGroupPlanEntity;

public interface ChatbotService {

    List<ChatbotGroupPlanEntity> getChatbotPlanList(ChatbotMainPlanDTO param);

    ChatbotGroupPlanEntity getChatbotPlanListById(Integer id);

}
