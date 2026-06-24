package kr.co.ucomp.web.cmm.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.cmm.dto.FileMngDto;
import kr.co.ucomp.web.cmm.entity.FileMngEntity;
import kr.co.ucomp.web.cmm.service.FileMngService;
import kr.co.ucomp.web.order.service.DailySequenceService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cmm/filemng")
@Slf4j
public class FileMngController {
	
	@Autowired private FileService fileService;
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired FileMngService		fileMngService;
	@Autowired DailySequenceService 	sequenceService;
	
	/**
	 * jobType 에 따라 document upload 및 파일 정보 저장
	 * @param request
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value="/document")
	public ResponseEntity<CustomApiResponse<FileMngEntity>> handleDocumentUpload(MultipartHttpServletRequest request, @RequestParam Map<String, Object> obj)
	{
		
		
		FileMngEntity ent = new FileMngEntity();
		
		try {
		
			String jobType = obj.get("jobType").toString().toUpperCase();
			ent.setJobType(		jobType					);
			ent.setJobSeq(		getJobSeqNo(jobType)	);
			
			MultipartFile docuFile = request.getFile("docuFile");
			if ( !StringUtils.isEmpty(docuFile.getOriginalFilename()) ) {
				
				// --- 
				String rtnStr = fileService.FileUpload("document", docuFile);			// 경로명  document
				// ---
				
				ObjectMapper mapper 	= new ObjectMapper();
				Map<String, Object> map = mapper.readValue(rtnStr, Map.class);
				ent.setOrgFileNm(		(String) map.get("orgFileNm")		);
				ent.setSysFileNm(		(String) map.get("sysFileNm")		);
				ent.setFileUrl(			(String) map.get("fileUrl")		);
				ent.setFileInfo(		rtnStr		);
				
			}
			
			ent.setEtc1(			(String) obj.get("etc1")		);
			ent.setEtc2(			(String) obj.get("etc2")		);
			ent.setEtc3(			(String) obj.get("etc3")		);
			
			int insId = fileMngService.insertFileInfo(ent);
			ent.setId(insId);
			
			return CustomApiResponse.success(ResponseCode.OK, ent);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "document upload : " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "document upload : " + e.getMessage());
		}
	}
	
	public String getJobSeqNo(String jobType) {
		String rtn = "";
		String 		today 		= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
		int 		nextSeq 	= sequenceService.getNextSequence();
		String 		nextSeqStr 	= String.format("%07d", nextSeq);
		String 		orderSeq 	= jobType + today + nextSeqStr;
		rtn = orderSeq;
		
		return rtn;
	}
	
	
	/**
	 * system file name으로 직접 파일 받기
	 * @param filename
	 * @return
	 */
	@GetMapping(value = "/getDocument/{filename}")
	public ResponseEntity<byte[]> getEditorImage(@PathVariable(name = "filename") String filename) {
		
		
		try {
			System.out.println(filename);
			String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
			System.out.println(decodedFilename);
			filename = decodedFilename;
			
			// 보안 검증: 경로 조작 공격 방지
			if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
				return ResponseEntity.badRequest().body(null);
			}

			// 파일 경로 설정
			String fileFullPath = Paths.get(uploadDir, "document", filename).toString();
			File file = new File(fileFullPath);

			// 파일 존재 여부 확인
			if (!file.exists()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			// 파일을 byte 배열로 변환
			byte[] fileBytes = Files.readAllBytes(file.toPath());

			// 응답 헤더 설정 (파일 다운로드 가능하게 설정)
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file.toPath())); // 파일 MIME 타입 자동 설정
			//headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
			// 한글 파일명 UTF-8로 변환
			String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20"); // 공백을 `%20`으로 변경
			headers.add("Content-Disposition", "inline; filename*=UTF-8''" + encodedFilename);
			
			return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	
	
	
	@ResponseBody
	@PostMapping(value = "/getFileInfo")
	public ResponseEntity<CustomApiResponse<FileMngEntity>> getFileInfo (HttpServletRequest request, @RequestBody FileMngDto param) throws Exception{
		FileMngEntity fileInfo		= null;
		try {
			fileInfo 		= fileMngService.selectFileInfo(param); 
			return CustomApiResponse.success(ResponseCode.OK, fileInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getFileInfo: " + e.getMessage());
		}
	}
	



}
