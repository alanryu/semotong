package kr.co.ucomp.web.stl.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.ApiExampleHttp;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.stl.dto.SettlementMngSearchDto;
import kr.co.ucomp.web.stl.dto.SettlementMngUploadDto;
import kr.co.ucomp.web.stl.entity.SettlementMSTEntity;
import kr.co.ucomp.web.stl.entity.SettlementMngEntity;
import kr.co.ucomp.web.stl.service.SettlementMngService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/stl/settlement_mng") 
@Slf4j
public class SettlementMngController {
	
	@Autowired SettlementMngService service;
	
	
	  /**
     * 2024-12-18 (수) 백신의
     * - 정산 목록
     *
     * @param searchRequest               서치 params
     * @param List<SettlementMngEntity> 정산 조회 리스트
     */
    @PostMapping("/listMst")
    public ResponseEntity<CustomApiResponse<List<SettlementMSTEntity>>> settlementMstList(HttpServletResponse response,
    		@RequestBody SettlementMngSearchDto searchRequest
    		) throws IOException {
    	
    	List<SettlementMSTEntity> list = null;
    	
    	try {
    		list = service.getListSettlementMst(searchRequest);
    		return CustomApiResponse.success(ResponseCode.OK,list.size(),list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "settlementMstList: " + e.getMessage());
		}
    }
    
	
	
    /**
     * 2024-12-18 (수) 백신의
     * - 정산 목록
     *
     * @param searchRequest               서치 params
     * @param List<SettlementMngEntity> 정산 조회 리스트
     */
    @PostMapping("/list")
    public ResponseEntity<CustomApiResponse<List<SettlementMngEntity>>> settlementList(HttpServletResponse response,
    		@RequestBody SettlementMngSearchDto searchRequest
    		) throws IOException {
    	
    	List<SettlementMngEntity> list = null;
    	Integer listCount = 0;
    	try {
    		list = service.getListSettlement(searchRequest);
    		if(list != null ) listCount = list.size();
    		return CustomApiResponse.success(ResponseCode.OK,listCount, list);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "settlementList: " + e.getMessage());
		}
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 정산 상세 조회
     *
     * @param searchRequest               서치 params
     * @param SettlementMngEntity 정산 상세 정보
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<SettlementMngEntity>> getSettlement(HttpServletResponse response,
    		@PathVariable("id") int searchId
    		) throws IOException {
    	
    	try {
    		SettlementMngEntity info = service.getSettlement(searchId);
    		if (info == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND, "정산이 존재하지 않습니다.");
            }
    		return CustomApiResponse.success(ResponseCode.OK, info);
    	} catch (Exception e) {
    		return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getSettlement: " + e.getMessage());
		}
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 정산 신규 입력
     *
     * @param record 정산 정보
     */
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<SettlementMngEntity>> createSettlement(HttpServletResponse response,
    		@RequestBody SettlementMngEntity record
    		) throws IOException {
    	
    	try {
    		service.create(record);
    		return CustomApiResponse.success(ResponseCode.CREATED, record);
        } catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "createSettlement: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "createSettlement: " + e.getMessage());
        }
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 정산 업데이트
     *
     * @param record 정산 정보
     */
    @PostMapping("/update")
    public ResponseEntity<CustomApiResponse<SettlementMngEntity>> updateSettlement(HttpServletResponse response,
    		@RequestBody SettlementMngEntity record
    		) throws IOException {
    	try {
    		service.update(record);
    		return CustomApiResponse.success(ResponseCode.OK, record);
    	} catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "updateSettlement: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "updateSettlement: " + e.getMessage());
        }
    }
    
    /**
     * 2024-12-18 (수) 백신의
     * - 정산 삭제
     *
     * @param delId 삭제할 정산 ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> deleteSettlement(HttpServletResponse response,
			@PathVariable("id") int delId
    		) throws IOException {
    	
    	try {
    		service.delSettlement(delId);
    		return CustomApiResponse.success(ResponseCode.OK, "delete success");
    	} catch (IllegalArgumentException e) {
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "deleteSettlement: " + e.getMessage());
        } catch (Exception e) {
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "deleteSettlement: " + e.getMessage());
        }
    }

    @PostMapping(value="/excel/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomApiResponse<Map<String,Object>>> uploadSettlementData(
            HttpServletRequest request,
            @RequestPart(value="file", required = true) MultipartFile file,
            @RequestPart("sltUploadDto") SettlementMngUploadDto param
    ) throws IOException {
        log.info("ㅁㄴㅇㅁㄴㅇㅁㄴㅇ");
        Map<String,Object> uploadRes = new HashMap<String,Object>();
        
        log.info("엑셀 파일 업로드 시작");
        try {
            // 기존 데이터 삭제
            service.deleteByYearMonthAndCompany(param);
            
            // 엑셀 데이터 처리 및 업로드
            Map<String,Object> uploadCnt = service.uploadSettlementData(file, param);
            uploadRes.put("totCnt", uploadCnt.get("totCnt"));
            uploadRes.put("saveCnt", uploadCnt.get("saveCnt"));
            
            return CustomApiResponse.success(ResponseCode.OK, uploadRes);
        } catch (IllegalArgumentException e) {
            log.error("엑셀 데이터 처리 중 유효성 검사 오류: {}", e.getMessage());
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "엑셀 데이터 형식이 올바르지 않습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("엑셀 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "엑셀 파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
