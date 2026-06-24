package kr.co.ucomp.web.csm.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.dto.FaqSearchDto;
import kr.co.ucomp.web.csm.entity.FaqEntity;
import kr.co.ucomp.web.csm.mapper.FaqMapper;
import kr.co.ucomp.web.csm.service.FaqService;

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
		List<FaqEntity> list = mapper.getListFaq(param);
		
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
	
	/**
	 * faq 저장
	 * @param : FaqEntity
	 * @return 결과(생성 갯수)
	 */
	@Override
	@Transactional
	public long create(FaqEntity param) {
		
		return mapper.create(param);
		
	}
	
	/**
	 * faq 수정
	 * @param : FaqEntity
	 * @return 결과(수정 갯수)
	 */
	@Override
	@Transactional
	public long update(FaqEntity param) {
		
		return mapper.update(param);
		
	}
	
	/**
	 * faq 단건 조회
	 * @param : id(삭제 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional
	public long delFaq(@Param("id") int param) {
		
		return mapper.delFaq(param);
	}
	
}
