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
import org.springframework.web.bind.annotation.GetMapping;
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
import kr.co.ucomp.web.pmb.dto.PlanBenefitSearchDto;
import kr.co.ucomp.web.pmb.entity.PlanBenefitEntity;
import kr.co.ucomp.web.pmb.entity.PlanBenefitMappingEntity;
import kr.co.ucomp.web.pmb.service.PlanBenefitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author 임경한
 * @since 2025.04.25
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/pbm/benefit")
@Slf4j
@PreAuthorize("hasAnyAuthority('ALL', 'PROD_MNG')")
public class BenefitController {

  @Autowired
  private CompanyListService companyListService;

  @Autowired
  private PlanBenefitService benefitService;

  @Autowired
  private FileService fileService;

  /**
   * 혜택 리스트 화면
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

    return "pages/pmb/benefit/list";
  }

  @ResponseBody
  @PostMapping(value = "/ajaxList")
  public ResponseEntity<CustomApiResponse<List<PlanBenefitEntity>>> ajaxList(HttpServletRequest request,
      @RequestBody PlanBenefitSearchDto param) throws IOException {

    try {

      long resulCnt = benefitService.infolistCount(param);

      List<PlanBenefitEntity> resultList = new ArrayList<PlanBenefitEntity>();
      if (resulCnt > 0) {
        resultList = benefitService.infolist(param);
      }

      return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);

    } catch (Exception e) {

      e.printStackTrace();
      return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

    }

  }

  @ResponseBody
  @PostMapping(value = "/ajaxCopyBenefit")
  public ResponseEntity<CustomApiResponse<List<PlanBenefitEntity>>> ajaxCopyBenefit(
      HttpServletRequest request,
      @RequestBody List<Integer> param) throws IOException {

    try {
      List<PlanBenefitEntity> copiedBenefits = new ArrayList<>();

      // 세션에서 사용자 정보 가져오기
      HttpSession session = request.getSession();
      AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");

      // 각 benefitId에 대해 복사 작업 수행
      param.forEach(benefitId -> {
        log.info("Copying benefit ID: {}", benefitId);

        // 1. 기존 혜택 정보 조회
        PlanBenefitEntity originalBenefit = benefitService.infoDetail(benefitId);

        if (originalBenefit != null) {
          // 2. 새로운 혜택 엔티티 생성 (복사)
          PlanBenefitEntity copiedBenefit = new PlanBenefitEntity();

          // 기존 데이터 복사 (ID는 제외)
          copiedBenefit.setMajorCategoryId(originalBenefit.getMajorCategoryId());
          copiedBenefit.setMinorCategoryId(originalBenefit.getMinorCategoryId());
          copiedBenefit.setTitle(originalBenefit.getTitle() + "_복사"); // 복사본임을 표시
          copiedBenefit.setContent(originalBenefit.getContent());
          copiedBenefit.setDisplayYn(originalBenefit.getDisplayYn());
          copiedBenefit.setProvider(originalBenefit.getProvider());
          copiedBenefit.setName(originalBenefit.getName());
          copiedBenefit.setMemo(originalBenefit.getMemo());
          copiedBenefit.setBenefitUrl(originalBenefit.getBenefitUrl());
          copiedBenefit.setBenefitContent(originalBenefit.getBenefitContent());
          copiedBenefit.setDisplayPeriodStart(originalBenefit.getDisplayPeriodStart());
          copiedBenefit.setDisplayPeriodEnd(originalBenefit.getDisplayPeriodEnd());
          copiedBenefit.setIcon(originalBenefit.getIcon());
          copiedBenefit.setBenefitImage(originalBenefit.getBenefitImage());

          // 생성자 정보 설정
          copiedBenefit.setCreateId(adminInfo.getId());

          // 3. DB에 새로운 혜택 저장
          benefitService.createInfo(copiedBenefit);

          // // 4. 기존 혜택과 연결된 요금제 매핑 정보 조회
          // List<PlanBenefitMappingEntity> originalMappings =
          // benefitService.mapDetailBenefit(benefitId);

          // 5. 매핑 정보도 복사
          int newBenefitId = copiedBenefit.getId();

          // for (int i = 0; i < originalMappings.size(); i++) {
          // PlanBenefitMappingEntity originalMapping = originalMappings.get(i);

          // PlanBenefitMappingEntity newMapping = new PlanBenefitMappingEntity();
          // newMapping.setPlanListId(originalMapping.getPlanListId());
          // newMapping.setPlanBenefitsId(newBenefitId);
          // newMapping.setOrderNo(originalMapping.getOrderNo());
          // newMapping.setCreateId(adminInfo.getId());

          // benefitService.createmap(newMapping);
          // }

          // 복사된 혜택을 리스트에 추가
          copiedBenefits.add(copiedBenefit);

          log.info("Successfully copied benefit ID {} to new ID {}", benefitId, newBenefitId);
        } else {
          log.warn("Benefit ID {} not found", benefitId);
        }
      });

      return CustomApiResponse.success(ResponseCode.OK, copiedBenefits);

    } catch (Exception e) {
      log.error("Error copying benefits", e);
      e.printStackTrace();
      return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }

  }

  @GetMapping("/insert")
  public String insert(HttpServletRequest request, Model model) {

    PlanBenefitEntity result = new PlanBenefitEntity();
    result.setId(0);
    model.addAttribute("result", result);

    return "pages/pmb/benefit/edit";
  }

  @GetMapping("/update/{id}")
  public String update(HttpServletRequest request, Model model, @PathVariable("id") Integer id) {

    PlanBenefitEntity result = benefitService.infoDetail(id);
    List<PlanBenefitMappingEntity> planList = benefitService.mapDetailBenefit(id);

    log.info("Entity: {}", ReflectionToStringBuilder.toString(result,
        ToStringStyle.MULTI_LINE_STYLE));

    model.addAttribute("result", result);
    model.addAttribute("planList", planList);

    return "pages/pmb/benefit/edit";
  }

  @GetMapping("/copy/{id}")
  public String copy(HttpServletRequest request, Model model, @PathVariable("id") Integer id) {

    PlanBenefitEntity result = benefitService.infoDetail(id);
    List<PlanBenefitMappingEntity> planList = benefitService.mapDetailBenefit(id);

    log.info("Entity: {}", ReflectionToStringBuilder.toString(result,
        ToStringStyle.MULTI_LINE_STYLE));

    model.addAttribute("result", result);
    model.addAttribute("planList", planList);
    model.addAttribute("isCopy", true);

    return "pages/pmb/benefit/edit";
  }

  @PostMapping("/insupdProc")
  public ResponseEntity<CustomApiResponse<PlanBenefitEntity>> insupdProc(MultipartHttpServletRequest request,
      @RequestParam Map<String, Object> obj) {

    try {

      int id = 0;
      if (!StringUtils.isEmpty(obj.get("id").toString())) {
        id = Integer.parseInt(obj.get("id").toString());
      }

      PlanBenefitEntity entity = benefitService.infoDetail(id);
      if (entity == null) {
        entity = new PlanBenefitEntity();
      }

      obj.forEach((key, value) -> {
        log.info("Key: {}, Value: {}, Value Type: {}", key, value,
            (value != null ? value.getClass().getName() : "null"));
      });

      entity.setId(id);
      entity.setMajorCategoryId(obj.get("majorCategoryId").toString());
      entity.setMinorCategoryId(obj.get("minorCategoryId").toString());
      entity.setTitle(obj.get("title").toString());
      entity.setContent(obj.get("content").toString());
      entity.setDisplayYn("Y");

      entity.setProvider(obj.get("provider").toString());
      entity.setName(obj.get("name").toString());

      entity.setMemo(obj.get("memo").toString());
      entity.setBenefitUrl(obj.get("benefitUrl").toString());
      entity.setBenefitContent(obj.get("benefitContent").toString());
      entity.setDisplayPeriodStart(obj.get("displayPeriodStart").toString());

      String dateTimeStr = obj.get("displayPeriodEnd").toString();
      String dateOnly = dateTimeStr.substring(0, 10);
      String endOfDay = dateOnly + " 23:59:59.000";
      entity.setDisplayPeriodEnd(endOfDay);

      /* 세션 user get */
      HttpSession session = request.getSession();
      AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");
      entity.setCreateId(adminInfo.getId());

      MultipartFile benefitThumbnail = request.getFile("benefitThumbnailU");
      MultipartFile benefitImage = request.getFile("benefitImage");

      String benefitIconFilePath = obj.get("benefitThumbnail").toString();

      // 혜택 아이콘 업로드
      if (!StringUtils.isEmpty(benefitThumbnail.getOriginalFilename())) {
        String fileUpload = fileService.FileUpload("benefit", benefitThumbnail);
        log.info("혜택 아이콘 업로드 결과 : {}", fileUpload);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> map = mapper.readValue(fileUpload, Map.class);

        if (map.get("fileUrl") != null) {
          benefitIconFilePath = (String) map.get("fileUrl");
        }

        entity.setIcon(benefitIconFilePath);
      }

      // 혜택 아이콘 없을때 기본 아이콘
      if (benefitIconFilePath.isEmpty()) {

        benefitIconFilePath = Paths.get("/uploads", "benefit", "benefit-default-icon.png").toString().replace("\\",
            "/");
        entity.setIcon(benefitIconFilePath);
      }

      // 혜택 상세이미지 업로드
      if (!StringUtils.isEmpty(benefitImage.getOriginalFilename())) {
        String fileUpload = fileService.FileUpload("benefit", benefitImage);
        log.info("혜택 상세이미지 업로드 결과 : {}", fileUpload);

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> map = mapper.readValue(fileUpload, Map.class);
        String benefitImageFilePath = "";
        if (map.get("fileUrl") != null) {
          benefitImageFilePath = (String) map.get("fileUrl");
        }

        entity.setBenefitImage(benefitImageFilePath);
      }

      /* 수정 */
      if (id > 0) {
        entity.setModifiedId(adminInfo.getId());
        long resStat = benefitService.updateInfo(entity);
        benefitService.deletemapBenefit(entity.getId());

      } else {
        benefitService.createInfo(entity);
      }

      int planBenefitId = entity.getId();

      String planIdList = obj.get("planIdList").toString();

      planIdList = planIdList.replace("[", "").replace("]", "").replace("\"", "");

      if (planIdList.length() > 0) {
        String[] planIds = planIdList.split(",");

        for (int i = 0; i < planIds.length; i++) {
          log.info(planIds.length + "");

          PlanBenefitMappingEntity planBenefitMappingEntity = new PlanBenefitMappingEntity();
          planBenefitMappingEntity.setPlanListId(Integer.parseInt(planIds[i]));
          planBenefitMappingEntity.setPlanBenefitsId(planBenefitId);
          planBenefitMappingEntity.setOrderNo((i + 1));
          planBenefitMappingEntity.setCreateId(adminInfo.getId());

          benefitService.createmap(planBenefitMappingEntity);
        }
      }

      return CustomApiResponse.success(ResponseCode.OK, entity);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

  }

  @GetMapping("/bulkList")
  public String bulkList(HttpServletRequest request, Model model) {

    return "pages/pmb/benefit/bulkList";

  }

  @GetMapping("/bulkDetail/{id}")
  public ResponseEntity<CustomApiResponse<List<PlanBenefitMappingEntity>>> bulkDetail(HttpServletRequest request,
      Model model,
      @PathVariable("id") Integer id) {

    List<PlanBenefitMappingEntity> planList = benefitService.mapDetailBenefit(id);

    return CustomApiResponse.success(ResponseCode.OK, planList);

  }

  @Transactional
  @PostMapping("/bulkInsert")
  public ResponseEntity<CustomApiResponse<PlanBenefitMappingEntity>> bulkInsert(@RequestBody Map<String, Object> obj,
      HttpServletRequest request) {

    List<String> plans = (List<String>) obj.get("plans");
    List<String> benefits = (List<String>) obj.get("benefits");
    String action = obj.get("action").toString();
    HttpSession session = request.getSession();
    AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");

    try {
      if (action.equals("unregister")) {
        benefitService.deletemapBenefit(Integer.parseInt(benefits.get(0)));
      }

      for (int i = 0; i < plans.size(); i++) {

        PlanBenefitMappingEntity planBenefitMappingEntity = new PlanBenefitMappingEntity();
        planBenefitMappingEntity.setPlanListId(Integer.parseInt(plans.get(i)));

        for (int j = 0; j < benefits.size(); j++) {
          planBenefitMappingEntity.setPlanBenefitsId(Integer.parseInt(benefits.get(j)));
          planBenefitMappingEntity.setOrderNo((j + 1));
          planBenefitMappingEntity.setCreateId(adminInfo.getId());
          benefitService.createmap(planBenefitMappingEntity);
        }

      }

      PlanBenefitMappingEntity entity = new PlanBenefitMappingEntity();

      return CustomApiResponse.success(ResponseCode.OK, entity);
    } catch (Exception e) {
      return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "중복된 요금제가 있습니다.");
    }

  }

  @Transactional
  @PostMapping("/deleteBenefit")
  public ResponseEntity<CustomApiResponse<String>> deleteBenefit(HttpServletRequest request,
      @RequestBody List<Integer> benefits) {
    try {

      HttpSession session = request.getSession();
      AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");

      for (int benefit : benefits) {
        benefitService.deleteBenefit(benefit, adminInfo.getId());
      }

      return CustomApiResponse.success(ResponseCode.OK, "success");

    } catch (Exception e) {
      return CustomApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage());
    }

  }

}
