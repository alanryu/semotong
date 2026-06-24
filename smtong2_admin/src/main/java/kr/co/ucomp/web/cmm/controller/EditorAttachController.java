package kr.co.ucomp.web.cmm.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;

import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
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
	public ResponseEntity<Map<String, String>> handleImageUpload(@RequestParam("attach") MultipartFile attach) 
	{
		Map<String, String> response = new HashMap<>();
		try {
			String rtnStr = fileService.FileUpload("attach", attach);
			ObjectMapper mapper = new ObjectMapper();
			
			Map<String, Object> map = mapper.readValue(rtnStr, Map.class);
			String filePath ="";
			if(map.get("fileUrl") !=null) {
				filePath = (String) map.get("fileUrl");
			}
			response.put("location", filePath);
			
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			e.printStackTrace();
            response.put("error", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
