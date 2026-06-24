package kr.co.ucomp.web.chatbot.service;

import kr.co.ucomp.web.chatbot.entity.ChatbotHistoryReqEntity;

public interface ChatbotHistoryService {
    long insertChatbotHistory(ChatbotHistoryReqEntity param);
}
