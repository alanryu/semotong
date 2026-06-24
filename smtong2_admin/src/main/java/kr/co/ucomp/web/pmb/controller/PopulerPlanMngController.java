package kr.co.ucomp.web.pmb.controller;

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
import kr.co.ucomp.web.pmb.dto.PlanUpdateDto;
import kr.co.ucomp.web.pmb.service.PlanService;
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
@RequestMapping(value = "/pbm/popPlan")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'PROD_MNG')")
public class PopulerPlanMngController {

  @Autowired
  private CompanyListService companyListService;

  @Autowired PlanService service;
  
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

    return "pages/pmb/popPlan/list";
  }


	@ResponseBody
	@PostMapping(value = "/ajaxOrderInit")
	public ResponseEntity<CustomApiResponse<String>> ajaxOrderInit( HttpServletRequest request
	) throws IOException {

		try{
			long ret = service.updataPopulerOrderInit();
			return CustomApiResponse.success(ResponseCode.OK,"OK" );

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}
	
	@ResponseBody
	@PostMapping(value = "/ajaxOrder")
	public ResponseEntity<CustomApiResponse<PlanUpdateDto>> ajaxOrder(
			HttpServletRequest request,
			@RequestBody PlanUpdateDto param
	) throws IOException {

		try{

			service.updataPopulerOrder(param);
			param.setUpdateErrYn("N");
			return CustomApiResponse.success(ResponseCode.OK, param);

		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

		}

	}



}
