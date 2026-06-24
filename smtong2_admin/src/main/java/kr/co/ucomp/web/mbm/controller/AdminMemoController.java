package kr.co.ucomp.web.mbm.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.mbm.dto.AdminMemoDto;
import kr.co.ucomp.web.mbm.entity.AdminMemoEntity;
import kr.co.ucomp.web.mbm.service.AdminMemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/mbm/admin-memo")
@Slf4j
public class AdminMemoController {

    @Autowired private AdminMemoService adminMemoService;

    @PostMapping(value = "/list")
    public ResponseEntity<CustomApiResponse<List<AdminMemoEntity>>> getAdminMemoList(
            HttpServletRequest request,
            @RequestBody AdminMemoDto param
    ) throws IOException {

        try {

            List<AdminMemoEntity> resultList = adminMemoService.adminMemoList(param);

            if (resultList.isEmpty()) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND,"데이터를 찾을 수 없습니다.");
            }

            int cnt = resultList.size();

            return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<AdminMemoEntity>> getAdminMemo(
            HttpServletRequest request,
            @PathVariable("id") long id
    ) throws IOException {

        try {

            AdminMemoEntity result = adminMemoService.adminMemo(id);

            if (result == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND,"데이터를 찾을 수 없습니다.");
            }

            return CustomApiResponse.success(ResponseCode.OK, result);

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<AdminMemoEntity>> insertAdminMemo(
            HttpServletRequest request,
            @RequestBody AdminMemoEntity param
    ) throws IOException {

        try {

            adminMemoService.insertAdminMemo(param);

            return CustomApiResponse.success(ResponseCode.CREATED, param);

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }



    @PostMapping("/update")
    public ResponseEntity<CustomApiResponse<AdminMemoEntity>> updateAdminMemo(
            HttpServletRequest request,
            @RequestBody AdminMemoEntity param
    ) throws IOException {

        try {

            adminMemoService.updateAdminMemo(param);

            return CustomApiResponse.success(ResponseCode.OK, param);

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> deleteAdminMemo(
            HttpServletRequest request,
            @PathVariable("id") long id
    ) throws IOException {

        try {

            adminMemoService.deleteAdminMemo(id);

            return CustomApiResponse.success(ResponseCode.OK, "삭제 성공");

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
