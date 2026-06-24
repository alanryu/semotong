package kr.co.ucomp.web.svc.chatbot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.service.PlanService;
import kr.co.ucomp.web.svc.chatbot.dto.SearchChatbotGroupDTO;
import kr.co.ucomp.web.svc.chatbot.dto.chatbotGroupPlanDTO;
import kr.co.ucomp.web.svc.chatbot.dto.searchChatbotGroupPlanDTO;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupEntity;
import kr.co.ucomp.web.svc.chatbot.entity.ChatbotGroupPlanEntity;
import kr.co.ucomp.web.svc.chatbot.service.ChatbotGroupService;
import kr.co.ucomp.web.svc.chatbot.service.ChatbotService;
import lombok.extern.slf4j.Slf4j;


/**
 *
 * @author 이정민
 * @since 2024.12.27
 * @version v1.0
 */
@Controller
@RequestMapping("svc/chatbotGroup")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class ChatbotGroupController {
	
	@Autowired private ChatbotGroupService chatbotGroupService;
	@Autowired private PlanService planService;
	@Autowired private ChatbotService chatbotService;

	/**
	 * 챗봇요금제 관리 리스트 진입
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping( value = "/list" )
	public String list ( HttpServletRequest request, Model model ) {
		
		log.info("챗봇요금제 관리 리스트 진입");
		
		return "pages/svc/chatbot/list";
	}
	
	
	/**
	 * 챗봇요금제 관리 리스트 진입
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping( value = "/edit" )
	public String edit ( HttpServletRequest request, Model model,@RequestParam(value="searchId", defaultValue = "") String searchId ) {
		
		log.info("챗봇요금제 관리 요금제 등록 화면 진입");
		List<PlanEntity>  planLis = new ArrayList<PlanEntity>();
		ChatbotGroupEntity record = new ChatbotGroupEntity();
		if(StringUtils.isNotBlank(searchId)) {
			SearchPlanDto param = new SearchPlanDto();
			param.setSearchChatbotGroupId(Integer.parseInt(searchId));
			planLis = planService.getChatbotPlanList(param);
			record = chatbotGroupService.getDetail(Integer.parseInt(searchId));			
		}
		model.addAttribute("record", record);
		model.addAttribute("planLis", planLis);
		model.addAttribute("searchId", searchId);
		
		return "pages/svc/chatbot/edit";
	}
	
	
	
	/**
	 * 검색 프로세스
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@PostMapping( value = "/listProc" )
	public ResponseEntity<CustomApiResponse<List<ChatbotGroupEntity>>> listProc ( HttpServletRequest request, @RequestBody SearchChatbotGroupDTO param ) throws IOException { 
		
		try{
			
			long resultcnt = chatbotGroupService.listCount(param);
			List<ChatbotGroupEntity> resultList = new ArrayList<ChatbotGroupEntity>();
			if ( resultcnt > 0 ) {
				resultList = chatbotGroupService.list(param);
			}
            
            return CustomApiResponse.success(ResponseCode.OK, resultcnt, resultList);

        } catch (Exception e) {
        		
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }
	}
	
	/**
	 * 챗봇 요금제 그룹 상세
	 * @param id
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/detail" )
	public ResponseEntity<CustomApiResponse<ChatbotGroupEntity>> detail ( HttpServletResponse response, @RequestParam("id") String id ) {
		
		try {
			ChatbotGroupEntity entity = chatbotGroupService.getDetail(Integer.parseInt(id));
    		return CustomApiResponse.success(ResponseCode.OK, entity);
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "ChatbotGroupEntity getDetail : " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ChatbotGroupEntity getDetail : " + e.getMessage());
        }
	}
	

	

	
	/**
	 * 순서 정렬 (요금제 소개 배너)
	 * @param params
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/listorder" )
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> listorder ( @RequestParam("orderIds") String orderIds ) {
		
		try {
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			if ( !StringUtils.isEmpty(orderIds) ) {
				int sort = 1;
				ChatbotGroupEntity entity = new ChatbotGroupEntity();
				for ( String str : orderIds.split(",") ) {
					entity = chatbotGroupService.getDetail(Integer.parseInt(str));
					entity.setOrderNo(sort);
					chatbotGroupService.update(entity);
					sort++;
				}
			}
			
			return CustomApiResponse.success(ResponseCode.OK, result);
		} catch (Exception e) {
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
		}
	}
	
	/**
	 * 삭제 프로세스
	 * @param response
	 * @param delId
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@DeleteMapping( value = "/delete" )
	public ResponseEntity<CustomApiResponse<String>> delete( HttpServletResponse response, @RequestParam("delId") String delId ) throws IOException {
    	
    	try {
    		// 정보 삭제
    		chatbotGroupService.delete(Integer.parseInt(delId));
    		//등록된 요금제 삭제
    		chatbotService.deleteChatbotPlanAllByGroupId(Integer.parseInt(delId));
    		
    		 return CustomApiResponse.success(ResponseCode.OK, "del ok");
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "delete admin user: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "delete admin user: " + e.getMessage());
        }
    }
	
	
	
	/**
	 * MNO별 DATA, QOS GROUPBY
	 * @param response
	 * @param id
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/schDataQos" )
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> schDataQos ( HttpServletResponse response, @RequestParam("mno") String mno ) {
		
		try {
			
			Map<String, Object> result = new HashMap<String, Object>();
			SearchPlanDto dto = new SearchPlanDto();
			/* 전체가 아닐시 통신사별 데이터 groupby */
			if ( !StringUtils.equals("ALL", mno) ) {
				dto.setSearchMno(mno);
			}
			dto.setSearchType("data");
			List<PlanEntity> dataQosList = planService.getDataQosGroupBy(dto);
			result.put("data", dataQosList);
			
			dto.setSearchType("qos");
			dataQosList = planService.getDataQosGroupBy(dto);
			result.put("qos", dataQosList);
			
    		return CustomApiResponse.success(ResponseCode.OK, result);
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "ChatbotGroupEntity getDetail : " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ChatbotGroupEntity getDetail : " + e.getMessage());
        }
	}
	
	
	
	/**
	 * 챗봇 그룹 등록/수정
	 * @param response
	 * @param entity
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/insupdProcOrg" )
	public ResponseEntity<CustomApiResponse<String>> insupdProcOrg ( HttpServletRequest request, @RequestBody ChatbotGroupEntity entity ) {
		
		try {
			
			/* 세션 user get */
			HttpSession session = request.getSession();
			AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");
			
			ChatbotGroupEntity procEntity = chatbotGroupService.getDetail(entity.getId());
			
			/* 수정 */
			if ( procEntity != null ) {
				procEntity.setMvno(entity.getMvno());
				procEntity.setSupData(entity.getSupData());
				procEntity.setSubQos(entity.getSubQos());
				procEntity.setGroupMemo(entity.getGroupMemo());
				
				String groupName = getGroupName(entity.getMvno(), entity.getSupData(), entity.getSubQos());
				
				procEntity.setGroupName(groupName);
				procEntity.setModifiedId(adminInfo.getId());
				procEntity.setTitle(entity.getTitle());
				chatbotGroupService.update(procEntity);
				
			/* 등록 */
			} else {
				
				/* order용 카운트 */
				SearchChatbotGroupDTO param = new SearchChatbotGroupDTO();
				long resultcnt = chatbotGroupService.listCount(param);
				
				procEntity = new ChatbotGroupEntity();
				procEntity.setMvno(entity.getMvno());
				procEntity.setSupData(entity.getSupData());
				procEntity.setSubQos(entity.getSubQos());
				procEntity.setOrderNo((int)resultcnt + 1);
				procEntity.setTitle(entity.getTitle());
				
				String groupName = getGroupName(entity.getMvno(), entity.getSupData(), entity.getSubQos());
				
				procEntity.setGroupName(groupName);
				procEntity.setCreateId(adminInfo.getId());
				chatbotGroupService.create(procEntity);
			}
			
    		return CustomApiResponse.success(ResponseCode.OK, String.valueOf(procEntity.getId()));
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "ChatbotGroupEntity getDetail : " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ChatbotGroupEntity getDetail : " + e.getMessage());
        }
	}
	
	// 요금제 등록 수정 ================================================
	
	/**
	 * 조회 셀렉트 박스 조회
	 * @param response
	 * @param id
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/schCond" )
	public ResponseEntity<CustomApiResponse<Map<String, Object>>> schDataQos ( HttpServletResponse response, @RequestBody SearchPlanDto param ) {
		
		try {
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			
			List<String> dataQosList = planService.selectPlanSearchCond(param);
			result.put("data", dataQosList);
			
    		return CustomApiResponse.success(ResponseCode.OK, result);
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "ChatbotGroupEntity getDetail : " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ChatbotGroupEntity getDetail : " + e.getMessage());
        }
	}
	
	
	
	
	
	
	/**
	 * 챗봇 그룹 등록/수정
	 * @param response
	 * @param entity
	 * @return
	 */
	@ResponseBody
	@PostMapping( value = "/insupdProc" )
	public ResponseEntity<CustomApiResponse<String>> insupdProc ( HttpServletRequest request, @RequestBody chatbotGroupPlanDTO entity ) {
		
		try {
			
			/* 세션 user get */
			HttpSession session = request.getSession();
			AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");
			ChatbotGroupEntity record = entity.getChatbotGroupEntity();
			List<ChatbotGroupPlanEntity> planList = entity.getChatbotGroupPlans();
			
			ChatbotGroupEntity procEntity = chatbotGroupService.getDetail(record.getId());
			
			/* 수정 */
			if ( procEntity != null ) {
				procEntity.setMvno(record.getMvno());
				procEntity.setSupData(record.getSupData());
				procEntity.setSubQos(record.getSubQos());
				procEntity.setTitle(record.getTitle());
				procEntity.setGroupMemo(record.getGroupMemo());
				String groupName = getGroupName(record.getMvno(), record.getSupData(), record.getSubQos());
				
				procEntity.setGroupName(groupName);
				procEntity.setModifiedId(adminInfo.getId());
				
				chatbotGroupService.update(procEntity);
				
			/* 등록 */
			} else {
				
				/* order용 카운트 */
				SearchChatbotGroupDTO param = new SearchChatbotGroupDTO();
				long resultcnt = chatbotGroupService.listCount(param);
				
				procEntity = new ChatbotGroupEntity();
				procEntity.setMvno(record.getMvno());
				procEntity.setSupData(record.getSupData());
				procEntity.setSubQos(record.getSubQos());
				procEntity.setOrderNo((int)resultcnt + 1);
				procEntity.setTitle(record.getTitle());
				procEntity.setGroupMemo(record.getGroupMemo());
				
				
				String groupName = getGroupName(record.getMvno(), record.getSupData(), record.getSubQos());
				
				procEntity.setGroupName(groupName);
				procEntity.setCreateId(adminInfo.getId());
				chatbotGroupService.create(procEntity);
			}
			
			
			
			Integer chbotGroupId = procEntity.getId();
			
			chatbotService.deleteChatbotPlanAllByGroupId(chbotGroupId);
			for (ChatbotGroupPlanEntity plan : planList) {
				plan.setChatbotGroupId(chbotGroupId);
				plan.setCreateId(adminInfo.getId());
				chatbotService.insertChatbotPlan(plan);
			}
			
    		return CustomApiResponse.success(ResponseCode.OK, String.valueOf(procEntity.getId()));
    	}  catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "ChatbotGroupEntity getDetail : " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "ChatbotGroupEntity getDetail : " + e.getMessage());
        }
	}
	
	/**
	 * 그룹명 생성
	 * @param mvno
	 * @param subData
	 * @param qos
	 * @return
	 */
	public String getGroupName( String mvno, int subData, int qos ) {
		
		StringBuilder returnStr = new StringBuilder();
		
		/* DATA */
		if ( subData >= 1024 ) {
			
			if ( subData == 10238976 ) {
				returnStr.append("무제한");
			} else {
				if ( (subData % 1024) == 0 ) {
					returnStr.append(subData/1024).append("GB+");
				} else {
					float subDataf = subData;
					float div = 1024.0f;
					returnStr.append( String.format("%.1f", (float)(subDataf/div)) ).append("GB+");
				}
			}
		} else {
			returnStr.append(subData).append("MB+");
		}
		
		/* QOS */
		if ( qos >= 1024 ) {
			returnStr.append(qos/1024).append("Mbps");
		} else {
			returnStr.append(qos).append("Kbps");
		}
		
		return returnStr.toString();
	}
}
