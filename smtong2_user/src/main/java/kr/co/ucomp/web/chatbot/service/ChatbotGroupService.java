package kr.co.ucomp.web.chatbot.service;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.chatbot.dto.SearchChatbotGroupDTO;
import kr.co.ucomp.web.chatbot.entity.ChatbotGroupEntity;

public interface ChatbotGroupService {
    List<ChatbotGroupEntity> list(SearchChatbotGroupDTO param);
    
    long listCount(SearchChatbotGroupDTO param);

    ChatbotGroupEntity getDetail(@Param("id") int id);


}
