package kr.co.ucomp.web.csm.banner.contoroller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.csm.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.csm.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.csm.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.csm.banner.entity.MainDealMstEntity;
import kr.co.ucomp.web.csm.banner.service.DealBannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.util.List;

/**
 * Main Deal Mst
 * @author 김재희
 * @since 2024.12.20
 * @version v1.0
 *
 * Main Deal Banner
 * @author 김재희
 * @since 2024.12.21
 * @version v1.0
 */

@Controller
@RequestMapping(value = "/svc")
@Slf4j
public class DealBannerController {

    @Autowired private DealBannerService dealBannerService;

    /* ============================== Main Deal ============================== */
    
    @PostMapping(value = "/deal-mst/list")
    public ResponseEntity<CustomApiResponse<List<MainDealMstEntity>>> getMainDealMstList(
            HttpServletRequest request,
            @RequestBody MainDealMstDto param
    ) throws IOException {

        try{

            List<MainDealMstEntity> resultList = dealBannerService.mainDealMstList(param);


            int cnt = resultList.size();

            return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());

        }

    }

    @GetMapping("/deal-mst/detail/{id}")
    public ResponseEntity<CustomApiResponse<MainDealMstEntity>> getMainDealMst(
            HttpServletRequest request,
            @PathVariable("id") long id
    ) throws IOException {

        try{

            MainDealMstEntity result = dealBannerService.mainDealMst(id);

            if (result == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND, "메인 딜 배너 정보가 존재 하지 않습니다.");
            }

            return CustomApiResponse.success(ResponseCode.OK, result);

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getMainDealMst: " + e.getMessage());

        }

    }

    /* ============================================================================== */



    /* ============================== Main Deal Banner ============================== */

    @PostMapping(value = "/deal-banner/list")
    public ResponseEntity<CustomApiResponse<List<MainDealBannerEntity>>> getMainDealBannerList(
            HttpServletRequest request,
            @RequestBody MainDealBannerDto param
    ) throws IOException {

        try{

            List<MainDealBannerEntity> resultList = dealBannerService.mainDealBannerList(param);

            int cnt = resultList.size();

            return CustomApiResponse.success(ResponseCode.OK, cnt, resultList);

        } catch (Exception e) {

            e.printStackTrace();
            // TODO: handle exception
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "Error processing kakao callback: " + e.getMessage());

        }

    }

    @GetMapping("/deal-banner/detail/{id}")
    public ResponseEntity<CustomApiResponse<MainDealBannerEntity>> getMainDealBanner(
            HttpServletRequest request,
            @PathVariable("id") long id
    ) throws IOException {

        try{

            MainDealBannerEntity result = dealBannerService.mainDealBanner(id);

            if (result == null) {
                return CustomApiResponse.error(ResponseCode.NOT_FOUND, "메인 딜 배너가 존재하지 않습니다.");
            }

            return CustomApiResponse.success(ResponseCode.OK, result);

        } catch (Exception e) {

            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "getMainDealBanner: " + e.getMessage());

        }

    }


    /* ============================================================================== */

}
