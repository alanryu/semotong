package kr.co.ucomp.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class FileService {
	
	 @Value("${file.upload-dir}")
	 private String uploadDir;
	 
	public String FileUpload(String filePath,MultipartFile uploadFie) {
		String result = "";
		
		try {
			Map<String, String> fileuploadMap = new HashMap<String, String>();
			
			String uploadPath = uploadDir + "/" + filePath;
			 // 업로드 디렉토리 생성 (존재하지 않으면)
	        Path path = Paths.get(uploadPath);
	        if (!Files.exists(path)) {
	            Files.createDirectories(path);
	        }

	        // 파일 저장
	        String originalFileName = uploadFie.getOriginalFilename();
	        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;
	        Path filePath1 = path.resolve(uniqueFileName);
	        uploadFie.transferTo(filePath1.toFile());

	        String fileExtension = "";
	        if (originalFileName != null && originalFileName.contains(".")) {
	        	fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
	        }
	        
	        long fileSize = uploadFie.getSize(); // 파일 크기 (바이트)
	        String contentType = uploadFie.getContentType(); // MIME 타입	        
	        fileuploadMap.put("orgFileNm", originalFileName);
	        fileuploadMap.put("sysFileNm", uniqueFileName);
	        fileuploadMap.put("fileExt", fileExtension);
	        fileuploadMap.put("fileSize", String.valueOf(fileSize));
	        fileuploadMap.put("fileUrl", "/uploads/" + filePath + "/" + uniqueFileName);
	        fileuploadMap.put("contentType", contentType);
	        
	        JSONObject jsonObject = new JSONObject(fileuploadMap);
	        result = jsonObject.toJSONString();
	        
	        
		} catch (Exception e) {
			// TODO: handle exception
		}
        
		
		return result;
		
	}
	
	
	
	
	public byte[] getExcelData(String[] headers, List<Map<String, Object>> dataList) throws Exception {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be null or empty");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // Create header row using map keys
            Row headerRow = sheet.createRow(0);
            Map<String, Object> firstRow = dataList.get(0);
            int columnIndex = 0;
            
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Populate data rows
            for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
                Row dataRow = sheet.createRow(rowIndex + 1);
                Map<String, Object> rowData = dataList.get(rowIndex);

                columnIndex = 0;
                for (Object value : rowData.values()) {
                    dataRow.createCell(columnIndex++).setCellValue(value != null ? value.toString() : "");
                }
            }

            // Write workbook to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
	

}
