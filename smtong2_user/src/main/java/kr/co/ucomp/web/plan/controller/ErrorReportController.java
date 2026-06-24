package kr.co.ucomp.web.plan.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import kr.co.ucomp.web.plan.dto.ErrorReportDto;
import kr.co.ucomp.web.plan.entity.ErrorReportEntity;
import kr.co.ucomp.web.plan.service.ErrorReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 김재희
 * @since 2024.12.20
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/pbm/error-report")
@Slf4j
public class ErrorReportController {

    @Autowired private ErrorReportService errorReportService;

    @ResponseBody
    @PostMapping(value = "/list")
    public ResponseEntity<CustomApiResponse<List<ErrorReportEntity>>> getReviewList(
            HttpServletRequest request,
            @RequestBody ErrorReportDto param
    ) throws IOException {

        try{

            List<ErrorReportEntity> resultList = errorReportService.errorReportList(param);

            if (resultList.isEmpty()) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND);
            }

            int cnt = resultList.size();

            return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }

    }
    
    @ResponseBody
    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<ErrorReportEntity>> getErrorReport(
            HttpServletRequest request,
            @PathVariable("id") int id
    ) throws IOException {

        try{

            ErrorReportEntity result = errorReportService.errorReport(id);
            
            

            if (result == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND);
            }

            return CustomApiResponse.success(ResponseCode.OK, result);

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }

    }
    
    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<ErrorReportEntity>> insertErrorReport(
            HttpServletRequest request,
            @RequestBody ErrorReportEntity param
    ) throws IOException {

        try{
        	HttpSession session = request.getSession(false);
        	UserDTO userInfoMap = (UserDTO)session.getAttribute("userInfo");
        	
        	System.out.println(userInfoMap);
        	
        	param.setCreateId((int) userInfoMap.getId());
        	errorReportService.insertErrorReport(param);
            return CustomApiResponse.success(ResponseCode.CREATED, param);

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }

    }

    @ResponseBody
    @PostMapping("/update")
    public ResponseEntity<CustomApiResponse<ErrorReportEntity>> updateErrorReport(
            HttpServletRequest request,
            @RequestBody ErrorReportEntity param
    ) throws IOException {

        try{
            errorReportService.updateErrorReport(param);
            return CustomApiResponse.success(ResponseCode.OK, param);

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }

    }

    @ResponseBody
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomApiResponse<String>> deleteErrorReport(
            HttpServletRequest request,
            @PathVariable("id") int id
    ) throws IOException {

        try{
            errorReportService.deleteErrorReport(id);
            return CustomApiResponse.success(ResponseCode.OK, "삭제 완료");

        } catch (IllegalArgumentException e) {

            return CustomApiResponse.error(ResponseCode.BAD_REQUEST);

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);

        }

    }

}
