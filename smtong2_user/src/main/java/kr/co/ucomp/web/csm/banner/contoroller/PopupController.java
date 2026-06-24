package kr.co.ucomp.web.csm.banner.contoroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.csm.banner.entity.PopupEntity;
import kr.co.ucomp.web.csm.banner.service.PopupService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/svc/popup")
@Slf4j
public class PopupController {

  @Autowired
  private PopupService service;

  @GetMapping("/list")
  public ResponseEntity<CustomApiResponse<List<PopupEntity>>> popupList(
      HttpServletRequest request) throws IOException {

    try {

      List<PopupEntity> entity = service.popupList();

      return CustomApiResponse.success(ResponseCode.OK, entity);

    } catch (Exception e) {

      return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, " " + e.getMessage());

    }

  }
}
