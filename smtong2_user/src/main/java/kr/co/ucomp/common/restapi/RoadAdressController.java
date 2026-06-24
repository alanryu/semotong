package kr.co.ucomp.common.restapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ucomp.common.response.CustomApiResponse;
import kr.co.ucomp.common.response.ResponseCode;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;

@Controller
@RequestMapping("api/roadAdress")
public class RoadAdressController {
	
	@Value("${roadjuso.business.url}") 
	String apiBaseUrl;
	@Value("${roadjuso.business.confmKey}") 
	String confmKey;
	
	
	@Autowired
	private RestTempletUtil rest;

	@PostMapping(value="/getAddrApi")
	@ResponseBody
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> getAddrApi( HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception {
		// 요청변수 설정
		Integer currentPage = (Integer)param.get("currentPage");    //요청 변수 설정 (현재 페이지. currentPage : n > 0)
		Integer countPerPage = (Integer)param.get("countPerPage");  //요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100)
		String resultType = "json";      //요청 변수 설정 (검색결과형식 설정, json)
		String keyword = (String)param.get("keyword");            //요청 변수 설정 (키워드)
		
		try {
		
		// OPEN API 호출 URL 정보 설정
		String apiUrl = apiBaseUrl+"?currentPage="+currentPage+"&countPerPage="+countPerPage+"&keyword="+URLEncoder.encode(keyword,"UTF-8")+"&confmKey="+confmKey+"&resultType="+resultType;

		Map<String, Object>  res = rest.sendRestApi("GET_ADDR",apiUrl, "POST", null, null);
		
		return CustomApiResponse.success(ResponseCode.OK, res);
		
		} catch (Exception e) {
			
			return CustomApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR);
		}
    }
	
	
	 /**
	  * 2025-02-20 조일근
	  * 09) esim 정보 입력
	  * @param CommCodeSearchDto
	  * @param 
	  */
	 @GetMapping("/searchAddrPop")
	 public String  searchAddrPop(  HttpServletRequest request , HttpServletResponse response,Model model,@ModelAttribute PlanOrderEntity planOrderEntity )  {
	 	
		 
	 	return "pages/cmm/util/searchAddrPop";
	 }
}
