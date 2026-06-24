package kr.co.ucomp.web.csm.faq.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.faq.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.faq.entity.FaqEntity;


public interface FaqService {
	
	List<FaqEntity> getListFaq(FaqSearchDto param);
	
	long getListFaqCount(FaqSearchDto param);
	
	
	FaqEntity getFaq(@Param("id") int param);
	
	
	
	
}
