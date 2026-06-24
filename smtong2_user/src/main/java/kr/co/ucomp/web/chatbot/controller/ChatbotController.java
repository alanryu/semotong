package kr.co.ucomp.web.chatbot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.chatbot.dto.SearchChatbotGroupDTO;
import kr.co.ucomp.web.chatbot.entity.ChatbotGroupEntity;
import kr.co.ucomp.web.chatbot.service.ChatbotGroupService;
import kr.co.ucomp.web.chatbot.service.ChatbotService;
import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;
import kr.co.ucomp.web.csm.banner.service.BannerService;
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
@RequestMapping("svc/chatbot")
@AllArgsConstructor
public class ChatbotController {
	
    private final ChatbotGroupService chatbotGroupService;
    private final ChatbotService chatbotService;
    private final PlanService planService;
    
    @Autowired
	private BannerService bannerService;
    
    /**
     * 챗봇 메인
     * @param request
     * @param model
     * @return
     */
    @GetMapping( value = "/index")
    public String ChatbotIndex( HttpServletRequest request, Model model) {
    	
    	return "/pages/chatbot/index";
    }
    
    /**
     * 알뜰 요금제 사용 경험자
     * @param request
     * @param model
     * @return
     */
    @GetMapping( value = "/experienced" )
    public String Experienced ( HttpServletRequest request, Model model ) {
    	
    	SearchChatbotGroupDTO param = new SearchChatbotGroupDTO();
    	List<ChatbotGroupEntity> list = chatbotGroupService.list(param);
    	
    	if ( list.size() > 0 ) {
    		
    		for ( ChatbotGroupEntity temp : list ) {
    			
    			/* 검색 파라미터 세팅 */
    			SearchPlanDto dto = new SearchPlanDto();
    		
    			dto.setSearchChatbotGroupId(temp.getId());
    			
    			List<PlanEntity> subList = planService.getChatbotPlanList(dto);
    			temp.setList(subList);
    			temp.setPlanCnt(subList.size());
    		}
    	}
    	
    	model.addAttribute("list", list);
    	
    	return "/pages/chatbot/experienced";
    }
    
    /**
     * 알뜰 요금제 사용 미경험자
     * @param request
     * @param model
     * @return
     */
    @GetMapping( value = "/unexperienced/{step}" )
    public String Unexperienced ( HttpServletRequest request, Model model, @PathVariable("step") String step ) {
    	
    	String returnUrl = "/pages/chatbot/unexperienced";
    	
    	switch (step) {
			case "step1" : {
				returnUrl = returnUrl + "S1";
				break;
			}
			case "step2" : {
				returnUrl = returnUrl + "S2";
				break;
			}
			case "step3" : {
				returnUrl = returnUrl + "S3";
				break;
			}
			case "step4" : {
				returnUrl = returnUrl + "S4";
				break;
			}
			case "step5" : {
				returnUrl = returnUrl + "S5";
				break;
			}
			case "step6" : {
				returnUrl = returnUrl + "S6";
				break;
			}
			default :
				break;
		}
    	
    	return returnUrl;
    }
    
    /**
     * 알뜰 요금제 사용 미경험자 선택결과
     * @param request
     * @return
     */
    @PostMapping( value = "/unexperiencedResult" )
    @ResponseBody
    public ResponseEntity<CustomApiResponse<List<PlanEntity>>> unexperiencedResult( HttpServletRequest request, @RequestBody Map<String, Object> param ) throws IOException {
    	
    	long cnt = 0L;
    	List<PlanEntity> list = new ArrayList<PlanEntity>();
    	
    	try {
			
    		/*** 검색 파라미터 세팅 ***/
    		SearchPlanDto searchParam = new SearchPlanDto();
    		
    		/* 알뜰폰 or 통신3사 or both */
    		String hostType = MapUtils.getString(param, "hostType", "all");
    		if ( !StringUtils.equals("all", hostType) ) {
    			searchParam.setSearchSection(hostType);
    		}
    		
    		/* 사용중인 통신사 선택 */
    		String mno = MapUtils.getString(param, "mno", "");
    		if ( !StringUtils.isEmpty(mno) ) {
    			searchParam.setSearchMno(mno);
    		}
    		
    		/* 데이터 선택 */
    		String supData = MapUtils.getString(param, "supData", "");
    		if ( !StringUtils.isEmpty(supData) ) {
    			
    			/* 100GB 이상 */
    			if ( StringUtils.equals("1", supData) ) {
    				searchParam.setSearchDataTermFrom( String.valueOf(100 * 1024) );
    			/* 50GB ~ 100GB */
    			} else if ( StringUtils.equals("2", supData) ) {
    				searchParam.setSearchDataTermFrom( String.valueOf(50 * 1024) );
    				searchParam.setSearchDataTermTo( String.valueOf((100 * 1024) - 1) );
    			/* 10GB ~ 50GB */
    			} else if ( StringUtils.equals("3", supData) ) {
    				searchParam.setSearchDataTermFrom( String.valueOf(10 * 1024) );
    				searchParam.setSearchDataTermTo( String.valueOf((50 * 1024) - 1) );
    			/* 5GB ~ 10GB */
    			} else if ( StringUtils.equals("4", supData) ) {
    				searchParam.setSearchDataTermFrom( String.valueOf(5 * 1024) );
    				searchParam.setSearchDataTermTo( String.valueOf((10 * 1024) - 1) );
    			/* ~ 5GB */
    			} else if ( StringUtils.equals("5", supData) ) {
    				searchParam.setSearchDataTermTo( String.valueOf((5 * 1024) - 1) );
    			}
    		}
    		
    		/* 통화량 선택 */
    		String supCall = MapUtils.getString(param, "supCall", "");
    		if ( !StringUtils.isEmpty(supCall) ) {
    			
    			/* 400분 */
    			if ( StringUtils.equals("400", supCall) ) {
    				searchParam.setSearchCallTermFrom(String.valueOf(400));
    			} else if ( StringUtils.equals("200", supCall) ) {
    				searchParam.setSearchCallTermTo(String.valueOf(199));
    			} else if ( StringUtils.equals("100", supCall) ) {
    				searchParam.setSearchCallTermTo(String.valueOf(99));
    			} else if ( StringUtils.equals("30", supCall) ) {
    				searchParam.setSearchCallTermTo(String.valueOf(29));
    			}
    		}
    		
    		/* 결합 여부 선택 */
    		String combination = MapUtils.getString(param, "combination", "0");
    		if ( StringUtils.equals("1", combination) ) {
    			searchParam.setSearchCombination(1);
    		}
    		
    		searchParam.setSearchDispYn(1);
    		searchParam.setSearchOrderType("SPAlly");
    		
    		cnt = planService.getListCount(searchParam);
    		list = planService.getList(searchParam);
    		
    		return CustomApiResponse.success(ResponseCode.OK, cnt, list);
    		
		} catch (Exception e) {
			
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    }
    
    /**
     * 챗봇배너 불러오기
     * @param request
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	@ResponseBody
    @GetMapping( value = "/banner" )
    public ResponseEntity<CustomApiResponse<List<BannerEntity>>> banner( HttpServletRequest request ) throws IOException {
    	
    	long cnt = 0L;
    	List<BannerEntity> list = new ArrayList<BannerEntity>();
    	
    	try {
    		
    		ObjectMapper mapper = new ObjectMapper();
    		
    		/* 챗봇배너 들고오기 (searchType : 02) */
        	BannerSearchDto bannerSearchDto = new BannerSearchDto();
        	bannerSearchDto.setSearchUseYn("Y");
        	bannerSearchDto.setSearchBannerType("06");
        	list = bannerService.list(bannerSearchDto);
			
        	if ( list.size() > 0 ) {
        		
        		for ( BannerEntity temp : list ) {
        			if(StringUtils.isNoneBlank(temp.getImagePc())) {
        				Map<String, Object> map = mapper.readValue(temp.getImagePc(), Map.class);
            			temp.setImagePc(map.get("fileUrl").toString());	
        			}
        			if(StringUtils.isNoneBlank(temp.getImageMo())) {    			
        				Map<String, Object> map  = mapper.readValue(temp.getImageMo(), Map.class);
    	    			temp.setImageMo(map.get("fileUrl").toString());
        			}
        		}
        	}
        	
    		return CustomApiResponse.success(ResponseCode.OK, cnt, list);
    		
		} catch (Exception e) {
			
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    }
}
