package kr.co.ucomp.web.main.controller;



import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.meta.MetaInfoService;
import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.dto.MainDealBannerDto;
import kr.co.ucomp.web.csm.banner.dto.MainDealMstDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;
import kr.co.ucomp.web.csm.banner.entity.MainDealBannerEntity;
import kr.co.ucomp.web.csm.banner.entity.MainDealMstEntity;
import kr.co.ucomp.web.csm.banner.service.BannerService;
import kr.co.ucomp.web.csm.banner.service.DealBannerService;
import kr.co.ucomp.web.csm.review.dto.SemotongReviewDto;
import kr.co.ucomp.web.csm.review.entity.SemotongReviewEntity;
import kr.co.ucomp.web.csm.review.service.SemotongReviewService;
import kr.co.ucomp.web.event.service.EvtService;
import kr.co.ucomp.web.plan.dto.RecomPlanMngSearchDto;
import kr.co.ucomp.web.plan.entity.RecomPlanEntity;
import kr.co.ucomp.web.plan.service.RecomPlanMngService;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 김재희
 * @since 2024.12.20
 * @version v1.0
 */
@Controller
@RequestMapping(value = "/")
@Slf4j
public class MainController {
	
	@Autowired
	private DealBannerService dealBannerService;
	
	@Autowired
	private BannerService bannerService;
	
	@Autowired
	private SemotongReviewService reviewService;
	
	@Autowired
	private EvtService evtService;
	
	
	@Autowired private RecomPlanMngService recomPlanMngService;
	
    /**
     * 메인 화면
     * @param response
     * @param model
     * @return
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    @SuppressWarnings("unchecked")
	@GetMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model ) throws Exception  {
    	
    	MetaInfoService.getInstance().setMetaInfo(model, request); // 현재 URI title, keyword, description 바로 호출 가능
    	
    	ObjectMapper mapper = new ObjectMapper();
    	MainDealMstDto params = new MainDealMstDto();
    	
    	/* 타임딜 가져오기 ( type : 08 ) */
    	params.setType("08");
    	MainDealMstEntity timeDeal = dealBannerService.mainDealMstRec(params);
    	if ( timeDeal != null ) {
    		
    		if(StringUtils.isNoneBlank(timeDeal.getPop_image())) {
    			Map<String, Object> map = mapper.readValue(timeDeal.getPop_image(), Map.class);
    			timeDeal.setPop_image(map.get("fileUrl").toString());
			}
    		
    		/* 타임딜 (요금제)*/
    		MainDealBannerDto param = new MainDealBannerDto();
        	param.setMain_deal(timeDeal.getId());
        	param.setSearchOrderType("orderNo");
        	
        	List<MainDealBannerEntity> timeDealList = dealBannerService.mainPageDealBanner(param);
        	model.addAttribute("timeDealList", timeDealList);
        	
        	for ( MainDealBannerEntity temp : timeDealList ) {
        		
        		boolean isView = !StringUtils.isEmpty(temp.getSecond_price());
        		temp.setSecondView(isView);
        	}
        	
        	/*** 현재 시간과 이벤트 종료시간 비교 ***/
    		LocalDateTime now = LocalDateTime.now();
    		Date endDate = timeDeal.getEnd_date();
    		
    		/* Date -> LocalDateTime */
    		LocalDateTime endDateLocal = endDate.toInstant()
    				.atZone(ZoneId.systemDefault())
    				.toLocalDateTime();
    		
    		/* LocalDateTime 비교 후 단위 환산 */
    		long duration = Duration.between(now, endDateLocal).getSeconds();
    		model.addAttribute("sec", duration);
    		
    		/* 딤드처리시간 계산 */
    		Date startDate = timeDeal.getStart_date();
    		
    		/* Date -> LocalDateTime */
    		LocalDateTime startDateLocal = startDate.toInstant()
    				.atZone(ZoneId.systemDefault())
    				.toLocalDateTime();
    		
    		/* LocalDateTime 비교 후 단위 환산 */
    		long dimmedDuration = Duration.between(now, startDateLocal).getSeconds();
    		if ( dimmedDuration > 0 ) {
    			model.addAttribute("dimmedSec", dimmedDuration);
    		}
    		
    		/* 타임딜 이벤트 ID */
    		long eventId = evtService.getTimeDealEventId();
    		timeDeal.setEventId(eventId);
    		
    		model.addAttribute("timeDeal", timeDeal);
    	}
    	
    	/* 추천딜 가져오기 (type : 07 ) */
    	params.setType("07");
    	MainDealMstEntity mainDeal = dealBannerService.mainDealMstRec(params);
    	
    	/* 추천딜 요금제 get */
    	if ( mainDeal != null ) {
    		
    		model.addAttribute("mainDeal", mainDeal);
    		
    		/*** 현재 시간과 이벤트 종료시간 비교 ***/
    		LocalDateTime now = LocalDateTime.now();
    		Date endDate = mainDeal.getEnd_date();
    		
    		/* Date -> LocalDateTime */
    		LocalDateTime endDateLocal = endDate.toInstant()
    				.atZone(ZoneId.systemDefault())
    				.toLocalDateTime();
    		
    		/* LocalDateTime 비교 후 단위 환산 */
    		Duration duration = Duration.between(now, endDateLocal);
    		long sec = duration.toSeconds();
    		long hour = (sec / 60) / 60;
    		long minute = (sec / 60) % 60;
    		long second = sec % 60;
    		
    		log.info("시간 환산 값 = {}:{}:{} ", String.format("%03d", hour), String.format("%02d", minute), String.format("%02d",second));
    		
    		//model.addAttribute("sec", sec);
    		
    		/* 추천딜 배너(요금제)*/
    		MainDealBannerDto param = new MainDealBannerDto();
        	param.setMain_deal(mainDeal.getId());
        	param.setSearchOrderType("orderNo");
        	
        	List<MainDealBannerEntity> mainDealList = dealBannerService.mainPageDealBanner(param);
        	model.addAttribute("mainDealList", mainDealList);
        	
        	for ( MainDealBannerEntity temp : mainDealList ) {
        		
        		boolean isView = !StringUtils.isEmpty(temp.getSecond_price());
        		temp.setSecondView(isView);
        	}


    	}
    	
    	/* 요금제 배너 (searchType : 02) */
    	BannerSearchDto bannerSearchDto = new BannerSearchDto();
    	bannerSearchDto.setSearchUseYn("Y");
    	bannerSearchDto.setSearchBannerType("02");
    	bannerSearchDto.setIsDispStatusDsp(1);
    	List<BannerEntity> bannerList = bannerService.list(bannerSearchDto);
    	
    	if ( bannerList.size() > 0 ) {
    		
    		for ( BannerEntity temp : bannerList ) {
    			if(StringUtils.isNoneBlank(temp.getImagePc())) {
    				Map<String, Object> map = mapper.readValue(temp.getImagePc(), Map.class);
        			temp.setImagePc(map.get("fileUrl").toString());	
    			}
    			if(StringUtils.isNoneBlank(temp.getImageMo())) {    			
    				Map<String, Object> map  = mapper.readValue(temp.getImageMo(), Map.class);
	    			temp.setImageMo(map.get("fileUrl").toString());
    			}
    		}
    	}
    	
    	model.addAttribute("chargeBannerList", bannerList);
    	
    	/* 중단 배너 (searchType : 03) */
    	bannerSearchDto = new BannerSearchDto();
    	bannerSearchDto.setSearchUseYn("Y");
    	bannerSearchDto.setSearchBannerType("03");
    	bannerSearchDto.setIsDispStatusDsp(1);
    	bannerList = bannerService.list(bannerSearchDto);
    	
    	if ( bannerList.size() > 0 ) {
    		
    		for ( BannerEntity temp : bannerList ) {
    			if(StringUtils.isNoneBlank(temp.getImagePc())) {
	    			Map<String, Object> map = mapper.readValue(temp.getImagePc(), Map.class);
	    			temp.setImagePc(map.get("fileUrl").toString());
    			}
    			if(StringUtils.isNoneBlank(temp.getImageMo())) {
	    			Map<String, Object> map = mapper.readValue(temp.getImageMo(), Map.class);
	    			temp.setImageMo(map.get("fileUrl").toString());
    			}
    		}
    	}
    	
    	model.addAttribute("middleBannerList", bannerList);
    	
    	/* 이벤트 배너 (searchType : 04) */
    	bannerSearchDto = new BannerSearchDto();
    	bannerSearchDto.setSearchUseYn("Y");
    	bannerSearchDto.setSearchBannerType("04");
    	bannerSearchDto.setIsDispStatusDsp(1);
    	bannerList = bannerService.list(bannerSearchDto);
    	
    	if ( bannerList.size() > 0 ) {
    		
    		for ( BannerEntity temp : bannerList ) {
    			if(StringUtils.isNoneBlank(temp.getImagePc())) {
	    			Map<String, Object> map = mapper.readValue(temp.getImagePc(), Map.class);
	    			temp.setImagePc(map.get("fileUrl").toString());
    			}
    			if(StringUtils.isNoneBlank(temp.getImageMo())) {
    				Map<String, Object> map = mapper.readValue(temp.getImageMo(), Map.class);
	    			temp.setImageMo(map.get("fileUrl").toString());
    			}
    		}
    	}
    	
    	model.addAttribute("eventBannerList", bannerList);
    	
    	SemotongReviewDto rvparam = new SemotongReviewDto();
    	rvparam.setPage(1);
    	rvparam.setRecordSize(3);
    	rvparam.setDisplayYn("Y");
    	rvparam.setReviewType("SEMOTONG");
    	int rvlstCnt = reviewService.reviewListCount(rvparam);
    	List<SemotongReviewEntity> rvresultList = new ArrayList<SemotongReviewEntity>();
    	if(rvlstCnt > 0) {
    		rvresultList = reviewService.reviewList(rvparam);	
    		model.addAttribute("rvresultList", rvresultList);
    	}
    	
    	
    	// 추천요금제 정보 리스트
    	RecomPlanMngSearchDto recomPlanParam = new RecomPlanMngSearchDto();
    	recomPlanParam.setUseYn(1);
        long recomPlanCnt = recomPlanMngService.infolistCount(recomPlanParam);

        List<RecomPlanEntity> recomPlanList = new ArrayList<RecomPlanEntity>();
        if (recomPlanCnt > 0) {
        	recomPlanList = recomPlanMngService.infolist(recomPlanParam);
        	model.addAttribute("recomPlanList", recomPlanList);
        }
        
        
        
        
        /*메인 빅 배너 (searchType : 07) */
        bannerSearchDto = new BannerSearchDto();
    	bannerSearchDto.setSearchUseYn("Y");
    	bannerSearchDto.setSearchBannerType("07");
    	bannerSearchDto.setIsDispStatusDsp(1);
    	bannerList = bannerService.list(bannerSearchDto);
    	
    	if ( bannerList.size() > 0 ) {
    		
    		for ( BannerEntity temp : bannerList ) {
    			if(StringUtils.isNoneBlank(temp.getImagePc())) {
	    			Map<String, Object> map = mapper.readValue(temp.getImagePc(), Map.class);
	    			temp.setImagePc(map.get("fileUrl").toString());
    			}
    			if(StringUtils.isNoneBlank(temp.getImageMo())) {
    				Map<String, Object> map = mapper.readValue(temp.getImageMo(), Map.class);
	    			temp.setImageMo(map.get("fileUrl").toString());
    			}
    		}
    	}    	
    	model.addAttribute("bigBannerList", bannerList);
    	
    	return "pages/main";
    }
    
}
