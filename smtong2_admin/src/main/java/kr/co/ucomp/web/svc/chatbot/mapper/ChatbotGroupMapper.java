package kr.co.ucomp.web.svc.chatbot.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.svc.chatbot.dto.SearchChatbotGroupDTO;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupEntity;

import java.util.List;

@Mapper
public interface ChatbotGroupMapper {

    List<ChatbotGroupEntity> list(SearchChatbotGroupDTO param);
    
    long listCount(SearchChatbotGroupDTO param);

    ChatbotGroupEntity getDetail(@Param("id") long id);

    long create(ChatbotGroupEntity param);

    long update(ChatbotGroupEntity param);

    long delete(@Param("id") long id);			

}
