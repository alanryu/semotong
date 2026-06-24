package kr.co.ucomp.web.pmb.service.impl;

import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.web.pmb.dto.InternetReqMngSearchDto;
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.entity.InternetReqMngEntity;
import kr.co.ucomp.web.pmb.mapper.InternetReqMngMapper;
import kr.co.ucomp.web.pmb.service.InternetReqMngService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class InternetReqMngServiceImpl implements InternetReqMngService {
	private InternetReqMngMapper mapper;

	@Override
	public List<InternetReqMngEntity> getList(InternetReqMngSearchDto searchDto) {
		return mapper.getList(searchDto);
	}

	@Override
	public long getListCount(InternetReqMngSearchDto internetReqMngSearchDto) {
		return mapper.getListCount(internetReqMngSearchDto);
	}

	@Override
	public InternetReqMngEntity getDetail(@Param("id") int id) {

		return mapper.getDetail(id);
	}

	@Override
	public long update(InternetReqMngEntity param) {
		return mapper.update(param);
	}

	@Override
	public long updateState(InternetReqMngEntity param) {
		return mapper.update(param);
	}

	@Override
	public List<InternetReqMngEntity> getListWithOutLimit(InternetReqMngSearchDto param) {
		return mapper.getListWithOutLimit(param);
	}

	// 사용자 데이터 처리
	public Map<String, Object> reqExcelUpload(MultipartFile file, String isNew, String outboundCenter)
			throws IOException {

		System.out.println("@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(outboundCenter);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@");

		Map<String, Object> uploadRes = new HashMap<String, Object>();
		long saveCnt = 0;
		long failCnt = 0;
		int totCnt = 0;
		List<String> failUUid = new ArrayList<String>();

		if (StringUtils.isBlank(isNew)) {
			isNew = "Y";
		}

		if ("1".equals(isNew))
			isNew = "Y";
		if ("0".equals(isNew))
			isNew = "N";
		try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			totCnt = sheet.getLastRowNum();
			for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header
				Row row = sheet.getRow(i);

				InternetReqMngEntity item = new InternetReqMngEntity();

				if (row.getCell(1) != null && row.getCell(2) != null) {
					String reqNm = CommonUtil.readExcelCell(row.getCell(1));
					String reqHp = CommonUtil.readExcelCell(row.getCell(2));
					String relComp = CommonUtil.readExcelCell(row.getCell(3));

					if (!StringUtils.isEmpty(reqNm.trim()) && !StringUtils.isEmpty(reqHp.trim())) {
						item.setInputName(reqNm);
						item.setInputNumber(reqHp);
						item.setIsNew(isNew);
						item.setRelComp(relComp);
						item.setOutboundCenter(outboundCenter);

						long cnt = mapper.create(item);
						if (cnt > 0) {
							saveCnt = saveCnt + cnt;
						} else {
							failCnt = failCnt + 1;
							failUUid.add(row.getCell(0).getStringCellValue());
						}
					} else {
						failCnt = failCnt + 1;
						failUUid.add("uuid or plancode is empty");
					}
				} else {
					failCnt = failCnt + 1;
					failUUid.add("uuid or plancode is empty");
				}
			}
		}

		uploadRes.put("saveCnt", saveCnt);
		uploadRes.put("totCnt", totCnt);
		uploadRes.put("failCnt", failCnt);
		uploadRes.put("failUUid", failUUid);

		return uploadRes;

	}

}
