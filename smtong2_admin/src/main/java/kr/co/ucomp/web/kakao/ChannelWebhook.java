package kr.co.ucomp.web.kakao;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.ucomp.web.mbm.entity.UserEntity;
import kr.co.ucomp.web.mbm.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/kakao")
public class ChannelWebhook {

	@Autowired
	private UserService userService;
	
	@SuppressWarnings("unused")
	@PostMapping(value = "/webhook")
    public void webhook ( @RequestBody Map<String, Object> result ) throws Exception {
		
		log.info("Kakao Webhook Result :: {}", result);
		
		log.info("kakao id :: {}", result.get("id"));
		
		if ( result != null ) {
			
			UserEntity entity = new UserEntity();
			
			String kakaoId = MapUtils.getString(result, "id", "");
			if ( !StringUtils.isEmpty(kakaoId) ) {
				
				String kakaoUserId = "kakao_" + result.get("id").toString();
				String event = MapUtils.getString(result, "event", "");
				
				entity.setKakaoUserId(kakaoUserId);
				if ( StringUtils.equals("added", event) ) {
					entity.setChannelYn(1);
				} else if ( StringUtils.equals("blocked", event) ) {
					entity.setChannelYn(0);
				}
				
				int resultType = userService.updateChannel(entity);
				
				log.info("User Chnnel Update Success!! kakaoid :: {}", kakaoUserId);
			}
		} else {
			log.info("Channel Webhook Result fail!!");
		}
    }
}
