package kr.co.ucomp.web.svc.chatbot.service;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.chatbot.dto.searchChatbotGroupPlanDTO;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupPlanEntity;

public interface ChatbotService {
    long insertChatbotPlan(ChatbotGroupPlanEntity param);

    List<ChatbotGroupPlanEntity> getChatbotPlanList(searchChatbotGroupPlanDTO param);

    ChatbotGroupPlanEntity getChatbotPlanListById(Integer id);

    long updateChatbotPlan(ChatbotGroupPlanEntity param);

    long updateChatbotPlanOrder(Integer id, int orderSeq);

    int deleteChatbotPlan(Integer id);
    
    int deleteChatbotPlanAllByGroupId(@Param("groupId") Integer groupId);	
}
