package kr.co.ucomp.web.csm.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.csm.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.entity.SemotongBaekseoEntity;
import kr.co.ucomp.web.csm.service.SemotongBaekseoSevice;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 김재희
 * @since 2024.12.19
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/csm/baekseo")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class SemotongBaekseoController {

	@Autowired private SemotongBaekseoSevice service;
	@Autowired private FileService fileService;

	@GetMapping("/list")
	public String list ( HttpServletRequest request, Model model ) {
		
		log.info("baekseo 리스트 진입");
				
		return "pages/svc/baekseo/list";
	}
	
	@PostMapping("/ajaxList")
	public ResponseEntity<CustomApiResponse<List<SemotongBaekseoEntity>>> getBaekseoList(HttpServletResponse response, @RequestBody SemotongBaekseoDto dto) throws IOException  {
	
		Long totCnt  							= null;
		List<SemotongBaekseoEntity> resultList 	= null;
		try{
			totCnt  			= service.getListBaekseoCount(dto);
			if(totCnt != null && totCnt > 0) { 
				resultList 		= service.getListBaekseo(dto);
			}
			return CustomApiResponse.success(ResponseCode.OK, totCnt, resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getEventPlanList: " + e.getMessage());
		}
	}
	
	@PostMapping( value = "/ajaxDisplaySp" )
	public ResponseEntity<CustomApiResponse<SemotongBaekseoEntity>> updateEvent (HttpServletRequest request, @RequestBody SemotongBaekseoEntity ent) throws Exception 
	{
		try {
			service.updateDisplaySp(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "Event updateDisplaySp: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Event updateDisplaySp: " + e.getMessage());
		}
	}
	
	/**
	 * 삭제한다.
	 */
	@PostMapping( value = "/ajaxDel" )
	public ResponseEntity<CustomApiResponse<SemotongBaekseoEntity>> deleteBaekseo (HttpServletRequest request, @RequestBody SemotongBaekseoEntity ent) throws Exception
	{
		try {
			service.deleteBaekseo(ent);
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "deleteEvent: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "deleteEvent: " + e.getMessage());
		}
	}
	
	
	
	/**
	 * 수정 페이지 이동
	 */
	@GetMapping("/goUpdatePage/{id}")
	public String goUpdatepate ( HttpServletRequest request, @PathVariable("id") int id, Model model ) {
		
		log.info("baekseo goUpdatepate - id:" + id);
		SemotongBaekseoDto param = new SemotongBaekseoDto();
		param.setId(id);
		SemotongBaekseoEntity result = service.getBaekseo(param);
		model.addAttribute("result", result);
		return "pages/svc/baekseo/edit";
	}
	
	
	@PostMapping("/insupdProc")
	public ResponseEntity<CustomApiResponse<SemotongBaekseoEntity>> insupdProc( MultipartHttpServletRequest request, @RequestParam Map<String, Object> obj ) {
		try {
			
			HttpSession session = request.getSession();
			AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
			
			SemotongBaekseoEntity ent			= new SemotongBaekseoEntity();
			int nowId							= 0;
			if(obj.get("id") != null && !"".equals(obj.get("id")) ) nowId =  Integer.parseInt( obj.get("id").toString() );
			ent.setId(nowId);
			ent.setContentSp(		(String) obj.get("contentSp")		);
			ent.setDisplaySp(		(String) obj.get("displaySp")		);
			ent.setDisplayStartDttm(		LocalDateTime.parse(obj.get("displayStartDttm").toString(), formatter) 	);
			ent.setDisplayEndDttm(			LocalDateTime.parse(obj.get("displayEndDttm").toString(), formatter) 	);
			ent.setTitle(			(String) obj.get("title")			);
			ent.setContent(			(String) obj.get("content")			);
			ent.setImage( 			(String) obj.get("bannerImage")		);
			
			
			MultipartFile file = request.getFile("file1");
			if (file != null && !file.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				String imageMoJson = fileService.FileUpload("baeseobanner", file);
				Map<String, Object> map = mapper.readValue(imageMoJson, Map.class);
				String imageSrc = (String)map.get("fileUrl");
				System.out.println(imageSrc);
				ent.setImage(imageSrc);
			}
			
			
			if(ent.getId() > 0) {
				ent.setModifiedId(loginadminInfo.getId());
				service.updateBaekseo(ent);
			}else {
				ent.setCreateId(loginadminInfo.getId());
				service.insertBaekseo(ent);
			}
			
    		 return CustomApiResponse.success(ResponseCode.CREATED,0, ent);
        } catch (IllegalArgumentException e) {
        	System.out.println(e);
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);
        } catch (Exception e) {
        	System.out.println(e);
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    	
    }
    
	

}
