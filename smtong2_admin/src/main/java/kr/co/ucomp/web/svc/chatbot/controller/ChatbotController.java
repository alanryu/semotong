package kr.co.ucomp.web.svc.chatbot.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;


import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.service.PlanService;
import kr.co.ucomp.web.svc.chatbot.dto.searchChatbotGroupPlanDTO;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupPlanEntity;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotHistoryReqEntity;
import kr.co.ucomp.web.svc.chatbot.service.ChatbotHistoryService;
import kr.co.ucomp.web.svc.chatbot.service.ChatbotService;
import lombok.AllArgsConstructor;


/**
 *
 * @author 이정민
 * @since 2024.12.27
 * @version v1.0
 */
@Controller
@RequestMapping("svc/chatbot")
@AllArgsConstructor
public class ChatbotController {
    private final ChatbotService chatbotService;
    private final ChatbotHistoryService chatbotHistoryService;
    @Autowired PlanService planService;
    

    /*
      ===========================
      ===== Chatbot Mapping =====
      ===========================
      - Endpoint
      svc/chatbot/{endpoint}

     */
    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> insertChatbotPlan(
            @RequestBody ChatbotGroupPlanEntity param
            ){
        long id =chatbotService.insertChatbotPlan(param);
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        return CustomApiResponse.success(ResponseCode.CREATED, 1, map);
    }

    @ResponseBody
    @PostMapping("/planList")
    public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getChatbotPlanList(@RequestBody SearchPlanDto param){
        List<PlanEntity> response = planService.getChatbotPlanList(param);
        return CustomApiResponse.success(ResponseCode.OK, response.size() ,response);
    }


    @ResponseBody
    @PostMapping("/update")
    public ResponseEntity<CustomApiResponse<ChatbotGroupPlanEntity>> updateChatbotPlan(
            @RequestBody ChatbotGroupPlanEntity param
    ){
        chatbotService.updateChatbotPlan(param);
        ChatbotGroupPlanEntity response = chatbotService.getChatbotPlanListById(param.getId());

        return CustomApiResponse.success(ResponseCode.OK, 1, response);
    }

    @ResponseBody
    @PostMapping("/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> deleteChatbotPlan(
            @PathVariable Integer id
    ) {
        if (chatbotService.deleteChatbotPlan(id) == 1){
            return CustomApiResponse.success(ResponseCode.ACCEPTED, 1, "DELETE COMP");
        }
        else {
            return CustomApiResponse.error(ResponseCode.NOT_FOUND, "삭제못했어요");
        }
    }

    
    /*
      ===================================
      ===== Chatbot History Mapping =====
      ===================================
      - Endpoint
      svc/chatbot/history/{endpoint}

     */

    /**
     * @param  reqEntity Chatbot History 를 import 하기 위한 ReqEntity
     * @return <CustomApiResponse<ChatbotHistoryReqEntity>> response
     * @throws IllegalArgumentException
     * @throws IOException
     */
    @PostMapping("/history/create")
    public ResponseEntity<CustomApiResponse<ChatbotHistoryReqEntity>> insertChatbotHistory(
            @RequestBody ChatbotHistoryReqEntity reqEntity
            ) throws IllegalArgumentException,  IOException {
        try {
            chatbotHistoryService.insertChatbotHistory(reqEntity);
            return CustomApiResponse.success(ResponseCode.CREATED, reqEntity);
        } catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
