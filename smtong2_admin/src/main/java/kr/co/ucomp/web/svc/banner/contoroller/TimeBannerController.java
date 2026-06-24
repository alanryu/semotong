package kr.co.ucomp.web.svc.banner.contoroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.mbm.service.CompanyListService;
import kr.co.ucomp.web.pmb.service.PlanService;
import kr.co.ucomp.web.svc.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.svc.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.svc.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.svc.banner.entity.MainDealMstEntity;
import kr.co.ucomp.web.svc.banner.service.DealBannerService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/svc/mainbanner")
@Slf4j
public class TimeBannerController {

	@Autowired 
    private DealBannerService dealBannerService;
    @Autowired
    private CompanyListService companyListService;
    @Autowired
    private PlanService planService;
	
	
}
