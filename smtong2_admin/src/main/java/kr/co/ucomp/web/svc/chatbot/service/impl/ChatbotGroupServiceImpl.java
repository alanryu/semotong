package kr.co.ucomp.web.svc.chatbot.service.impl;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.svc.chatbot.dto.SearchChatbotGroupDTO;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupEntity;
import kr.co.ucomp.web.svc.chatbot.mapper.ChatbotGroupMapper;
import kr.co.ucomp.web.svc.chatbot.service.ChatbotGroupService;

import java.util.List;

@AllArgsConstructor
@Service
@Component
public class ChatbotGroupServiceImpl implements ChatbotGroupService {

	   @Autowired
	   ChatbotGroupMapper mapper;

	    @Override
	    public List<ChatbotGroupEntity> list(SearchChatbotGroupDTO param) {
	        return mapper.list(param);
	    }

	    @Override
	    public long listCount(SearchChatbotGroupDTO param) {
	        return mapper.listCount(param);
	    }

	    
	    @Override
	    public ChatbotGroupEntity getDetail(long id) {
	        return mapper.getDetail(id);
	    }

	    @Override
	    public long create(ChatbotGroupEntity param) {
	        return mapper.create(param);
	    }

	    @Override
	    public long update(ChatbotGroupEntity param) {
	        return mapper.update(param);
	    }

	    @Override
	    public long delete(long id) {
	        return mapper.delete(id);
	    }

}
