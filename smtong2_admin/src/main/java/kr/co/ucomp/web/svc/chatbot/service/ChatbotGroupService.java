package kr.co.ucomp.web.svc.chatbot.service;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.chatbot.dto.SearchChatbotGroupDTO;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupEntity;

public interface ChatbotGroupService {
    List<ChatbotGroupEntity> list(SearchChatbotGroupDTO param);
    
    long listCount(SearchChatbotGroupDTO param);

    ChatbotGroupEntity getDetail(@Param("id") long id);

    long create(ChatbotGroupEntity param);

    long update(ChatbotGroupEntity param);

    long delete(@Param("id") long id);		
}
