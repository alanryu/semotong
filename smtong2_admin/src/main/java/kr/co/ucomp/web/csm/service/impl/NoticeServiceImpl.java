package kr.co.ucomp.web.csm.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.dto.NoticeSearchDto;
import kr.co.ucomp.web.csm.entity.NoticeEntity;
import kr.co.ucomp.web.csm.mapper.NoticeMapper;
import kr.co.ucomp.web.csm.service.NoticeService;

import java.util.List;

@Service("NoticeService")
public class NoticeServiceImpl implements NoticeService {
	@Autowired
	NoticeMapper mapper;
	
	/**
	 * faq list 조회
	 * @param : FaqSearchDto
	 * @return List<FaqEntity>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<NoticeEntity> getListNotice(NoticeSearchDto param) {
		
		return  mapper.getListNotice(param);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long getListNoticeCount(NoticeSearchDto param) {
		long count = mapper.getListNoticeCount(param);
		return count;
	}
	
	/**
	 * faq 단건 조회
	 * @param : id(조회 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public NoticeEntity getNotice(@Param("id") int param) {
//		NoticeEntity record = mapper.getNotice(param);
		return mapper.getNotice(param);
	}
	
	/**
	 * faq 저장
	 * @param : FaqEntity
	 * @return 결과(생성 갯수)
	 */
	@Override
	@Transactional
	public long create(NoticeEntity param) {
		
		return mapper.create(param);
		
	}
	
	/**
	 * faq 수정
	 * @param : FaqEntity
	 * @return 결과(수정 갯수)
	 */
	@Override
	@Transactional
	public long update(NoticeEntity param) {
		
		return mapper.update(param);
		
	}
	
	/**
	 * faq 단건 조회
	 * @param : id(삭제 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional
	public long delNotice(@Param("id") int param) {
		
		return mapper.delNotice(param);
	}
	
}
