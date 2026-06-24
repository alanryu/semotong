package kr.co.ucomp.web.chatbot.mapper;


import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.chatbot.entity.ChatbotHistoryReqEntity;

import java.util.List;

@Mapper
public interface ChatbotHistoryMapper {

    long insertChatbotHistory(ChatbotHistoryReqEntity param);

}
