package kr.co.ucomp.web.csm.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.co.ucomp.web.csm.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.entity.FaqEntity;


public interface FaqService {
	
	List<FaqEntity> getListFaq(FaqSearchDto param);
	
	long getListFaqCount(FaqSearchDto param);
	
	
	FaqEntity getFaq(@Param("id") int param);
	
	long create(FaqEntity param);
	
	long update(FaqEntity param);
	
	long delFaq(@Param("id") int param);
	
	
	
	
}
