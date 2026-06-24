package kr.co.ucomp.web.main.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.csm.banner.dto.BannerSearchDto;
import kr.co.ucomp.web.csm.banner.entity.BannerEntity;
import kr.co.ucomp.web.csm.banner.service.BannerService;
import kr.co.ucomp.web.csm.faq.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.faq.entity.FaqEntity;
import kr.co.ucomp.web.csm.faq.service.FaqService;
import kr.co.ucomp.web.plan.dto.SearchPlanDto;
import kr.co.ucomp.web.plan.entity.PlanEntity;
import kr.co.ucomp.web.plan.service.PlanService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/main")
@Slf4j
public class BannerPlanController {
	
	@Autowired 
	private PlanService planService;
	
	@Autowired 
	private BannerService bannerService;
	
	@Autowired 
	private FaqService faqService;
	
	@SuppressWarnings("unchecked")
	@GetMapping( value = "/bannPlanList" )
	public String bannPlanList ( HttpServletResponse response, Model model, @RequestParam("id") String id ) throws Exception  {
		
		ObjectMapper mapper = new ObjectMapper();
		
		/* 진입 배너 */
		BannerEntity entity = bannerService.getDetail(Integer.parseInt(id));
		if(StringUtils.isNoneBlank(entity.getImagePc())) {
			Map<String, Object> map = mapper.readValue(entity.getImagePc(), Map.class);
			entity.setImagePc(map.get("fileUrl").toString());	
		}
		if(StringUtils.isNoneBlank(entity.getImageMo())) {    			
			Map<String, Object> map  = mapper.readValue(entity.getImageMo(), Map.class);
			entity.setImageMo(map.get("fileUrl").toString());
		}
		model.addAttribute("entity", entity);
		
		/* 결합 배너 (searchType : 05) */
    	BannerSearchDto bannerSearchDto = new BannerSearchDto();
    	bannerSearchDto.setSearchUseYn("Y");
    	bannerSearchDto.setSearchBannerType("05");
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
    	
    	model.addAttribute("bannerList", bannerList);
    	
    	/* 추천요금제 자주묻는질문 */
		FaqSearchDto faqSearchDto = new FaqSearchDto();
		faqSearchDto.setCategoryId("cate07");
		faqSearchDto.setDisplayYn("1");
		List<FaqEntity> faqList = faqService.getListFaq(faqSearchDto);
		
		model.addAttribute("faqList", faqList);
		
		return "pages/main/bannPlanList";
	}
	
	/**
	 * 배너에 등록된 요금제 가져오기
	 * @param request
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
    @PostMapping(value = "/getPlanlist")
    public ResponseEntity<CustomApiResponse<List<PlanEntity>>> getReviewList( HttpServletRequest request, @RequestBody SearchPlanDto param ) throws IOException { 
		
		try{
        	List<PlanEntity> resultList = new ArrayList<PlanEntity>();
        	long resulCnt = 0;
        	
    		resulCnt = planService.getBannerPlanListCount(param);
       	 
        	if( resulCnt > 0 ) {
        		resultList = planService.getBannerPlanList(param);
        	}	
        	
            return CustomApiResponse.success(ResponseCode.OK, resulCnt, resultList);
        } catch (Exception e) {
            e.printStackTrace();
            return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
        }
	}
}
