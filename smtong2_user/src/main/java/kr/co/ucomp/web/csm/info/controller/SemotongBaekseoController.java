package kr.co.ucomp.web.csm.info.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.meta.MetaInfoService;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.csm.info.dto.SemotongBaekseoDto;
import kr.co.ucomp.web.csm.info.entity.SemotongBaekseoEntity;
import kr.co.ucomp.web.csm.info.service.SemotongBaekseoSevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 김재희
 * @since 2024.12.19
 * @version v1.0
 */
@Controller
@RequestMapping("/csm/baekseo")
public class SemotongBaekseoController {
	
	@Autowired private SemotongBaekseoSevice service;

	@ResponseBody
	@PostMapping("/list")
	public ResponseEntity<CustomApiResponse<List<SemotongBaekseoEntity>>> getBaekseoList(HttpServletRequest request, HttpServletResponse response,@RequestBody SemotongBaekseoDto dto) throws IOException  {
		
		try{
			long cnt = service.getListBaekseoCount(dto);
			List<SemotongBaekseoEntity> list = new ArrayList<SemotongBaekseoEntity>();
			if(cnt > 0) {
				list = service.getListBaekseo(dto);
			}
			return CustomApiResponse.success(ResponseCode.OK, cnt, list);
		}catch (Exception e){
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());
		}
	}


	@GetMapping("/detail/{id}")
	public String getBaekseo(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id, Model model ) throws IOException {
		
		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		
		SemotongBaekseoEntity baekseo = service.getBaekseo(id);
		
		model.addAttribute("detail", baekseo);
		
		return "pages/csm/info/baekseoDetail";
	}


	/**
 	  * 2024-12-18 조일근
 	  * 알뜰 폰이란
 	  * @param model
 	  * @param 
 	  */
 	@GetMapping("/mvnoInfo")
 	public String  mvnoInfo(HttpServletRequest request, HttpServletResponse response,Model model)  {
 		
 		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
 		
 		SemotongBaekseoEntity baekseo = service.getMvnoInfo();
		
		model.addAttribute("detail", baekseo);
		
 	 	return "pages/csm/info/mvnoInfo";
 	 }
 	 
	
	   /**
	  * 2024-12-18 조일근
	  *
	  * @param model
	  * @param 파트너사 화면 
	  */
	@GetMapping("/baekseoList")
	public String  baekseoList(HttpServletRequest request, HttpServletResponse response,Model model)  {
		
		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		 
	 	return "pages/csm/info/baekseoList";
	 }
	 
	 
	 
	   /**
	  * 2024-12-18 조일근
	  *
	  * @param model
	  * @param 파트너사 화면 
	  */
	@GetMapping("/baekseoDetail")
	public String  baekseoDetail(HttpServletRequest request, HttpServletResponse response,Model model)  {
		
		MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
		 
	 	return "pages/csm/info/baekseoDetail";
	 }

	 
	/* old 
	@GetMapping("/detail/{id}")
	public ResponseEntity<CustomApiResponse<SemotongBaekseoEntity>> getBaekseo(
			HttpServletResponse response,
			@PathVariable("id") long id
	) throws IOException {
		try{
			SemotongBaekseoEntity baekseo = service.getBaekseo(id);
			if (baekseo == null) {
				return CustomApiResponse.error(ResponseCode.NOT_FOUND, "백서가 존재하지 앟습니다.");
			}
			return CustomApiResponse.success(ResponseCode.OK, baekseo);
		}catch (Exception e){
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getBaekseo: " + e.getMessage());
		}
	}
	*/

		
		
}
