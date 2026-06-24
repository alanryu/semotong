package kr.co.ucomp.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	public String FileUpload(String filePath, MultipartFile uploadFie) {
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

	// public byte[] getExcelData(String[] headers, List<Map<String, Object>>
	// dataList) throws Exception {
	// if (dataList == null || dataList.isEmpty()) {
	// throw new IllegalArgumentException("Data list cannot be null or empty");
	// }

	// try (Workbook workbook = new XSSFWorkbook()) {
	// Sheet sheet = workbook.createSheet("Sheet1");

	// // Create header row using map keys
	// Row headerRow = sheet.createRow(0);
	// Map<String, Object> firstRow = dataList.get(0);
	// int columnIndex = 0;

	// for (int i = 0; i < headers.length; i++) {
	// headerRow.createCell(i).setCellValue(headers[i]);
	// }

	// // Populate data rows
	// for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
	// Row dataRow = sheet.createRow(rowIndex + 1);
	// Map<String, Object> rowData = dataList.get(rowIndex);

	// columnIndex = 0;
	// for (Object value : rowData.values()) {
	// dataRow.createCell(columnIndex++).setCellValue(value != null ?
	// value.toString() : "");
	// }
	// }

	// // Write workbook to byte array
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// workbook.write(out);
	// return out.toByteArray();
	// }
	// }

	public byte[] getExcelData(String[] headers, List<Map<String, Object>> dataList) throws Exception {
		return getExcelData(headers, dataList, Set.of("counselContent", "procContent"));
	}

	public byte[] getExcelData(String[] headers, List<Map<String, Object>> dataList, Set<String> wrapTextColumns)
			throws Exception {
		if (dataList == null || dataList.isEmpty()) {
			throw new IllegalArgumentException("Data list cannot be null or empty");
		}

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Sheet1");

			// 줄바꿈을 위한 셀 스타일 생성
			CellStyle wrapTextStyle = workbook.createCellStyle();
			wrapTextStyle.setWrapText(true);
			wrapTextStyle.setVerticalAlignment(VerticalAlignment.TOP);

			// 헤더 스타일 생성
			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Create header row
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				Cell headerCell = headerRow.createCell(i);
				headerCell.setCellValue(headers[i]);
				headerCell.setCellStyle(headerStyle);
			}

			// Populate data rows
			for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
				Row dataRow = sheet.createRow(rowIndex + 1);
				Map<String, Object> rowData = dataList.get(rowIndex);

				int columnIndex = 0;
				for (Map.Entry<String, Object> entry : rowData.entrySet()) {
					Cell cell = dataRow.createCell(columnIndex);
					Object value = entry.getValue();
					String key = entry.getKey();

					if (value != null) {
						String stringValue = value.toString();

						// 줄바꿈이 필요한 컬럼인지 확인
						if (wrapTextColumns != null && wrapTextColumns.contains(key)) {
							// JSON에서 오는 \n 문자열을 실제 줄바꿈으로 변환
							stringValue = convertNewlineForExcel(stringValue);
							cell.setCellValue(stringValue);
							cell.setCellStyle(wrapTextStyle);

							// 줄바꿈이 있는 경우 행 높이 조정
							if (stringValue.contains("\n")) {
								dataRow.setHeight((short) -1);
							}
						} else {
							cell.setCellValue(stringValue);
						}
					} else {
						cell.setCellValue("");
					}

					columnIndex++;
				}
			}

			// 컬럼 너비 자동 조정
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);

				// 줄바꿈 컬럼은 더 넓게 설정
				if (i >= 10) { // 상담내용, 처리결과 등
					sheet.setColumnWidth(i, 18000);
				}

				// 최대 너비 제한
				if (sheet.getColumnWidth(i) > 20000) {
					sheet.setColumnWidth(i, 20000);
				}
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			return out.toByteArray();
		}
	}

	// 줄바꿈 문자 변환 유틸리티 메소드
	private String convertNewlineForExcel(String text) {
		if (text == null) {
			return "";
		}
		// JSON에서 오는 \n 문자열을 실제 줄바꿈으로 변환
		return text.replace("\\n", "\n");
	}

}
