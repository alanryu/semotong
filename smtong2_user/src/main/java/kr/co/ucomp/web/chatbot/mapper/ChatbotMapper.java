package kr.co.ucomp.web.chatbot.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.ucomp.web.chatbot.dto.ChatbotMainPlanDTO;
import kr.co.ucomp.web.chatbot.entity.ChatbotGroupPlanEntity;

import java.util.List;

@Mapper
public interface ChatbotMapper {
 
    List<ChatbotGroupPlanEntity> getChatbotPlanList(ChatbotMainPlanDTO param);			//<select id="getChatbotPlanList" resultMap="kr.co.ucomp.domain.svc.chatbot.dto.ChatbotMainPlanDTO">

    ChatbotGroupPlanEntity getChatbotPlanListById(Integer id);		//<select id="getChatbotPlanListById" parameterType="Long" resultMap="kr.co.ucomp.domain.svc.chatbot.dto.ChatbotMainPlanDTO">

}
