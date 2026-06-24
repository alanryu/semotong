package kr.co.ucomp.common.exception.handler;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;



@ControllerAdvice
public class GlobalModelAddHandler {

	@Value("${google.analytics.id}") String googleAnaId;
	@Value("${google.tagManager.id}") String googleTagMngId;
	@Value("${app.base-url}") String baseUrl;

	@ModelAttribute
	public void addGlobalAttributes(Model model) {
		model.addAttribute("googleAnalyticsId", googleAnaId);
		model.addAttribute("googleTagManagerId", googleTagMngId);
		model.addAttribute("baseUrl", baseUrl);
	}

}

