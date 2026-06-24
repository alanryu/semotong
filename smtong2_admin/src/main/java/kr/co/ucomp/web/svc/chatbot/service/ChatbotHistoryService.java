package kr.co.ucomp.web.svc.chatbot.service;

import kr.co.ucomp.web.svc.chatbot.entity.ChatbotHistoryReqEntity;

public interface ChatbotHistoryService {
    long insertChatbotHistory(ChatbotHistoryReqEntity param);
}
