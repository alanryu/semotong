package kr.co.ucomp.web.cmm.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.event.dto.EvtPlanSearchDTO;
import kr.co.ucomp.web.plan.dto.PlanUpdateDto;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cmm/editor")
@Slf4j
public class EditorAttachController {
	
	@Autowired private FileService fileService;
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	/**
	 * 2024.12.24 최의규
	 *  - 에디터 그림 등 파일첨부
	 * @param response
	 * @param searchRequest
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value="/attach", consumes =MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadEditorAttach(
									HttpServletRequest request
									,@RequestPart(value="attach",required = true) MultipartFile attach
									) throws IOException 
	{

		String rtnStr = ""; 
		try {
			rtnStr = fileService.FileUpload("attach", attach);
			return rtnStr;
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
	}
	
	@GetMapping(value = "/getAttach/{filename}", produces = {MediaType.ALL_VALUE })
	public byte[] getEditorImage(HttpServletRequest request
				,@PathVariable("filename") String filename
			) 
	{
		// 업로드된 파일의 전체 경로
		String fileFullPath = Paths.get(uploadDir, "attach" + File.separator +  filename).toString();
		System.out.println(fileFullPath);
	
		// 파일이 없는 경우 예외 throw
		File uploadedFile = new File(fileFullPath);
		if (uploadedFile.exists() == false) {
			throw new RuntimeException();
		}
	
		try {
			// 이미지 파일을 byte[]로 변환 후 반환
			byte[] imageBytes = Files.readAllBytes(uploadedFile.toPath());
			return imageBytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	

}
