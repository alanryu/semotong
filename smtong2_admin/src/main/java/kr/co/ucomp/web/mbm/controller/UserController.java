package kr.co.ucomp.web.mbm.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.cmm.service.CommCodeService;
import kr.co.ucomp.web.mbm.dto.AdminMemoDto;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.UserSearchDTO;
import kr.co.ucomp.web.mbm.entity.AdminMemoEntity;
import kr.co.ucomp.web.mbm.entity.UserEntity;
import kr.co.ucomp.web.mbm.service.AdminMemoService;
import kr.co.ucomp.web.mbm.service.UserService;
import kr.co.ucomp.web.point.dto.PointAccDTO;
import kr.co.ucomp.web.point.dto.PointHistoryDTO;
import kr.co.ucomp.web.point.entity.PointAccEntity;
import kr.co.ucomp.web.point.entity.PointHistoryEntity;
import kr.co.ucomp.web.point.service.PointAccService;
import kr.co.ucomp.web.point.service.PointHistoryService;

/**
 *
 * @author 이정민
 * @since 2024.12.11
 * @version v1.0
 */
@Controller
@RequestMapping("/mbm/users")
@PreAuthorize("hasAnyAuthority('ALL', 'USER_MNG')")
public class UserController {
	@Autowired
    private UserService userService;
	@Autowired FileService fileService;
	@Autowired private AdminMemoService adminMemoService;
	
	@Autowired private CommCodeService commCodeService;
	
	@Autowired PointAccService			pointAccService;
	@Autowired PointHistoryService		pointHistoryService;
	

    /**
     * 사용자 리스트 조회
     * @param response
     * @param searchRequest
     * @return
     * @throws IOException
     */
	@ResponseBody
    @PostMapping("/ajaxList")
    public ResponseEntity<CustomApiResponse<List<UserEntity>>> ajaxList(HttpServletResponse response,
    		@RequestBody UserSearchDTO param
    		) throws IOException {
    	
    	List<UserEntity> list = null;
    	
    	try {
    		long totCnt = userService.getListCount(param);
    		if(totCnt > 0) {
    			list = userService.getList(param);
    			
    			for ( UserEntity entity : list ) {
    				
    				/* 이름 마스킹 처리 */
    				int length = entity.getUsername().length();
    				StringBuilder maskedName = new StringBuilder(entity.getUsername());
    				
    				switch(length) {
    					case 2 :
    						maskedName.setCharAt(0, '*');
    						break;
    					case 3 :
    						maskedName.setCharAt(1, '*');
    						break;
    					case 4 :
    						maskedName.setCharAt(1, '*');
    						maskedName.setCharAt(2, '*');
    						break;
    					case 5 :
    						maskedName.setCharAt(1, '*');
    						maskedName.setCharAt(2, '*');
    						maskedName.setCharAt(3, '*');
    						break;
    					default :
    						for (int i = 1; i < length - 1; i++) {
    			                maskedName.setCharAt(i, '*');
    			            }
    				}
    				
    				entity.setUsername(maskedName.toString());
    				
    				/* 이메일 마스킹 처리 */
    				String parts[] = entity.getEmail().split("@");
    				String idPart = parts[0];
    				
    				StringBuilder maskedEmail = new StringBuilder();
    				
    				maskedEmail.append(idPart.substring(0, 1));
    		        for (int i = 1; i < idPart.length() - 1; i++) {
    		            maskedEmail.append('*');
    		        }
    		        maskedEmail.append(idPart.substring(idPart.length() - 1, idPart.length()));
    		        maskedEmail.append('@');
    		        
    		        String domainPartArr[] = parts[1].split("\\.");
    		        String domainFront = domainPartArr[0];
    		        
    		        maskedEmail.append(domainFront.substring(0, 1));
    		        for (int i = 1; i < domainFront.length(); i++) {
    		            maskedEmail.append('*');
    		        }
    		        maskedEmail.append(domainFront.substring(domainFront.length() - 1, domainFront.length()));
    		        
    		        for ( int idx = 1; idx < domainPartArr.length; idx++ ) {
    		        	maskedEmail.append(".").append(domainPartArr[idx]);
    		        }
    		        
    		        entity.setEmail(maskedEmail.toString());
    			}
    		}
    		
    		return CustomApiResponse.success(ResponseCode.OK,totCnt, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    }
    
	
	
	
	@ResponseBody
    @PostMapping("/exceldown")
	public ResponseEntity<byte[]> downPlanMap(@RequestBody UserSearchDTO param) throws Exception {
    	
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>(); 
    	List<UserEntity> list =  userService.getList(param);
    	for (UserEntity itm  :  list) {
        	Map<String, Object> data = new LinkedHashMap<String, Object>();
        	
        	String channelYn = "Y";
        	if ( itm.getChannelYn() == null || itm.getChannelYn() == 0 ) {
        		channelYn = "N";
        	}

			String smsAgreeYn = "Y";
			if ( itm.getSmsAgreeYn() == null || "0".equals(itm.getSmsAgreeYn())) {
				smsAgreeYn = "N";
			}

			String emailAgreeYn = "Y";
			if ( itm.getEmailAgreeYn() == null || "0".equals(itm.getEmailAgreeYn())) {
				emailAgreeYn = "N";
			}
        	
        	data.put("username", itm.getUsername());
        	data.put("ageGroup", itm.getAgeGroup());
        	data.put("birthDay", itm.getBirthDay());
        	data.put("birthYear", itm.getBirthYear());
//        	data.put("kakaoUserId", itm.getKakaoUserId() );
        	data.put("email", itm.getEmail());
        	data.put("channelYn", channelYn);
			data.put("phoneNumber", itm.getPhoneNumber());
			data.put("smsAgreeYn", smsAgreeYn);
			data.put("emailAgreeYn", emailAgreeYn);
        	data.put("memberStat", itm.getMemberStatNm());
        	data.put("createDate", itm.getCreateDate());
        	dataList.add(data);    		
    	}

    	
    	// 엑셀 헤더 설정
    	String[] headers = {"이름", "연령대", "생일","출생년도","카카오계정", "카카오채널 추가여부", "전화번호", "SMS수신 동의여부", "이메일수신 동의여부", "회원구분", "등록일"};
    	
        byte[] excelData = fileService.getExcelData(headers,dataList);

        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=userList.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
	
   /**
    * 
    * @param request
    * @param response
    * @param model
    * @return
    */
	@GetMapping("/list")
	public String  companyList( HttpServletRequest request, HttpServletResponse response,Model model)  {
		
		return "pages/mbm/user/list";
	}
    
	
	/**
     * 사용자 리스트 조회
     * @param response
     * @param searchRequest
     * @return
     * @throws IOException
     */
	@ResponseBody
    @PostMapping("/updateState")
    public ResponseEntity<CustomApiResponse<UserEntity>> updateState(HttpServletRequest request, HttpServletResponse response,
    		@RequestBody UserEntity param
    		) throws IOException {
    	
    	try {
    		
    		
    		String nowState = param.getMemberStatNow();
    		String chgState = param.getMemberStat();
    		String _etc1 = commCodeService.getCode("member_status", nowState)   + "->" + commCodeService.getCode("member_status", chgState);
    		
    		
    		
    		HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
    		
    		param.setModifiedId(loginadminInfo.getId());
    		param.setDisableMngId(loginadminInfo.getId());
    		if("ACTIVE".equals(chgState)) {
    			param.setActiveYn("1");	
    		} else {
    			param.setActiveYn("0");
    		}
    		
    		int result = userService.updateState(param);
    		
    		
    		AdminMemoEntity memoparam = new AdminMemoEntity();
    		
    		memoparam.setMemoType("tb_mbm_user-member_stat");
    		memoparam.setUserId(param.getId());
    		memoparam.setMemo(param.getDisableResn());
    		memoparam.setEtc1(_etc1);
    		memoparam.setCreateId(loginadminInfo.getId());
    		
    		adminMemoService.insertAdminMemo(memoparam);
    		
    		return CustomApiResponse.success(ResponseCode.OK,result, param);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
    }
	
	
	/**
    * 
    * @param request
    * @param response
    * @param model
    * @return
    */
	@GetMapping("/edit")
	public String  userEdit(@RequestParam(value="searchId" , required = false) String searchId, HttpServletRequest request, HttpServletResponse response,Model model)  {
		
		UserEntity record = new UserEntity();
		List<AdminMemoEntity> memoList = new ArrayList<AdminMemoEntity>();
		
		PointAccEntity userAccInfo			= new PointAccEntity();
		PointHistoryEntity hTotInfo			= new PointHistoryEntity();
		List<PointHistoryEntity> hitoryList	= new ArrayList<PointHistoryEntity>();
		
		try {
			if(StringUtils.isNoneEmpty(searchId)) {
				record = userService.getUserUser(Integer.parseInt(searchId));
				
				AdminMemoDto usermemoParam = new AdminMemoDto();
				usermemoParam.setMemoType("tb_mbm_user-member_stat");
				usermemoParam.setUserId(Integer.parseInt(searchId));
				memoList = adminMemoService.adminMemoList(usermemoParam);
				
				//1. point 에서 balance 구하기
				PointAccDTO paramacc = new PointAccDTO();
				paramacc.setSearchUserId( Integer.parseInt(searchId) );
				userAccInfo = pointAccService.getMyPoint(paramacc);
				
				//2. history에서 누적으로 적립/차감 각 합계 구하기
				//PointHistoryEntity 			getPointTotInfo(PointHistoryDTO param);
				PointHistoryDTO paramhistory = new PointHistoryDTO();
				paramhistory.setSearchUserId(Integer.parseInt(searchId));
				hTotInfo = pointHistoryService.getPointTotInfo(paramhistory);
				
				//3. history에서 이력 구하기
				paramhistory.setPage(1);
				paramhistory.setRecordSize(9999);
				//hitoryList = pointHistoryService.getPointHistory(paramhistory);
				hitoryList = pointHistoryService.getPointHistoryNew(paramhistory);
			}
			
    	} catch (Exception e) {
    		
		}
		model.addAttribute("memoList", memoList);
		model.addAttribute("record", record);
		
		model.addAttribute("userAccInfo"		, userAccInfo);
		model.addAttribute("hTotInfo"			, hTotInfo);
		model.addAttribute("hitoryList"			, hitoryList);
		return "pages/mbm/user/edit";
	}
	
	
	
    
}
