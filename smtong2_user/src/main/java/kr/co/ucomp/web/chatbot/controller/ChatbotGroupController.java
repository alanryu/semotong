package kr.co.ucomp.web.chatbot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.chatbot.dto.SearchChatbotGroupDTO;
import kr.co.ucomp.web.chatbot.entity.ChatbotGroupEntity;
import kr.co.ucomp.web.chatbot.service.ChatbotGroupService;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.AllArgsConstructor;


/**
 *
 * @author 이정민
 * @since 2024.12.27
 * @version v1.0
 */
@Controller
@RequestMapping("svc/chatbotGroup")
@AllArgsConstructor
public class ChatbotGroupController {
    private final ChatbotGroupService service;
    private @Autowired PlanService planService;

    @GetMapping("/list")
    public ResponseEntity<CustomApiResponse<List<ChatbotGroupEntity>>> getList(HttpServletRequest request,
            @RequestBody SearchChatbotGroupDTO param){
        List<ChatbotGroupEntity> response = service.list(param);
        return CustomApiResponse.success(ResponseCode.OK, response.size() ,response);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<ChatbotGroupEntity>> getDetail(
            @PathVariable int id
    ){
    	ChatbotGroupEntity response = service.getDetail(id);
        return CustomApiResponse.success(ResponseCode.OK, 1, response);
    }

    
    
    
    @ResponseBody
    @PostMapping("/planList")
    public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getChatbotGroupPlanList(@RequestBody SearchPlanDto param){
        List<PlanEntity> response = planService.getChatbotPlanList(param);
        return CustomApiResponse.success(ResponseCode.OK, response.size() ,response);
    }
    


}
