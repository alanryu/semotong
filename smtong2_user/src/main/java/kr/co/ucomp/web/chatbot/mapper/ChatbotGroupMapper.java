package kr.co.ucomp.web.chatbot.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.chatbot.dto.SearchChatbotGroupDTO;
import kr.co.ucomp.web.chatbot.entity.ChatbotGroupEntity;

import java.util.List;

@Mapper
public interface ChatbotGroupMapper {

    List<ChatbotGroupEntity> list(SearchChatbotGroupDTO param);
    
    long listCount(SearchChatbotGroupDTO param);

    ChatbotGroupEntity getDetail(@Param("id") int id);


}
