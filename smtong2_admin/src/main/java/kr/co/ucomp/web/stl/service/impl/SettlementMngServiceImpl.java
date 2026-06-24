package kr.co.ucomp.web.stl.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ucomp.web.stl.dto.SettlementMngSearchDto;
import kr.co.ucomp.web.stl.dto.SettlementMngUploadDto;
import kr.co.ucomp.web.stl.entity.SettlementMSTEntity;
import kr.co.ucomp.web.stl.entity.SettlementMngEntity;
import kr.co.ucomp.web.stl.mapper.SettlementMngMapper;
import kr.co.ucomp.web.stl.service.SettlementMngService;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Cell;
import java.math.RoundingMode;

@Service("SettlementMngService")
@Slf4j
public class SettlementMngServiceImpl implements SettlementMngService {
	@Autowired
	SettlementMngMapper mapper;
	
	/**
	 * 정산 목록 월별 합산 조회
	 * @param : SettlementSearchDto
	 * @return List<SettlementMngEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SettlementMSTEntity> getListSettlementMst(SettlementMngSearchDto param) {
		List<SettlementMSTEntity> list = new ArrayList<SettlementMSTEntity>();
		list =  mapper.getListSettlementMst(param);
		return list;
	}
	
	/**
	 * 정산 목록 조회
	 * @param : SettlementSearchDto
	 * @return List<SettlementMngEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SettlementMngEntity> getListSettlement(SettlementMngSearchDto param) {
		List<SettlementMngEntity> list = null;
		long count = mapper.getListSettlementCount(param);
		if(count > 0) list = mapper.getListSettlement(param);
		
		return list;
	}
	
	/**
	 * 정산 단건 조회
	 * @param : id(조회 id)
	 * @return SettlementMngEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public SettlementMngEntity getSettlement(@Param("id") int param) {
		return mapper.getSettlement(param);
	}
	
	/**
	 * 정산 저장
	 * @param : SettlementMngEntity
	 * @return 결과(생성 갯수)
	 */
	@Override
	@Transactional
	public long create(SettlementMngEntity param) {
		return mapper.create(param);
	}
	
	/**
	 * 정산 수정
	 * @param : SettlementMngEntity
	 * @return 결과(수정 갯수)
	 */
	@Override
	@Transactional
	public long update(SettlementMngEntity param) {
		return mapper.update(param);
	}
	
	/**
	 * 정산 삭제
	 * @param : id(삭제 id)
	 * @return 결과(삭제 갯수)
	 */
	@Override
	@Transactional
	public long delSettlement(@Param("id") int param) {
		return mapper.delSettlement(param);
	}
	
	@Override
	@Transactional
	public Map<String,Object> uploadSettlementData(MultipartFile file, SettlementMngUploadDto param) throws Exception {
		Map<String,Object> result = new HashMap<>();
		int totalCount = 0;
		int errorCount = 0;
		int savedCount = 0;
		StringBuilder errorDetails = new StringBuilder();
		
		try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			
			// 두 번째 행부터 시작 (인덱스 1)
			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) continue;
				
				totalCount++;
				log.info("Processing row: {}", rowIndex); // 디버깅용 로그
				
				try {
					SettlementMngEntity entity = new SettlementMngEntity();
					entity.setCompanyId(param.getCompanyId());
					entity.setYyyymm(param.getStlTYyyymm());
					entity.setCreateId(param.getManageId());
					entity.setModifiedId(param.getManageId());

					// 숫자 필드 처리
					Cell cell5 = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					Cell cell6 = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					
					String basicFeeStr = "";
					String discountAmountStr = "";

					// 기본료 처리
					if (cell5 != null) {
						basicFeeStr = cell5.toString().trim();
					}

					// 할인금액 처리
					if (cell6 != null) {
						discountAmountStr = cell6.toString().trim();
					}

					// 숫자로 변환
					try {
						if (basicFeeStr.isEmpty()) {
							entity.setBasicFee(BigDecimal.ZERO);
						} else {
							entity.setBasicFee(new BigDecimal(basicFeeStr.replaceAll("[,\\s₩]", "")));
						}
						
						if (discountAmountStr.isEmpty()) {
							entity.setDiscountAmount(BigDecimal.ZERO);
						} else {
							entity.setDiscountAmount(new BigDecimal(discountAmountStr.replaceAll("[,\\s₩]", "")));
						}
					} catch (NumberFormatException e) {
						throw new RuntimeException(String.format("숫자 변환 실패 - 행: %d, 기본료: '%s', 할인금액: '%s'", 
							rowIndex, basicFeeStr, discountAmountStr));
					}

					// 나머지 필드들도 문자열로 처리
					entity.setStlType(row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim());
					entity.setMno(row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim());
					entity.setNetwork(row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim());
					entity.setPlanName(row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim());
					entity.setDataPlan(row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim());
					entity.setDiscountPeriod(row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim());
					entity.setApplicant(row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim());
					entity.setContactNumber(row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().trim());
					entity.setActivationDate(getDateCellValue(row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));

					create(entity);
					savedCount++;
				} catch (Exception e) {
					errorCount++;
					String errorMsg = String.format("행 %d 처리 중 오류: %s", rowIndex, e.getMessage());
					errorDetails.append(errorMsg).append("\n");
					log.error(errorMsg);
					continue;
				}
			}
			
			result.put("totCnt", totalCount);
			result.put("saveCnt", savedCount);
			result.put("errorCnt", errorCount);
			result.put("errorDetails", errorDetails.toString());
		}
		return result;
	}
	
	private Date getDateCellValue(Cell cell) {
		if (cell == null) return null;
		try {
			if (cell.getCellType() == CellType.STRING) {
				String dateStr = cell.toString().trim();
				if (dateStr.isEmpty()) return null;
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
				return dateFormat.parse(dateStr);
			}
			return cell.getDateCellValue();
		} catch (Exception e) {
			// 에러를 로그로 남기고 상위로 전파
			String errorMsg = String.format("날짜 변환 실패 (셀 값: '%s', 에러: %s)", cell, e.getMessage());
			log.error(errorMsg);
			throw new RuntimeException(errorMsg);
		}
	}
	
	@Override
	@Transactional
	public void deleteByYearMonthAndCompany(SettlementMngUploadDto param) {
		mapper.deleteByYearMonthAndCompany(param);
	}

}
