package kr.co.ucomp.web.stl.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.stl.dto.EarningSearchDto;
import kr.co.ucomp.web.stl.entity.EarningEntity;
import kr.co.ucomp.web.stl.entity.EarningListEntity;
import kr.co.ucomp.web.stl.mapper.EarningMapper;
import kr.co.ucomp.web.stl.service.EarningService;

@Component
@Service
public class EarningServiceImpl implements EarningService {
	
	@Autowired
	private EarningMapper earningMapper;
	
	@Override
	public long listCount(EarningSearchDto param) {
		return earningMapper.listCount(param);
	}

	@Override
	public List<EarningEntity> list(EarningSearchDto param) {
		return earningMapper.list(param);
	}
	
	@Override
	public EarningEntity selectEarning(EarningSearchDto param) {
		return earningMapper.selectEarning(param);
	}
	
	@Override
	public void deleteEarningList(@Param("earningId") long id) {
		earningMapper.deleteEarningList(id);
	}

	@Override
	public int createEarning(EarningEntity entity) {
		return earningMapper.createEarning(entity);
	}

	@Override
	public void excelUpload(MultipartFile file, AdminUserDto loginadminInfo, long earningId) {
		
		int totCnt = 0;
		try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            totCnt = sheet.getLastRowNum();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header
                Row row = sheet.getRow(i);
                
                if ( !StringUtils.equals("", row.getCell(0).toString()) ) {
                	EarningListEntity item = new EarningListEntity();
                	item.setEarningId(earningId);
                    item.setAgencyName(row.getCell(0).toString());
                    item.setAppDate(row.getCell(1).toString());
                    item.setOpenDate(row.getCell(2).toString());
                    item.setMno(row.getCell(3).toString());
                    item.setContractNum(row.getCell(4).toString());
                    item.setAccType(row.getCell(5).toString());
                    item.setPhoneNum(row.getCell(6).toString());
                    item.setAccName(row.getCell(7).toString());
                    item.setPlanName(row.getCell(8).toString());
                    item.setStatus(row.getCell(9).toString());
                    
                    item.setCreateId(loginadminInfo.getId());
                    
                    earningMapper.createEarningList(item);
                } else {
                	break;
                }
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long detailListCount(EarningSearchDto param) {
		return earningMapper.detailListCount(param);
	}

	@Override
	public List<EarningListEntity> detailList(EarningSearchDto param) {
		return earningMapper.detailList(param);
	}

	@Override
	public List<EarningListEntity> detailListWithoutLimit(EarningSearchDto param) {
		return earningMapper.detailListWithoutLimit(param);
	}

}
