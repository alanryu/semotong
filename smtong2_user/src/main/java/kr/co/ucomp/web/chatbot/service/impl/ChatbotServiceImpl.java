package kr.co.ucomp.web.chatbot.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.chatbot.dto.ChatbotMainPlanDTO;
import kr.co.ucomp.web.chatbot.entity.ChatbotGroupPlanEntity;
import kr.co.ucomp.web.chatbot.mapper.ChatbotMapper;
import kr.co.ucomp.web.chatbot.service.ChatbotService;

import java.util.List;

@AllArgsConstructor
@Service
@Component
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatbotMapper chatbotMapper;

    @Override
    public List<ChatbotGroupPlanEntity> getChatbotPlanList(ChatbotMainPlanDTO param) {
        return chatbotMapper.getChatbotPlanList(param);
    }

    @Override
    public ChatbotGroupPlanEntity getChatbotPlanListById(Integer id) {
        return chatbotMapper.getChatbotPlanListById(id);
    }

 }
