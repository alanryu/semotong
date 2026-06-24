package kr.co.ucomp.web.csm.notice.service.impl;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ucomp.web.csm.notice.dto.NoticeSearchDto;
import kr.co.ucomp.web.csm.notice.entity.NoticeEntity;
import kr.co.ucomp.web.csm.notice.mapper.NoticeMapper;
import kr.co.ucomp.web.csm.notice.service.NoticeService;

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
		List<NoticeEntity> list = null;
		long count = mapper.getListNoticeCount(param);
		if(count > 0) list = mapper.getListNotice(param);
		
		return list;
	}
	
	

	@Override
	public long getListNoticeCount(NoticeSearchDto param) {
		return mapper.getListNoticeCount(param);
	}
	
	
	/**
	 * faq 단건 조회
	 * @param : id(조회 id)
	 * @return FaqEntity record
	 */
	@Override
	@Transactional(readOnly = true)
	public NoticeEntity getNotice(@Param("id") long param) {
//		NoticeEntity record = mapper.getNotice(param);
		return mapper.getNotice(param);
	}



}
