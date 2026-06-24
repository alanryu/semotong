package kr.co.ucomp.web.svc.banner.contoroller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.common.util.FileService;
import kr.co.ucomp.web.mbm.dto.AdminUserDto;
import kr.co.ucomp.web.svc.banner.dto.PopupSearchDto;
import kr.co.ucomp.web.svc.banner.entity.PopupEntity;
import kr.co.ucomp.web.svc.banner.service.PopupService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/svc/mainbanner")
@Slf4j
public class PopupController {

  @Autowired
  private PopupService popupService;

  @Autowired
  private FileService fileService;

  @GetMapping("/popupList")
  public String popupList(HttpServletRequest request, Model model) {

    return "pages/svc/mainbanner/popupList";
  }

  @PostMapping("/popupSearch")
  public ResponseEntity<CustomApiResponse<List<PopupEntity>>> popupSearch(HttpServletRequest request,
      @RequestBody PopupSearchDto popupSearchDto) throws IOException {
    try {
      log.debug("팝업 목록 조회 요청 - 검색조건: {}", popupSearchDto);

      // 페이징 기본값 설정
      if (popupSearchDto.getPage() <= 0) {
        popupSearchDto.setPage(1);
      }
      if (popupSearchDto.getRecordSize() <= 0) {
        popupSearchDto.setRecordSize(10);
      }

      // 팝업 목록 조회
      List<PopupEntity> popupList = popupService.list(popupSearchDto);

      // 전체 개수 조회
      long totalCount = popupService.listCount(popupSearchDto);

      return CustomApiResponse.success(ResponseCode.OK, totalCount, popupList);

    } catch (Exception e) {
      e.printStackTrace();
      return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
  }

  // 순서 이동 API 추가
  @PostMapping("/popupMoveOrder")
  public ResponseEntity<CustomApiResponse<String>> popupMoveOrder(HttpServletRequest request,
      @RequestBody Map<String, String> requestMap) {
    try {
      String id = requestMap.get("id");
      String direction = requestMap.get("direction");

      log.debug("팝업 순서 이동 요청 - ID: {}, 방향: {}", id, direction);

      if (id == null || direction == null) {
        return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "필수 파라미터가 누락되었습니다.");
      }

      Long popupId = Long.parseLong(id);
      boolean result = popupService.moveOrder(popupId, direction);

      if (result) {
        return CustomApiResponse.success(ResponseCode.OK, "순서가 변경되었습니다.");
      } else {
        return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "순서 변경에 실패했습니다.");
      }
    } catch (NumberFormatException e) {
      log.error("잘못된 ID 형식: {}", requestMap.get("id"));
      return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "잘못된 ID 형식입니다.");
    } catch (Exception e) {
      log.error("팝업 순서 이동 중 오류 발생", e);
      return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "순서 변경 중 오류가 발생했습니다.");
    }
  }

  @PostMapping("/popupDelete")
  public ResponseEntity<CustomApiResponse<String>> popupDelete(HttpServletRequest request,
      @RequestBody Map<String, String> requestMap) {
    try {
      String id = requestMap.get("id");

      log.info("팝업 삭제 요청 - ID: {}", id);

      if (id == null || id.trim().isEmpty()) {
        return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "삭제할 팝업 ID가 필요합니다.");
      }

      Long popupId = Long.parseLong(id);

      // 팝업 존재 여부 확인
      PopupEntity existingPopup = popupService.getDetail(popupId);
      if (existingPopup == null) {
        return CustomApiResponse.error(ResponseCode.NOT_FOUND, "삭제할 팝업을 찾을 수 없습니다.");
      }

      // 현재 활성화된 팝업인지 확인 (선택사항)
      if (isActivePopup(existingPopup)) {
        return CustomApiResponse.success(ResponseCode.OK,
            "현재 노출 중인 팝업은 삭제할 수 없습니다. 먼저 팝업을 비활성화해주세요.");
      }

      long result = popupService.delete(popupId);

      if (result > 0) {
        log.info("팝업 삭제 완료 - ID: {}, 팝업명: {}", popupId, existingPopup.getName());
        return CustomApiResponse.success(ResponseCode.OK, "팝업이 성공적으로 삭제되었습니다.");
      } else {
        return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "팝업 삭제에 실패했습니다.");
      }

    } catch (NumberFormatException e) {
      log.error("잘못된 ID 형식: {}", requestMap.get("id"));
      return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "잘못된 ID 형식입니다.");
    } catch (Exception e) {
      log.error("팝업 삭제 중 오류 발생 - ID: {}", requestMap.get("id"), e);
      return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "팝업 삭제 중 오류가 발생했습니다.");
    }
  }

  private boolean isActivePopup(PopupEntity popup) {
    LocalDateTime now = LocalDateTime.now();

    // 사용여부가 N이면 비활성
    if (!"Y".equals(popup.getUseYn())) {
      return false;
    }

    // 현재 시간이 시작일과 종료일 사이에 있는지 확인
    return now.isAfter(popup.getStartDate()) && now.isBefore(popup.getEndDate());
  }

  @GetMapping("/popupForm")
  public String popupForm(HttpServletRequest request, Model model,
      @RequestParam(value = "id", required = false) Long id) {

    log.debug("팝업 폼 페이지 요청 - ID: {}", id);

    // 수정인 경우 기존 데이터 조회
    if (id != null) {
      try {
        PopupEntity entity = popupService.getDetail(id);
        if (entity != null) {
          model.addAttribute("entity", entity);
          log.debug("팝업 수정 폼 - 기존 데이터 조회 완료: {}", entity.getName());
        } else {
          log.warn("팝업 수정 폼 - 존재하지 않는 ID: {}", id);
          // 존재하지 않는 경우 목록으로 리다이렉트
          return "redirect:/svc/mainbanner/popupList";
        }
      } catch (Exception e) {
        log.error("팝업 상세 조회 중 오류 발생 - ID: {}", id, e);
        return "redirect:/svc/mainbanner/popupList";
      }
    }

    return "pages/svc/mainbanner/popupForm";
  }

  @PostMapping("/popupUpdate")
  public ResponseEntity<CustomApiResponse<String>> popupUpdate(
      HttpServletRequest request,
      PopupEntity popupEntity,
      @RequestParam(value = "imagePc", required = false) MultipartFile imagePc,
      @RequestParam(value = "imageMo", required = false) MultipartFile imageMo) {

    try {
      log.debug("팝업 수정 요청 - ID: {}, 이름: {}", popupEntity.getId(), popupEntity.getName());

      if (popupEntity.getId() == null) {
        return CustomApiResponse.error(ResponseCode.OK, "수정할 팝업 ID가 필요합니다.");
      }

      // 기존 데이터 조회
      PopupEntity existingPopup = popupService.getDetail(popupEntity.getId());
      if (existingPopup == null) {
        return CustomApiResponse.error(ResponseCode.OK, "수정할 팝업을 찾을 수 없습니다.");
      }

      // 필수 값 검증
      if (popupEntity.getName() == null || popupEntity.getName().trim().isEmpty()) {
        return CustomApiResponse.error(ResponseCode.OK, "항목명을 입력해주세요.");
      }

      if (popupEntity.getStartDate() == null || popupEntity.getEndDate() == null) {
        return CustomApiResponse.error(ResponseCode.OK, "게시기간을 입력해주세요.");
      }

      // 게시기간 유효성 검증
      if (popupEntity.getStartDate().isAfter(popupEntity.getEndDate())) {
        return CustomApiResponse.error(ResponseCode.OK, "게시 종료일은 시작일보다 늦어야 합니다.");
      }

      // 새로운 PC 이미지가 업로드된 경우
      if (imagePc != null && !imagePc.isEmpty()) {

        if (!StringUtils.isEmpty(imagePc.getOriginalFilename())) {
          String fileName = fileService.FileUpload("banner", imagePc);

          ObjectMapper mapper = new ObjectMapper();

          Map<String, Object> map = mapper.readValue(fileName, Map.class);

          if (map.get("fileUrl") != null) {
            fileName = (String) map.get("fileUrl");
          }

          popupEntity.setImagePcUrl(fileName);
        }
      } else {
        popupEntity.setImagePcUrl(existingPopup.getImagePcUrl());
      }

      // 새로운 모바일 이미지가 업로드된 경우
      if (imageMo != null && !imageMo.isEmpty()) {
        if (!StringUtils.isEmpty(imageMo.getOriginalFilename())) {
          String fileName = fileService.FileUpload("banner", imageMo);
          ObjectMapper mapper = new ObjectMapper();

          Map<String, Object> map = mapper.readValue(fileName, Map.class);

          if (map.get("fileUrl") != null) {
            fileName = (String) map.get("fileUrl");
          }
          popupEntity.setImageMobileUrl(fileName);
        }
      } else {
        popupEntity.setImageMobileUrl(existingPopup.getImageMobileUrl());
      }

      // 엔티티 설정
      popupEntity.setUseYn(existingPopup.getUseYn());
      popupEntity.setSort(existingPopup.getSort());

      HttpSession session = request.getSession();
      AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");

      popupEntity.setModifiedId(adminInfo.getId());

      long result = popupService.update(popupEntity);

      if (result > 0) {
        log.info("팝업 수정 완료 - ID: {}, 이름: {}", popupEntity.getId(), popupEntity.getName());
        return CustomApiResponse.success(ResponseCode.OK, "팝업이 성공적으로 수정되었습니다.");
      } else {
        return CustomApiResponse.error(ResponseCode.OK, "팝업 수정에 실패했습니다.");
      }

    } catch (Exception e) {
      log.error("팝업 수정 중 오류 발생 - ID: {}", popupEntity.getId(), e);
      return CustomApiResponse.error(ResponseCode.OK, "팝업 수정 중 오류가 발생했습니다.");
    }
  }

  @PostMapping("/popupInsert")
  public ResponseEntity<CustomApiResponse<String>> popupInsert(
      HttpServletRequest request,
      PopupEntity popupEntity,
      @RequestParam(value = "imagePc", required = false) MultipartFile imagePc,
      @RequestParam(value = "imageMo", required = false) MultipartFile imageMo) {

    try {
      log.debug("팝업 등록 요청 - ID: {}, 이름: {}", popupEntity.getId(), popupEntity.getName());

      // 필수 값 검증
      if (popupEntity.getName() == null || popupEntity.getName().trim().isEmpty()) {
        return CustomApiResponse.error(ResponseCode.OK, "항목명을 입력해주세요.");
      }

      if (popupEntity.getStartDate() == null || popupEntity.getEndDate() == null) {
        return CustomApiResponse.error(ResponseCode.OK, "게시기간을 입력해주세요.");
      }

      // 게시기간 유효성 검증
      if (popupEntity.getStartDate().isAfter(popupEntity.getEndDate())) {
        return CustomApiResponse.error(ResponseCode.OK, "게시 종료일은 시작일보다 늦어야 합니다.");
      }

      // 새로운 PC 이미지가 업로드된 경우
      if (imagePc != null && !imagePc.isEmpty()) {

        if (!StringUtils.isEmpty(imagePc.getOriginalFilename())) {
          String fileName = fileService.FileUpload("banner", imagePc);

          ObjectMapper mapper = new ObjectMapper();

          Map<String, Object> map = mapper.readValue(fileName, Map.class);

          if (map.get("fileUrl") != null) {
            fileName = (String) map.get("fileUrl");
          }
          popupEntity.setImagePcUrl(fileName);
        }

      }

      // 새로운 모바일 이미지가 업로드된 경우
      if (imageMo != null && !imageMo.isEmpty()) {
        if (!StringUtils.isEmpty(imageMo.getOriginalFilename())) {
          String fileName = fileService.FileUpload("banner", imageMo);
          ObjectMapper mapper = new ObjectMapper();

          Map<String, Object> map = mapper.readValue(fileName, Map.class);

          if (map.get("fileUrl") != null) {
            fileName = (String) map.get("fileUrl");
          }
          popupEntity.setImageMobileUrl(fileName);
        }
      }

      // 엔티티 설정
      popupEntity.setUseYn("Y");

      // 처음에 0으로 넣는다. 이후 정렬 쿼리 한번 더 실행함
      popupEntity.setSort(0);

      HttpSession session = request.getSession();
      AdminUserDto adminInfo = (AdminUserDto) session.getAttribute("loginUser");

      popupEntity.setCreateId(adminInfo.getId());

      popupEntity.setTarget("_blank");

      long result = popupService.create(popupEntity);

      if (result > 0) {
        log.info("팝업 등록 완료 - ID: {}, 이름: {}", popupEntity.getId(), popupEntity.getName());
        popupService.reorderSort();
        return CustomApiResponse.success(ResponseCode.OK, "팝업이 성공적으로 등록되었습니다.");
      } else {
        return CustomApiResponse.error(ResponseCode.OK, "팝업 등록에 실패했습니다.");
      }

    } catch (Exception e) {
      log.error("팝업 등록 중 오류 발생 - ID: {}", popupEntity.getId(), e);
      return CustomApiResponse.error(ResponseCode.OK, "팝업 등록 중 오류가 발생했습니다.");
    }
  }

}
