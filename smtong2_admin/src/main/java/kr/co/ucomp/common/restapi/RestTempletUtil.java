package kr.co.ucomp.common.restapi;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.ucomp.common.restapi.entity.RestApiLogEntity;
import kr.co.ucomp.common.restapi.entity.RestApiTokenMngEntity;
import kr.co.ucomp.common.restapi.mapper.RestApiMapper;
import kr.co.ucomp.web.order.entity.PlanOrderEntity;

@Service
public class RestTempletUtil {
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RestApiMapper restApiMapper;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	
	
	public <T> Map<String, Object> sendRestApi(String apiCd,String url,String methodType,T bodyMap,Map<String,String> headerMap) throws JsonProcessingException {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultBody  = new HashMap<String, Object>(); 
		String resultStatus = "";
		String resultMsg = "";
		try {
			 
			// 1. URI 설정
	        URI uri = URI.create(url);

	        // 2. 요청 헤더 설정 (선택 사항, 예: Authorization 헤더)
	        HttpHeaders headers = new HttpHeaders();
	        
	        if(headerMap !=null) {
	        	Iterator<String> iterator = headerMap.keySet().iterator();
		        while (iterator.hasNext()) {
		            String key = iterator.next();
		            String keyVal = headerMap.get(key);
		            headers.set(key, keyVal);
		        }	
	        }
	        
	        headers.set("Accept", "*/*");
	        headers.set("Content-Type", "application/json");
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        
	        // 3. 요청 바디 설정
	        String body = "";
	        if (bodyMap != null) {
	            body = objectMapper.writeValueAsString(bodyMap); // Convert bodyMap to JSON string
	            body = body.replace("\\\\n", "\\n");
	        }

	        HttpEntity<String> entity = new HttpEntity<>(body, headers);
	        
	        
	        
	        // method 설정
	        HttpMethod method = "GET".equals(methodType) ? HttpMethod.GET : HttpMethod.POST;
	        
            //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
            ResponseEntity<Map> responseEntity = restTemplate.exchange(uri, method, entity, Map.class);
            if(responseEntity !=null) {
            	resultBody = (Map<String, Object>) responseEntity.getBody();
                resultStatus = responseEntity.getStatusCode() !=null ? responseEntity.getStatusCode().toString() : "999";
                resultMsg =  "sucess";
            }
            
 
        } catch (HttpClientErrorException | HttpServerErrorException e) {
        	resultStatus = "999";
        	resultMsg =  e.getMessage();
 
        } catch (Exception e) {
        	resultStatus = "999";
        	resultMsg =  e.getMessage();
        }
		
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		String reqMsg = objectMapper.writeValueAsString(bodyMap);
		String reqBody = objectMapper.writeValueAsString(resultBody);
		
		
		RestApiLogEntity logparam = new RestApiLogEntity();
		logparam.setApiCode(apiCd);
		logparam.setApiName(apiCd);
		logparam.setApiUrl(url);
		logparam.setReqMsg(reqMsg);
		logparam.setResBody(reqBody);
		logparam.setResMsg(resultMsg);
		restApiMapper.createLog(logparam);
		
		
		
		result.put("resultStatus", resultStatus);
		result.put("resultMsg", resultMsg);
		result.put("resultBody", resultBody);
		
		return result;
		
	}
	

    @Transactional
    public long createToken(RestApiTokenMngEntity param) {
        return restApiMapper.createToken(param);
    }

    @Transactional
    public long deleteToken(@Param("tokenCode") String tokenCode) {
        return restApiMapper.deleteToken(tokenCode);
    }
    
    @Transactional
    public RestApiTokenMngEntity getToken(@Param("tokenCode") String tokenCode) {
        return restApiMapper.getToken(tokenCode);
    }
    
	

}
