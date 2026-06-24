package kr.co.ucomp.web.svc.chatbot.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.chatbot.entity.ChatbotHistoryReqEntity;
import kr.co.ucomp.web.svc.chatbot.mapper.ChatbotHistoryMapper;
import kr.co.ucomp.web.svc.chatbot.service.ChatbotHistoryService;
import kr.co.ucomp.web.svc.chatbot.service.ChatbotService;

@AllArgsConstructor
@Component
@Service
public class ChatbotHistoryServiceImpl implements ChatbotHistoryService {
    private ChatbotHistoryMapper chatbotHistoryMapper;

    @Override
    public long insertChatbotHistory(ChatbotHistoryReqEntity param) {
        return chatbotHistoryMapper.insertChatbotHistory(param);
    }


}
