package kr.co.ucomp.web.csm.faq.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.faq.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.faq.entity.FaqEntity;
import kr.co.ucomp.web.csm.faq.mapper.FaqMapper;
import kr.co.ucomp.web.csm.faq.service.FaqService;

@Service("FaqService")

public class FaqServiceImpl implements FaqService {
	@Autowired
	FaqMapper mapper;
	
	/**
	 * faq list 조회
	 * @param : NoticeSearchDto
	 * @return List<FaqEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FaqEntity> getListFaq(FaqSearchDto param) {
		List<FaqEntity> list = null; 	
		long count = mapper.getListFaqCount(param);
		if(count > 0)
		list = mapper.getListFaq(param);
		
		return list;
		
	}
	
	public long getListFaqCount(FaqSearchDto param)  {
		long count = mapper.getListFaqCount(param);
		return  count;
	}
	
	
	/**
	 * faq 단건 조회
	 * @param : id(조회 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public FaqEntity getFaq(@Param("id") int param) {
		FaqEntity record = mapper.getFaq(param);
		return record;
	}
	
}
