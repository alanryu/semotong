package kr.co.ucomp.web.svc.recomplan.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.mbm.dto.CompanyListSearchDto;
import kr.co.ucomp.web.mbm.entity.CompanyListEntity;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import kr.co.ucomp.web.svc.recomplan.service.RecomPlanMngService;
import kr.co.ucomp.web.svc.recomplan.dto.RecomPlanMngSearchDto;
import kr.co.ucomp.web.svc.recomplan.dto.RecomPlanReqDto;
import kr.co.ucomp.web.svc.recomplan.entity.RecomPlanEntity;
import kr.co.ucomp.web.svc.recomplan.entity.RecomPlanPlanListEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author 조일근
 * @since 2025.04.25
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/svc/recomPlan")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'SERVICE_MNG')")
public class RecomPlanMngController {

  @Autowired
  private CompanyListService companyListService;

  @Autowired
  private RecomPlanMngService service;

  @Autowired
  private FileService fileService;

  /**
   * 추천요금제 관리 리스트 화면
   *
   * @param response
   * @param model
   * @return
   */
  @GetMapping("/list")
  public String listview(HttpServletResponse response, Model model) {

    CompanyListSearchDto searchRequest = new CompanyListSearchDto();
    searchRequest.setSearchUseYn(1);
    List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
    model.addAttribute("compnyList", compnyList);

    return "pages/svc/recomPlan/list";
  }

  @ResponseBody
  @PostMapping(value = "/ajaxList")
  public ResponseEntity<CustomApiResponse<List<RecomPlanEntity>>> ajaxList(HttpServletRequest request,
      @RequestBody RecomPlanMngSearchDto param) throws IOException {

    try {

      long resulCnt = service.infolistCount(param);

      List<RecomPlanEntity> resultList = new ArrayList<RecomPlanEntity>();
      if (resulCnt > 0) {
        resultList = service.infolist(param);
      }

      return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

    } catch (Exception e) {

      e.printStackTrace();
      return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

    }

  }


  @GetMapping("/insert")
  public String insert(HttpServletRequest request, Model model) {

	  
	  CompanyListSearchDto searchRequest = new CompanyListSearchDto();
	    searchRequest.setSearchUseYn(1);
	    List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
	    model.addAttribute("compnyList", compnyList);
	    
	    
	  RecomPlanEntity result = new RecomPlanEntity();
	  result.setId(0);
	  model.addAttribute("result", result);
	  
	  

	  return "pages/svc/recomPlan/edit";
  }

  @GetMapping("/edit/{id}")
  public String update(HttpServletRequest request, Model model, @PathVariable("id") Integer id) {

	  
	  CompanyListSearchDto searchRequest = new CompanyListSearchDto();
	    searchRequest.setSearchUseYn(1);
	    List<CompanyListEntity> compnyList = companyListService.getListCompanyListWithoutLimit(searchRequest);
	    model.addAttribute("compnyList", compnyList);
	    
	    
	  RecomPlanEntity result = service.infoDetail(id);
	  
	  RecomPlanMngSearchDto parma = new RecomPlanMngSearchDto();
	  parma.setMngid(id);
	  
	  long planListCnt = service.maplistCount(parma);
	  List<RecomPlanPlanListEntity> planList = new ArrayList<RecomPlanPlanListEntity>();
	  if(planListCnt > 0 ) {
		  planList = service.maplist(parma);  
	  }
	  
	  
	  

	  log.info("Entity: {}", ReflectionToStringBuilder.toString(result, ToStringStyle.MULTI_LINE_STYLE));

	  model.addAttribute("result", result);
	  model.addAttribute("planList", planList);

	  return "pages/svc/recomPlan/edit";
  }

  
  
  @ResponseBody
  @PostMapping("/insupdProc")
  public ResponseEntity<CustomApiResponse<RecomPlanEntity>> insupdProc(MultipartHttpServletRequest request,
      @ModelAttribute RecomPlanReqDto obj) {

    try {

      RecomPlanEntity entity =  obj.getRecord();
      
      Integer id = entity.getId() == null ? -1 : entity.getId();


      /* 세션 user get */
      HttpSession session = request.getSession();
      AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");
      entity.setCreateId(adminInfo.getId());

      MultipartFile iconImage = request.getFile("fileInput1");

      String iconImageFilePath = "";
      // 추천요금제 아이콘 업로드
      if(iconImage !=null) {
    	  if (!StringUtils.isEmpty(iconImage.getOriginalFilename())) {
	        String fileUpload = fileService.FileUpload("recomPlan", iconImage);
	        log.info("추천요금제 아이콘 업로드 결과 : {}", fileUpload);

	        ObjectMapper mapper = new ObjectMapper();

	        Map<String, Object> map = mapper.readValue(fileUpload, Map.class);

	        if (map.get("fileUrl") != null) {
	          iconImageFilePath = (String) map.get("fileUrl");
	        }
	      }
    	  entity.setIconImage(iconImageFilePath);
      }
      

      
      
      // 혜택 아이콘 없을때 기본 아이콘
//      if (iconImageFilePath.isEmpty()) {
//
//    	  iconImageFilePath = Paths.get("/uploads", "benefit", "recom-default-icon.png").toString().replace("\\","/");
//      }

      

      
      
      /* 수정 */
      if (id > 0) {
        entity.setModifiedId(adminInfo.getId());
        long resStat = service.updateInfo(entity);
        service.deletemapListByMngId(entity.getId());
      } else {
        service.createInfo(entity);
      }
      
      

      int mngId = entity.getId();
      List<RecomPlanPlanListEntity> planIdList = obj.getPlanList();
      
      if (planIdList.size() > 0) {
    	  for(RecomPlanPlanListEntity itm : planIdList) {
    		  itm.setMngId(mngId);
    		  service.createmap(itm);
    	  }

      }

      return CustomApiResponse.success(ResponseCode.OK, entity);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

  }

  
  @ResponseBody
  @PostMapping("/updateData")
  public ResponseEntity<CustomApiResponse<String>> updateData(HttpServletRequest request,
		 @RequestBody RecomPlanEntity entity) {
    try {

    	HttpSession session = request.getSession();
	 AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");
     entity.setModifiedId(adminInfo.getId());
    	 
      service.updateInfo(entity);

      return CustomApiResponse.success(ResponseCode.OK, "success");

    } catch (Exception e) {
      return CustomApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage());
    }

  }
  
  
  @ResponseBody
  @DeleteMapping("/delete")
  public ResponseEntity<CustomApiResponse<String>> deleteRecomPlan(HttpServletRequest request,
		 @RequestParam("id") Integer delId) {
    try {

      service.deleteInfo(delId);

      return CustomApiResponse.success(ResponseCode.OK, "success");

    } catch (Exception e) {
      return CustomApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage());
    }

  }

}
