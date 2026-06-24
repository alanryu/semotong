package kr.co.ucomp.web.svc.chatbot.service.impl;

import lombok.AllArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.chatbot.dto.searchChatbotGroupPlanDTO;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupPlanEntity;
import kr.co.ucomp.web.svc.chatbot.mapper.ChatbotMapper;
import kr.co.ucomp.web.svc.chatbot.service.ChatbotService;

import java.util.List;

@AllArgsConstructor
@Service
@Component
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatbotMapper chatbotMapper;

    @Override
    public long insertChatbotPlan(ChatbotGroupPlanEntity param) {
        return chatbotMapper.insertChatbotPlan(param);
    }

    @Override
    public List<ChatbotGroupPlanEntity> getChatbotPlanList(searchChatbotGroupPlanDTO param) {
        return chatbotMapper.getChatbotPlanList(param);
    }

    @Override
    public ChatbotGroupPlanEntity getChatbotPlanListById(Integer id) {
        return chatbotMapper.getChatbotPlanListById(id);
    }

    @Override
    public long updateChatbotPlan(ChatbotGroupPlanEntity param) {
        return chatbotMapper.updateChatbotPlan(param);
    }

    @Override
    public long updateChatbotPlanOrder(Integer id, int orderSeq) {
        return chatbotMapper.updateChatbotPlanOrder(id, orderSeq);
    }

    @Override
    public int deleteChatbotPlan(Integer id) {
        return chatbotMapper.deleteChatbotPlan(id);
    }
    
    
    @Override
    public int deleteChatbotPlanAllByGroupId(Integer id) {
        return chatbotMapper.deleteChatbotPlanAllByGroupId(id);
    }
    
}
