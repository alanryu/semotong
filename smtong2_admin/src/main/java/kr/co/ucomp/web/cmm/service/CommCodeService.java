package kr.co.ucomp.web.cmm.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.mapper.CommCodeMngMapper;
import kr.co.ucomp.web.pmb.dto.PlanSalesSearchDto;
import kr.co.ucomp.web.pmb.entity.SalesPlanEntity;

@Service("CommCodeService")
public class CommCodeService {

	@Autowired CommCodeMngMapper mapper;
    public String getCode(  String groupCode, String code ) throws IOException {
    	String resut = "";
    	try {
    		CodeEntity  info =  mapper.getCode(groupCode,code);
    		 if (info != null) {
    			 resut = info.getCodeName();
             }
    	} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return resut;
    }
    
    public Map<String,CodeEntity> getCodeList(String groupCode) throws IOException {
    	List<CodeEntity> list = new ArrayList<CodeEntity>();
    	
    	Map<String,CodeEntity> result = new HashMap<String,CodeEntity>();
    	
    	CommCodeSearchDto searchRequest = new CommCodeSearchDto();
    	searchRequest.setCodeGroup(groupCode);
    	searchRequest.setUserYn("Y");
    	searchRequest.setPage(1);
    	searchRequest.setRecordSize(999);
    	try {
    		list =  mapper.getListCode(searchRequest);	
    		 if (list != null && list.size()>0) {
    			 for (CodeEntity itm : list) {
    				 result.put(itm.getCode(), itm);
    			 }
             }
    	} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return result;
    }
    
    
    public List<CodeEntity> getCodeList2(String groupCode) throws IOException {
    	List<CodeEntity> list = new ArrayList<CodeEntity>();
    	
    	CommCodeSearchDto searchRequest = new CommCodeSearchDto();
    	searchRequest.setCodeGroup(groupCode);
    	searchRequest.setUserYn("Y");
    	searchRequest.setPage(1);
    	searchRequest.setRecordSize(999);
    	try {
    		list =  mapper.getListCode(searchRequest);	

    	} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return list;
    }
    
    
}
