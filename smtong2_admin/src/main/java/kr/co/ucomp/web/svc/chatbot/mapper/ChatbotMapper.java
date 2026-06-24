package kr.co.ucomp.web.svc.chatbot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.chatbot.dto.searchChatbotGroupPlanDTO;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupPlanEntity;

import java.util.List;

@Mapper
public interface ChatbotMapper {
    long insertChatbotPlan(ChatbotGroupPlanEntity param);		

    List<ChatbotGroupPlanEntity> getChatbotPlanList(searchChatbotGroupPlanDTO param);			

    long updateChatbotPlan(ChatbotGroupPlanEntity param);		

    ChatbotGroupPlanEntity getChatbotPlanListById(Integer id);		

    long updateChatbotPlanOrder(Integer id, int orderSeq);		

    int deleteChatbotPlan(Integer id);			
    
    int deleteChatbotPlanAllByGroupId(@Param("groupId") Integer groupId);		
}
