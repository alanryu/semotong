package kr.co.ucomp.common.meta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ucomp.common.meta.entity.MetaInfo;
import kr.co.ucomp.web.cmm.dto.CommCodeSearchDto;
import kr.co.ucomp.web.cmm.entity.CodeEntity;
import kr.co.ucomp.web.cmm.service.CommCodeMngService;
	
@Service
public class MetaInfoService {
	 private static MetaInfoService instance;

	private final CommCodeMngService commCodeMngService;

	@Autowired
	public MetaInfoService(CommCodeMngService commCodeMngService) {
		this.commCodeMngService = commCodeMngService;
		instance = this; // 싱글톤 인스턴스 저장
	}

	public static MetaInfoService getInstance() {
		return instance;
	}

	public void setMetaInfo(Model model, HttpServletRequest request) {
		String fullUrl = request.getRequestURI();
		String normalizedUrl = normalizeUrl(fullUrl);
		
//		System.out.println("////////////////////////////////////////////////////////////");
//		System.out.println("normalizedUrl:" + normalizedUrl);
//		System.out.println("////////////////////////////////////////////////////////////");
		
		CommCodeSearchDto searchDto = new CommCodeSearchDto();
		searchDto.setCodeGroup("meta_info");
		searchDto.setUserYn("Y");
		List<CodeEntity> codeList = commCodeMngService.getListCode(searchDto);

		MetaInfo metaInfo = new MetaInfo();
		for(CodeEntity itm : codeList) {
			if(itm.getCodeDesc().equals(normalizedUrl) && itm.getEtc5().equals("Y")) {
				metaInfo.setMetaTitle(itm.getEtc1());
				metaInfo.setMetaKeywords(itm.getEtc2());
				metaInfo.setMetaDescription(itm.getEtc3());
			}
		}
		
		//없을 때 기본 text html 내 있다.
//		if (metaInfo == null) {
//			metaInfo = new MetaInfo(null, "기본 타이틀", "기본 키워드", "기본 설명");
//		}

		model.addAttribute("metaTitle", metaInfo.getMetaTitle());
		model.addAttribute("metaKeywords", metaInfo.getMetaKeywords());
		model.addAttribute("metaDescription", metaInfo.getMetaDescription());
	}
	
	private String normalizeUrl(String url) {
		return url.replaceAll("/\\d+", "").replaceAll("/[a-f0-9\\-]{36}", ""); // ID숫자 제거, UUID패턴 제거
	}
}