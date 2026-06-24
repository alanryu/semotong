package kr.co.ucomp.web.pmb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.pmb.dto.PlanCateMngDto;
import kr.co.ucomp.web.pmb.dto.SearchPlanDto;
import kr.co.ucomp.web.pmb.entity.PlanCateMngEntity;
import kr.co.ucomp.web.pmb.entity.PlanCateTagEntity;
import kr.co.ucomp.web.pmb.entity.PlanEntity;
import kr.co.ucomp.web.pmb.service.PlanService;
import kr.co.ucomp.web.pmb.service.PlanCateMngService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/pmb/plan_cate")
@Slf4j
public class PlanCateMngController {

	@Autowired private PlanCateMngService service;
	@Autowired PlanService planService;
	
	
    @PostMapping(value = "/catelist")
    public ResponseEntity<CustomApiResponse<List<PlanCateMngEntity>>> getCateList(
            HttpServletRequest request,
            @RequestBody PlanCateMngDto param
    ) throws IOException {

        try{
        	
        	List<PlanCateMngEntity> resultList = new ArrayList<PlanCateMngEntity>(); 
        	long cnt = service.getCateListCount(param);
        	if(cnt >0) resultList = service.getCatelist(param);

            return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"getCateList : " + e.getMessage());

        }

    }
    
    
    @PostMapping(value = "/taglist")
    public ResponseEntity<CustomApiResponse<List<PlanCateMngEntity>>> getTagList(
            HttpServletRequest request,
            @RequestBody PlanCateMngDto param
    ) throws IOException {

        try{
        	
        	List<PlanCateMngEntity> resultList = new ArrayList<PlanCateMngEntity>(); 
        	resultList = service.getTaglist(param);
        	int cnt = resultList.size();

            return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"getTagList : " + e.getMessage());

        }

    }
    
    
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<PlanCateMngEntity>> create(
            HttpServletRequest request,
            @RequestBody PlanCateMngEntity param
    ) throws IOException {

        try{
        	HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");

    		param.setCreateId(loginadminInfo.getId());
    		param.setModifiedId(loginadminInfo.getId());
    		
    		String codeGroup = param.getCodeGroup();
    		String maxCode = "";
    		if("plan_tag_code_group".equals(codeGroup)) {
    			// 카테고리 추가
    			maxCode = service.maxCateCode();
    			maxCode = String.format("%02d", Integer.parseInt(maxCode) + 1);
    			
    		} else {
    			// tag 추가
    			maxCode = service.maxTagCode();
    			maxCode = String.format("%02d", Integer.parseInt(maxCode) + 1);
    		}

    		param.setCode(maxCode);
    		
        	service.create(param);
            return CustomApiResponse.success(ResponseCode.CREATED, param);

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST,"createCate : " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"createCate : " + e.getMessage());

        }

    }
    
    
    @PostMapping("/update")
    public ResponseEntity<CustomApiResponse<PlanCateMngEntity>> update(
            HttpServletRequest request,
            @RequestBody PlanCateMngEntity param
    ) throws IOException {

        try{
        	
        	HttpSession session = request.getSession();
    		AdminUserDto loginadminInfo = (AdminUserDto) session.getAttribute("loginUser");
        	param.setModifiedId(loginadminInfo.getId());
        	service.update(param);
            return CustomApiResponse.success(ResponseCode.CREATED, param);

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST,"createZzim : " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"createZzim : " + e.getMessage());

        }

    }
    
    
    @PostMapping("/delete")
    public ResponseEntity<CustomApiResponse<String>> delete(
            HttpServletRequest request,
            @RequestBody PlanCateMngEntity param
    ) throws IOException {

        try{
        	String codeGroup = param.getCodeGroup();
        	String cateCode = param.getCode();
        	
        	service.delete(param);
        	if("plan_tag_code_group".equals(codeGroup)) {
        		service.deleteTagCateMap(cateCode);	
        	}
        	
        	
            return CustomApiResponse.success(ResponseCode.OK, "해제 완료");

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST,"deleteZzim : " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"deleteZzim : " + e.getMessage());

        }

    }
    
    
    
    
    @PostMapping("/cateTagMap")
    public ResponseEntity<CustomApiResponse<Map<String,String>>> cateTagMap(
            HttpServletRequest request,
            @RequestBody List<PlanCateTagEntity> paramList
    ) throws IOException {

    	Map<String,String> res = new HashMap<String,String>();
        try{
        	String cateCode = "";
        	 for (PlanCateTagEntity param : paramList) {
        		 cateCode = param.getCateCode();
         	 }
        	 service.deleteTagCateMap(cateCode);
        	 
        	 
        	 for (PlanCateTagEntity param : paramList) {
             	String tagCode = param.getTagCode();
             	service.insertTagCateMap(cateCode, tagCode);	 
             	
        	 }
        	 
        	 res.put("proc", "sucess");
            return CustomApiResponse.success(ResponseCode.CREATED, res);

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST,"createZzim : " + e.getMessage());

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,"createZzim : " + e.getMessage());

        }

    }
    
  
    
	/**
	 * 요금제 카테고리 리스트
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String  listview( HttpServletResponse response, Model model)  {
		
		
		return "pages/pmb/planCate/list";
	}
    
    

	
}
