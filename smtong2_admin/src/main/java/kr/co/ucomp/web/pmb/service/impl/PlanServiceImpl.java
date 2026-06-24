package kr.co.ucomp.web.pmb.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ucomp.common.util.CommonUtil;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeService;
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanDataEntity;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.mapper.PlanMapper;
import kr.co.ucomp.web.pmb.service.PlanService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("PlanService")
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanMapper mapper;
    @Autowired private  CommCodeService codeservice;

    @Override
    @Transactional(readOnly = true)
    public List<PlanEntity> getList(SearchPlanDto param) {
        List<PlanEntity> list  = mapper.getList(param);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public long getListCount(SearchPlanDto param) {
        return mapper.getListCount(param);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanEntity getDetail(int id) {
        return mapper.getDetail(id);
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public PlanEntity getDetailByUUid(String searchUUid) {
        return mapper.getDetailByUUid(searchUUid);
    }
    
    
    @Override
    @Transactional
    public long updataPlanCode(PlanUpdateDto param) {
        return mapper.updataByUuid(param);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getListCountByPlanCode(PlanUpdateDto param) {
        return mapper.getListCountByPlanCode(param);
    }

    
    
    @Override
    @Transactional
    public long update(PlanUpdateDto param) {
        return mapper.update(param);
    }
    

    @Override
    @Transactional
    public long delete(int id) {
        return mapper.delete(id);
    }
    
    
    // excel 매핑 데이터 조회
    @Override
    @Transactional(readOnly = true)
    public List<PlanEntity> getMapListExcel(SearchPlanDto param) {
        List<PlanEntity> list  = mapper.getMapListExcel(param);
        return list;
    }
    
    
  
    
    // 사용자 데이터 처리
    public Map<String,Object> planMapUpload(MultipartFile file) throws IOException {
    	Map<String,Object> uploadRes = new HashMap<String,Object>();
    	long saveCnt = 0;
    	long failCnt = 0;
    	int totCnt = 0;
    	List<String> failUUid = new ArrayList<String>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            totCnt = sheet.getLastRowNum();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header
                Row row = sheet.getRow(i);

                PlanUpdateDto item = new PlanUpdateDto();
                
                
                if ( row.getCell(0) != null && row.getCell(9) != null ) {
                	String _uuid = CommonUtil.readExcelCell(row.getCell(0));
                	String _planCode = CommonUtil.readExcelCell(row.getCell(9));
                	
                	if ( !StringUtils.isEmpty(_uuid.trim()) && !StringUtils.isEmpty(_planCode.trim()) ) {
                		item.setUuid(_uuid);
                        item.setPlanCode(_planCode);
                        long cnt = mapper.updataByUuid(item);
                        if(cnt>0) {
                        	saveCnt = saveCnt + cnt;	
                        } else {
                        	failCnt = failCnt +1;
                        	failUUid.add(row.getCell(0).getStringCellValue());
                        }
                	} else {
                		failCnt = failCnt +1;
                    	failUUid.add("uuid or plancode is empty");
                	}
                } else {
                	failCnt = failCnt +1;
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
    
    
    @Override
    @Transactional(readOnly = true)
    public List<PlanEntity> getAllListByPlanIds(SearchPlanDto param) {
        List<PlanEntity> list  = mapper.getAllListByPlanIds(param);
        return list;
    }

	@Override
	public List<PlanEntity> getListWithoutLimit(SearchPlanDto param) {
		return mapper.getListWithoutLimit(param);
	}

	@Override
	public List<PlanEntity> getDataQosGroupBy(SearchPlanDto dto) {
		return mapper.getDataQosGroupBy(dto);
	}
	
	
	@Override
	public List<PlanEntity> getOrderList(SearchPlanDto dto) {
		return mapper.getOrderList(dto);
	}
	
	
	

	
	   // 사용자 데이터 처리
    public Map<String,Object> planMapUploadOrder(String searchOrderListSp,MultipartFile file) throws IOException {
    	Map<String,Object> uploadRes = new HashMap<String,Object>();
    	long saveCnt = 0;
    	long failCnt = 0;
    	int totCnt = 0;
    	List<String> failUUid = new ArrayList<String>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            totCnt = sheet.getLastRowNum();
            
            
            String orderSp = "";
			if("01".equals(searchOrderListSp) || "08".equals(searchOrderListSp)) {
				orderSp = "RECOM01";
			} else if("02".equals(searchOrderListSp)) {
				orderSp = "RECOM03";
			} else {
				orderSp = "RECOM02";
			}
			
			// 업로드 요금제 구분 모두 0으로 초기화
			PlanUpdateDto param = new PlanUpdateDto(); 
			param.setPlanOrderSp(orderSp);
			param.setSearchOrderListSp(searchOrderListSp);
			mapper.planMapUploadOrderInit(param);
			
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header
                Row row = sheet.getRow(i);
                PlanUpdateDto item = new PlanUpdateDto();
                
                if ( row.getCell(2) != null && row.getCell(14) != null ) {
                	String _uuid = CommonUtil.readExcelCell(row.getCell(2));
                	String _planOrder = CommonUtil.readExcelCell(row.getCell(15));
                	if(_planOrder.indexOf(".") >0) {
                		_planOrder = _planOrder.split("\\.")[0];
                	}
                	
                	if ( !StringUtils.isEmpty(_uuid.trim()) && !StringUtils.isEmpty(_planOrder.trim())  && !"0".equals(_planOrder)) {
                		
                		item.setUuid(_uuid);
                		item.setPlanOrderSp(orderSp);
                		item.setSearchOrderListSp(searchOrderListSp);
                		
                		if("RECOM01".equals(orderSp)) {
                			item.setRecomOrder1(Integer.valueOf(_planOrder));	
                		} else  if("RECOM02".equals(orderSp)) {
                			item.setRecomOrder2(Integer.valueOf(_planOrder));
                		} else {
                			item.setRecomOrder3(Integer.valueOf(_planOrder));
                		}
                        
                        long cnt = mapper.updataByUuid(item);
                        if(cnt>0) {
                        	saveCnt = saveCnt + cnt;	
                        } else {
                        	failCnt = failCnt +1;
                        	failUUid.add(row.getCell(0).getStringCellValue());
                        }
                	} else {
                		failCnt = failCnt +1;
                    	failUUid.add("uuid or plancode is empty");
                	}
                } else {
                	failCnt = failCnt +1;
                	failUUid.add("uuid or plancode is empty");
                }
            }
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        
        
        
        uploadRes.put("saveCnt", saveCnt);
        uploadRes.put("totCnt", totCnt);
        uploadRes.put("failCnt", failCnt);
        uploadRes.put("failUUid", failUUid);
        
        return uploadRes;
    }
    
    
    /**
     * planData 테이블 조회
     */
    @Override
    @Transactional(readOnly = true)
    public PlanDataEntity getDetailOrg(String searchUUid) {
        return mapper.getDetailOrg(searchUUid);
    }
    
    
    @Override
    @Transactional
    public long createOrg(PlanDataEntity param) {
        return mapper.createOrg(param);
    }
    
    
    @Override
    @Transactional
    public long updateOrg(PlanDataEntity param) {
        return mapper.updateOrg(param);
    }

	@Override
	public List<PlanEntity> getChatbotPlanList(SearchPlanDto param) {
		return mapper.getChatbotPlanList(param);
	}

	@Override
	public List<String> selectPlanSearchCond(SearchPlanDto searchPlanDto) {
		// TODO Auto-generated method stub
		return mapper.selectPlanSearchCond(searchPlanDto);
	}
	

    @Override
    @Transactional
    public long updataPopulerOrderInit() {
        return mapper.updataPopulerOrderInit();
    }
    
    
    @Override
    @Transactional
    public long updataPopulerOrder(PlanUpdateDto param) {
        return mapper.updataPopulerOrder(param);
    }
    
    
    @Override
    @Transactional
    public long updataPlanTag(PlanUpdateDto param) {
        return mapper.updataPlanTag(param);
    }
    
    
    
    
    // 사용자 데이터 처리
    public Map<String,Object> planTagUpload(MultipartFile file) throws IOException {
    	Map<String,Object> uploadRes = new HashMap<String,Object>();
    	long saveCnt = 0;
    	long failCnt = 0;
    	int totCnt = 0;
    	List<String> failUUid = new ArrayList<String>();
    	List<CodeEntity> tagCodeList = codeservice.getCodeList2("plan_tag_code");
    	
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            totCnt = sheet.getLastRowNum();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header
                Row row = sheet.getRow(i);

                PlanUpdateDto item = new PlanUpdateDto();
                if ( row.getCell(0) != null  ) {
                	
                	
                	String planId = CommonUtil.readExcelCell(row.getCell(0));
                	String tag1Nm = CommonUtil.readExcelCell(row.getCell(15));
                	String tag2Nm = CommonUtil.readExcelCell(row.getCell(16));
                	String tag3Nm = CommonUtil.readExcelCell(row.getCell(17));
                	String tag4Nm = CommonUtil.readExcelCell(row.getCell(18));
                	String tag5Nm = CommonUtil.readExcelCell(row.getCell(19));
                	
                	tag1Nm = tag1Nm != null ? tag1Nm.trim() : "";
                	tag2Nm = tag2Nm != null ? tag2Nm.trim() : "";
                	tag3Nm = tag3Nm != null ? tag3Nm.trim() : "";
                	tag4Nm = tag4Nm != null ? tag4Nm.trim() : "";
                	tag5Nm = tag5Nm != null ? tag5Nm.trim() : "";
                	
                	
                	String tag1 = "";
                	String tag2 = "";
                	String tag3 = "";
                	String tag4 = "";
                	String tag5 = "";
                	
                	for (CodeEntity itm : tagCodeList) {
                		if(StringUtils.isNotBlank(tag1Nm)  && itm.getCodeName().trim().equals(tag1Nm) ) {
                			tag1 = itm.getCode(); 
                		}
                		
                		if(StringUtils.isNotBlank(tag2Nm)  &&itm.getCodeName().trim().equals(tag2Nm) ) {
                			tag2 = itm.getCode(); 
                		}
                		
                		if(StringUtils.isNotBlank(tag3Nm)  &&itm.getCodeName().trim().equals(tag3Nm) ) {
                			tag3 = itm.getCode(); 
                		}
                		
                		if(StringUtils.isNotBlank(tag4Nm)  &&itm.getCodeName().trim().equals(tag4Nm) ) {
                			tag4 = itm.getCode(); 
                		}
                		
                		if(StringUtils.isNotBlank(tag5Nm)  &&itm.getCodeName().trim().equals(tag5Nm) ) {
                			tag5 = itm.getCode(); 
                		}
					}
                	
                	
                	
                	
                	if (!StringUtils.isEmpty(planId.trim()) ) {
                		item.setId(Long.parseLong(planId));
                		
                        item.setPlanTag1(tag1);
                        item.setPlanTag2(tag2);
                        item.setPlanTag3(tag3);
                        item.setPlanTag4(tag4);
                        item.setPlanTag5(tag5);
                        
                        long cnt = mapper.updataPlanTag(item);
                        if(cnt>0) {
                        	saveCnt = saveCnt + cnt;	
                        } else {
                        	failCnt = failCnt +1;
                        	failUUid.add(row.getCell(0).getStringCellValue());
                        }
                	} else {
                		failCnt = failCnt +1;
                    	failUUid.add("uuid or plancode is empty");
                	}
                } else {
                	failCnt = failCnt +1;
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