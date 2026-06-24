package kr.co.ucomp.web.csm.review.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.csm.review.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.review.entity.SemotongReviewEntity;
import kr.co.ucomp.web.csm.review.service.SemotongReviewService;
import kr.co.ucomp.web.mypage.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;

/**
 *
 * @author 김재희
 * @since 2024.12.18
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/csm/review")
@Slf4j
public class SemotongReviewController {

	@Autowired private SemotongReviewService reviewService;
	  
    @ResponseBody
    @PostMapping(value = "/list")
    public ResponseEntity<CustomApiResponse<List<SemotongReviewEntity>>> getReviewList(
            HttpServletRequest request,
            @RequestBody SemotongReviewDto param
    ) throws IOException {

        try{

        	int lstCnt = reviewService.reviewListCount(param);
        	List<SemotongReviewEntity> resultList = new ArrayList<SemotongReviewEntity>();
        	if(lstCnt > 0) {
        		resultList = reviewService.reviewList(param);	
        	}
            
            return CustomApiResponse.success(ResponseCode.OK, lstCnt, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());

        }

    }
    
    
    @ResponseBody
    @PostMapping(value = "/orderReviewList")
    public ResponseEntity<CustomApiResponse<List<SemotongReviewEntity>>> orderReviewList(HttpServletRequest request
    ) throws IOException {

        try{
        	
        	List<SemotongReviewEntity> resultList = new ArrayList<SemotongReviewEntity>();
        	
        	HttpSession session = request.getSession(false);
        	UserDTO loginInfo = (UserDTO)session.getAttribute("userInfo");

       		resultList = reviewService.userOrderReviewList(loginInfo.getId());	
            
            return CustomApiResponse.success(ResponseCode.OK, resultList.size(), resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());

        }

    }

    @ResponseBody
    @GetMapping("/detail/{id}")
    public ResponseEntity<CustomApiResponse<SemotongReviewEntity>> getReview(
            HttpServletRequest request,
            @PathVariable("id") long id
    ) throws IOException {

        try{

            SemotongReviewEntity result = reviewService.review(id);

            if (result == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND, "리뷰가 존재하지 않습니다.");
            }

            return CustomApiResponse.success(ResponseCode.OK, result);

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getReview: " + e.getMessage());

        }

    }

    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<SemotongReviewEntity>> insertReview(
            HttpServletRequest request,
            @RequestBody SemotongReviewEntity param
    ) throws IOException {

        try{
            reviewService.insertReview(param);
         
            
            return CustomApiResponse.success(ResponseCode.CREATED, param);

        } catch (IllegalArgumentException e) {
        	System.out.println(e);
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "insertReview: " + e.getMessage());

        } catch (Exception e) {
        	System.out.println(e);
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "insertReview: " + e.getMessage());

        }

    }


    
    
    @ResponseBody
    @PostMapping("/update")
    public ResponseEntity<CustomApiResponse<SemotongReviewEntity>> updateReview(
            HttpServletRequest request,
            @RequestBody SemotongReviewEntity param
    ) throws IOException {
    	
        try{
            reviewService.updateReview(param);
            return CustomApiResponse.success(ResponseCode.OK, param);
        } catch (IllegalArgumentException e) {
        	System.out.println(e);
            return CustomApiResponse.error(ResponseCode.BAD_REQUEST, "updateReview: " + e.getMessage());
        } catch (Exception e) {
        	System.out.println(e);
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "updateReview: " + e.getMessage());
        }
    }


    @GetMapping("/delete/{id}")
    public String deleteReview(HttpServletResponse response,@PathVariable("id") long searchId) throws IOException {
    	
    	long info = reviewService.deleteReview(searchId);
    	
    	//model.addAttribute("info", info);
    	
    	return "redirect:/users/myinfo";
    }

}
